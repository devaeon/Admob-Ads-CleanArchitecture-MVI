package com.devaeon.adsTemplate.data.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import com.devaeon.adsTemplate.data.source.InterstitialAdsDataSource
import com.devaeon.adsTemplate.data.source.RemoteAdsDataSource
import com.devaeon.adsTemplate.data.source.UserConsentDataSource
import com.devaeon.adsTemplate.di.Dispatcher
import com.devaeon.adsTemplate.di.HiltCoroutineDispatchers
import com.devaeon.adsTemplate.domain.model.AdState
import com.devaeon.adsTemplate.domain.model.UserConsentState
import com.devaeon.adsTemplate.domain.repository.AdsRepository
import com.devaeon.adsTemplate.domain.model.RemoteAdState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdsRepositoryImpl @Inject internal constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(HiltCoroutineDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    @Dispatcher(HiltCoroutineDispatchers.Main) mainDispatcher: CoroutineDispatcher,
    userConsentDataSource: UserConsentDataSource,
    private val remoteAdsDataSource: RemoteAdsDataSource,
    private val interstitialAdsDataSource: InterstitialAdsDataSource
) : AdsRepository {

    private val coroutineScopeIo: CoroutineScope = CoroutineScope(SupervisorJob() + ioDispatcher)
    private val coroutineScopeMain: CoroutineScope = CoroutineScope(SupervisorJob() + mainDispatcher)

    override val userConsentState: Flow<UserConsentState> =
        combine(
            userConsentDataSource.isInitialized,
            userConsentDataSource.isUserConsentingForAds,
            ::toUserConsentState,
        )

    override val isPrivacySettingRequired: Flow<Boolean> = userConsentDataSource.isPrivacyOptionsRequired

    override val adsState: StateFlow<AdState> = interstitialAdsDataSource.remoteAdState.map(::toAdState)
        .stateIn(coroutineScopeIo, SharingStarted.Eagerly, AdState.NOT_INITIALIZED)


    init {
        // Once the user has given his consent, initialize the ads sdk
        initAdsOnConsentFlow(context, userConsentDataSource.isUserConsentingForAds, adsState).launchIn(coroutineScopeIo)
    }

    private fun initAdsOnConsentFlow(context: Context, consent: Flow<Boolean>, adsState: Flow<AdState>): Flow<Unit> =
        combine(consent, adsState) { isConsenting, state ->
            if (!isConsenting || state != AdState.NOT_INITIALIZED) return@combine

            Log.i(TAG, "User consenting for ads, initialize ads SDK")
            interstitialAdsDataSource.initialize(context)
        }

    private fun toUserConsentState(
        isConsentInit: Boolean,
        isConsenting: Boolean
    ): UserConsentState =
        when {
            isConsenting -> UserConsentState.CAN_REQUEST_ADS
            isConsentInit && !isConsenting -> UserConsentState.CANNOT_REQUEST_ADS
            else -> UserConsentState.UNKNOWN
        }

    private fun toAdState(remoteAdState: RemoteAdState): AdState =
        when (remoteAdState) {
            RemoteAdState.SdkNotInitialized -> AdState.NOT_INITIALIZED
            RemoteAdState.Initialized -> AdState.INITIALIZED
            RemoteAdState.Loading -> AdState.LOADING
            is RemoteAdState.NotShown -> AdState.READY
            RemoteAdState.Showing -> AdState.SHOWING
            is RemoteAdState.Shown -> AdState.VALIDATED

            is RemoteAdState.Error.LoadingError,
            is RemoteAdState.Error.ShowError,
            RemoteAdState.Error.NoImpressionError -> AdState.ERROR
        }

    override fun loadInterstitialAdIfNeeded() {
        interstitialAdsDataSource.loadAd(context)
    }

    override fun showInterstitialAd(activity: Activity) {
        when (adsState.value) {
            AdState.READY -> interstitialAdsDataSource.showAd(activity)
            AdState.ERROR -> interstitialAdsDataSource.forceShown()
            else -> Unit
        }
    }

    override fun fetchRemoteConfiguration(fetchCallback: (Boolean) -> Unit) {
        remoteAdsDataSource.checkRemoteConfig(fetchCallback)
    }
}

private const val TAG = "AdsRepositoryImpl"
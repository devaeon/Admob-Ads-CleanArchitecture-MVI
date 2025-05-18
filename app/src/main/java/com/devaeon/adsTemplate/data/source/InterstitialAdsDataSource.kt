package com.devaeon.adsTemplate.data.source

import android.app.Activity
import android.content.Context
import android.util.Log
import com.devaeon.adsTemplate.BuildConfig
import com.devaeon.adsTemplate.R
import com.devaeon.adsTemplate.core.utilities.manager.InternetManager
import com.devaeon.adsTemplate.core.utilities.manager.SharedPreferenceUtils
import com.devaeon.adsTemplate.data.configuration.DebugAdStateReceiver
import com.devaeon.adsTemplate.data.configuration.getAdsDebugTestDevicesIds
import com.devaeon.adsTemplate.di.Dispatcher
import com.devaeon.adsTemplate.di.HiltCoroutineDispatchers
import com.devaeon.adsTemplate.domain.enums.InterAdKey
import com.devaeon.adsTemplate.domain.model.RemoteAdState
import com.devaeon.adsTemplate.domain.repository.IAdsSdk
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class InterstitialAdsDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(HiltCoroutineDispatchers.Main) mainDispatcher: CoroutineDispatcher,
    private val sharedPreferenceUtils: SharedPreferenceUtils,
    private val internetManager: InternetManager,
    private val adsSdk: IAdsSdk,
) {
    private val coroutineScopeMain: CoroutineScope = CoroutineScope(SupervisorJob() + mainDispatcher)


    /**
     * If a call to [loadAd] is made before the sdk is initialized, we register it and execute it as soon as the sdk
     * initialization is complete. Executed on the main thread due to sdk requirement.
     */
    private val pendingLoadRequest: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val debugRemoteAdState: MutableStateFlow<RemoteAdState?> = MutableStateFlow(null)

    private val _remoteAdState: MutableStateFlow<RemoteAdState> = MutableStateFlow(RemoteAdState.Initialized)

    val remoteAdState: Flow<RemoteAdState> = combine(_remoteAdState, debugRemoteAdState) { adState, debugState ->
        debugState ?: adState
    }

    private var debugReceiver: DebugAdStateReceiver? = null

    init {
        loadAdRequestConsumerFlow(context, _remoteAdState, pendingLoadRequest)
            .launchIn(coroutineScopeMain)

        if (BuildConfig.DEBUG) {
            debugReceiver = DebugAdStateReceiver { adState ->
                debugRemoteAdState.value = adState
            }.apply { register(context) }
        }
    }

    fun initialize(context: Context) {
        if (_remoteAdState.value != RemoteAdState.SdkNotInitialized) return

        Log.i(TAG, "Initialize MobileAds")

        adsSdk.initializeSdk(context) {
            getAdsDebugTestDevicesIds()?.let(adsSdk::setTestDevices)
            coroutineScopeMain.launch {
                _remoteAdState.emit(RemoteAdState.Initialized)
            }
        }
    }


    fun checkRemoteEnable(interAdKey: InterAdKey): Boolean {
        return when (interAdKey) {
            InterAdKey.SPLASH -> sharedPreferenceUtils.splashInterstitialAdUnit.trim().isNotEmpty()
        }
    }

    fun getAdId(interAdKey: InterAdKey):String{
        return if(BuildConfig.DEBUG){
            when (interAdKey) {
                InterAdKey.SPLASH -> context.resources.getString(R.string.admob_interstitial_ad_unit)
            }
        }else{
            when (interAdKey) {
                InterAdKey.SPLASH -> sharedPreferenceUtils.splashInterstitialAdUnit.trim()
            }
        }
    }

    fun loadAd(context: Context,interAdKey: InterAdKey) {
        if (_remoteAdState.value == RemoteAdState.SdkNotInitialized) {
            Log.i(TAG, "Load ad request delayed, SDK is not initialized")
            pendingLoadRequest.value = true
            return
        }

        if (_remoteAdState.value != RemoteAdState.Initialized && _remoteAdState.value !is RemoteAdState.Error) return

        if (sharedPreferenceUtils.isAppPurchased){
            Log.i(TAG, "loadAd: Premium user can't load ad")
            return
        }

        if (internetManager.isInternetConnected.not()){
            Log.i(TAG, "loadAd: internet not connected")
            return
        }

        if (checkRemoteEnable(interAdKey).not()){
            Log.i(TAG, "loadAd: ad not enabled")
            return
        }

        if (_remoteAdState.value is RemoteAdState.Loading){
            Log.i(TAG, "loadAd: ad is already loading")
            return
        }



            Log.i(TAG, "Load interstitial ad")
        coroutineScopeMain.launch {
            _remoteAdState.emit(RemoteAdState.Loading)

            adsSdk.loadInterstitialAd(
                context = context,
                interAdId = getAdId(interAdKey),
                onLoaded = ::onAdLoaded,
                onError = { code, message -> onAdLoadFailed(context, code, message) },
            )
        }
    }


    fun showAd(activity: Activity) {
        val remoteAd = _remoteAdState.value
        if (remoteAd !is RemoteAdState.NotShown) {
            Log.w(TAG, "Can't show ad, loading is not completed")
            return
        }

        Log.i(TAG, "Show interstitial ad")

        adsSdk.showInterstitialAd(
            activity = activity,
            onShow = ::onAdShown,
            onDismiss = ::onAdDismissed,
            onError = ::onAdShowError,
        )
    }

    fun reset() {
        if (_remoteAdState.value == RemoteAdState.Initialized
            || _remoteAdState.value == RemoteAdState.SdkNotInitialized
        ) return

        Log.i(TAG, "Reset ad state")
        coroutineScopeMain.launch {
            _remoteAdState.emit(RemoteAdState.Initialized)
        }
    }

    fun forceShown() {
        Log.i(TAG, "Force add shown")
        coroutineScopeMain.launch {
            _remoteAdState.emit(RemoteAdState.Shown)
        }
    }

    private fun onAdLoaded() {
        Log.i(TAG, "onAdLoaded")
        coroutineScopeMain.launch {
            _remoteAdState.emit(RemoteAdState.NotShown)
        }
    }

    private fun onAdLoadFailed(context: Context, errorCode: Int, errorMessage: String) {
        Log.w(TAG, "onAdFailedToLoad:  $errorCode:$errorMessage")
        coroutineScopeMain.launch {
            _remoteAdState.emit(RemoteAdState.Error.LoadingError(errorCode, errorMessage))
        }
    }

    private fun onAdShown() {
        Log.i(TAG, "onAdShown")

        coroutineScopeMain.launch {
            _remoteAdState.emit(RemoteAdState.Showing)
        }
    }

    private fun onAdDismissed(impression: Boolean) {
        Log.i(TAG, "onAdDismissed, impression=$impression")

        coroutineScopeMain.launch {
            _remoteAdState.emit(
                if (impression) RemoteAdState.Shown
                else RemoteAdState.Error.NoImpressionError
            )
        }
    }

    private fun onAdShowError(errorCode: Int, errorMessage: String) {
        Log.w(TAG, "onAdShowError: $errorCode:$errorMessage")

        coroutineScopeMain.launch {
            _remoteAdState.emit(RemoteAdState.Error.ShowError(errorCode, errorMessage))
        }
    }

    private fun loadAdRequestConsumerFlow(
        context: Context,
        adState: Flow<RemoteAdState>,
        isPending: MutableStateFlow<Boolean>,
    ): Flow<Unit> =
        combine(adState, isPending) { remoteAd, haveLoadRequest ->
            if (remoteAd != RemoteAdState.Initialized || !haveLoadRequest) return@combine

            Log.i(TAG, "Ads SDK is now initialized, consuming pending ad load request")

            isPending.emit(false)
            loadAd(context, InterAdKey.SPLASH)
        }
}

private const val TAG = "InterstitialAdsDataSource"
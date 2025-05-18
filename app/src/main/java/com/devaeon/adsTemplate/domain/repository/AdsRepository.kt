package com.devaeon.adsTemplate.domain.repository

import android.app.Activity
import android.content.Context
import com.devaeon.adsTemplate.domain.enums.InterAdKey
import com.devaeon.adsTemplate.domain.model.AdState
import com.devaeon.adsTemplate.domain.model.UserConsentState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AdsRepository {
    val userConsentState: Flow<UserConsentState>
    val isPrivacySettingRequired: Flow<Boolean>

     fun startUserConsentRequestUiFlowIfNeeded(activity: Activity)

    val adsState: StateFlow<AdState>

    fun fetchRemoteConfiguration(fetchCallback: (Boolean) -> Unit   )

    fun loadInterstitialAdIfNeeded(interAdKey: InterAdKey)
    fun showInterstitialAd(activity: Activity)

}
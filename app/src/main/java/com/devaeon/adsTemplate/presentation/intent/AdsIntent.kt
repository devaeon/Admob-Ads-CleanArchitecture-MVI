package com.devaeon.adsTemplate.presentation.intent

import android.app.Activity
import com.devaeon.adsTemplate.domain.enums.InterAdKey

sealed class AdsIntent {
    object FetchRemoteConfiguration : AdsIntent()
    data class LoadInterstitialAd(val interAdKey:  InterAdKey) : AdsIntent()
    data class ShowInterstitialAd(val activity: Activity) : AdsIntent()

    // Add more intents here
    // Native
    // Banner
    // Rewarded
    // AppOpen
}

package com.devaeon.adsTemplate.presentation.intent

import android.app.Activity

sealed class AdsIntent {
    object FetchRemoteConfiguration : AdsIntent()
    object LoadInterstitialAd : AdsIntent()
    data class ShowInterstitialAd(val activity: Activity) : AdsIntent()

    // Add more intents here
    // Native
    // Banner
    // Rewarded
    // AppOpen
}

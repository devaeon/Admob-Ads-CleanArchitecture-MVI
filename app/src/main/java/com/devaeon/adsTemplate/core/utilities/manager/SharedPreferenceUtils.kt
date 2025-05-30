package com.devaeon.adsTemplate.core.utilities.manager

import android.content.Context
import android.content.SharedPreferences
import com.devaeon.adsTemplate.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class SharedPreferenceUtils @Inject constructor(@ApplicationContext private val context: Context) {

    private val sharedPreferences by lazy { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }

    private val billingRequireKey = "isAppPurchased"
    private val isShowFirstScreenKey = "showFirstScreen"

    /* ---------- Billing ---------- */

    var isAppPurchased: Boolean
        get() = sharedPreferences.getBoolean(billingRequireKey, false)
        set(value) {
            sharedPreferences.edit().apply {
                putBoolean(billingRequireKey, value)
                apply()
            }
        }

    /* ---------- UI ---------- */

    var showFirstScreen: Boolean
        get() = sharedPreferences.getBoolean(isShowFirstScreenKey, true)
        set(value) {
            sharedPreferences.edit().apply {
                putBoolean(isShowFirstScreenKey, value)
                apply()
            }
        }

    /* ---------------------------------------- Ads ---------------------------------------- */

    val appOpen = "appOpen"
    val appOpenSplash = "appOpenSplash"

    val bannerHome = "bannerHome"

    val interOnBoarding = "interOnBoarding"
    val interFeature = "interFeature"

    val rewardedAiFeature = "rewardedAiFeature"
    val rewardedInterAiFeature = "rewardedInterAiFeature"

    val nativeLanguage = "nativeLanguage"
    val nativeOnBoarding = "nativeOnBoarding"
    val nativeFeature = "nativeFeature"
    val nativeHome = "nativeHome"
    val nativeExit = "nativeExit"

    /* ----- AppOpen Ads ----- */

    var rcAppOpen: Int
        get() = sharedPreferences.getInt(appOpen, 0)
        set(value) {
            sharedPreferences.edit().apply {
                putInt(appOpen, value)
                apply()
            }
        }

    var rcAppOpenSplash: Int
        get() = sharedPreferences.getInt(appOpenSplash, 1)
        set(value) {
            sharedPreferences.edit().apply {
                putInt(appOpenSplash, value)
                apply()
            }
        }

    /* ----- Banner Ads ----- */

    var rcBannerHome: Int
        get() = sharedPreferences.getInt(bannerHome, 0)
        set(value) {
            sharedPreferences.edit().apply {
                putInt(bannerHome, value)
                apply()
            }
        }

    /* ----- Interstitial Ads ----- */

    private  val INTER_SPLASH_AD_UNIT="inter_splash_ad_unit"

    val admobSplashInterstitialAdUnit = "admob_splash_interstitial_ad_unit"

    var splashInterstitialAdUnit: String
        get() = sharedPreferences.getString(
            admobSplashInterstitialAdUnit,
            context.resources.getString(R.string.admob_interstitial_ad_unit)
        ) ?: ""
        set(value) {
            sharedPreferences.edit().apply {
                putString(admobSplashInterstitialAdUnit, value)
                apply()
            }
        }

    var rcInterFeature: Int
        get() = sharedPreferences.getInt(interFeature, 1)
        set(value) {
            sharedPreferences.edit().apply {
                putInt(interFeature, value)
                apply()
            }
        }

    var rcInterOnBoarding: Int
        get() = sharedPreferences.getInt(interOnBoarding, 0)
        set(value) {
            sharedPreferences.edit().apply {
                putInt(interOnBoarding, value)
                apply()
            }
        }

    /* ----- Rewarded Ads ----- */

    var rcRewardedAiFeature: Int
        get() = sharedPreferences.getInt(rewardedAiFeature, 1)
        set(value) {
            sharedPreferences.edit().apply {
                putInt(rewardedAiFeature, value)
                apply()
            }
        }

    var rcRewardedInterAiFeature: Int
        get() = sharedPreferences.getInt(rewardedInterAiFeature, 0)
        set(value) {
            sharedPreferences.edit().apply {
                putInt(rewardedInterAiFeature, value)
                apply()
            }
        }

    /* ----- Native Ads ----- */

    var rcNativeLanguage: Int
        get() = sharedPreferences.getInt(nativeLanguage, 1)
        set(value) {
            sharedPreferences.edit().apply {
                putInt(nativeLanguage, value)
                apply()
            }
        }

    var rcNativeOnBoarding: Int
        get() = sharedPreferences.getInt(nativeOnBoarding, 0)
        set(value) {
            sharedPreferences.edit().apply {
                putInt(nativeOnBoarding, value)
                apply()
            }
        }

    var rcNativeHome: Int
        get() = sharedPreferences.getInt(nativeHome, 0)
        set(value) {
            sharedPreferences.edit().apply {
                putInt(nativeHome, value)
                apply()
            }
        }

    var rcNativeFeature: Int
        get() = sharedPreferences.getInt(nativeFeature, 0)
        set(value) {
            sharedPreferences.edit().apply {
                putInt(nativeFeature, value)
                apply()
            }
        }

    var rcNativeExit: Int
        get() = sharedPreferences.getInt(nativeExit, 0)
        set(value) {
            sharedPreferences.edit().apply {
                putInt(nativeExit, value)
                apply()
            }
        }
}
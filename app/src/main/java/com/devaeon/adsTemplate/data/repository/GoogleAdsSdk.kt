package com.devaeon.adsTemplate.data.repository

import android.app.Activity
import android.content.Context
import androidx.annotation.MainThread
import com.devaeon.adsTemplate.R
import com.devaeon.adsTemplate.domain.repository.IAdsSdk
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAdsSdk @Inject constructor() : IAdsSdk {

    private var interstitialAd: InterstitialAd? = null

    @MainThread
    override fun initializeSdk(context: Context, onComplete: () -> Unit) {
        MobileAds.initialize(context) { onComplete() }
    }

    @MainThread
    override fun setTestDevices(deviceIds: List<String>) {
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(deviceIds).build()
        )
    }

    @MainThread
    override fun loadInterstitialAd(
        context: Context,
        onLoaded: () -> Unit,
        onError: (code: Int, message: String) -> Unit,
    ) = InterstitialAd.load(
        context,
        context.getString(R.string.admob_interstitial_ad_unit),
        buildInterstitialAdRequest(),
        newAdLoadCallback(onLoaded, onError),
    )

    @MainThread
    override fun showInterstitialAd(
        activity: Activity,
        onShow: () -> Unit,
        onDismiss: (impression: Boolean) -> Unit,
        onError: (code: Int, message: String) -> Unit,
    ) {
        val ad = interstitialAd ?: return

        ad.fullScreenContentCallback = newAdShowCallback(onShow, onDismiss, onError)
        ad.show(activity)
        interstitialAd = null
    }

    private fun buildInterstitialAdRequest(): AdRequest =
        AdRequest.Builder().build()

    private fun newAdLoadCallback(onLoaded: () -> Unit, onError: (code: Int, message: String) -> Unit) =
        object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad
                onLoaded()
            }

            override fun onAdFailedToLoad(adError: LoadAdError): Unit =
                onError(adError.code, adError.message)
        }

    private fun newAdShowCallback(
        onShow: () -> Unit,
        onDismiss: (impression: Boolean) -> Unit,
        onError: (code: Int, message: String) -> Unit,
    ) = object : FullScreenContentCallback() {
        var impression = false
        override fun onAdImpression() {
            impression = true
        }

        override fun onAdShowedFullScreenContent(): Unit = onShow()
        override fun onAdDismissedFullScreenContent(): Unit = onDismiss(impression)
        override fun onAdFailedToShowFullScreenContent(adError: AdError): Unit = onError(adError.code, adError.message)
    }
}
package com.devaeon.adsTemplate.domain.repository

import android.app.Activity
import android.content.Context
import androidx.annotation.MainThread


interface IAdsSdk {

    @MainThread
    fun initializeSdk(context: Context, onComplete: () -> Unit)

    @MainThread
    fun setTestDevices(deviceIds: List<String>)

    @MainThread
    fun loadInterstitialAd(
        context: Context,
        interAdId: String,
        onLoaded: () -> Unit,
        onError: (code: Int, message: String) -> Unit,
    )

    @MainThread
    fun showInterstitialAd(
        activity: Activity,
        onShow: () -> Unit,
        onDismiss: (impression: Boolean) -> Unit,
        onError: (code: Int, message: String) -> Unit,
    )
}
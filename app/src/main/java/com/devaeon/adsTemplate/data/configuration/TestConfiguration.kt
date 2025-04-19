package com.devaeon.adsTemplate.data.configuration

import android.content.Context
import android.util.Log
import com.devaeon.adsTemplate.BuildConfig
import com.google.android.ump.ConsentDebugSettings

internal fun getConsentDebugSettings(context: Context): ConsentDebugSettings? {
    val testDeviceIds = BuildConfig.CONSENT_TEST_DEVICES_IDS
        .takeIf { BuildConfig.DEBUG && it.isNotBlank() }
        ?.split(",")
        ?.map { it.trim() }

    if (testDeviceIds.isNullOrEmpty()) return null

    return ConsentDebugSettings.Builder(context).apply {
        testDeviceIds.forEach { testDeviceId ->
            Log.d(TAG, "Using $testDeviceId as consent test device id")
            addTestDeviceHashedId(testDeviceId)
        }

        if (DEBUG_CONSENT_GEOGRAPHY != ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_DISABLED) {
            Log.d(TAG, "Using consent test geography $DEBUG_CONSENT_GEOGRAPHY")
            setDebugGeography(DEBUG_CONSENT_GEOGRAPHY)
        }
    }.build()
}

internal fun getAdsDebugTestDevicesIds(): List<String>? {
    val testDeviceIds = BuildConfig.ADS_TEST_DEVICES_IDS
        .takeIf { BuildConfig.DEBUG && it.isNotBlank() }
        ?.split(",")
        ?.map { it.trim() }

    if (testDeviceIds.isNullOrEmpty()) return null

    return buildList {
        testDeviceIds.forEach { testDeviceId ->
            Log.d(TAG, "Using $testDeviceId as ads test device id")
            add(testDeviceId)
        }
    }
}

@ConsentDebugSettings.DebugGeography
private val DEBUG_CONSENT_GEOGRAPHY: Int by lazy {
    when (BuildConfig.CONSENT_TEST_GEOGRAPHY.trim()) {
        "1" -> ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA
        "2" -> ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_NOT_EEA
        else -> ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_DISABLED
    }
}


private const val TAG = "RevenueTestConfiguration"
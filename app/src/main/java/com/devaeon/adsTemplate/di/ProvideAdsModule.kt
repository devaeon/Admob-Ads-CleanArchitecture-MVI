package com.devaeon.adsTemplate.di

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.devaeon.adsTemplate.core.utilities.manager.InternetManager
import com.devaeon.adsTemplate.core.utilities.manager.SharedPreferenceUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProvideAdsModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferenceUtils {
        return SharedPreferenceUtils(context)
    }

    @Provides
    @Singleton
    fun provideInternetManager(@ApplicationContext context: Context): InternetManager {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return InternetManager(connectivityManager)
    }
    
}
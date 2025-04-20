package com.devaeon.adsTemplate.di

import com.devaeon.adsTemplate.data.repository.AdsRepositoryImpl
import com.devaeon.adsTemplate.data.repository.GoogleAdsSdk
import com.devaeon.adsTemplate.domain.repository.AdsRepository
import com.devaeon.adsTemplate.domain.repository.IAdsSdk
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BindHiltModule{
    @Binds
    abstract fun bindAdsRepository(adsRepositoryImpl: AdsRepositoryImpl): AdsRepository

    @Binds
    abstract fun bindAdsSdk(googleAdsSdk: GoogleAdsSdk): IAdsSdk
}


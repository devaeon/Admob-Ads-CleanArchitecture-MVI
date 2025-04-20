package com.devaeon.adsTemplate.domain.repository

interface RemoteConfigRepository {
    suspend fun fetchAndActivate(): Result<Boolean> // returns if ads are enabled
}

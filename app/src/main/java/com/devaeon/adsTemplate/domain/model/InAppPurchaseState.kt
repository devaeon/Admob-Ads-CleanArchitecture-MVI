package com.devaeon.adsTemplate.domain.model

internal enum class InAppPurchaseState {
    NOT_PURCHASED,
    PENDING,
    PURCHASED,
    PURCHASED_AND_ACKNOWLEDGED,
    ERROR
}
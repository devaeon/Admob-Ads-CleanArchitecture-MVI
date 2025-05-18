package com.devaeon.adsTemplate.presentation.state

import com.devaeon.adsTemplate.domain.model.AdState
import com.devaeon.adsTemplate.domain.model.UserConsentState

data class AdsViewState(
    val adState: AdState = AdState.NOT_INITIALIZED,
    val isPrivacySettingRequired: Boolean = false,
    val userConsentState: UserConsentState = UserConsentState.CAN_REQUEST_ADS,
)
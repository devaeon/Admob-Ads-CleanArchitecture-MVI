package com.devaeon.adsTemplate.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devaeon.adsTemplate.domain.repository.AdsRepository
import com.devaeon.adsTemplate.presentation.intent.AdsIntent
import com.devaeon.adsTemplate.presentation.state.AdsViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AdsViewModel @Inject constructor(private val adsRepository: AdsRepository) : ViewModel() {

    private val _viewState = MutableStateFlow(AdsViewState())
    val viewState: StateFlow<AdsViewState> = _viewState.asStateFlow()

    private val intentChannel = Channel<AdsIntent>(Channel.UNLIMITED)
    val intents: SendChannel<AdsIntent> = intentChannel

    init {
        collectIntents()
        collectRepositoryState()
    }

    private fun collectIntents() {
        viewModelScope.launch {
            intentChannel.consumeAsFlow().collect { intent ->
                when (intent) {
                    is AdsIntent.FetchRemoteConfiguration -> {
                        adsRepository.fetchRemoteConfiguration { success ->
                            if (success) {
                                adsRepository.loadInterstitialAdIfNeeded()
                            } else {
                                // Handle error if needed
                            }
                        }
                    }
                    is AdsIntent.LoadInterstitialAd -> adsRepository.loadInterstitialAdIfNeeded()
                    is AdsIntent.ShowInterstitialAd -> adsRepository.showInterstitialAd(intent.activity)
                }
            }
        }
    }

    private fun collectRepositoryState() {
        viewModelScope.launch {
            combine(
                adsRepository.adsState,
                adsRepository.isPrivacySettingRequired,
                adsRepository.userConsentState
            ) { adState, privacyRequired, userConsent ->
                AdsViewState(
                    adState = adState,
                    isPrivacySettingRequired = privacyRequired,
                    userConsentState = userConsent
                )
            }.distinctUntilChanged()
                .collect {
                _viewState.value = it
            }
        }
    }
}
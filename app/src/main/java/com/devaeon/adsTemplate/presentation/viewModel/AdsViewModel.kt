package com.devaeon.adsTemplate.presentation.viewModel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.devaeon.adsTemplate.R
import com.devaeon.adsTemplate.core.ui.bindings.buttons.LoadableButtonState
import com.devaeon.adsTemplate.domain.model.AdState
import com.devaeon.adsTemplate.domain.repository.AdsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class AdsViewModel @Inject constructor(private val adsRepository: AdsRepository) : ViewModel() {

    val adsState: StateFlow<AdState> =adsRepository.adsState


    fun loadInterstitialAdIfNeeded(context: Context) {
        adsRepository.loadInterstitialAdIfNeeded(context)
    }

    fun showInterstitialAd(activity: Activity) {
        adsRepository.showInterstitialAd(activity)
    }

    sealed class DialogState {

        internal data class NotPurchased(val adButtonState: LoadableButtonState): DialogState()

        internal data object Purchased : DialogState()
        internal data object AdShowing : DialogState()
        internal data object AdWatched : DialogState()
    }

    private fun AdState.toAdButtonState(context: Context): LoadableButtonState = when (this) {
        AdState.INITIALIZED, AdState.LOADING -> LoadableButtonState.Loading(text = context.getString(R.string.button_text_watch_ad_loading))

        AdState.ERROR, AdState.READY -> LoadableButtonState.Loaded.Enabled(text = context.getString(R.string.button_text_watch_ad))

        AdState.SHOWING, AdState.VALIDATED -> LoadableButtonState.Loaded.Disabled(text = context.getString(R.string.button_text_watch_ad))

        AdState.NOT_INITIALIZED -> LoadableButtonState.Loaded.Disabled(text = context.getString(R.string.button_text_watch_ad_error))
    }

}
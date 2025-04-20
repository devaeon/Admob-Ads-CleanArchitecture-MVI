package com.devaeon.adsTemplate.presentation

import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.devaeon.adsTemplate.core.utilities.base.activities.BaseActivity
import com.devaeon.adsTemplate.databinding.ActivityStartingBinding
import com.devaeon.adsTemplate.domain.model.AdState
import com.devaeon.adsTemplate.presentation.intent.AdsIntent
import com.devaeon.adsTemplate.presentation.viewModel.AdsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StartingActivity : BaseActivity<ActivityStartingBinding>(ActivityStartingBinding::inflate) {

    private val adsViewModel by viewModels<AdsViewModel>()

    override fun onPreCreated() {
        installSplashTheme()
        enableMaterialDynamicTheme()
        super.onPreCreated()
        hideStatusBar(0)
    }

    override fun onCreated() {
        observeDataStateChanges()
    }

    private fun observeDataStateChanges() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { adsViewModel.intents.send(AdsIntent.FetchRemoteConfiguration) }

                launch {
                    adsViewModel.viewState.collectLatest { adState ->
                        when (adState.adState) {
                            AdState.NOT_INITIALIZED -> Log.i(TAG, "Not Initialized")

                            AdState.INITIALIZED -> Log.i(TAG, "Initialized")

                            AdState.LOADING -> Log.i(TAG, "Loading")

                            AdState.READY -> {
                                Log.i(TAG, "Ready")
                                adsViewModel.intents.send(AdsIntent.ShowInterstitialAd(this@StartingActivity))
                            }

                            AdState.SHOWING -> Log.i(TAG, "Showing")

                            AdState.VALIDATED -> {
                                Log.i(TAG, "Validated")
                                val intent = Intent(this@StartingActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }

                            AdState.ERROR -> Log.i(TAG, "Error")
                        }
                    }
                }
            }
        }
    }
}

private const val TAG = "StartingActivityLogsInformation"
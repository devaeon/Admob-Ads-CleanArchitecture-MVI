package com.devaeon.adsTemplate.domain.model

internal sealed class RemoteAdState {

    data object SdkNotInitialized : RemoteAdState()
    data object Initialized : RemoteAdState()
    data object Loading : RemoteAdState()
    data object NotShown : RemoteAdState()
    data object Showing : RemoteAdState()
    data object Shown : RemoteAdState()

    sealed class Error : RemoteAdState() {
        abstract val code: Int?
        abstract val message: String?

        data class LoadingError(override val code: Int, override val message: String) : Error()
        data class ShowError(override val code: Int, override val message: String) : Error()
        data object NoImpressionError : Error() {
            override val code = null
            override val message = null
        }
    }
}
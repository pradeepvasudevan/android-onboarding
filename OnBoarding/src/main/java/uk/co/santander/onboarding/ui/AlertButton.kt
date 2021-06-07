package uk.co.santander.onboarding.ui

data class AlertButton (val action: ACTION, val type: TYPE, val text: String) {
    enum class ACTION{
        OK, CANCEL, RETRY
    }
    enum class TYPE {
        POSITIVE, NEGATIVE, NEUTRAL
    }
}
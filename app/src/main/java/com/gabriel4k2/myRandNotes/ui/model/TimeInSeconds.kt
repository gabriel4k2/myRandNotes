package com.gabriel4k2.myRandNotes.ui.model

@JvmInline
value class TimeInSeconds(val value: String) {
    companion object {
        val UNKNOWN = TimeInSeconds("-")
    }
}

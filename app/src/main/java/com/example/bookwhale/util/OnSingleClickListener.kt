package com.example.bookwhale.util

import android.os.SystemClock
import android.view.View

abstract class OnSingleClickListener : View.OnClickListener {

    abstract fun onSingleClick(view: View)

    override fun onClick(v: View?) {
        val currentClickTime = SystemClock.uptimeMillis()
        val possibleTime = currentClickTime - LAST_CLICK_TIME
        LAST_CLICK_TIME = currentClickTime

        if (possibleTime > MIN_CLICK_INTERVAL) {
            onSingleClick(v!!)
        }
    }

    companion object {
        private const val MIN_CLICK_INTERVAL: Long = 500
        private var LAST_CLICK_TIME: Long = 0
    }
}

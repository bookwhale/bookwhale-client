package com.example.bookwhale.util

import android.os.SystemClock
import android.view.View

abstract class OnSingleClickListener : View.OnClickListener{

    private var MIN_CLICK_INTERVAL : Long = 500;
    private var LAST_CLICK_TIME : Long = 0;

    abstract fun onSingleClick(view: View)

    override fun onClick(v: View?) {
        var current_click_time = SystemClock.uptimeMillis()
        var possible_time = current_click_time - LAST_CLICK_TIME
        LAST_CLICK_TIME = current_click_time

        if (possible_time > MIN_CLICK_INTERVAL){
            onSingleClick(v!!)
        }
    }
}
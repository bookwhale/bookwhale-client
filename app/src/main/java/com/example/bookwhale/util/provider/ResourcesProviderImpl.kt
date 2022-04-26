package com.example.bookwhale.util.provider

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

class ResourcesProviderImpl(
    private val context: Context
) : ResourcesProvider {
    override fun getString(@StringRes resId: Int): String = context.getString(resId)

    override fun getString(@StringRes resId: Int, vararg formArgs: Any): String = context.getString(resId, *formArgs)

    override fun getColor(@ColorRes resId: Int): Int = ContextCompat.getColor(context, resId)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getColorStateList(@ColorRes resId: Int): ColorStateList = context.getColorStateList(resId)

    override fun getStringArray(resId: Int): Array<out String> = context.resources.getStringArray(resId)
}

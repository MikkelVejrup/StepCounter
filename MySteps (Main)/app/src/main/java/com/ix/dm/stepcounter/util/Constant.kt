package com.ix.dm.stepcounter.util

import android.app.Activity
import android.content.Context
import com.ix.dm.stepcounter.other.*

object Constant {
    fun getSharePref(context: Context) =
        context.getSharedPreferences(SHARE, Activity.MODE_PRIVATE)

    fun editor(context: Context) = getSharePref(context).edit()
}
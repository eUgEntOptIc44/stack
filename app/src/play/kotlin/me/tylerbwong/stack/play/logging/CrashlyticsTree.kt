package me.tylerbwong.stack.play.logging

import android.util.Log
import timber.log.Timber
import javax.inject.Inject

class CrashlyticsTree @Inject constructor() : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        when (priority) {
            Log.DEBUG, Log.INFO, Log.VERBOSE -> return
            else -> {
                Log.d(tag, message)
            }
        }
    }
}

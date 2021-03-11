package com.dede.toasty

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * 全局Activity生命周期监听
 */
internal class GlobalActivityLifecycleObserver : Application.ActivityLifecycleCallbacks {

    companion object {
        var isForeground = false
            private set
    }

    fun register(application: Application) {
        application.unregisterActivityLifecycleCallbacks(this)
        application.registerActivityLifecycleCallbacks(this)
    }

    interface LifecycleListener {
        fun onCreate(activity: Activity)
        fun onStart(activity: Activity)
        fun onResume(activity: Activity)
        fun onPause(activity: Activity)
        fun onStop(activity: Activity)
        fun onDestroy(activity: Activity)
    }

    var lifecycleCallback: LifecycleListener? = null
        internal set

    private var activeCount = 0

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        lifecycleCallback?.onCreate(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        if (activeCount++ == 0) {
            isForeground = true
        }
        lifecycleCallback?.onStart(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        lifecycleCallback?.onResume(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        lifecycleCallback?.onPause(activity)
    }

    override fun onActivityStopped(activity: Activity) {
        if (--activeCount == 0) {
            isForeground = false
        }
        lifecycleCallback?.onStop(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        lifecycleCallback?.onDestroy(activity)
    }
}
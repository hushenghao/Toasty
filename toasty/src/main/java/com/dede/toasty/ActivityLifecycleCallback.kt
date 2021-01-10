package com.dede.toasty

import android.app.Activity
import android.app.Application
import android.os.Bundle

internal class ActivityLifecycleCallback : Application.ActivityLifecycleCallbacks {

    interface LifecycleListener {
        fun onCreate(activity: Activity)
        fun onStart(activity: Activity)
        fun onStop(activity: Activity)
        fun onDestroy(activity: Activity)
    }

    var lifecycleCallback: LifecycleListener? = null

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        lifecycleCallback?.onCreate(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        lifecycleCallback?.onStart(activity)
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
        lifecycleCallback?.onStop(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        lifecycleCallback?.onDestroy(activity)
    }
}
package com.dede.toasty_demo

import android.app.Application
import com.dede.toasty.DialogToastyStrategy
import com.dede.toasty.Toasty
import com.dede.toasty.ToastyViewFactory

class App : Application() {

    private val customConfig = true

    override fun onCreate() {
        super.onCreate()
        if (customConfig) {
            Toasty.init(
                application = this,
                toastyStrategy = DialogToastyStrategy(),
                viewFactory = ToastyViewFactory(),
                toastyErrorCallback = {
                    it.printStackTrace()
                }
            )
        }
    }
}
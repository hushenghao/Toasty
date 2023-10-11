package com.dede.toasty_demo

import android.app.Application
import android.view.Gravity
import com.dede.toasty.DialogToastyStrategy
import com.dede.toasty.Toasty
import com.dede.toasty.ToastyBuilder
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
                },
                toastDefaultConfig = ToastyBuilder().gravity(Gravity.CENTER)
            )
        }
    }
}
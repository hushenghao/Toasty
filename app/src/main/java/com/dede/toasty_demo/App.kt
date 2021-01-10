package com.dede.toasty_demo

import android.app.Application
import com.dede.toasty.PopupWindowStrategy
import com.dede.toasty.Toasty

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Toasty.init(this, toastyStrategy = PopupWindowStrategy())
    }
}
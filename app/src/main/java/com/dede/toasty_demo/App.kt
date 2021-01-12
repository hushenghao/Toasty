package com.dede.toasty_demo

import android.app.Application
import com.dede.toasty.Toasty
import com.dede.toasty.ToastyViewFactory

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Toasty.init(
            application = this, toastyStrategy =
            com.dede.toasty.DialogToastyStrategy()
//            com.dede.toasty.PopupWindowStrategy()
//            com.dede.toasty.WindowManagerStrategy()
            , viewFactory = ToastyViewFactory()
        )
    }
}
package com.dede.toasty

import android.app.Activity
import android.graphics.PixelFormat
import android.view.View
import android.view.WindowManager

class WindowManagerStrategy : Toasty.ToastyStrategy<View> {

    override fun show(activity: Activity, view: View, builder: ToastyBuilder): View {
        val params = WindowManager.LayoutParams()
        params.type = WindowManager.LayoutParams.TYPE_APPLICATION
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        params.format = PixelFormat.TRANSLUCENT
        params.gravity = builder.gravity
        params.width = WindowManager.LayoutParams.WRAP_CONTENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        params.y = builder.offsetY()
        activity.windowManager.addView(view, params)
        return view
    }

    override fun hide(activity: Activity, t: View) {
        activity.windowManager.removeView(t)
    }
}
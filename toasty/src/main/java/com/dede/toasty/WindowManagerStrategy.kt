package com.dede.toasty

import android.app.Activity
import android.graphics.PixelFormat
import android.view.View
import android.view.WindowManager

/**
 * Activity的WindowManager实现
 */
open class WindowManagerStrategy : Toasty.ToastyStrategy<View> {

    override fun show(activity: Activity, view: View, builder: ToastyBuilder): View {
        val params = WindowManager.LayoutParams()
        params.type = getWindowType()
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        params.format = PixelFormat.TRANSLUCENT
        params.gravity = builder.gravity
        params.width = WindowManager.LayoutParams.WRAP_CONTENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        params.y = builder.offsetYpx
        params.x = builder.offsetXpx
        getWindowManager(activity).addView(view, params)
        return view
    }

    override fun update(activity: Activity, view: View, builder: ToastyBuilder, old: View): View {
        hide(activity, old)
        show(activity, view, builder)
        return view
    }

    override fun hide(activity: Activity, t: View) {
        getWindowManager(activity).removeView(t)
    }

    /**
     * 获取WindowManager显示View的type
     * @see WindowManager.LayoutParams.type
     */
    open fun getWindowType(): Int {
        return WindowManager.LayoutParams.TYPE_APPLICATION
    }

    /**
     * 获取WindowManager对象
     */
    open fun getWindowManager(activity: Activity): WindowManager {
        return activity.windowManager
    }
}
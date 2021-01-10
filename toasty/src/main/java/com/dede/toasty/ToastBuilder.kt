package com.dede.toasty

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import android.widget.Toast

class ToastBuilder {

    internal var message: CharSequence? = null
    internal var duration: Long = Toasty.TOAST_SHORT
    private var offsetYdp: Float = 50f
    internal var gravity: Int = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
    internal var customView: View? = null
    internal var showDelay = ToastyHandler.DEFAULT_SHOW_DELAY

    fun message(message: CharSequence): ToastBuilder {
        this.message = message
        return this
    }

    fun duration(duration: Long): ToastBuilder {
        this.duration = duration
        return this
    }

    fun offsetY(offsetYdp: Float): ToastBuilder {
        this.offsetYdp = offsetYdp
        return this
    }

    fun gravity(gravity: Int): ToastBuilder {
        this.gravity = gravity
        return this
    }

    fun customView(view: View): ToastBuilder {
        this.customView = view
        return this
    }

    internal fun offsetY(): Int {
        return Toasty.dip(this.offsetYdp)
    }

    @SuppressLint("ShowToast")
    internal fun nativeToast(): Toast {
        val duration =
            if (this.duration == Toasty.TOAST_LONG) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        val toast = Toast.makeText(Toasty.applicationContext, this.message, duration)
        if (this.customView != null) {
            toast.view = this.customView
        }
        return ToastCompat.wrapper(toast)
    }

    fun show() {
        Toasty.toastyHandler.show(this)
    }

    fun showNow() {
        showDelay = 0L
        show()
    }
}
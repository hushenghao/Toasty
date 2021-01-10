package com.dede.toasty

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.View
import android.widget.Toast

class ToastyBuilder {

    internal var message: CharSequence? = null
    internal var duration: Long = Toasty.TOAST_SHORT
    internal var offsetYdp: Float = 50f
    internal var useOffset = false
    internal var gravity: Int = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
    internal var customView: View? = null

    internal var showDelay = ToastyHandler.DEFAULT_SHOW_DELAY

    fun message(message: CharSequence): ToastyBuilder {
        this.message = message
        return this
    }

    fun duration(duration: Long): ToastyBuilder {
        this.duration = duration
        return this
    }

    fun offsetY(offsetYdp: Float): ToastyBuilder {
        this.offsetYdp = offsetYdp
        this.useOffset = true
        return this
    }

    fun gravity(gravity: Int): ToastyBuilder {
        this.gravity = gravity
        return this
    }

    fun customView(view: View): ToastyBuilder {
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
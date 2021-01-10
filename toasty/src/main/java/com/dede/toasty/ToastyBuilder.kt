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

    internal var postAt: Long = 0L

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

    override fun toString(): String {
        return "ToastyBuilder(" +
                "message=$message, " +
                "customView=$customView, " +
                "duration=$duration, " +
                "gravity=${gravityToString(gravity)}" +
                ")"
    }

    internal fun isCenterVertical(): Boolean {
        return gravity == Gravity.CENTER || gravity == Gravity.CENTER_VERTICAL
    }

    fun gravityToString(gravity: Int): String {
        val result = StringBuilder()
        if (gravity and Gravity.FILL == Gravity.FILL) {
            result.append("FILL").append(' ')
        } else {
            if (gravity and Gravity.FILL_VERTICAL == Gravity.FILL_VERTICAL) {
                result.append("FILL_VERTICAL").append(' ')
            } else {
                if (gravity and Gravity.TOP == Gravity.TOP) {
                    result.append("TOP").append(' ')
                }
                if (gravity and Gravity.BOTTOM == Gravity.BOTTOM) {
                    result.append("BOTTOM").append(' ')
                }
            }
            if (gravity and Gravity.FILL_HORIZONTAL == Gravity.FILL_HORIZONTAL) {
                result.append("FILL_HORIZONTAL").append(' ')
            } else {
                if (gravity and Gravity.START == Gravity.START) {
                    result.append("START").append(' ')
                } else if (gravity and Gravity.LEFT == Gravity.LEFT) {
                    result.append("LEFT").append(' ')
                }
                if (gravity and Gravity.END == Gravity.END) {
                    result.append("END").append(' ')
                } else if (gravity and Gravity.RIGHT == Gravity.RIGHT) {
                    result.append("RIGHT").append(' ')
                }
            }
        }
        if (gravity and Gravity.CENTER == Gravity.CENTER) {
            result.append("CENTER").append(' ')
        } else {
            if (gravity and Gravity.CENTER_VERTICAL == Gravity.CENTER_VERTICAL) {
                result.append("CENTER_VERTICAL").append(' ')
            }
            if (gravity and Gravity.CENTER_HORIZONTAL == Gravity.CENTER_HORIZONTAL) {
                result.append("CENTER_HORIZONTAL").append(' ')
            }
        }
        if (result.length == 0) {
            result.append("NO GRAVITY").append(' ')
        }
        if (gravity and Gravity.DISPLAY_CLIP_VERTICAL == Gravity.DISPLAY_CLIP_VERTICAL) {
            result.append("DISPLAY_CLIP_VERTICAL").append(' ')
        }
        if (gravity and Gravity.DISPLAY_CLIP_HORIZONTAL == Gravity.DISPLAY_CLIP_HORIZONTAL) {
            result.append("DISPLAY_CLIP_HORIZONTAL").append(' ')
        }
        result.deleteCharAt(result.length - 1)
        return result.toString()
    }
}
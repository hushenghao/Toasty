package com.dede.toasty

import android.annotation.SuppressLint
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast

class ToastyBuilder {

    companion object {
        private const val TAG = "ToastyBuilder"
    }

    internal var message: CharSequence? = null
    internal var duration: Long = Toasty.TOAST_SHORT
    internal var offsetYdp: Float = 50f
    internal var gravity: Int = Gravity.BOTTOM
    internal var replaceType: Int = Toasty.REPLACE_BEHIND
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

    fun offsetY(dp: Float): ToastyBuilder {
        this.offsetYdp = dp
        return this
    }

    fun gravity(gravity: Int): ToastyBuilder {
        when (gravity) {
            Gravity.CENTER -> {
                this.gravity = gravity
            }
            Gravity.CENTER_VERTICAL, Gravity.TOP, Gravity.BOTTOM -> {
                this.gravity = gravity or Gravity.CENTER_HORIZONTAL
            }
            else -> {
                throw IllegalArgumentException("Not Support gravity: ${gravityToString(gravity)}")
            }
        }
        return this
    }

    fun replaceType(replaceType: Int): ToastyBuilder {
        when (replaceType) {
            Toasty.REPLACE_NOW, Toasty.REPLACE_BEHIND, Toasty.DISCARD -> {
                this.replaceType = replaceType
            }
            else -> {
                throw IllegalArgumentException("Not Support replaceType: $replaceType")
            }
        }
        return this
    }

    fun customView(view: View): ToastyBuilder {
        this.customView = view
        return this
    }

    fun offsetYpx(): Int {
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
        Log.i(TAG, "gravity: " + gravityToString(gravity))
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

    internal fun gravityToString(gravity: Int): String {
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
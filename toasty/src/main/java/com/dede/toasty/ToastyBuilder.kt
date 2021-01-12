package com.dede.toasty

import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.annotation.StringRes

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
    internal var isNative: Boolean = false

    internal var showDelay = ToastyHandler.DEFAULT_SHOW_DELAY

    fun message(message: CharSequence?): ToastyBuilder {
        this.message = message
        return this
    }

    fun message(@StringRes resId: Int): ToastyBuilder {
        this.message = Toasty.applicationContext.getText(resId)
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
            Gravity.START -> {
                this.gravity = Gravity.TOP
            }
            Gravity.END -> {
                this.gravity = Gravity.BOTTOM
            }
            else -> {
                throw IllegalArgumentException("Not Support gravity: ${gravityToString()}")
            }
        }
        return this
    }

    fun replaceType(replaceType: Int): ToastyBuilder {
        when (replaceType) {
            Toasty.REPLACE_NOW, Toasty.REPLACE_BEHIND, Toasty.DISCARD -> {
                this.replaceType = replaceType
                if (replaceType == Toasty.REPLACE_NOW) {
                    this.showDelay = 0L
                }
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

    fun nativeToast(): ToastyBuilder {
        this.showDelay = 0L
        this.isNative = true
        return this
    }

    fun show() {
        Log.i(TAG, "gravity: " + gravityToString())
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
                "gravity=${gravityToString()}" +
                ")"
    }

    internal fun isCenterVertical(): Boolean {
        return gravity == Gravity.CENTER || gravity == Gravity.CENTER_VERTICAL
    }

}
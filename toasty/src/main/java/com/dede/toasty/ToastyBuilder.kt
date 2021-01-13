package com.dede.toasty

import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.annotation.IntRange
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

    /**
     * Toast文案
     * @param message 文字
     */
    fun message(message: CharSequence?): ToastyBuilder {
        this.message = message
        return this
    }

    /**
     * Toast文案资源id
     * @param resId String资源
     */
    fun message(@StringRes resId: Int): ToastyBuilder {
        this.message = Toasty.applicationContext.getText(resId)
        return this
    }

    /**
     * Toast显示时长
     * @param duration 单位ms [Toasty.TOAST_SHORT], [Toasty.TOAST_LONG]
     */
    fun duration(@IntRange(from = 0) duration: Long): ToastyBuilder {
        this.duration = duration
        return this
    }

    /**
     * Toast垂直方向的偏移量
     * @param dp 单位dp
     */
    fun offsetY(dp: Float): ToastyBuilder {
        this.offsetYdp = dp
        return this
    }

    /**
     * Toast显示位置
     * @param gravity [Gravity.TOP], [Gravity.CENTER], [Gravity.BOTTOM]
     */
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

    /**
     * 已经有Toast在显示时的替换模式
     * @param replaceType [Toasty.REPLACE_BEHIND], [Toasty.REPLACE_NOW], [Toasty.DISCARD]
     */
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

    /**
     * Toast自定义View, 优先级高于message
     * @param view
     */
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

    /**
     * 显示Toast
     */
    fun show() {
        Log.i(TAG, "gravity: " + gravityToString())
        Toasty.toastyHandler.show(this)
    }

    /**
     * 立即显示Toast，忽略显示钱默认的等待时间
     */
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
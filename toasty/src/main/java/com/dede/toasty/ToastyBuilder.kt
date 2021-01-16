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

    /**
     * Toast message
     */
    var message: CharSequence? = null
        private set

    /**
     * Toast 显示时长 ms
     */
    var duration: Long = Toasty.TOAST_SHORT
        private set

    /**
     * Toast 垂直方向的偏移量 dp, 默认50dp
     * @see [Toasty.DEFAULT_OFFSET_Y]
     */
    var offsetYdp: Float = Toasty.DEFAULT_OFFSET_Y
        private set

    /**
     * Toast 垂直方向的偏移量 px
     */
    val offsetYpx: Int get() = Toasty.dip(offsetYdp)

    /**
     * Toast 水平方向的偏移量 dp
     */
    var offsetXdp: Float = 0f
        private set

    /**
     * Toast 水平方向的偏移量 px
     */
    val offsetXpx: Int get() = Toasty.dip(offsetXdp)

    /**
     * Toast 显示时的gravity
     */
    var gravity: Int = Gravity.BOTTOM
        private set
    internal var replaceType: Int = Toasty.REPLACE_BEHIND

    /**
     * Toast 自定义View
     */
    var customView: View? = null
        private set
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
     * Toast水平方向的偏移量
     */
    fun offsetX(dp: Float): ToastyBuilder {
        this.offsetXdp = dp
        return this
    }

    /**
     * Toast显示位置
     * @param gravity
     */
    fun gravity(gravity: Int): ToastyBuilder {
        when (gravity) {
            Gravity.CENTER -> {
                offsetXdp = 0f
                offsetYdp = 0f
                Log.i(TAG, "gravity: ${gravity.gravityToString()}, reset offset")
            }
            Gravity.CENTER_VERTICAL -> {
                offsetYdp = 0f
                Log.i(TAG, "gravity: ${gravity.gravityToString()}, reset offsetY")
            }
            Gravity.CENTER_HORIZONTAL -> {
                offsetXdp = 0f
                Log.i(TAG, "gravity: ${gravity.gravityToString()}, reset offsetX")
            }
        }
        this.gravity = gravity
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
        this.message = null
        return this
    }

    /**
     * 使用原生Toast
     */
    fun nativeToast(): ToastyBuilder {
        this.showDelay = 0L
        this.isNative = true
        return this
    }

    /**
     * 显示Toast
     */
    fun show() {
        Log.i(TAG, toString())
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
                "duration=$duration, " +
                "isNative=$isNative, " +
                "offsetXdp=$offsetXdp, " +
                "offsetYdp=$offsetYdp, " +
                "gravity=${gravityToString()}, " +
                "customView=$customView" +
                ")"
    }

    internal fun isCenterVertical(): Boolean {
        return gravity == Gravity.CENTER || gravity == Gravity.CENTER_VERTICAL
    }

}
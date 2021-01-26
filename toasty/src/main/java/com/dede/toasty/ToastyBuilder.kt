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
     * @see [Toasty.DEFAULT_OFFSET_Y_DIP]
     */
    var offsetYdp: Float = Toasty.DEFAULT_OFFSET_Y_DIP
        private set

    /**
     * Toast 垂直方向的偏移量 px
     */
    val offsetYpx: Int get() = offsetYdp.dip()

    /**
     * Toast 水平方向的偏移量 dp
     */
    var offsetXdp: Float = 0f
        private set

    /**
     * Toast 水平方向的偏移量 px
     */
    val offsetXpx: Int get() = offsetXdp.dip()

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

    internal var reshow = true

    /**
     * Toast未显示结束时页面切换是否重新显示
     * @param reshow true 重新显示, 默认值
     */
    fun reshow(reshow: Boolean): ToastyBuilder {
        this.reshow = reshow
        return this
    }

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
    fun duration(@IntRange(from = 0L) duration: Long): ToastyBuilder {
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
     * @param dp 单位dp
     */
    fun offsetX(dp: Float): ToastyBuilder {
        this.offsetXdp = dp
        return this
    }

    /**
     * Toast偏移量
     * @param offsetXdp 水平方向的偏移量
     * @param offsetYdp 垂直方向的偏移量
     */
    fun offset(offsetXdp: Float, offsetYdp: Float): ToastyBuilder {
        this.offsetXdp = offsetXdp
        this.offsetYdp = offsetYdp
        return this
    }

    /**
     * Toast显示位置
     * @param gravity
     * @see [Gravity]
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
        if (!Toasty.isInitialized) {
            throw IllegalStateException("Toasty没有正确初始化")
        }
        Log.i(TAG, toString())
        Toasty.toastyHandler.show(this)
    }

    /**
     * 立即显示Toast，忽略显示前默认的等待时间
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
                "gravity=${gravity.gravityToString()}, " +
                "customView=$customView" +
                "reshow=$reshow" +
                ")"
    }

    internal fun isCenterVertical(): Boolean {
        return gravity == Gravity.CENTER || gravity == Gravity.CENTER_VERTICAL
    }

}
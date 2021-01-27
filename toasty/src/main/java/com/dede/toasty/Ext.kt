package com.dede.toasty

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.*
import android.widget.Toast
import kotlin.math.roundToInt


internal fun Context.getNavigationBarHeight(): Int {
    var result = 0
    if (this.hasNavigationBar()) {
        val res: Resources = this.resources
        val resourceId: Int = res.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId)
        }
    }
    return result
}

internal fun Context.hasNavigationBar(): Boolean {
    val hasMenuKey = ViewConfiguration.get(this)
        .hasPermanentMenuKey()
    val hasBackKey = KeyCharacterMap
        .deviceHasKey(KeyEvent.KEYCODE_BACK)
    return hasMenuKey || hasBackKey
}


internal fun View.detachLayout() {
    val parent = this.parent
    if (parent is ViewGroup) {
        parent.removeViewInLayout(this)
    }
}


internal fun Float.dip(): Int {
    return (Toasty.applicationContext.resources.displayMetrics.density * this).roundToInt()
}

internal fun Activity?.isFinished(): Boolean {
//    return false
    if (this == null) return false
    return this.isFinishing || this.isDestroyed
}

/**
 * 转换为原生Toast的duration
 */
internal fun ToastyBuilder.nativeDuration(): Int {
    return if (this.duration == Toasty.TOAST_LONG) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
}

/**
 * 转换为原生Toast的显示时长
 */
internal fun ToastyBuilder.nativeDurationMillis(): Long {
    return if (this.duration == Toasty.TOAST_LONG) NativeToastImpl.LONG_MILLIS else NativeToastImpl.SHORT_MILLIS
}

internal fun ToastyBuilder.makeNativeToast(): Toast {
    return makeText(Toasty.applicationContext, this.message, this.nativeDuration())
}

internal fun Int.gravityToString(): String {
    val gravity = this
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

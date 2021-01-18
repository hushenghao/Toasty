@file:JvmMultifileClass
@file:JvmName("ToastCompat")

package com.dede.toasty

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes


private const val TAG = "ToastCompat"

private val isN =
    Build.VERSION.SDK_INT > Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.O

/**
 * 包装系统Toast
 * fix Android N 系统toast崩溃
 */
fun Toast.wrapper(): Toast {
    if (!isN) return this

    try {
        val mNTField = Toast::class.java.getDeclaredField("mTN")
        mNTField.isAccessible = true
        val mTN = mNTField.get(this)
        val mHandlerField = mTN.javaClass.getDeclaredField("mHandler")
        mHandlerField.isAccessible = true
        val mHandler = mHandlerField.get(mTN) as Handler
        if (mHandler !is SafeHandler) {
            val newHandler = SafeHandler(mHandler)
            mHandlerField.set(mTN, newHandler)
        }
    } catch (e: Exception) {
        Log.w(TAG, "wrapper toast error: ", e)
    }
    return this
}

/**
 * 安全显示Toast
 */
fun Toast.safeShow() {
    this.wrapper().show()
}

private class SafeHandler(private val base: Handler) : Handler(Looper.getMainLooper(), null) {

    override fun handleMessage(msg: Message) {
        try {
            base.handleMessage(msg)
        } catch (e: Exception) {
            Log.w(TAG, "handler toast error: ", e)
        }
    }
}


/**
 * make Toast
 * @see Toast.makeText
 */
fun makeText(context: Context, text: CharSequence?, duration: Int): Toast {
    return Toast.makeText(context, text, duration).wrapper()
}

/**
 * make Toast
 * @see Toast.makeText
 */
fun makeText(context: Context, @StringRes resId: Int, duration: Int): Toast {
    return Toast.makeText(context, resId, duration).wrapper()
}

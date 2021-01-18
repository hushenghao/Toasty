package com.dede.toasty

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes

/**
 * fix Android N 系统toast崩溃
 */
object ToastCompat {

    private const val TAG = "ToastCompat"

    private val isN =
        Build.VERSION.SDK_INT == Build.VERSION_CODES.N || Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1

    /**
     * fix 原生Toast
     */
    fun wrapper(toast: Toast): Toast {
        if (isN) {
            try {
                val mNTField = Toast::class.java.getDeclaredField("mTN")
                mNTField.isAccessible = true
                val mTN = mNTField.get(toast)
                val mHandlerField = mTN.javaClass.getDeclaredField("mHandler")
                mHandlerField.isAccessible = true
                val mHandler = mHandlerField.get(mTN) as Handler
                val newHandler = SafeHandler(mHandler)
                mHandlerField.set(mTN, newHandler)
            } catch (e: Exception) {
                Log.w(TAG, "wrapper toast error: ", e)
            }
        }
        return toast
    }

    fun makeText(context: Context, text: CharSequence?, duration: Int): Toast {
        return wrapper(Toast.makeText(context, text, duration))
    }

    fun makeText(context: Context, @StringRes resId: Int, duration: Int): Toast {
        return wrapper(Toast.makeText(context, resId, duration))
    }

    private class SafeHandler(val base: Handler) : Handler(Looper.getMainLooper(), null) {

        override fun handleMessage(msg: Message) {
            try {
                base.handleMessage(msg)
            } catch (e: Exception) {
                Log.w(TAG, "handler toast error: ", e)
            }
        }
    }
}
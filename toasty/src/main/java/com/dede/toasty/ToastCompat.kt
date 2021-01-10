package com.dede.toasty

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast

internal object ToastCompat {

    fun wrapper(toast: Toast): Toast {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N ||
            Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1
        ) {
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
                e.printStackTrace()
            }
        }
        return toast
    }

    private class SafeHandler(val base: Handler) : Handler(Looper.getMainLooper(), null) {

        override fun handleMessage(msg: Message) {
            try {
                base.handleMessage(msg)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
package com.dede.toasty

import android.view.View
import android.widget.Toast


/**
 * 原生Toast实现
 */
internal class NativeToastImpl {

    companion object {
        // 系统toast显示时长
        const val SHORT_MILLIS = 2000L
        const val LONG_MILLIS = 3500L

        private fun Toast.setToastView(view: View?) {
            if (view == null) return
            view.detachLayout()
            this.view = view
        }
    }

    fun showNative(builder: ToastyBuilder): Toast {
        val toast = builder.makeNativeToast()
        toast.setToastView(builder.customView)
        toast.setGravity(builder.gravity, builder.offsetXpx, builder.offsetYpx)
        toast.show()
        return toast
    }

    fun updateNative(builder: ToastyBuilder, old: Toast?): Toast {
        val toast = old ?: builder.makeNativeToast()
        toast.duration = builder.nativeDuration()
        toast.setText(builder.message)
        toast.setToastView(builder.customView)
        toast.setGravity(builder.gravity, builder.offsetXpx, builder.offsetYpx)
        toast.show()
        return toast
    }

    fun hideNative(toast: Toast?) {
        toast?.cancel()
    }

}
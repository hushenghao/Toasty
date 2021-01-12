package com.dede.toasty

import android.view.View
import android.widget.Toast


internal class NativeToastImpl {

    companion object {
        // 系统toast显示时长
        const val SHORT_DELAY = 2000L
        const val LONG_DELAY = 3500L
    }

    fun showNative(builder: ToastyBuilder): Toast {
        val toast = builder.makeNativeToast()
        setCustomView(toast, builder.customView)
        toast.setGravity(builder.gravity, 0, builder.offsetYpx())
        toast.show()
        return toast
    }

    fun updateNative(builder: ToastyBuilder, old: Toast?): Toast {
        val toast = old ?: builder.makeNativeToast()
        toast.duration = builder.nativeDuration()
        toast.setText(builder.message)
        setCustomView(toast, builder.customView)
        toast.setGravity(builder.gravity, 0, builder.offsetYpx())
        toast.show()
        return toast
    }

    private fun setCustomView(toast: Toast, customView: View?) {
        if (customView == null) return
        customView.detachLayout()
        toast.view = customView
    }

    fun hideNative(toast: Toast?) {
        toast?.cancel()
    }

}
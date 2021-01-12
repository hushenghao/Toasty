package com.dede.toasty

import android.view.View
import android.view.ViewGroup
import android.widget.Toast


internal class NativeToastStrategy {

    companion object {
        // 系统toast显示时长
        const val SHORT_DELAY = 2000L
        const val LONG_DELAY = 3500L
    }

    fun showNative(builder: ToastyBuilder): Toast {
        val toast = builder.makeToast()
        setCustomView(toast, builder.customView)
        toast.setGravity(builder.gravity, 0, builder.offsetYpx())
        toast.show()
        return toast
    }

    fun updateNative(builder: ToastyBuilder, old: Toast?): Toast {
        val toast = old ?: builder.makeToast()
        toast.duration = builder.nativeDuration()
        toast.setText(builder.message)
        setCustomView(toast, builder.customView)
        toast.setGravity(builder.gravity, 0, builder.offsetYpx())
        toast.show()
        return toast
    }

    private fun setCustomView(toast: Toast, customView: View?) {
        if (customView == null) return
        val parent = customView.parent
        if (parent is ViewGroup) {
            parent.removeViewInLayout(customView)
        }
        toast.view = customView
    }

    fun hideNative(t: Toast?) {
        t?.cancel()
    }

}
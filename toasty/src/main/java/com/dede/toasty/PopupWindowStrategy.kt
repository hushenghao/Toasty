package com.dede.toasty

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow


class PopupWindowStrategy : Toasty.ToastyStrategy<PopupWindow> {

    override fun show(activity: Activity, view: View, builder: ToastyBuilder): PopupWindow {
        val popupWindow = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        showPopupWindow(popupWindow, activity, builder)
        return popupWindow
    }

    override fun update(
        activity: Activity,
        view: View,
        builder: ToastyBuilder,
        old: PopupWindow
    ): PopupWindow {
        old.dismiss()
        old.contentView = view
        showPopupWindow(old, activity, builder)
        return old
    }

    override fun hide(activity: Activity, t: PopupWindow) {
        t.dismiss()
    }

    private fun showPopupWindow(
        popupWindow: PopupWindow,
        activity: Activity,
        builder: ToastyBuilder
    ) {
        val parent = activity.findViewById<View>(android.R.id.content)
        val offset = if (builder.isCenterVertical()) 0 else activity.getNavigationBarHeight()
        popupWindow.showAtLocation(
            parent,
            builder.gravity,
            0,
            builder.offsetYpx() + offset
        )
    }

}
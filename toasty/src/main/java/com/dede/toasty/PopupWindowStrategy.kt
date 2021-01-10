package com.dede.toasty

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.*
import android.widget.PopupWindow


class PopupWindowStrategy : Toasty.ToastyStrategy<PopupWindow> {

    override fun show(activity: Activity, view: View, builder: ToastyBuilder): PopupWindow {
        val popupWindow = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val parent = activity.findViewById<View>(android.R.id.content)
        val offset = if (builder.isCenterVertical()) 0 else getNavigationBarHeight(activity)
        popupWindow.showAtLocation(
            parent,
            builder.gravity,
            0,
            builder.offsetY() + offset
        )
        return popupWindow
    }

    override fun hide(activity: Activity, t: PopupWindow) {
        t.dismiss()
    }

    private fun getNavigationBarHeight(context: Context): Int {
        var result = 0
        if (hasNavigationBar(context)) {
            val res: Resources = context.resources
            val resourceId: Int = res.getIdentifier("navigation_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId)
            }
        }
        return result
    }

    private fun hasNavigationBar(context: Context?): Boolean {
        val hasMenuKey = ViewConfiguration.get(context)
            .hasPermanentMenuKey()
        val hasBackKey = KeyCharacterMap
            .deviceHasKey(KeyEvent.KEYCODE_BACK)
        return hasMenuKey || hasBackKey
    }
}
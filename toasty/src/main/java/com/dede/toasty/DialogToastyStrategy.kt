package com.dede.toasty

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.WindowManager

/**
 * Dialog实现
 */
class DialogToastyStrategy : Toasty.ToastyStrategy<Dialog> {

    override fun show(activity: Activity, view: View, builder: ToastyBuilder): Dialog {
        val dialog = Dialog(activity, R.style.Toasty_Dialog)
        dialog.setContentView(view)
        dialog.window?.let {
            it.addFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            )
            val params = it.attributes
            params.gravity = builder.gravity
            params.y = builder.offsetYpx()
            it.attributes = params
        }
        dialog.show()
        return dialog
    }

    override fun update(
        activity: Activity,
        view: View,
        builder: ToastyBuilder,
        old: Dialog
    ): Dialog {
        old.window?.let {
            val params = it.attributes
            params.gravity = builder.gravity
            params.y = builder.offsetYpx()
            it.attributes = params
        }
        old.setContentView(view)
        return old
    }

    override fun hide(activity: Activity, t: Dialog) {
        t.dismiss()
    }
}
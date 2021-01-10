package com.dede.toasty

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView

class ToastyViewFactory : Toasty.ViewFactory {

    override fun createView(context: Context, builder: ToastyBuilder): View {
        val view = LayoutInflater.from(context).inflate(R.layout.toasty_layout_toast, null, false)
        val textView = view.findViewById<TextView>(R.id.tv_toast)
        textView.text = builder.message
        return view
    }
}
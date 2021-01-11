package com.xhe.toasty

import android.content.Context
import com.dede.toasty.Toasty
import com.dede.toasty.ToastyBuilder


@Deprecated(message = "旧版本Toasty")
object Toasty {

    @JvmStatic
    @Deprecated(
        message = "旧版本Toasty",
        replaceWith = ReplaceWith("Toasty.with()", "com.dede.toasty.Toasty")
    )
    fun with(context: Context?): ToastyBuilder {
        return Toasty.with()
    }

}
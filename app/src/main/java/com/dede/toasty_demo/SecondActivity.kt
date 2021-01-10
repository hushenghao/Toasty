package com.dede.toasty_demo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dede.toasty.Toasty

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
    }

    fun toast(view: View) {
        Toasty.with().message("第二个页面！").duration(Toasty.TOAST_LONG).show()
    }

    fun toastFinish(view: View) {
        Toasty.with().message("第二个页面！Finish").duration(Toasty.TOAST_SHORT).show()
        finish()
    }
}
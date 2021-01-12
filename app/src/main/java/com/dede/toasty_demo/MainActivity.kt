package com.dede.toasty_demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dede.toasty.Toasty

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG, "onCreate: ")
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart: ")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop: ")
    }

    fun toast(view: View) {
        Toasty.with().message("我是Toast！").show()
    }

    fun open(view: View) {
        startActivity(Intent(this, SecondActivity::class.java))
    }

    fun view(view: View) {
        val inflate = layoutInflater.inflate(R.layout.layout_custom_toast, null, false)
        Toasty.with()
            .customView(inflate)
            .gravity(Gravity.CENTER)
            .offsetY(0f)
            .duration(5000L)
            .show()
    }

    private var behind = 0
    private var replace = 0
    private var discard = 0

    fun behind(view: View) {
        Toasty.with("Toast队列 ${behind++}").replaceType(Toasty.REPLACE_BEHIND).show()
    }

    fun replace(view: View) {
        Toasty.with("Toast替换 ${replace++}").replaceType(Toasty.REPLACE_NOW).show()
    }

    fun discard(view: View) {
        Toasty.with("Toast丢弃 ${discard++}").replaceType(Toasty.DISCARD).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
    }

    fun _native(view: View) {
        Toasty.with("原生Toast").nativeToast().show()
    }

}
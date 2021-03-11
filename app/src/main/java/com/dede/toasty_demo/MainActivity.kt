package com.dede.toasty_demo

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
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

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: ")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop: ")
    }

    fun toast(view: View) {
        Toasty.with().message("我是Toast！").show()
    }

    fun open(view: View) {
        startActivityForResult(Intent(this, SecondActivity::class.java), 10)
    }

    fun view(view: View) {
        val inflate = layoutInflater.inflate(R.layout.layout_custom_toast, null, false)
        Toasty.with()
            .customView(inflate)
            .gravity(Gravity.CENTER)
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

    fun customNative(view: View) {
        val inflate = layoutInflater.inflate(R.layout.layout_custom_toast, null, false)
        Toasty.with()
            .customView(inflate)
            .gravity(Gravity.CENTER)
            .duration(Toasty.TOAST_LONG)
            .nativeToast()
            .show()
    }

    fun clear(view: View) {
        Toasty.clear()
    }

    private var time = 0L

    override fun onBackPressed() {
        val uptimeMillis = SystemClock.uptimeMillis()
        if (uptimeMillis - time > 1000) {
            time = uptimeMillis
            Toasty.with("再按一次退出")
                .replaceType(Toasty.REPLACE_NOW)
                .reshow(false)
                .showNow()
        } else {
            super.onBackPressed()
        }
    }

}
package com.dede.toasty_demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dede.toasty.Toasty

class SecondActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SecondActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
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
        Toasty.with().message("第二个页面！").duration(Toasty.TOAST_LONG).show()
    }

    fun toastFinish(view: View) {
        Toasty.with().message("第二个页面！Finish").duration(Toasty.TOAST_SHORT).show()
        setResult(Activity.RESULT_OK, Intent())
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
    }
}
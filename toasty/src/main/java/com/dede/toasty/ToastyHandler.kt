package com.dede.toasty

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.ViewGroup
import java.util.*

internal class ToastyHandler : Handler(Looper.getMainLooper()),
    ActivityLifecycleCallback.LifecycleListener {

    companion object {
        private const val TAG = "ToastyHandler"

        private const val SHOW = 1
        private const val HIDE = 2

        // toast前的延迟, 防止toast show后立刻关闭页面, 使toast显示两次
        internal const val DEFAULT_SHOW_DELAY = 80L
    }

    private val toastQueue = LinkedList<ToastyBuilder>()
    private val showingToast = LinkedList<ToastEntry>()
    private var currentAct: Activity? = null

    init {
        Toasty.activityLifecycleCallback.lifecycleCallback = this
    }

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            SHOW -> {
                if (toastQueue.isEmpty()) {
                    return
                }
                val toastBuilder = toastQueue.pop()
                showToastInternal(toastBuilder)
            }
            HIDE -> {
                val entry = msg.obj as ToastEntry
                hideToastInternal(entry)
            }
        }
    }

    private fun hideToastInternal(entry: ToastEntry) {
        val attachAct = entry.attachAct
        val surplus = SystemClock.uptimeMillis() - entry.showWhen
        if (surplus < entry.builder.duration) {
            // 没有显示完，更新显示时间重新显示
            entry.builder.duration = surplus
            toastQueue.remove(entry.builder)
            toastQueue.addFirst(entry.builder)
//            toastQueue.sortWith { o1, o2 -> o1.showWhen.compareTo(o2.showWhen) }
//            Log.i(TAG, "hideToastInternal: " + entry.builder)
        }

        val t = entry.toastObj
        try {
            Toasty.toastyStrategy.hide(attachAct, t)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        showingToast.remove(entry)

        showNext()
    }

    private fun showToastInternal(builder: ToastyBuilder) {
        val attachAct = this.currentAct
        if (attachAct == null) {
            builder.nativeToast().show()
            return
        }
        val view = if (builder.customView != null) {
            builder.customView!!
        } else {
            Toasty.viewFactory.createView(attachAct, builder)
        }
        val parent = view.parent
        if (parent is ViewGroup) {
            parent.removeViewInLayout(view)
        }

        Log.i(TAG, "gravity: " + builder.gravityToString(builder.gravity))
        // 垂直居中
        val centerVertical = builder.isCenterVertical()
        Log.i(TAG, "isCenterVertical: $centerVertical")
        if (!builder.useOffset && centerVertical) {
            builder.offsetYdp = 0f
        }

        try {
            val toastObj = Toasty.toastyStrategy.show(attachAct, view, builder)
            val toastEntry = ToastEntry(builder, toastObj, attachAct, SystemClock.uptimeMillis())
            showingToast.add(toastEntry)
            val message = Message.obtain(this, HIDE, toastEntry)
            sendMessageDelayed(message, builder.duration)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showNext() {
        if (toastQueue.isEmpty()) {
            return
        }
        if (isShowing(currentAct)) {
            return
        }
        sendEmptyMessage(SHOW)
    }

    private fun isShowing(activity: Activity?): Boolean {
        if (activity == null) {
            return false
        }
        for (toastEntry in showingToast) {
            if (toastEntry.attachAct == activity) {
                return true
            }
        }
        return false
    }

    class ToastEntry(
        val builder: ToastyBuilder,
        val toastObj: Any,
        val attachAct: Activity,
        val showWhen: Long
    )

    fun show(builder: ToastyBuilder) {
        builder.postAt = SystemClock.uptimeMillis()
        toastQueue.add(builder)
        if (isShowing(currentAct)) {
            return
        }
        sendEmptyMessageDelayed(SHOW, builder.showDelay)
    }

    override fun onCreate(activity: Activity) {
        showWithLifecycle(activity)
    }

    override fun onStart(activity: Activity) {
        showWithLifecycle(activity)
    }

    override fun onStop(activity: Activity) {
        hideWithLifecycle(activity)
    }

    override fun onDestroy(activity: Activity) {
        hideWithLifecycle(activity)
    }

    private fun showWithLifecycle(activity: Activity) {
        currentAct = activity
        if (isShowing(activity)) {
            return
        }
        showNext()
    }

    private fun hideWithLifecycle(activity: Activity) {
        if (activity == currentAct) {
            currentAct = null
        }
        for (toastEntry in showingToast) {
            if (toastEntry.attachAct == activity) {
                removeMessages(HIDE, toastEntry)
                hideToastInternal(toastEntry)
                return
            }
        }
    }
}
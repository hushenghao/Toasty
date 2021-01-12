package com.dede.toasty

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import java.util.*

internal class ToastyHandler : Handler(Looper.getMainLooper()),
    ActivityLifecycleCallback.LifecycleListener {

    companion object {
        private const val TAG = "ToastyHandler"

        private const val SHOW = 1
        private const val HIDE = 2

        // toast前的延迟, 防止toast显示后立刻关闭页面, 使toast显示两次
        internal const val DEFAULT_SHOW_DELAY = 80L

        // toast忽略重新显示的时长. 当已显示时间大于1s, 切换页面时就算没有显示完也不再重新显示
        private const val IGNORE_RESHOW_DURATION = 1000L
    }

    private val toastQueue = LinkedList<ToastyBuilder>()
    // 当前显示的Toast
    private var showingToastEntry: ToastEntry? = null
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
                val toastEntry = showingToastEntry
                when (toastBuilder.replaceType) {
                    Toasty.REPLACE_NOW -> {
                        if (toastEntry != null) {
                            updateToastInternal(toastBuilder, toastEntry)
                            return
                        }
                    }
                    Toasty.DISCARD -> {
                        if (toastEntry != null) {
                            return
                        }
                    }
                    Toasty.REPLACE_BEHIND -> {
                        if (toastEntry != null) {
                            // 重新放到队首
                            toastQueue.addFirst(toastBuilder)
                            return
                        }
                    }
                }
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
        if (attachAct == null) {
            Toasty.nativeToastStrategy.hideNative(entry.toastObj as? Toast)

            showingToastEntry = null
            showNext()
            return
        }

        val surplus = SystemClock.uptimeMillis() - entry.showWhen
        if (surplus < IGNORE_RESHOW_DURATION && surplus < entry.builder.duration) {
            // 没有显示完，更新显示时间重新显示
            entry.builder.duration = surplus
            toastQueue.remove(entry.builder)
            toastQueue.addFirst(entry.builder)
        }

        val t = entry.toastObj
        try {
            Toasty.toastyStrategy.hide(attachAct, t)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        showingToastEntry = null
        showNext()
    }

    private fun showToastInternal(builder: ToastyBuilder) {
        val attachAct = this.currentAct
        if (attachAct == null || isFinish(attachAct)) {
            val toast = Toasty.nativeToastStrategy.showNative(builder)
            val newEntry = ToastEntry(builder, toast, null, -1)
            showingToastEntry = newEntry
            val message = Message.obtain(this, HIDE, newEntry)
            sendMessageDelayed(message, builder.nativeDelay())
            return
        }

        val view = prepareToastView(builder, attachAct)

        try {
            val toastObj = Toasty.toastyStrategy.show(attachAct, view, builder)
            val toastEntry = ToastEntry(builder, toastObj, attachAct, SystemClock.uptimeMillis())
            showingToastEntry = toastEntry
            val message = Message.obtain(this, HIDE, toastEntry)
            sendMessageDelayed(message, builder.duration)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateToastInternal(builder: ToastyBuilder, toastEntry: ToastEntry) {
        removeMessages(HIDE, toastEntry)

        val attachAct = this.currentAct
        if (attachAct == null || isFinish(attachAct)) {
            val toast = Toasty.nativeToastStrategy.updateNative(builder, toastEntry.toastObj as? Toast)
            val newEntry = ToastEntry(builder, toast, null, -1)
            showingToastEntry = newEntry
            val message = Message.obtain(this, HIDE, newEntry)
            sendMessageDelayed(message, builder.nativeDelay())
            return
        }

        val view = prepareToastView(builder, attachAct)

        try {
            val toastObj =
                Toasty.toastyStrategy.update(attachAct, view, builder, toastEntry.toastObj)
            val newEntry = ToastEntry(builder, toastObj, attachAct, SystemClock.uptimeMillis())
            showingToastEntry = newEntry
            val message = Message.obtain(this, HIDE, newEntry)
            sendMessageDelayed(message, builder.duration)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun prepareToastView(builder: ToastyBuilder, attachAct: Activity): View {
        val view = if (builder.customView != null) {
            builder.customView!!
        } else {
            Toasty.viewFactory.createView(attachAct, builder)
        }
        val parent = view.parent
        if (parent is ViewGroup) {
            try {
                parent.removeViewInLayout(view)
            } catch (ignore: Exception) {
            }
        }
        return view
    }

    private fun showNext() {
        if (toastQueue.isEmpty()) {
            return
        }
        if (isShowing(currentAct)) {
            return
        }
        val message = Message.obtain(this, SHOW, currentAct)
        sendMessage(message)
    }

    private fun isShowing(activity: Activity?): Boolean {
        if (hasMessages(SHOW, activity)) {
            return true
        }
        return showingToastEntry != null
    }

    private fun isFinish(activity: Activity?): Boolean {
        if (activity == null) return true

        return activity.isFinishing || activity.isDestroyed
    }

    class ToastEntry(
        val builder: ToastyBuilder,
        val toastObj: Any,
        val attachAct: Activity?,
        val showWhen: Long
    )

    fun show(builder: ToastyBuilder) {
        if (builder.replaceType == Toasty.REPLACE_NOW) {
            builder.showDelay = 0L
            toastQueue.addFirst(builder)
        } else {
            toastQueue.add(builder)
        }
        val message = Message.obtain(this, SHOW, currentAct)
        sendMessageDelayed(message, builder.showDelay)
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
        removeMessages(SHOW, activity)
        val toastEntry = showingToastEntry
        if (toastEntry != null) {
            removeMessages(HIDE, toastEntry)
            hideToastInternal(toastEntry)
        }
    }
}
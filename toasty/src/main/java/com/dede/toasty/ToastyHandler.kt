package com.dede.toasty

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Toast
import java.util.*

internal class ToastyHandler : Handler(Looper.getMainLooper()),
    GlobalActivityLifecycleObserver.LifecycleListener {

    companion object {
        private const val TAG = "ToastyHandler"

        private const val MSG_SHOW = 1
        private const val MSG_HIDE = 2

        // toast前的延迟, 防止toast显示后立刻关闭页面, 使toast显示两次
        internal const val DEFAULT_SHOW_DELAY = 100L

        // toast忽略重新显示的时长. 当已显示时间大于1s, 切换页面时就算没有显示完也不再重新显示
        private const val IGNORE_RESHOW_DURATION = 1000L
    }

    // 待显示的Toast队列
    private val toastQueue = LinkedList<ToastyBuilder>()

    // 当前显示的Toast
    private var showingToastEntry: ToastEntry? = null

    // 当前处于前台的Activity
    private var currentAct: Activity? = null

    init {
        // 注册Activity生命周期监听
        Toasty.activityLifecycleObserver.lifecycleCallback = this
    }

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            MSG_SHOW -> {
                if (!hasNext()) {
                    return
                }
                val toastBuilder = toastQueue.pop()// 消费当前toast
                val toastEntry = showingToastEntry

                val attachAct = this.currentAct
                // 应用在后台且不是原生toast
                if (!isForeground && !toastBuilder.isNative) {// 非原生Toast
                    if (attachAct.isFinished()) {
                        // 页面在关闭中
                        toastQueue.addFirst(toastBuilder)
                        return
                    }
                }
                when (toastBuilder.replaceType) {
                    Toasty.REPLACE_NOW -> {
                        if (toastEntry != null) {
                            if (toastBuilder.isNative == toastEntry.isNative) {
                                // 类型相同才能替换
                                updateToastInternal(toastBuilder, toastEntry)
                            } else {
                                // 先隐藏再显示
                                hideToastInternal(toastEntry, false)
                                showToastInternal(toastBuilder)
                            }
                            return
                        }
                    }
                    Toasty.REPLACE_BEHIND -> {
                        if (isShowing(currentAct)) {
                            // 上面已经消费了toast, 重新放到队首
                            toastQueue.addFirst(toastBuilder)
                            return
                        }
                    }
                }
                showToastInternal(toastBuilder)
            }
            MSG_HIDE -> {
                val entry = msg.obj as ToastEntry
                hideToastInternal(entry, true)
            }
        }
    }

    /**
     * 隐藏Toast
     */
    private fun hideToastInternal(entry: ToastEntry, reshow: Boolean) {
        val builder = entry.builder
        val needReshow = reshow && builder.reshow
        if (needReshow) {
            val showMillis = SystemClock.uptimeMillis() - entry.showWhen
            val surplusMillis = builder.duration - showMillis
            if (showMillis < IGNORE_RESHOW_DURATION && surplusMillis > 0) {
                // 没有显示完，更新显示时间重新显示
                builder.duration(surplusMillis)
                toastQueue.remove(builder)
                toastQueue.addFirst(builder)
            }
        }

        val attachAct = entry.attachAct
        if (attachAct == null || entry.isNative) {
            Toasty.nativeToastImpl.hideNative(entry.toastObj as? Toast)

            showNext()
            return
        }

        val t = entry.toastObj
        try {
            Toasty.toastyStrategy.hide(attachAct, t)
        } catch (e: Exception) {
            Toasty.postError(e)
        }

        // 显示下一个Toast
        showNext()
    }

    /**
     * 显示Toast
     */
    private fun showToastInternal(builder: ToastyBuilder) {
        Log.i(TAG, "show: $builder")
        val attachAct = this.currentAct
        if (builder.isNative || attachAct == null) {
            val toast = Toasty.nativeToastImpl.showNative(builder)
            val newEntry = ToastEntry(builder, toast, attachAct, -1, true)
            sendHideMessage(newEntry, builder.nativeDurationMillis())
            return
        }

        val view = prepareToastView(builder, attachAct)

        try {
            val toastObj = Toasty.toastyStrategy.show(attachAct, view, builder)
            val toastEntry = ToastEntry(builder, toastObj, attachAct, SystemClock.uptimeMillis())
            sendHideMessage(toastEntry, builder.duration)
        } catch (e: Exception) {
            Toasty.postError(e)
        }
    }

    /**
     * 更新Toast
     */
    private fun updateToastInternal(builder: ToastyBuilder, toastEntry: ToastEntry) {
        cancelHideMessage(toastEntry)

        val attachAct = this.currentAct
        if (builder.isNative || attachAct == null) {
            val toast = Toasty.nativeToastImpl.updateNative(builder, toastEntry.toastObj as? Toast)
            val newEntry = ToastEntry(builder, toast, attachAct, -1, true)
            sendHideMessage(newEntry, builder.nativeDurationMillis())
            return
        }

        val view = prepareToastView(builder, attachAct)

        try {
            val toastObj =
                Toasty.toastyStrategy.update(attachAct, view, builder, toastEntry.toastObj)
            val newEntry = ToastEntry(builder, toastObj, attachAct, SystemClock.uptimeMillis())
            sendHideMessage(newEntry, builder.duration)
        } catch (e: Exception) {
            Toasty.postError(e)
        }
    }

    /**
     * 准备Toast View
     */
    private fun prepareToastView(builder: ToastyBuilder, attachAct: Activity): View {
        val view = builder.customView ?: Toasty.viewFactory.createView(attachAct, builder)
        view.detachLayout()
        return view
    }

    private fun hasNext(): Boolean = !toastQueue.isEmpty()

    /**
     * 显示下一个Toast
     */
    private fun showNext() {
        showingToastEntry = null
        if (!hasNext()) {
            return
        }
        if (isShowing(currentAct)) {
            return
        }
        sendShowMessage(currentAct)
    }

    /**
     * 当前Activity是否在显示中
     */
    private fun isShowing(activity: Activity?): Boolean {
        if (hasMessages(MSG_SHOW, activity)) {
            return true
        }
        return showingToastEntry != null
    }

    /**
     * Toast事务Entry
     * @see showingToastEntry
     */
    private class ToastEntry(
        /** Toast属性 */
        val builder: ToastyBuilder,
        /**
         * Toast实现对象, 用于隐藏,更新Toast
         * @see Toasty.toastyStrategy
         */
        val toastObj: Any,
        /** Toast依附的Activity对象 */
        val attachAct: Activity?,
        /** 显示时的时间 */
        val showWhen: Long,
        val isNative: Boolean = false
    )

    private val isForeground get() = GlobalActivityLifecycleObserver.isForeground

    /**
     * 显示Toast
     */
    fun show(builder: ToastyBuilder) {
        // todo 判断currentAct不可见时不显示Toast
        when (builder.replaceType) {
            Toasty.REPLACE_NOW -> {
                toastQueue.addFirst(builder)
            }
            Toasty.DISCARD -> {
                if (isShowing(currentAct)) {
                    // 丢弃 不需要处理
                    Log.i(TAG, "discard show: $builder")
                    return
                } else {
                    toastQueue.add(builder)
                }
            }
            Toasty.REPLACE_BEHIND -> {
                toastQueue.add(builder)
            }
        }
        if (!isForeground && !builder.isNative) {
            return
        }
        sendShowMessage(currentAct, builder.showDelay)
    }

    /**
     * 取消当前显示的Toast, 并清空Toast队列
     */
    fun clear() {
        toastQueue.clear()

        cancelShowMessage(this.currentAct)
        val toastEntry = showingToastEntry
        if (toastEntry != null) {
            hideNow(toastEntry, false)
        }
    }

    /** [GlobalActivityLifecycleObserver.lifecycleCallback] */

    override fun onCreate(activity: Activity) {
    }

    override fun onStart(activity: Activity) {
        showWithLifecycle(activity)
    }

    override fun onResume(activity: Activity) {
        // fix 关闭透明Activity时，底部Activity不onStart，使事件处理暂时停止问题
        showWithLifecycle(activity)
    }

    override fun onPause(activity: Activity) {
    }

    override fun onStop(activity: Activity) {
        hideWithLifecycle(activity)
    }

    override fun onDestroy(activity: Activity) {
        hideWithLifecycle(activity)
    }

    /**
     * Activity生命周期变化触发的显示Toast
     *
     * @param activity 当前Activity
     * @see onStart
     */
    private fun showWithLifecycle(activity: Activity) {
        currentAct = activity
        if (isShowing(activity)) {
            return
        }
        showNext()
    }

    /**
     * Activity生命周期变化触发的隐藏Toast
     *
     * @param activity 当前Activity
     * @see onStop
     * @see onDestroy
     */
    private fun hideWithLifecycle(activity: Activity) {
        if (activity == currentAct) {
            currentAct = null
        }
        // cancelShowMessage(activity)
        val toastEntry = showingToastEntry
        if (toastEntry != null && toastEntry.attachAct == activity) {
            // TODO fix WindowManagerStrategy window leaked
            hideNow(toastEntry, toastEntry.builder.reshow)
        }
    }

    // Hide now. fix window leaked warn
    private fun hideNow(toastEntry: ToastEntry, reshow: Boolean) {
        cancelHideMessage(toastEntry)
        hideToastInternal(toastEntry, reshow)
    }

    private fun cancelShowMessage(obj: Activity?) {
        removeMessages(MSG_SHOW, obj)
    }

    private fun sendShowMessage(obj: Activity?, delayMillis: Long = 0L) {
        val message = Message.obtain(this, MSG_SHOW, obj)
        sendMessageDelayed(message, delayMillis)
    }

    private fun cancelHideMessage(obj: ToastEntry) {
        removeMessages(MSG_HIDE, obj)
    }

    private fun sendHideMessage(obj: ToastEntry, delayMillis: Long = 0L) {
        showingToastEntry = obj
        val message = Message.obtain(this, MSG_HIDE, obj)
        sendMessageDelayed(message, delayMillis)
    }
}
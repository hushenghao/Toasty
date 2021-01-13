package com.dede.toasty

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.View
import androidx.annotation.StringRes
import kotlin.math.roundToInt

object Toasty {

    const val TOAST_SHORT = 2000L
    const val TOAST_LONG = 3500L

    const val REPLACE_NOW = 1
    const val REPLACE_BEHIND = 2
    const val DISCARD = -1

    internal val activityLifecycleCallback = ActivityLifecycleCallback()
    internal val toastyHandler = ToastyHandler()

    internal lateinit var applicationContext: Context
        private set

    internal lateinit var viewFactory: ViewFactory
    internal lateinit var toastyStrategy: ToastyStrategy<Any>
    internal val nativeToastImpl: NativeToastImpl = NativeToastImpl()

    /**
     * 初始化
     *
     * @param application Application
     * @param viewFactory [ViewFactory]
     * @param toastyStrategy [ToastyViewFactory]
     */
    @JvmStatic
    @JvmOverloads
    fun init(
        application: Application,
        viewFactory: ViewFactory = ToastyViewFactory(),
        toastyStrategy: ToastyStrategy<*> = DialogToastyStrategy()
    ) {
        applicationContext = application.applicationContext
        Toasty.viewFactory = viewFactory
        Toasty.toastyStrategy = toastyStrategy as ToastyStrategy<Any>
        application.unregisterActivityLifecycleCallbacks(activityLifecycleCallback)
        application.registerActivityLifecycleCallbacks(activityLifecycleCallback)
    }

    /**
     * 构建ToastyBuilder
     * @param message 文字
     */
    @JvmStatic
    @JvmOverloads
    fun with(message: CharSequence? = null): ToastyBuilder {
        val toastBuilder = ToastyBuilder()
        toastBuilder.message(message)
        return toastBuilder
    }

    /**
     * 构建ToastyBuilder
     * @param resId String资源
     */
    @JvmStatic
    fun with(@StringRes resId: Int): ToastyBuilder {
        val toastyBuilder = ToastyBuilder()
        toastyBuilder.message(resId)
        return toastyBuilder
    }

    internal fun dip(dp: Number): Int {
        val dpFloat = dp.toFloat()
        return (applicationContext.resources.displayMetrics.density * dpFloat).roundToInt()
    }

    /**
     * ToastyView工厂
     *
     * @see ToastyViewFactory
     */
    interface ViewFactory {
        fun createView(context: Context, builder: ToastyBuilder): View
    }

    /**
     * Toasty实现方式接口
     *
     * @see DialogToastyStrategy
     * @see WindowManagerStrategy
     * @see PopupWindowStrategy
     */
    interface ToastyStrategy<T> {
        fun show(activity: Activity, view: View, builder: ToastyBuilder): T
        fun update(activity: Activity, view: View, builder: ToastyBuilder, old: T): T
        fun hide(activity: Activity, t: T)
    }
}
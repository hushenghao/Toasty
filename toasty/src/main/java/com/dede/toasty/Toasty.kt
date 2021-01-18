package com.dede.toasty

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.View
import androidx.annotation.StringRes

object Toasty {

    /** 短时间显示, 2s */
    const val TOAST_SHORT = 2000L

    /** 长时间显示, 3.5s */
    const val TOAST_LONG = 3500L

    /** 如果已经有Toast在显示状态, 则立即替换正在显示的Toast */
    const val REPLACE_NOW = 1

    /** 队列显示Toast, 默认的replaceType @see[ToastyBuilder.replaceType] */
    const val REPLACE_BEHIND = 2

    /** 如果已经有Toast在显示状态, 则丢弃当前Toast */
    const val DISCARD = -1

    // 默认的垂直方向的偏移量
    const val DEFAULT_OFFSET_Y = 50f

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
     * @param toastyStrategy [ToastyStrategy]
     *
     * @see ToastyViewFactory 默认的Toasty布局
     *
     * @see DialogToastyStrategy Dialog实现
     * @see PopupWindowStrategy PopupWindow实现
     * @see WindowManagerStrategy 使用Activity的WindowManager, 不需要权限
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


    /**
     * ToastyView工厂
     *
     * @see ToastyViewFactory 默认的Toasty布局
     */
    interface ViewFactory {
        fun createView(context: Context, builder: ToastyBuilder): View
    }

    /**
     * Toasty实现方式接口
     *
     * @see DialogToastyStrategy Dialog实现
     * @see PopupWindowStrategy PopupWindow实现
     * @see WindowManagerStrategy 使用Activity的WindowManager, 不需要权限
     */
    interface ToastyStrategy<T> {
        fun show(activity: Activity, view: View, builder: ToastyBuilder): T
        fun update(activity: Activity, view: View, builder: ToastyBuilder, old: T): T
        fun hide(activity: Activity, t: T)
    }
}
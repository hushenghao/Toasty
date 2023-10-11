package com.dede.toasty

import android.annotation.SuppressLint
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

    /** 队列显示Toast, 默认的[ToastyBuilder.replaceType] */
    const val REPLACE_BEHIND = 2

    /** 如果已经有Toast在显示状态, 则丢弃当前Toast */
    const val DISCARD = -1

    // 默认的垂直方向的偏移量
    const val DEFAULT_OFFSET_Y_DIP = 50f

    internal val activityLifecycleObserver = GlobalActivityLifecycleObserver()
    @SuppressLint("StaticFieldLeak")
    internal val toastyHandler = ToastyHandler()// Toasty调度Handler

    internal lateinit var applicationContext: Context
        private set

    internal lateinit var viewFactory: ViewFactory// Toasty View工厂
    internal lateinit var toastyStrategy: ToastyStrategy<Any>// 当前Toasty实现

    @SuppressLint("StaticFieldLeak")
    internal lateinit var toastDefaultConfig: ToastyBuilder// 默认Toast配置

    internal val nativeToastImpl: NativeToastImpl = NativeToastImpl()// 显示系统Toast
    private var toastyErrorCallback: ToastyErrorCallback? = null

    internal val isInitialized: Boolean
        get() {
            return Toasty::applicationContext.isInitialized
        }

    /**
     * 初始化
     *
     * @param application Application
     * @param viewFactory [ViewFactory]
     * @param toastyStrategy [ToastyStrategy]
     * @param toastyErrorCallback [ToastyErrorCallback] toast 异常回调
     * @param toastDefaultConfig [ToastyBuilder] toast 默认配置
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
        toastyStrategy: ToastyStrategy<*> = DialogToastyStrategy(),
        toastyErrorCallback: ToastyErrorCallback? = null,
        toastDefaultConfig: ToastyBuilder = ToastyBuilder(),
    ) {
        applicationContext = application.applicationContext
        Toasty.viewFactory = viewFactory
        Toasty.toastyStrategy = toastyStrategy as ToastyStrategy<Any>
        Toasty.toastyErrorCallback = toastyErrorCallback
        Toasty.toastDefaultConfig = toastDefaultConfig
        activityLifecycleObserver.register(application)
    }

    /**
     * 构建ToastyBuilder
     * @param message 文字
     */
    @JvmStatic
    @JvmOverloads
    fun with(message: CharSequence? = null): ToastyBuilder {
        val toastBuilder = ToastyBuilder(toastDefaultConfig)
        toastBuilder.message(message)
        return toastBuilder
    }

    /**
     * 构建ToastyBuilder
     * @param resId String资源
     */
    @JvmStatic
    fun with(@StringRes resId: Int): ToastyBuilder {
        val toastyBuilder = ToastyBuilder(toastDefaultConfig)
        toastyBuilder.message(resId)
        return toastyBuilder
    }

    /**
     * 取消当前显示的Toast, 并清空Toast队列
     */
    fun clear() {
        toastyHandler.clear()
    }

    internal fun postError(e: Exception) {
        toastyErrorCallback?.invoke(e)
    }


    /**
     * 默认文字Toasty View工厂
     *
     * @see ToastyViewFactory 默认的Toasty布局
     */
    interface ViewFactory {

        /**
         * 创建默认文字Toast的View
         *
         * @param context 当前onStarted的Activity
         * @param builder Toast的属性: 文字[ToastyBuilder.message]等
         */
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

        /**
         * 显示Toast调用的方法
         *
         * @param activity 当前onStarted的Activity
         * @param view Toast显示的View, 默认文字或者自定义View
         * @param builder Toast的属性:
         *  位置[ToastyBuilder.gravity],
         *  时长[ToastyBuilder.duration],
         *  偏移量[ToastyBuilder.offsetXpx],[ToastyBuilder.offsetYpx]等
         * @return Toasty实现的对象, 用于更新或者隐藏Toast
         * @see update
         * @see hide
         */
        fun show(activity: Activity, view: View, builder: ToastyBuilder): T

        /**
         * 更新Toast时调用的方法
         *
         * @param activity 当前onStarted的Activity
         * @param view Toast显示的View, 默认文字或者自定义View
         * @param builder Toast的属性:
         *  位置[ToastyBuilder.gravity],
         *  时长[ToastyBuilder.duration],
         *  偏移量[ToastyBuilder.offsetXpx],[ToastyBuilder.offsetYpx]等
         * @param old 需要更新的Toasty实现对象
         * @return Toasty实现的对象, 可以根据实际情况进行复用
         * @see show
         * @see hide
         */
        fun update(activity: Activity, view: View, builder: ToastyBuilder, old: T): T

        /**
         * 隐藏Toast时调用的方法
         *
         * @param activity 当前onStarted的Activity
         * @param t Toasty实现的对象, 内容为[show]或[update]返回的对象
         * @see show
         * @see update
         */
        fun hide(activity: Activity, t: T)
    }
}
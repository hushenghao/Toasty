# Toasty

一个适用于Android平台的Toast实现

* 不需要Context参数, 摆脱上下文的束缚
* 支持自定义Toast实现
    * Dialog
    * PopupWindow
    * WindowManager
* 支持多种Toast显示配置
    * 支持自定义View
    * 支持显示时长
    * 支持替换模式（REPLACE_NOW, REPLACE_BEHIND, DISCARD）
* 支持Toast跨Activity显示
* 支持系统Toast兜底, 解决系统Toast的已知问题

# How to use

```kotlin
Toasty.with("toasty").show()
```

## 更多配置

```kotlin
Toasty.with()
    .customView(view)
    .gravity(Gravity.CENTER)
    .offsetY(0f)
    .duration(5000L)
    .replaceType(Toasty.REPLACE_NOW)
    .show()
```

## 自定义配置

默认自动初始化，如果需要更多配置，需要在Application中初始化

```kotlin
    Toasty.init(
        application = this, toastyStrategy =
        com.dede.toasty.DialogToastyStrategy()// 默认dialog实现
//       com.dede.toasty.PopupWindowStrategy()
//       com.dede.toasty.WindowManagerStrategy()
        , viewFactory = ToastyViewFactory()// 默认的Toast布局
    )
```


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
Toasty.with("Toasty").show()
```

## 更多配置

```kotlin
Toasty.with()
    .customView(view)
    .gravity(Gravity.CENTER)
    .offsetY(0f)
    .offsetX(0f)
    .duration(Toasty.TOAST_SHORT)
    .replaceType(Toasty.REPLACE_NOW)
    .reshow(true)
    // .nativeToast()// 使用系统toast
    // .message("文字Toast，优先级低于customView")
    .show()
```

## 自定义配置

默认自动初始化，如果需要自定义配置，需要在Application中初始化

```kotlin
    Toasty.init(
        application = this, toastyStrategy =
        DialogToastyStrategy()// 默认dialog实现
//       PopupWindowStrategy()
//       WindowManagerStrategy()
        , viewFactory = ToastyViewFactory()// 默认的Toast布局
    )
```


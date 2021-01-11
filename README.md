# Toasty

一个适用于Android平台的Toast实现

* 支持自定义Toast实现
    * Dialog
    * PopupWindow
    * WindowManager
* 支持多种Toast显示配置
    * 支持自定义View
    * 支持显示时长
    * 支持替换模式（REPLACE_NOW, REPLACE_BEHIND, DISCARD）
* 支持Toast跨Activity显示
* 系统Toast兜底, 修复系统Toast的问题

# How to use

1. 在Application中初始化

```kotlin
    Toasty.init(
        application, toastyStrategy =
        DialogToastyStrategy()
//            PopupWindowStrategy()
//            WindowManagerStrategy()
    )
```

2.toast

```kotlin
Toasty.with("toasty").show()
```

更多配置

```kotlin
Toasty.with()
    .customView(view)
    .gravity(Gravity.CENTER)
    .offsetY(0f)
    .duration(5000L)
    .replaceType(Toasty.REPLACE_NOW)
    .show()
```

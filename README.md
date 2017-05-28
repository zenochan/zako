# Zeno Android Dep Kit
> 个人用的 Android 开发包


## Usage

```groovy
repositories{
  maven {url "http://maven.mjtown.cn/"}
}
dependencies{
  compile "name.zeno:android:1.0.2"
}
```

##  Modules

#### BAT

- [微信 open sdk](./docs/wxsdk.md)
- 热修复: [HotFix](./docs/hotfix.md)
- [百度地图]()

#### 其他

- 日志打印: [logger](https://github.com/orhanobut/logger)
- [图片加载 - Glide](./docs/glide.md)
    - [glide-transformations](https://github.com/wasabeef/glide-transformations)

- Json 解析: [FastJson](https://github.com/alibaba/fastjson)
- View 绑定: [butterknife](https://github.com/JakeWharton/butterknife)
- 网络请求: [Retrofit](http://square.github.io/retrofit/) + RxAndroid + RxJava2Adapter + FastJsonConverter
- [Lombok](https://projectlombok.org/)
- 事件: [otto](https://github.com/square/otto)
- 动态权限: RxPermission
- [消息推送(个推)](./docs/getui.md)

- 分析统计: U 盟+

- [BottomBar](https://github.com/roughike/BottomBar)
- dialog: [material-dialogs](https://github.com/afollestad/material-dialogs)
- [Material Datetime Picker](https://github.com/wdullaer/MaterialDateTimePicker)


## TODO

- [ ] Otto 替换为 RxBus - Otto 官方推荐
- [ ] [数据库框架研究](https://www.zhihu.com/question/46449188?sort=created)

## CHANGE LOG

#### 1.0.5

- `U` `HotFix` 升级至 `2.0.9`

#### 1.0.4

- `F` BasePresenter#removeDisposable 异常 <font color='red'>"Disposable item is null"</font>
- `M` 调整 simpleActionBar 的返回按钮
- `F` Poi 搜索定位失败时无反馈

#### 1.0.3

- ZDrawable
    - resizeComponentDrawable: 调整 ComponentDrawable 的大小
    - tintComponentDrawable:   对 ComponentDrawable 着色

#### 1.0.2
- CommonAdapter 重构，提取为单独的 lib， 方便跟进原作者的更新
- 跟新微信 sdk
- [Glide 修复压缩过度白色变绿色现象](http://blog.mjtown.cn/blogs/104)

#### 1.0.1
- 升级 `rxjava adapter` 不向下兼容,包名改了

package name.zeno.zako

import androidx.multidex.MultiDexApplication
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.arouter.launcher.ARouterX
import name.zeno.android.jiguang.jPushInit

/**
 * @author 陈治谋 (513500085@qq.com)
 * @since 2018/5/24
 */
class ZakoApp : MultiDexApplication() {
  override fun onCreate() {
    super.onCreate()
    jPushInit()

    if (BuildConfig.DEBUG) {            // 这两行必须写在init之前，否则这些配置在init过程中将无效
      ARouter.openLog()                 // 打印日志
      ARouter.openDebug()               // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
    }
    ARouterX.init(this)                 // 尽可能早，推荐在Application中初始化
  }
}
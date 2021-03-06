@file:Suppress("unused")

package cn.izeno.android.core

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.content.Intent
import android.os.Process
import cn.izeno.android.data.models.UpdateInfo
import cn.izeno.android.presenter.ZActivity
import cn.izeno.android.presenter.ZFragment
import cn.izeno.android.presenter.activities.UpdateActivity
import kotlin.reflect.KClass

/**
 * - [Activity.nav]
 * - [Fragment.nav]
 *
 * - [Activity.home] 后台运行，回到安卓首页
 * - [Activity.runOnBack] 后台运行，回到安卓首页
 * @author 陈治谋 (513500085@qq.com)
 * @since 2017/10/18
 */


fun Activity.nav(clazz: KClass<out Activity>, data: (Intent.() -> Unit)? = null, onResult: ((Boolean, Intent?) -> Unit)? = null) {
  val intent = Intent(this, clazz.java)
  data?.invoke(intent)
  if (onResult != null) {
    val next = { ok: Boolean, intentData: Intent? -> onResult(ok, intentData) }

    when {
      this is ZActivity -> startActivityForResult(intent, next)
      else -> navigator().startActivityForResult(intent, next)
    }
  } else {
    startActivity(intent)
  }
}

fun Activity.nav(intent: Intent, onResult: ((ok: Boolean, data: Intent?) -> Unit)? = null) {
  if (onResult != null) {
    when {
      this is ZActivity -> startActivityForResult(intent, onResult)
      else -> navigator().startActivityForResult(intent, onResult)
    }
  } else {
    startActivity(intent)
  }
}


fun ZFragment.nav(intent: Intent, onResult: ((Boolean, Intent?) -> Unit)? = null) {
  if (onResult != null) {
    val next = { ok: Boolean, intentData: Intent? -> onResult(ok, intentData) }

    when {
      this is ZActivity -> startActivityForResult(intent, next)
      else -> navigator().startActivityForResult(intent, next)
    }
  } else {
    startActivity(intent)
  }
}


fun Fragment.nav(
    clazz: KClass<out Activity>,
    data: (Intent.() -> Unit)? = null,
    onResult: ((Boolean, Intent?) -> Unit)? = null
) {
  val intent = Intent(this.activity, clazz.java)
  data?.invoke(intent)
  if (onResult != null) {

    val next = { ok: Boolean, intentData: Intent? -> onResult(ok, intentData) }
    if (this is ZFragment) {
      startActivityForResult(intent, next)
    } else {
      navigator().startActivityForResult(intent, next)
    }
  } else {
    startActivity(intent)
  }
}

@SuppressLint("NewApi")
fun Fragment.navigator() = when {
  this is ZFragment -> this
  sdkInt > JELLY_BEAN_MR1 -> childFragmentManager.navigator()
  else -> fragmentManager.navigator()
}

fun Activity.navigator() = fragmentManager.navigator()

private fun FragmentManager.navigator(): ZFragment {
  val tag = "zeno:nav"
  var fragment: ZFragment? = findFragmentByTag(tag) as? ZFragment
  if (fragment == null) {
    fragment = ZFragment()
    val transaction = beginTransaction()
    transaction.add(fragment, tag)
    transaction.commit()
  }

  return fragment
}


/** 后台运行,回到安卓首页  */
fun Activity.home() {
  val intent = Intent(Intent.ACTION_MAIN)
  intent.addCategory(Intent.CATEGORY_HOME)
  intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
  startActivity(intent)
}

/** 后台运行,回到安卓首页  */
fun Activity.runOnBack() {
  moveTaskToBack(true)
}

/** 退出 App */
fun Activity.exit() {
  home()
  Process.killProcess(Process.myPid())
}

fun Activity.update(updateInfo: UpdateInfo) {
  val intent = UpdateActivity.getCallingIntent(this, updateInfo)
  startActivity(intent)
}

package cn.izeno.android.presenter

import android.app.Dialog
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import cn.izeno.android.presenter.activities.AutoHideIMActivity
import cn.izeno.android.third.rxjava.RxActivityResult
import java.util.*

/**
 * @author 陈治谋 (513500085@qq.com)
 * @since 16/6/9
 */
abstract class ZActivity : AutoHideIMActivity(), ActivityLauncher, LoadDataView {
  private var isDestroyed = false

  private var loadingDialog: Dialog? = null

  var isAfterSaveInstanceState = false
    private set

  private val listeners = ArrayList<LifecycleListener>()

  override val ctx: Context = this

  private val rxActivityResult = RxActivityResult(this)

  protected val rootView: View
    get() = (findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)

  override fun isDestroyed(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      super.isDestroyed()
    } else {
      isDestroyed
    }
  }

  override fun showMessage(msg: String?) = snack(msg ?: "")
  override fun showMessage(@StringRes resId: Int) = snack(resId)

  override fun registerLifecycleListener(listener: LifecycleListener) {
    if (!listeners.contains(listener)) {
      listeners.add(listener)
    }
  }

  override fun unregisterLifecycleListener(listener: LifecycleListener) {
    if (listeners.contains(listener)) {
      listeners.remove(listener)
    }
  }

  override fun setContentView(@LayoutRes layoutResID: Int) {
    super.setContentView(layoutResID)
    onViewCreated()
  }

  override fun setContentView(view: View) {
    super.setContentView(view)
    onViewCreated()
  }

  override fun setContentView(view: View, params: ViewGroup.LayoutParams) {
    super.setContentView(view, params)
    onViewCreated()
  }

  override fun onStart() {
    super.onStart()
    listeners.forEach { it.onStart() }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    listeners.forEach { it.onCreate() }

    // 沉浸式状态栏 fitsSystemWindows
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      val window = window
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
      window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
      window.statusBarColor = Color.TRANSPARENT
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      val window = window
      window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
      window.decorView.fitsSystemWindows = true
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    listeners.forEach { it.onActivityResult(requestCode, resultCode, data) }
  }

  override fun onResume() {
    super.onResume()
    listeners.forEach { it.onResume() }
    isAfterSaveInstanceState = false
  }

  override fun onPause() {
    super.onPause()
    listeners.forEach { it.onPause() }
    loadingDialog?.dismiss()
    loadingDialog = null
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    isAfterSaveInstanceState = true
  }

  override fun onDestroy() {
    super.onDestroy()
    isDestroyed = true
    for (listener in listeners) {
      listener.onDestroyView()
      listener.onDestroy()
    }
  }

  override fun showLoading() {
    if (loadingDialog == null) {
      loadingDialog = MaterialDialog.Builder(this)
          .progress(true, 10000, true)
          .content("加载中...")
          .cancelable(false)
          .build()
    }
    loadingDialog!!.show()
  }

  override fun hideLoading() {
    if (loadingDialog != null && loadingDialog!!.isShowing) {
      loadingDialog!!.dismiss()
    }
  }

  override fun toast(msg: String?) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
  }

  override fun toast(@StringRes resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
  }

  override fun showMessageAndFinish(message: String?) {
    MaterialDialog.Builder(this)
        .title("提示")
        .content(message ?: "")
        .neutralText("好")
        .onNeutral { _, _ -> finish() }
        .cancelable(false)
        .show()
  }

  fun startActivityForResult(intent: Intent, onResult: (Boolean, Intent?) -> Unit) {
    rxActivityResult.startActivityForResult(intent, onResult)
  }

  private fun onViewCreated() {
    listeners.forEach { it.onViewCreated() }
  }

  protected fun fullScreen() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
  }


  protected fun addFragment(@IdRes container: Int, fragment: Fragment) {
    val transaction = fragmentManager.beginTransaction()
    transaction.add(container, fragment)
    transaction.commit()
  }

  /**
   * 使用默认的布局加载一个Fragment
   *
   * @param provider fragment provider
   */
  protected inline fun <T : Fragment> setContentView(provider: () -> T) = setContentView(provider())

  protected fun setContentView(fragment: Fragment) {
    addFragment(android.R.id.content, fragment)
  }

  protected fun snack(text: String) {
    Snackbar.make(rootView, text, Snackbar.LENGTH_SHORT).show()
  }

  protected fun snack(@StringRes resId: Int) {
    Snackbar.make(rootView, resId, Snackbar.LENGTH_SHORT).show()
  }

  protected fun snack(msg: String, button: String, action: (() -> Unit)? = null) {
    Snackbar.make(rootView, msg, Snackbar.LENGTH_LONG)
        .setAction(button) { action?.invoke() }
        .show()
  }

}

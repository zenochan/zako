package name.zeno.android.system

import android.Manifest

/**
 * 需要动态申请的权限
 *
 * @author 陈治谋 (513500085@qq.com)
 * @see {@link android.Manifest.permission}
 *
 * @since 2016/11/21.
 */
object ZPermission {
  const val READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"
  const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE

  //  定位
  const val ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
  const val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
  const val READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE

  const val CALL_PHONE = Manifest.permission.CALL_PHONE
  const val CAMERA = Manifest.permission.CAMERA
}
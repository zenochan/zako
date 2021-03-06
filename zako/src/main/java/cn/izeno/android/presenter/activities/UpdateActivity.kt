package cn.izeno.android.presenter.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.core.content.FileProvider
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import io.reactivex.Emitter
import io.reactivex.Observable
import cn.izeno.android.app.AppInfo
import cn.izeno.android.core.exit
import cn.izeno.android.data.CommonConnector
import cn.izeno.android.data.models.UpdateInfo
import cn.izeno.android.exception.ZException
import cn.izeno.android.presenter.ZActivity
import cn.izeno.android.third.rxjava.RxUtils
import cn.izeno.android.third.rxjava.ZObserver
import cn.izeno.ktrxpermission.ZPermission
import cn.izeno.ktrxpermission.rxPermissions
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile

/**
 * 下载更新apk
 *
 * @param data [UpdateInfo]
 *
 * @author 陈治谋 (513500085@qq.com)
 * @since 2016/11/21.
 */
class UpdateActivity : ZActivity() {

  private lateinit var updateInfo: UpdateInfo
  private var localFilePath: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(View(this))
    updateInfo = intent.getParcelableExtra("data")
    showUpdate(updateInfo)
  }


  private fun showUpdate(version: UpdateInfo) {
    val builder = MaterialDialog.Builder(this)
        .title("发现新版本,是否现在升级?")
        .content(version.versionInfo ?: "")
        .positiveText("好")
        .negativeText("取消")
        .cancelable(false)
        .onNegative { _, _ -> finish() }
        .onPositive { _, _ -> preDownload(version) }

    if (version.isForceUpdate) {
      builder.title("发现新版本,需要升级后才能继续使用,是否现在升级?")
      builder.content(version.versionInfo ?: "")
      builder.negativeText("退出应用")
      builder.onNegative { _, _ -> exit() }
    }

    builder.show()
  }

  private fun preDownload(updateInfo: UpdateInfo) {
    rxPermissions(
        ZPermission.READ_EXTERNAL_STORAGE,
        ZPermission.WRITE_EXTERNAL_STORAGE
    ).subscribe { granted ->
      if (granted) {
        download(updateInfo.versionUrl ?: "", updateInfo.versionCode)
      } else {
        permisionDenied(updateInfo.isForceUpdate)
      }
    }
  }

  private fun permisionDenied(forceUpdate: Boolean) {
    if (forceUpdate) {
      exit()
    } else {
      finish()
    }
  }

  private fun download(fileUrl: String, versionName: Int) {
    // /downloads/{packageName}_{versionName}.apk
    localFilePath = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        + File.separator + packageName + "_" + versionName + ".apk")

    val dialog = MaterialDialog.Builder(this)
        .title("下载更新")
        .content("正在下载,请稍候")
        .progress(false, 100)
        .cancelable(false)
        .build()
    dialog.show()

    Observable.create(source@{ subscriber: Emitter<Float> ->
      val contentLength: Long
      var completeSize: Long = 0

      var inputStream: InputStream? = null
      var randomAccessFile: RandomAccessFile? = null

      try {
        val response = CommonConnector.download(fileUrl).execute()
        val code = response.raw().code()
        if (code != 200) {
          if (code == 404) {
            throw ZException("文件链接无效").code(ZException.ERR_NOT_FOUND)
          } else {
            throw ZException("未知错误:" + code).code(ZException.ERR_DEFAULT)
          }
        }

        val body = response.body()
        contentLength = body!!.contentLength()
        inputStream = body.source().inputStream()
        randomAccessFile = RandomAccessFile(localFilePath, "rwd")

        if (randomAccessFile.length() != 0L && randomAccessFile.length() == contentLength) {
          subscriber.onComplete()
          return@source
        }

        var length: Int
        var limit = -1
        val buffer = ByteArray(1024 * 1024)
        do {
          length = inputStream!!.read(buffer)
          if (length == -1) break

          randomAccessFile.write(buffer, 0, length)
          completeSize += length.toLong()

          if (completeSize < contentLength) {
            if (limit % 30 == 0) {
              //limit / 30 ==0 :限制更新频率
              val progress = completeSize.toFloat() / contentLength.toFloat()
              subscriber.onNext(progress)
            }
            limit++
          }
        } while (length != -1)

        subscriber.onComplete()

      } catch (e: Exception) {
        subscriber.onError(e)
      } finally {
        try {
          if (inputStream != null) {
            inputStream.close()
          }
          if (randomAccessFile != null) {
            randomAccessFile.close()
          }
        } catch (e: IOException) {
          subscriber.onError(e)
        }

      }
    }).compose(RxUtils.applySchedulers()).subscribe(object : ZObserver<Float>() {
      internal var err = false

      override fun handleError(e: ZException) {
        showMessage(e.message)
        dialog.setContent(e.message)
        dialog.setProgress(0)
        err = true
      }

      override fun onComplete() {
        val file = File(localFilePath!!)
        if (!err) {
          dialog.dismiss()
          MaterialDialog.Builder(this@UpdateActivity)
              .content("下载完成")
              .positiveText("安装")
              .negativeText("退出")
              .autoDismiss(false)
              .cancelable(false)
              .onPositive { _, _ -> installAPK(file) }
              .onNegative { _, _ -> exit() }
              .show()
          installAPK(file)
        }
      }

      override fun onNext(value: Float) {
        dialog.setProgress((value * 100).toInt())
      }
    })
  }

  private fun installAPK(file: File) {
    if (!file.exists()) return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      //通过FileProvider创建一个content类型的Uri
      val uri = FileProvider.getUriForFile(this, AppInfo.downloadFileProvider, file)

      val intent = Intent(Intent.ACTION_VIEW)
      intent.setDataAndType(uri, "application/vnd.android.package-archive")
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      startActivity(intent)
    } else {
      val intent = Intent(Intent.ACTION_VIEW)
      val uri = Uri.parse("file://" + file.toString())
      intent.setDataAndType(uri, "application/vnd.android.package-archive")
      //在服务中开启activity必须设置flag
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      startActivity(intent)
    }
  }

  companion object {
    fun getCallingIntent(context: Context, updateInfo: UpdateInfo): Intent {
      return Intent(context, UpdateActivity::class.java).putExtra("data", updateInfo)
    }
  }
}

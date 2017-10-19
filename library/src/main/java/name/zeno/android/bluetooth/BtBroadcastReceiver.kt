package name.zeno.android.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.TextUtils

/**
 * Create Date: 16/6/9
 *
 * @author 陈治谋 (513500085@qq.com)
 */
class BtBroadcastReceiver : BroadcastReceiver() {
  var listener: BluetoothListener? = null

  val filter: IntentFilter
    get() {
      val intentFilter = IntentFilter()
      intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
      intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
      intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
      intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
      intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
      intentFilter.addAction("android.bluetooth.device.action.PAIRING_REQUEST")

      return intentFilter
    }

  override fun onReceive(context: Context, intent: Intent?) {
    if (null == intent) {
      return
    }
    val action = intent.action
    if (TextUtils.isEmpty(action)) {
      return
    }

    if (BluetoothAdapter.ACTION_DISCOVERY_STARTED == action) {
      startDiscover()
    } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
      finishDiscover()
    } else if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
      stateChanged()
    } else if (BluetoothDevice.ACTION_FOUND == action) {
      foundDevice(intent)
    } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {
      bondStateChanged(intent)
    } else if ("android.bluetooth.device.action.PAIRING_REQUEST" == action) {
      pairingRequest(intent)
    }
  }

  fun startDiscover() {
    if (listener != null) {
      listener!!.onStartDiscovery()
    }
  }

  fun finishDiscover() {
    if (listener != null) {
      listener!!.onFinishDiscovery()
    }
  }

  fun stateChanged() {
    if (listener != null) {
      val btEnable = BluetoothHelper.isBluetoothEnable
      listener!!.onStatusChanged(btEnable)
    }
  }

  fun foundDevice(intent: Intent) {
    if (listener != null) {
      val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
      listener!!.onFoundDevice(device)
    }
  }

  fun bondStateChanged(intent: Intent) {
    if (listener != null) {
      listener!!.onBondStatusChange(intent)
    }
  }

  private fun pairingRequest(intent: Intent) {
    if (listener != null) {
      listener!!.btPairingRequest(intent)
    }
  }

}

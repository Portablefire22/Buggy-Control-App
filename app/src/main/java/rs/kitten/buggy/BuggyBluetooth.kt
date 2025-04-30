package rs.kitten.buggy

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat.getSystemService
import java.util.UUID

object BuggyBluetooth {
    var currentDevice: MutableState<BluetoothDevice?>? by mutableStateOf(null)
    private var deviceText by mutableStateOf("No Bluetooth device connected.")

    private val pairedDevices = BluetoothDeviceList()
    private val discoveredDevices = BluetoothDeviceList()

    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null

    private var bluetoothSocket: BluetoothServerSocket? = null

    lateinit var appContext: Context


    fun SetContext(context: Context) {
        appContext = context
        bluetoothManager = getSystemService(appContext, BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager?.adapter
    }

    val receiver = object : BroadcastReceiver() {

        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action.toString()
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(
                            BluetoothDevice.EXTRA_DEVICE,
                            BluetoothDevice::class.java
                        )!!
                    discoveredDevices.insert(device)
                }
            }
        }
    }
    private fun connectBT(device: BluetoothDevice) {
        val bluetoothSocket = device.createRfcommSocketToServiceRecord(
            UUID.fromString("3217eb42-92c7-43ab-b901-f7ac7f15345c"))

        bluetoothSocket.connect()


    }


    fun getAdapter(): BluetoothAdapter? {
        return bluetoothAdapter
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun DiscoverDevices() {
        discoveredDevices.getDevices().clear()

        BuggyBluetooth.getAdapter()!!.startDiscovery()

        Handler(Looper.getMainLooper()).postDelayed({
            BuggyBluetooth.getAdapter()!!.cancelDiscovery()
        }, 30000)
    }



    fun getPairedDevices(counter: MutableState<Int>?): MutableList<BluetoothDevice> {
        val pD: Set<BluetoothDevice>? = BuggyBluetooth.getAdapter()?.bondedDevices
        if (counter != null && pD != null) {
            counter.value = pD.size
        }

        pD?.forEach { device ->
            pairedDevices.insert(device)
        }
        return pairedDevices.getDevices()
    }

    fun getDiscoveredDevices(counter: MutableState<Int>?): MutableList<BluetoothDevice> {
        if (counter != null) {
            counter.value = discoveredDevices.length()
        }
        return discoveredDevices.getDevices()
    }


}
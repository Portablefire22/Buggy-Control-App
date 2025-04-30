package rs.kitten.buggy

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
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

    private var bluetoothSocket: BluetoothSocket? = null


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
    fun connect(device: BluetoothDevice) {
        bluetoothSocket = device
            .createInsecureRfcommSocketToServiceRecord(
                UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
        bluetoothAdapter?.cancelDiscovery()
        bluetoothSocket?.connect()
        currentDevice = mutableStateOf( device)
        Log.println(Log.INFO, "rs.kitten.buggy.BT", "Connected: ${bluetoothSocket.toString()}")
    }

    fun disconnect() {
        if (bluetoothSocket != null) {
            bluetoothSocket?.close()
            Log.println(Log.INFO, "rs.kitten.buggy.BT", "Disconnected: ${currentDevice.toString()}")
            currentDevice = null
        }
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
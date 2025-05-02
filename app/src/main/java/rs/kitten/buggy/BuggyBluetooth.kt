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
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import java.util.UUID

object BuggyBluetooth {
    var currentDevice: MutableState<BluetoothDevice?>? by mutableStateOf(null)

    private val pairedDevices = BluetoothDeviceList()
    private val discoveredDevices = BluetoothDeviceList()

    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null

     var bluetoothSocket: BluetoothSocket? = null


    private lateinit var connectedThread: ConnectedThread

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
    @OptIn(ExperimentalUnsignedTypes::class)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connect(device: BluetoothDevice) {
        if (ActivityCompat.checkSelfPermission(
            appContext,
            Manifest.permission.BLUETOOTH_SCAN
        ) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        bluetoothSocket = device
            .createInsecureRfcommSocketToServiceRecord(
                UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))


        bluetoothAdapter?.cancelDiscovery()
        var toast = Toast.makeText(appContext, "Connecting...", Toast.LENGTH_SHORT)
        toast.show()
        bluetoothSocket?.connect()
        toast = Toast.makeText(appContext, "Connected", Toast.LENGTH_SHORT)
        toast.show()
        currentDevice = mutableStateOf( device)

        if (bluetoothSocket != null) {
            connectedThread = ConnectedThread(socket = bluetoothSocket!!)
            connectedThread.start()
        }
        Log.println(Log.INFO, "rs.kitten.buggy.BT", "Connected: ${bluetoothSocket.toString()}")
    }

    fun disconnect() {
        if (bluetoothSocket != null) {
            bluetoothSocket?.close()
            //val toast = Toast.makeText(appContext, "Disconnected", Toast.LENGTH_SHORT)
            //toast.show()
            Log.println(Log.INFO, "rs.kitten.buggy.BT", "Disconnected: ${currentDevice.toString()}")
        }
        currentDevice = null
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



    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
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


    @OptIn(ExperimentalUnsignedTypes::class)
    fun write(data: UByteArray) {
        connectedThread.write(data)
    }
}
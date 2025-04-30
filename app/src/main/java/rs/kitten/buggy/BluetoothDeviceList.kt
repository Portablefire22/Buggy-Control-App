package rs.kitten.buggy

import android.bluetooth.BluetoothDevice
import java.io.Serializable
import kotlin.reflect.KProperty

class BluetoothDeviceList : Serializable {
    private val devices =  mutableListOf<BluetoothDevice>()

    fun insert(device: BluetoothDevice) {
        devices.add(device)
    }

    fun remove(device: BluetoothDevice) {
        devices.remove(device)
    }

    fun getDevices(): MutableList<BluetoothDevice> {
        return this.devices
    }

    fun length(): Int {
        return devices.size
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return this.devices.hashCode()
    }
}
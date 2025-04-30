package rs.kitten.buggy

import android.bluetooth.BluetoothDevice
import java.io.Serializable
import kotlin.reflect.KProperty

class BluetoothDeviceList : Serializable {
    private val devices =  mutableListOf<BluetoothDevice>()

    fun Insert(device: BluetoothDevice) {
        devices.add(device)
    }

    fun Remove(device: BluetoothDevice) {
        devices.remove(device)
    }

    fun Devices(): MutableList<BluetoothDevice> {
        return this.devices
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return this.devices.hashCode()
    }
}
package rs.kitten.buggy

import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.math.log

class ConnectedThread(socket: BluetoothSocket) : Thread() {
    private var mSocket: BluetoothSocket = socket
    private var mInStream = socket.inputStream
    private var mOutStream = socket.outputStream


    override fun run() {
        val buffer = ByteArray(1024)
        var bytes = 0

        while (BuggyBluetooth.currentDevice != null) {
            try {
                bytes = mInStream.read(buffer,0 ,8)
                Log.d("ConnectedThread", buffer.slice(0..<bytes).toString())
            } catch (e: IOException) {
                Log.e("ConnectedThread", "IO Error", e)
                BuggyBluetooth.disconnect()
                break
            }
        }
    }

    public fun write(byteArray: ByteArray) {
        try {
            mOutStream.write(byteArray)
        } catch (e: IOException) {
            Log.e("ConnectedThread", "IO Error writing", e)
        }
    }
}
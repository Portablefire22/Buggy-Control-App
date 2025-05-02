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
        var off = 0
        while (BuggyBluetooth.currentDevice != null) {
            try {
                var str = ""
                bytes = mInStream.read(buffer)
                Log.d("ConnectedThread", "Read: $bytes")
                buffer.slice(0..bytes).forEach { b ->
                    if (b.toInt() == 10) {
                        str = str + "\n"
                    } else {
                        str = str + b.toInt().toChar();
                    }

                }
                Log.d("ConnectedThread", str)
                off += bytes
            } catch (e: IOException) {
                Log.e("ConnectedThread", "IO Error", e)
                BuggyBluetooth.disconnect()
                break
            }
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    public fun write(byteArray: UByteArray) {
        try {
            mOutStream.write(byteArray.toByteArray())
        } catch (e: IOException) {
            Log.e("ConnectedThread", "IO Error writing", e)
        }
    }
}
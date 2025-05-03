package rs.kitten.buggy

import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException

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
                        str += "\n"
                    } else {
                        str += b.toUByte().toInt().toChar();
                    }
                }
                Log.d("ConnectedThread", "$str\n${buffer.slice(0..bytes).map{x -> x.toUByte()}}")
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
package rs.kitten.buggy.amf

import android.util.Log
import java.io.DataOutputStream
import java.io.OutputStream

class AmfSerializer(out: OutputStream?) : DataOutputStream(out) {
    // I am using AMF because I think it would be really funny to use some random Adobe format for
    // the communication with the buggy
    // Just adapting code from my League of Legends hobby project

    fun writeObject(obj: Any) {
        if (obj == null) {
            write(AMF3_NULL)
            return
        }
        when (obj) {
            is Boolean -> if (obj) {
                write(AMF3_TRUE)
                return
            } else {
                write(AMF3_FALSE)
                return
            }
            is UInt -> {
                writeInt32(obj.toInt())
                return
            }
            is Number -> {
                if (obj is Int || obj is Short || obj is Byte) {
                    writeInt32(obj.toInt())
                    return
                } else {
                    writeNumber(obj.toDouble())
                    return
                }
            }
            is String -> writeString(obj)
        }
    }

    private fun writeString(str: String) {
        write(AMF3_STRING)
        // AMF3 uses the right most bit to determine if the string is stored
        // We never store strings as that would be a waste on slow hardware
        val strLen = (str.length shl 1) or 0x01
        writeInt29(strLen)
        write(str.encodeToByteArray())
    }

    private fun writeNumber(num: Double) {
        write(AMF3_NUMBER)
        writeDouble(num)
    }

    fun writeInt32(num: Int) {
        if (num > AMF3_INTEGER_MAX || num < AMF3_INTEGER_MIN) {
            writeNumber(num.toDouble())
            return
        }
        write(AMF3_INTEGER)
        writeInt29(num)
        return
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun writeInt29(num: Int) {
        if (num > AMF3_INTEGER_MAX || num < AMF3_INTEGER_MIN) {
            writeNumber(num.toDouble())
            return
        }

        var shiftNum = num
        val bf = arrayOf(0,0,0,0)
        var stop = 0
        for (i in 0..3) {
            var tmp = shiftNum and 0x7F
            shiftNum = shiftNum shr 7
            if (i != 0) {
                tmp = tmp or 0x80
            }
            // Big endian
            bf[3 - i] = tmp
            if (shiftNum == 0) {
                break
            }
        }
        var i = 0
        while (i <= 3) {
            if (bf[i] != 0) {
                break
            }
            i++
        }
        var str = ""
        // Hopefully cuts off preceding 0 bytes
        bf.slice(i..3).forEach { x ->
            write(x)
            str = "$str ${Integer.toBinaryString(x)}"
        }
        Log.i("AMF", str)
        Log.i("AMF", "Wrote ${bf.slice(i..3)}")
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun write(uBA: UByteArray) {
        uBA.forEach { b ->
            Log.i("wads", b.toString())
            write(b.toInt() and 0xFF)
        }
    }

    // Kotlin doesn't have write(byte) ??
    fun write(b: Byte) {
        super.write(b.toInt() and 0xFF)
    }
}
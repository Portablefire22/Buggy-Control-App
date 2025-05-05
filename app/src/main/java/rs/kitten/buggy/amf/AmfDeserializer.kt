package rs.kitten.buggy.amf

import android.util.Log
import java.io.DataInputStream
import java.io.InputStream

class AmfDeserializer(`in`: InputStream?) : DataInputStream(`in`) {

    fun readAMF3(): Any? {
        val marker = read().toByte()

        when (marker) {
            AMF3_UNDEFINED, AMF3_NULL -> return null
            AMF3_FALSE -> return false
            AMF3_TRUE -> return true
            AMF3_INTEGER -> return readAMF3Integer()
            AMF3_STRING -> return readAMF3String()
        }
        Log.e("AMF Deserializer", "Could not determine marker: $marker")
        return null;
    }

    fun readAMF3Integer(): Int {
        var value = 0;
        var i = 0;
        var currentByte = 0;
        while (i < 3) {
            currentByte = read();
            if (currentByte and 0x80 == 0) {
                break;
            }
            value = (value shl 7) + (currentByte and 0x7F)
            i++;
        }
        if (i < 3) {
            value = (value shl 7) or currentByte
        } else {
            value = (value shl 8) or currentByte
        }

        return value
    }

    fun readAMF3String(): String {
        var stringLength = readAMF3Integer()
        stringLength = stringLength shr 1;
        return readAMF3UTF8(stringLength);
    }

    fun readAMF3UTF8(length: Int): String {
        var str = "";
        for (i in 0..<length) {
            val char = read()
            str += char.toChar()
        }
        return str
    }
}
package rs.kitten.buggy.amf

import rs.kitten.buggy.BuggyPacket
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.OutputStream

class PacketSerializer(out: OutputStream?) : DataOutputStream(out) {

    fun writePacket(packet: BuggyPacket) {
        write(packet.getPacketId())
        val tmpBuff = ByteArrayOutputStream()
        var amfSerializer = AmfSerializer(tmpBuff)

        val pckDat = packet.getData()
        if (pckDat == null) {
            amfSerializer.writeObject(1)
            amfSerializer.write(AMF3_NULL)
            return
        }

        amfSerializer.writeObject(pckDat)
        val outputBuffer = ByteArrayOutputStream()
        amfSerializer = AmfSerializer(outputBuffer)
        amfSerializer.writeInt29(tmpBuff.size())

        outputBuffer.toByteArray().forEach { b ->
            write(b.toUByte())
        }

        tmpBuff.toByteArray().forEach { b ->
            write(b.toUByte())
        }
    }

    private fun write(byte: UByte) {
        write(byte.toInt() and 0xFF)
    }
}
package rs.kitten.buggy

import rs.kitten.buggy.amf.AmfSerializer
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.Serializable


enum class BuggyPacketType(val typeByte: Byte) {
    StringPacket(0x1),
    IntegerPacket(0x2),
    FloatPacket(0x3),
    ArrayPacket(0x4),
    BoolPacket(0x5),
    UnknownPacket(0xF),
}

@OptIn(ExperimentalUnsignedTypes::class)
class BuggyPacket(packetId: UByte) {
    private var mData: Any? = null
    private var mPacketId: UByte = 0u

    init {
        mPacketId = packetId
    }

    fun getPacketId(): UByte {
        return mPacketId
    }

    fun getData(): Any? {
        return mData
    }

    fun setData(obj: Any) {
        mData = obj
    }
}
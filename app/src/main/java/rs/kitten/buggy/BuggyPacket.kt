package rs.kitten.buggy

import java.io.Serializable


enum class BuggyPacketType(val typeByte: Byte) {
    StringPacket(0x1),
    IntegerPacket(0x2),
    FloatPacket(0x3),
    ArrayPacket(0x4),
    UnknownPacket(0xF),
}

@OptIn(ExperimentalUnsignedTypes::class)
class BuggyPacket(packetId: UByte) {
    private var mTypeId: Byte = 0

    private var mBuffer: UByteArray = UByteArray(32)
    private var mLength: Int = 0

    init {
        mBuffer[0] = packetId
    }

    fun setData(data: UInt) {
        write4BytesToBuffer(mBuffer, 6, data)
        mLength = 4
        write4BytesToBuffer(mBuffer, 1, 4u)
        setTypeId(BuggyPacketType.IntegerPacket)
    }

    fun setPacketId(id: UByte) {
        mBuffer[0] = id;
    }

    fun setData(data: String) {
        var i = 0u
        data.toByteArray().forEach { byte ->
            mBuffer[1 + i.toInt()] = byte.toUByte()
            i++
        }
        mLength = i.toInt()
        write4BytesToBuffer(mBuffer, 1, i)
        setTypeId(BuggyPacketType.StringPacket)
    }

    fun toBytes(): UByteArray {
        return mBuffer.slice(0..5 +mLength).toUByteArray()
    }


    private fun setTypeId(type: BuggyPacketType) {
        mBuffer[5] = type.typeByte.toUByte()
    }

    private fun write4BytesToBuffer(buffer: UByteArray, offset: Int, data: UInt) {
        buffer[offset + 0] = (data shr 24).toUByte()
        buffer[offset + 1] = (data shr 16).toUByte()
        buffer[offset + 2] = (data shr 8).toUByte()
        buffer[offset + 3] = (data shr 0).toUByte()
    }

}
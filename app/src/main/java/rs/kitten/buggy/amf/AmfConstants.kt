package rs.kitten.buggy.amf

val AMF3_UNDEFINED: Byte = 0x00;
val AMF3_NULL: Byte = 0x01;
val AMF3_FALSE: Byte = 0x02;
val AMF3_TRUE: Byte = 0x03;
val AMF3_INTEGER: Byte = 0x04;
val AMF3_NUMBER: Byte = 0x05;
val AMF3_STRING: Byte = 0x06;
val AMF3_XML: Byte = 0x07;
val AMF3_DATE: Byte = 0x08;
val AMF3_ARRAY: Byte = 0x09;
val AMF3_OBJECT: Byte = 0x0A;
val AMF3_XMLSTRING: Byte = 0x0B;
val AMF3_BYTEARRAY: Byte = 0x0C;

// AMF3 integers use the left most bit of each byte to track if another byte should follow,
// leading to missing 3 bits in an integer
val AMF3_INTEGER_MAX = Int.MAX_VALUE shr 3;
val AMF3_INTEGER_MIN = Int.MIN_VALUE shr 3;
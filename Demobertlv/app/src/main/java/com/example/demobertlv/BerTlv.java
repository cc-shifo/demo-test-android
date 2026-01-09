/*
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Date                Author      Action
 * 2026-01-09 10:17    ${USER}      Create
 */

package com.example.demobertlv;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * BER (Basic Encoding Rules) Java Library
 * Contains the implementation of encoders and decoders for data types defined by the BER subset of ASN.1 standard
 */
public class BerTLV {
    /**
     * Parse a map of TLVs from a byte array.
     *
     * @param data The byte array containing the TLV objects
     * @return A map of TLV objects, keyed by their tag values
     */
    public static Map<Integer, TLVStruct> decode(byte[] data) {
        Map<Integer, TLVStruct> tlvMap = new HashMap<>();
        int index = 0;
        while (index < data.length) {
            // Parse Tag (can be multi-byte in BER-TLV)
            int tag = data[index++] & 0xFF;
            if ((tag & 0x1F) == 0x1F) { // long-form tag
                int nextByte;
                do {
                    nextByte = data[index++] & 0xFF;
                    tag = (tag << 8) | nextByte;
                } while ((nextByte & 0x80) == 0x80);
            }

            // Parse Length (short or long form)
            int length = data[index++] & 0xFF;
            if ((length & 0x80) == 0x80) { // long-form length
                int numBytes = length & 0x7F;
                length = 0;
                for (int i = 0; i < numBytes; i++) {
                    length = (length << 8) | (data[index++] & 0xFF);
                }
            }

            // Parse Value
            byte[] value = new byte[length];
            System.arraycopy(data, index, value, 0, length);
            index += length;

            tlvMap.put(tag, new TLVStruct(tag, length, value));
        }

        return tlvMap;
    }

    /**
     * Encode a tag and its value to a byte array.
     *
     * @param tag    The tag value to be encoded
     * @param value  The value bytes to be encoded
     * @param dest   The destination buffer to store encoded TLV
     * @param offset The offset in the destination buffer where encoding should start
     * @return The number of bytes TLV object
     */
    public static int encode(int tag, @NonNull byte[] value, @NonNull byte[] dest, int offset) {
        int tagSize = encodeTag(tag, dest, offset);
        int lengthSize = encodeLength(value.length, dest, offset + tagSize);
        encodeValue(value, dest, offset + tagSize + lengthSize, value.length);

        return tagSize + lengthSize + value.length;
    }

    /**
     * Calculate the number of bytes required to convert the integer value to a byte array.
     *
     * @param value The integer value to convert
     * @return The number of bytes required to convert the value to a byte array
     */
    public static int sizeOfInt(int value) {
        int n;
        if (value < 0x100) {
            n = 1;
        } else if (value < 0x10000) {
            n = 2;
        } else if (value < 0x1000000) {
            n = 3;
        } else {
            n = 4;
        }
        return n;

        // return (int) (Math.log(value) / Math.log(256)) + 1;
    }


     /**
     * Convert a value to its byte representation.
     *
     * @param value the value
     *
     * @return the value bytes of this object.
     */
    public static byte[] valueAsBytes(int value) {
        // int byteCount = (int)(Math.log(tag) / Math.log(256)) + 1;
        int byteCount = BerTLV.sizeOfInt(value);
        byte[] dest = new byte[byteCount];
        for (int i = 0; i < byteCount; i++) {
            int p = (byteCount - 1 - i) * 8;
            dest[i] = (byte) ((value >> p) & 0xFF);
        }

        return dest;
    }

    /**
     * Encode the tag to a byte array.
     *
     * @param tag The tag value to encode
     * @return The byte array containing the encoded tag
     */
    private static int encodeTag(int tag, @NonNull byte[] dest, int offset) {
        int index = offset;
        // BER TLV tag: 1~3 bytes
        int byteCount = sizeOfInt(tag);
        if (byteCount + offset > dest.length) {
            throw new RuntimeException("Buffer overflow: offset + size > dest.length when encoding tag");
        }
        for (int i = 0; i < byteCount; i++) {
            int p = (byteCount - 1 - i) << 3;
            dest[index++] = (byte) ((tag >> p) & 0xFF);
        }

        return index - offset;
    }

    /**
     * Encode a length value comply with ASN.1 Basic Encoding Rules.
     *
     * @param length The length value to be encoded
     * @param dest   The destination buffer to store encoded length
     * @return The number of bytes used to encode the length field
     */
    private static int encodeLength(int length, @NonNull byte[] dest, int offset) {
        int index = offset;
        // BER TLV length: 1~4 bytes
        int byteCount = length < 0x80 ? 1 : BerTLV.sizeOfInt(length);
        int size = byteCount == 1 ? byteCount : byteCount + 1;
        if (offset + size > dest.length) {
            throw new RuntimeException("Buffer overflow: offset + size > dest.length when encoding length");
        }

        if (byteCount == 1) {
            dest[index++] = (byte) (length & 0xFF);
        } else {
            dest[index++] = (byte) (0x80 | (byteCount & 0x7F));
            for (int i = 0; i < byteCount; i++) {
                int p = (byteCount - 1 - i) << 3;
                dest[index++] = (byte) ((length >> p) & 0xFF);
            }
        }

        return index - offset;
    }

    /**
     * Encode the value field to a byte array.
     *
     * @param value  The value bytes to encode
     * @param dest   The destination buffer to store encoded value
     * @param offset The offset in the dest array where the value field should start
     * @param size   The size of the dest array available for encoding
     */
    private static void encodeValue(byte[] value, @NonNull byte[] dest, int offset, int size) {
        if (offset + size > dest.length) {
            throw new RuntimeException("Buffer overflow: offset + size > dest.length");
        }
        if (size < value.length) {
            throw new RuntimeException("Buffer size is too small to encode value");
        }
        System.arraycopy(value, 0, dest, offset, value.length);
    }

}
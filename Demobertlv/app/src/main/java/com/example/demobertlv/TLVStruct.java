package com.example.demobertlv;

import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * following ISO/IEC 7816-4 BER-TLV rules
 */
public class TLVStruct {
    private int tag;
    private int length;
    private byte[] value;

    public TLVStruct() {
        // nothing
        value = new byte[0];
    }

    public TLVStruct(int tag, int length, byte[] value) {
        this.tag = tag;
        this.length = length;
        this.value = value;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @NonNull
    public byte[] getValue() {
        return value != null ? value : new byte[0];
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.US, "TLV[tag=0x%X, length=%d, value=%s]", tag, length, bytesToHex(value));
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

}

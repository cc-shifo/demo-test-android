package com.example.demobertlv;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.demobertlv", appContext.getPackageName());
    }

    @Test
    public void testTag1_L1_L3() {
        try {
            Log.v("test", "verbose log");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 单字节Tag测试
        // 1字节Tag + 1字节Length Tag=0x81, Len=1, Value=0x01
        String test1 = "42 01 01";
        test1 = test1.replaceAll("\\s+", "");
        byte[] test1Bytes = HexDump.hexStringToByteArray(test1);
        Map<Integer, TLVStruct> tlvMap1 = BerTLV.decode(test1Bytes);
        printTlvMap("test1", tlvMap1);
        String test2 = "        81 01 01";
        test2 = test2.replaceAll("\\s+", "");
        byte[] test2Bytes = HexDump.hexStringToByteArray(test2);
        Map<Integer, TLVStruct> tlvMap2 = BerTLV.decode(test2Bytes);
        printTlvMap("test2", tlvMap2);

        // 1字节Tag + 2字节Length Tag=0x82, Len=128(0x8180), Value=128个0x00
        StringBuilder sb = new StringBuilder();
        sb.append("82 81 80 00");
        for (int i = 0; i < 128; i++) {
            sb.append(String.format(Locale.US, " %02X", (byte) i));
        }
        String test3 = sb.toString();
        test3 = test3.replaceAll("\\s+", "");
        byte[] test3Bytes = HexDump.hexStringToByteArray(test3);
        Map<Integer, TLVStruct> tlvMap3 = BerTLV.decode(test3Bytes);
        printTlvMap("test3", tlvMap3);

        // 1字节Tag + 3字节Length Tag=0x83, Len=256(0x820100), Value=256个0x00
        sb.setLength(0);
        sb.append("83 82 01 00 00");
        for (int i = 0; i < 256; i++) {
            sb.append(String.format(Locale.US, " %02X", (byte) i));
        }
        String test4 = sb.toString();
        test4 = test4.replaceAll("\\s+", "");
        byte[] test4Bytes = HexDump.hexStringToByteArray(test4);
        Map<Integer, TLVStruct> tlvMap4 = BerTLV.decode(test4Bytes);
        printTlvMap("test4", tlvMap4);
    }

    @Test
    public void testTag2_L1_L3() {
        // 2字节Tag + 1字节Length Tag=0xDF80, Len=10
        String test5 = "DF 80 0A 0123456789ABCDEF  ";
        test5 = test5.replaceAll("\\s+", "");
        byte[] test5Bytes = HexDump.hexStringToByteArray(test5);
        Map<Integer, TLVStruct> tlvMap5 = BerTLV.decode(test5Bytes);
        printTlvMap("test5", tlvMap5);

        // 2字节Tag + 2字节Length Tag=0xDF81, Len=255(0x81FF), Value=255个0x00
        StringBuilder sb = new StringBuilder("DF 81 81 FF 00");
        for (int i = 0; i < 256; i++) {
            sb.append(String.format(Locale.US, " %02X", (byte) i));
        }
        String test6 = sb.toString();
        test6 = test6.replaceAll("\\s+", "");
        byte[] test6Bytes = HexDump.hexStringToByteArray(test6);
        Map<Integer, TLVStruct> tlvMap6 = BerTLV.decode(test6Bytes);
        printTlvMap("test6", tlvMap6);

        // 2字节Tag + 3字节Length Tag=0xDF82, Len=0x1000(4096), Value=4096个0xAA
        sb.setLength(0);
        sb.append("DF 82 83 00 00 00");
        for (int i = 0; i < 4096; i++) {
            sb.append(" AA");
        }
        String test7 = sb.toString();
        test7 = test7.replaceAll("\\s+", "");
        byte[] test7Bytes = HexDump.hexStringToByteArray(test7);
        Map<Integer, TLVStruct> tlvMap7 = BerTLV.decode(test7Bytes);
        printTlvMap("test7", tlvMap7);
    }

    @Test
    public void testTag3_L1_L3() {
        // 3字节Tag + 1字节Length Tag=0xDFFF80, Len=127, Value=127个0x00
        StringBuilder sb = new StringBuilder("DFFF 80 7F 00");
        for (int i = 0; i < 127; i++) {
            sb.append(String.format(Locale.US, " %02X", (byte) i));
        }
        String test8 = sb.toString();
        test8 = test8.replaceAll("\\s+", "");
        byte[] test8Bytes = HexDump.hexStringToByteArray(test8);
        Map<Integer, TLVStruct> tlvMap8 = BerTLV.decode(test8Bytes);
        printTlvMap("test8", tlvMap8);

        // 3字节Tag + 2字节Length Tag=0xDFFF81, Len=128, Value=128个0x00
        sb.setLength(0);
        sb.append("DFFF 81 82 00 00");
        for (int i = 0; i < 128; i++) {
            sb.append(String.format(Locale.US, " %02X", (byte) i));
        }
        String test9 = sb.toString();
        test9 = test9.replaceAll("\\s+", "");
        byte[] test9Bytes = HexDump.hexStringToByteArray(test9);
        Map<Integer, TLVStruct> tlvMap9 = BerTLV.decode(test9Bytes);
        printTlvMap("test9", tlvMap9);

        // 3字节Tag + 3字节Length Tag=0xDFFF82, Len=0x010000(65536), Value=65536个0x55
        sb.setLength(0);
        sb.append("DFFF 82 83 00 00 00");
        for (int i = 0; i < 65536; i++) {
            sb.append(" 55");
        }
        String test10 = sb.toString();
        test10 = test10.replaceAll("\\s+", "");
        byte[] test10Bytes = HexDump.hexStringToByteArray(test10);
        Map<Integer, TLVStruct> tlvMap10 = BerTLV.decode(test10Bytes);
        printTlvMap("test10", tlvMap10);
    }

    @Test
    public void testTag4_L1_L3() {
        // 4字节Tag + 1字节Length Tag=0xDFFFFF80, Len=0
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        sb.append("DFFFFF80 00");
        String test11 = sb.toString();
        test11 = test11.replaceAll("\\s+", "");
        byte[] test11Bytes = HexDump.hexStringToByteArray(test11);
        Map<Integer, TLVStruct> tlvMap11 = BerTLV.decode(test11Bytes);
        printTlvMap("test11", tlvMap11);

        // 4字节Tag + 1字节Length Tag=0xDFFFFF81, Len=127, Value=127个0x00
        sb.setLength(0);
        sb.append("DFFFFF81 7F");
        for (int i = 0; i < 127; i++) {
            sb.append(String.format(Locale.US, " %02X", (byte) i));
        }
        String test12 = sb.toString();
        test12 = test12.replaceAll("\\s+", "");
        byte[] test12Bytes = HexDump.hexStringToByteArray(test12);
        Map<Integer, TLVStruct> tlvMap12 = BerTLV.decode(test12Bytes);
        printTlvMap("test12", tlvMap12);

        // 4字节Tag + 2字节Length Tag=0xDFFFFF82, Len=256, Value=256个0x00
        sb.setLength(0);
        sb.append("DFFFFF82 82 01 00");
        for (int i = 0; i < 256; i++) {
            sb.append(String.format(Locale.US, " %02X", (byte) i));
        }
        String test13 = sb.toString();
        test13 = test13.replaceAll("\\s+", "");
        byte[] test13Bytes = HexDump.hexStringToByteArray(test13);
        Map<Integer, TLVStruct> tlvMap13 = BerTLV.decode(test13Bytes);
        printTlvMap("test13", tlvMap13);

        // 4字节Tag + 3字节Length Tag=0xDFFFFF83, Len=0x00010000(65536)
        sb.setLength(0);
        sb.append("DF FF FF 83 84 00 01 00 00");
        for (int i = 0; i < 65536; i++) {
            sb.append(" 00");
        }
        String test14 = sb.toString();
        test14 = test14.replaceAll("\\s+", "");
        byte[] test14Bytes = HexDump.hexStringToByteArray(test14);
        Map<Integer, TLVStruct> tlvMap14 = BerTLV.decode(test14Bytes);
        printTlvMap("test14", tlvMap14);
    }

    @Test
    public void testBoundaryLength() {
        // Length = 0
        // 01 00 00
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        sb.append("01 00 00");
        String test15 = sb.toString();
        test15 = test15.replaceAll("\\s+", "");
        byte[] test15Bytes = HexDump.hexStringToByteArray(test15);
        Map<Integer, TLVStruct> tlvMap15 = BerTLV.decode(test15Bytes);
        printTlvMap("test15", tlvMap15);
        // 9F 10 00 00
        sb.setLength(0);
        sb.append("9F 10 00 00");
        String test16 = sb.toString();
        test16 = test16.replaceAll("\\s+", "");
        byte[] test16Bytes = HexDump.hexStringToByteArray(test16);
        Map<Integer, TLVStruct> tlvMap16 = BerTLV.decode(test16Bytes);
        printTlvMap("test16", tlvMap16);

        // Length = 127 (1字节最大值) 05 7F [127字节数据]
        sb.setLength(0);
        sb.append("05 7F");
        for (int i = 0; i < 127; i++) {
            sb.append(String.format(Locale.US, " %02X", (byte) i));
        }
        String test17 = sb.toString();
        test17 = test17.replaceAll("\\s+", "");
        byte[] test17Bytes = HexDump.hexStringToByteArray(test17);
        Map<Integer, TLVStruct> tlvMap17 = BerTLV.decode(test17Bytes);
        printTlvMap("test17", tlvMap17);

        // Length = 128 (2字节Length开始) 05 81 80 [128字节数据]
        sb.setLength(0);
        sb.append("05 81 80");
        for (int i = 0; i < 128; i++) {
            sb.append(String.format(Locale.US, " %02X", (byte) i));
        }
        String test18 = sb.toString();
        test18 = test18.replaceAll("\\s+", "");
        byte[] test18Bytes = HexDump.hexStringToByteArray(test18);
        Map<Integer, TLVStruct> tlvMap18 = BerTLV.decode(test18Bytes);
        printTlvMap("test18", tlvMap18);

        // Length = 255 (1字节Length最大值) 05 81 FF [255字节数据]
        sb.setLength(0);
        sb.append("05 81 FF");
        for (int i = 0; i < 255; i++) {
            sb.append(String.format(Locale.US, " %02X", (byte) i));
        }
        String test19 = sb.toString();
        test19 = test19.replaceAll("\\s+", "");
        byte[] test19Bytes = HexDump.hexStringToByteArray(test19);
        Map<Integer, TLVStruct> tlvMap19 = BerTLV.decode(test19Bytes);
        printTlvMap("test19", tlvMap19);

        // Length = 256 (2字节:1字节Length最大值+1) 05 82 01 00 [256字节数据]
        sb.setLength(0);
        sb.append("05 82 01 00");
        for (int i = 0; i < 256; i++) {
            sb.append(String.format(Locale.US, " %02X", (byte) i));
        }
        String test20 = sb.toString();
        test20 = test20.replaceAll("\\s+", "");
        byte[] test20Bytes = HexDump.hexStringToByteArray(test20);
        Map<Integer, TLVStruct> tlvMap20 = BerTLV.decode(test20Bytes);
        printTlvMap("test20", tlvMap20);

        // Length = 65535 (2字节Length最大值) 05 82 FF FF [65535字节数据]
        sb.setLength(0);
        sb.append("05 82 FF FF");
        for (int i = 0; i < 65535; i++) {
            sb.append(String.format(Locale.US, " %02X", (byte) i));
        }
        String test21 = sb.toString();
        test21 = test21.replaceAll("\\s+", "");
        byte[] test21Bytes = HexDump.hexStringToByteArray(test21);
        Map<Integer, TLVStruct> tlvMap21 = BerTLV.decode(test21Bytes);
        printTlvMap("test21", tlvMap21);

        // Length = 65536 (3字节Length: 2字节Length最大值+1) 05 83 01 00 00 [65536字节数据]
        sb.setLength(0);
        sb.append("05 83 01 00 00");
        for (int i = 0; i < 65536; i++) {
            sb.append(String.format(Locale.US, " %02X", (byte) i));
        }
        String test22 = sb.toString();
        test22 = test22.replaceAll("\\s+", "");
        byte[] test22Bytes = HexDump.hexStringToByteArray(test22);
        Map<Integer, TLVStruct> tlvMap22 = BerTLV.decode(test22Bytes);
        printTlvMap("test22", tlvMap22);
    }

    @Test
    public void testBoundaryTag() {
        // Tag = 0x1F (单字节Tag最大值) 1F 01 01
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        sb.append("1F 01 01");
        String test23 = sb.toString();
        test23 = test23.replaceAll("\\s+", "");
        byte[] test23Bytes = HexDump.hexStringToByteArray(test23);
        Map<Integer, TLVStruct> tlvMap23 = BerTLV.decode(test23Bytes);
        printTlvMap("test23", tlvMap23);

        // Tag = 0x1F 0x80 (2字节Tag开始) 1F 80 01 01
        sb.setLength(0);
        sb.append("1F 80 01 01");
        String test24 = sb.toString();
        test24 = test24.replaceAll("\\s+", "");
        byte[] test24Bytes = HexDump.hexStringToByteArray(test24);
        Map<Integer, TLVStruct> tlvMap24 = BerTLV.decode(test24Bytes);
        printTlvMap("test24", tlvMap24);

        // Tag = 0x1F 0x7F (2字节Tag最大值) 1F 7F 01 01
        sb.setLength(0);
        sb.append("1F 7F 01 01");
        String test25 = sb.toString();
        test25 = test25.replaceAll("\\s+", "");
        byte[] test25Bytes = HexDump.hexStringToByteArray(test25);
        Map<Integer, TLVStruct> tlvMap25 = BerTLV.decode(test25Bytes);
        printTlvMap("test25", tlvMap25);

        // Tag = 0x1F 0xFF 0x7F (3字节Tag，最后字节bit8=0) 1F FF 7F 01 01
        sb.setLength(0);
        sb.append("1F FF 7F 01 01");
        String test26 = sb.toString();
        test26 = test26.replaceAll("\\s+", "");
        byte[] test26Bytes = HexDump.hexStringToByteArray(test26);
        Map<Integer, TLVStruct> tlvMap26 = BerTLV.decode(test26Bytes);
        printTlvMap("test26", tlvMap26);


        // Tag = 0x1F 0xFF 0x80 (3字节Tag，最后字节bit8=1) 1F FF 80 01 01 01
        sb.setLength(0);
        sb.append("1F FF 80 01 01 01");
        String test27 = sb.toString();
        test27 = test27.replaceAll("\\s+", "");
        byte[] test27Bytes = HexDump.hexStringToByteArray(test27);
        Map<Integer, TLVStruct> tlvMap27 = BerTLV.decode(test27Bytes);
        printTlvMap("test27", tlvMap27);
    }

    @Test
    public void testNestTag() {
        // E1 15 9F 01 06 010203040506 9F 02 09 A1A2A3A4A5A6A7A8A9
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        sb.append("E1 15 9F 01 06 010203040506 9F 02 09 A1A2A3A4A5A6A7A8A9");
        String test28 = sb.toString();
        test28 = test28.replaceAll("\\s+", "");
        byte[] test28Bytes = HexDump.hexStringToByteArray(test28);
        Map<Integer, TLVStruct> tlvMap28 = BerTLV.decode(test28Bytes);
        printTlvMap("test28", tlvMap28);

        // 9F01060102030405069F0209A1A2A3A4A5A6A7A8A9
        sb.setLength(0);
        sb.append("9F01060102030405069F0209A1A2A3A4A5A6A7A8A9");
        String test29 = sb.toString();
        test29 = test29.replaceAll("\\s+", "");
        byte[] test29Bytes = HexDump.hexStringToByteArray(test29);
        Map<Integer, TLVStruct> tlvMap29 = BerTLV.decode(test29Bytes);
        printTlvMap("test29", tlvMap29);

        // 三级嵌套
        // 61 20
        //     6F 1A
        //         50 15
        //             51 03 010203
        //             52 0E 112233445566778899AABBCCDDEE

        // 多个TLV连续排列
        // 01 01 11
        // 9F 10 02 2233
        // DF 80 03 445566
        // 1F FF 81 04 778899AA
    }

    @Test
    public void test1FFF800101() {
        StringBuilder sb = new StringBuilder();
        // Tag = 0x1F 0xFF 0x80 (3字节Tag，最后字节bit8=1) 1F FF 80 01 01 01
        sb.setLength(0);
        sb.append("1F FF 80 01 01 01");
        String test27 = sb.toString();
        test27 = test27.replaceAll("\\s+", "");
        byte[] test27Bytes = HexDump.hexStringToByteArray(test27);
        Map<Integer, TLVStruct> tlvMap27 = BerTLV.decode(test27Bytes);
        printTlvMap("test27", tlvMap27);
    }

    private void printTlvMap(String label, Map<Integer, TLVStruct> tlvMap) {
        for (Map.Entry<Integer, TLVStruct> entry : tlvMap.entrySet()) {
            Log.v(label, "tag: " + String.format(Locale.US, "0x%X", entry.getKey())
                    + ", length: " + entry.getValue().getLength()
                    + ", \nvalue: " + HexDump.toHexString(entry.getValue().getValue()));
        }
    }
}
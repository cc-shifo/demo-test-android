package com.demo.demopaymodule.utils;

public class HexUtil {
    /**
     * 用于建立十六进制字符的输出的小写字符数组
     */
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    /**
     * 用于建立十六进制字符的输出的大写字符数组
     */
    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data     byte[]
     * @param toLowerCase 用于控制输出字符是大写还是小写
     * @return 十六进制字符串
     */
    public static String encodeHexStr(byte[] data, boolean toLowerCase) {
        char[] toDigits = toLowerCase ? DIGITS_LOWER : DIGITS_UPPER;
        final int len = data.length;
        final char[] out = new char[len << 1];//len*2
        // two characters from the hex value.
        for (int i = 0, j = 0; i < len; i++, j++) {
            out[j] = toDigits[(0xF0 & data[i]) >>> 4];// 高位
            j++;
            out[j] = toDigits[0x0F & data[i]];// 低位
        }
        return new String(out);
    }
}

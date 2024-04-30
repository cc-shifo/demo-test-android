package com.example.demowificonnectivity;

import androidx.annotation.NonNull;

public class HexUtil {
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'};

    private HexUtil() {
        // nothing
    }

    /**
     * 将一个byte转换成两个字符的hex字符串。例如，byte[0] = 0x2D，转换后返回"2D"或者"2d"
     *
     * @param src  要转化的字节数组
     * @param size src的大小。
     * @return 转换后的字符串。
     */
    public static String byte2Hex(@NonNull byte[] src, int size) {
        if (size < 0 || size > src.length) {
            throw new IndexOutOfBoundsException();
        }

        if (size == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder(size << 1);
        for (int i = 0; i < size; i++) {
            byte b = src[i];
            int l = (0xF0 & b) >> 4;
            int r = 0x0F & b;
            builder.append(HEX_CHAR[l]).append(HEX_CHAR[r]).append(' ');
        }

        return builder.toString();
    }


    /**
     * 小端字节数组转long。
     *
     * @return 长整型
     */
    public static long leByte2Long(@NonNull byte[] src, int off) {
        long v = 0;
        for (int i = 7; i >= 0; i--) {
            v |= ((src[i + off] & 0xFFL) << (i * 8));
        }

        return v;
    }

    /**
     * 小端字节数组转int。
     *
     * @return 整型
     */
    public static int leByte2Int(@NonNull byte[] src, int off) {
        int v = 0;
        for (int i = 3; i >= 0; i--) {
            v |= ((src[i + off] & 0xFFL) << (i * 8));
        }

        return v;
    }

    /**
     * @param size size >= 8
     */
    public static byte[] double2LeBytes(double d, @NonNull byte[] b, int off, int size) {
        long value = Double.doubleToRawLongBits(d);
        if (off + 8 <= b.length) {
            int end = off + 8;
            for (int i = off; i < end; i++) {
                b[i] = (byte) ((value >> 8 * i) & 0xff);
            }
        }
        return b;
    }

    /**
     * @param size size >= 4
     */
    public static byte[] float2LeBytes(float d, @NonNull byte[] b, int off, int size) {
        int value = Float.floatToRawIntBits(d);
        if (off + 4 <= b.length) {
            int end = off + 4;
            for (int i = off; i < end; i++) {
                b[i] = (byte) ((value >> 8 * i) & 0xff);
            }
        }
        return b;
    }
}

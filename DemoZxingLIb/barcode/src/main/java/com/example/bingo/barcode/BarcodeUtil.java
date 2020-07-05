package com.example.bingo.barcode;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;
import java.util.Map;

public class BarcodeUtil {
    public static final int QR_SIZE_128 = 128;
    public static final int QR_SIZE_224 = 224;
    public static final int QR_SIZE_300 = 300;

    /**
     * @param content QRCode content
     * @param logo    the logo image data, 大小默认占二维码的20%, 二维码的颜色{@link Color#BLACK}
     * @param size    code width and height {@link BarcodeUtil#QR_SIZE_128},
     *                {@link BarcodeUtil#QR_SIZE_224}, {@link BarcodeUtil#QR_SIZE_300}
     * @return QRCode image
     */
    public static Bitmap createQRCode(String content, @Nullable Bitmap logo, int size) {
        return createQRCode(content, size, logo, 0.2f, Color.BLACK);
    }

    /**
     * 生成二维码
     *
     * @param content   二维码的内容
     * @param size      width and height in pixel
     * @param logo      二维码中间的logo
     * @param ratio     logo所占比例 因为二维码的最大容错率为30%，所以建议ratio的范围小于0.3
     * @param codeColor 二维码的颜色
     * @return QRCode image
     */
    public static Bitmap createQRCode(String content, int size,
                                      Bitmap logo, @FloatRange(from = 0.0f, to = 1.0f) float ratio,
                                      int codeColor) {
        //配置参数
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置空白边距的宽度
        //hints.put(EncodeHintType.MARGIN, 1); //default is 1
        return createQRCode(content, size, logo, ratio, hints, codeColor);
    }

    /**
     * 生成二维码
     *
     * @param content   二维码的内容
     * @param size      width and height in pixel
     * @param logo      二维码中间的logo
     * @param ratio     logo所占比例 因为二维码的最大容错率为30%，所以建议ratio的范围小于0.3
     * @param hints     Additional parameters to supply to the encoder
     * @param codeColor 二维码的颜色
     * @return QRCode image
     */
    public static Bitmap createQRCode(@NonNull String content, int size, @Nullable Bitmap logo,
                                      @FloatRange(from = 0.0f, to = 1.0f) float ratio,
                                      Map<EncodeHintType, ?> hints, int codeColor) {
        try {
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE,
                    size, size, hints);
            int[] pixels = new int[size * size];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * size + x] = codeColor;
                    } else {
                        pixels[y * size + x] = Color.WHITE;
                    }
                }
            }

            // 生成二维码图片的格式
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);

            if (logo != null) {
                bitmap = addLogo(bitmap, logo, ratio);
            }

            return bitmap;
        } catch (WriterException e) {
            LogUtils.w(e.getMessage());
        }

        return null;
    }

    /**
     * 在二维码中间添加Logo图案
     *
     * @param src   QRCode image
     * @param logo  logo image
     * @param ratio logo所占比例 因为二维码的最大容错率为30%，所以建议ratio的范围小于0.3
     * @return QRCode image
     */
    private static Bitmap addLogo(@NonNull Bitmap src, @Nullable Bitmap logo,
                                  @FloatRange(from = 0.0f, to = 1.0f) float ratio) {
        if (logo == null) {
            return src;
        }

        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();
        if (srcWidth == 0 || srcHeight == 0) {
            return null;
        }
        if (logoWidth == 0 || logoHeight == 0) {
            return src;
        }

        //logo大小为二维码整体大小
        float scaleFactor = srcWidth * ratio / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            LogUtils.w(e.getMessage());
        }

        return bitmap;
    }

    /**
     * generate bar code
     *
     * @param content       bar code content
     * @param format        bar code format {@link BarcodeFormat}
     * @param desiredWidth  barcode width
     * @param desiredHeight barcode height
     * @return bar code image
     */
    public static Bitmap createBarCode(String content, BarcodeFormat format,
                                       int desiredWidth, int desiredHeight) {
        String v = content;
        if (format.equals(BarcodeFormat.EAN_13)) {
            v = padCheckValueForEAN13(content);
        }

        //配置参数
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        return createBarCode(v, format, desiredWidth, desiredHeight, hints, true,
                40, Color.BLACK);
    }

    private static String padCheckValueForEAN13(String content) {
            if (content.length() == 12) {
                StringBuilder builder = new StringBuilder();
                int a = 0;
                int b = 0;
                int c = 0;
                for (int i = 0; i < 12; i++) {
                    int r = Integer.parseInt(content.substring(i, i+1));
                    if (r % 0x02 == 0) {
                        b += r;
                    } else {
                        a += r;
                    }
                }

                c = 10 - (a + b * 3) % 10;
                if (c == 10) {
                    c = 0;
                }
                builder.append(c);
                return builder.toString();
            }

            return content;
    }

    /**
     * generate bar code
     *
     * @param content       bar code content
     * @param format        bar code format {@link BarcodeFormat}
     * @param desiredWidth barcode width
     * @param desiredHeight barcode height
     * @param hints
     * @param isShowText if true show text under the bar code.
     * @param textSize size of the text under the bar code.
     * @param codeColor bar code color
     * @return bar code image
     */
    public static Bitmap createBarCode(String content, BarcodeFormat format,
                                       int desiredWidth, int desiredHeight,
                                       Map<EncodeHintType, ?> hints, boolean isShowText,
                                       int textSize, @ColorInt int codeColor) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }

        final int WHITE = Color.WHITE;
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix result = writer.encode(content, format, desiredWidth,
                    desiredHeight, hints);
            int width = result.getWidth();
            int height = result.getHeight();
            int[] pixels = new int[width * height];
            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = result.get(x, y) ? codeColor : WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            if (isShowText) {
                return addCode(bitmap, content, textSize, codeColor, textSize / 2);
            }
            return bitmap;
        } catch (WriterException e) {
            LogUtils.w(e.getMessage());
        }
        return null;
    }

    /**
     * 条形码下面添加文本信息
     *
     * @param src
     * @param code
     * @param textSize
     * @param textColor
     * @return
     */
    private static Bitmap addCode(@NonNull Bitmap src, String code, int textSize, @ColorInt int textColor,
                                  int offset) {
        if (TextUtils.isEmpty(code)) {
            return src;
        }

        //获取图片的宽高
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        if (srcWidth <= 0 || srcHeight <= 0) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight + textSize + offset * 2, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            TextPaint paint = new TextPaint();
            paint.setTextSize(textSize);
            paint.setColor(textColor);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(code, srcWidth / 2, srcHeight + textSize / 2 + offset, paint);
//            canvas.drawText(code, 0, srcHeight, paint);
            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            bitmap = null;
            LogUtils.w(e.getMessage());
        }

        return bitmap;
    }
}

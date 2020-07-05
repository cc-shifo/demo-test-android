package com.example.bingo.demozxinglib;

import android.graphics.Bitmap;
import android.graphics.Canvas;


public class PrinterCompat {
    /**
     * printer width is 384 pixels.
     */
    private static final int PRINTER_WIDTH = 384;

    Bitmap bitmap = Bitmap.createBitmap(PRINTER_WIDTH, PRINTER_WIDTH, Bitmap.Config.ARGB_8888);
//        try
//
//    {
//        Canvas canvas = new Canvas(bitmap);
//        canvas.drawBitmap(src, 0, 0, null);
//        canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
//        canvas.save();
//        canvas.restore();
//    } catch(
//    Exception e)
//
//    {
//        bitmap = null;
//        LogUtils.w(e.getMessage());
//    }
}

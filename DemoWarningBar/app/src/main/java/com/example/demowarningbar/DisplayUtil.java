package com.example.demowarningbar;

import android.content.Context;
import android.graphics.Point;
import android.util.Size;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;

public class DisplayUtil {
        public static Size getLandScreenSize(@NonNull Context context)  {
            WindowManager windowManager = (WindowManager) context.getSystemService(
                    Context.WINDOW_SERVICE);

            int screenWidth;
            int screenHeight;

            Display display = windowManager.getDefaultDisplay();

            Point outSize = new Point();
            display.getRealSize(outSize);

            screenWidth = Math.max(outSize.x, outSize.y);

            screenHeight = Math.min(outSize.x, outSize.y);

            return new Size(screenWidth, screenHeight);
        }
}

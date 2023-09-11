package com.noor.thenoorcar.Function;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

public class ScreenUtils {
    public static Point getScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point screenSize = new Point();
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getSize(screenSize);
        }
        return screenSize;
    }
}

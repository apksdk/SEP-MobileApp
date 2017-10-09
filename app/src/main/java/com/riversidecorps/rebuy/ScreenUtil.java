package com.riversidecorps.rebuy;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * created by yijun on 2017/10/8.
 */


public class ScreenUtil {

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }
}
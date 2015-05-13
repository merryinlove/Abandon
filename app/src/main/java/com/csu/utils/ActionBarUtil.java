package com.csu.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import com.csu.telecom.R;

/**
 * Created by ubuntu on 15-5-10.
 */
public class ActionBarUtil {

    private static int primaryColor ;

    public static void initToolBar(Context context ,ImageView statusBar,View content){
        primaryColor = context.getResources().getColor(R.color.primary);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
            statusBar.setImageDrawable(new ColorDrawable(darkenColor(primaryColor)));
        else
            statusBar.setImageDrawable(new ColorDrawable(primaryColor));
        content.setBackgroundColor(primaryColor);
    }

    //改变颜色深度
    public static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.85f; // value component
        return Color.HSVToColor(hsv);
    }
}

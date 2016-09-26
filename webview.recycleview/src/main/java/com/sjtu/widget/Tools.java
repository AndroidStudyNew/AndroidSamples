package com.sjtu.widget;

import android.content.Context;

/**
 * [description]
 * author: yifei
 * created at 16/9/26 上午10:24
 */

public class Tools {

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}

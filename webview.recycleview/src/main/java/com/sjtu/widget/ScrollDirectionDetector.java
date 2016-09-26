package com.sjtu.widget;

import android.util.Log;

/**
 * Created by taotao on 15/11/27.
 */
public class ScrollDirectionDetector {

    public static final int DIRECTION_DOWN = 1;
    public static final int DIRECTION_UP = 2;
    public static final int DIRECTION_NO = 0;


    public static int detectDirection(int disY, boolean showLog){
        int direction;
        if(disY > 0){
            direction = ScrollDirectionDetector.DIRECTION_DOWN;
            if(showLog) Log.v("DIRECTION", "DIRECTION vvvvvvvvvv " + direction);
        }else if(disY < 0){
            direction = ScrollDirectionDetector.DIRECTION_UP;
            if(showLog) Log.v("DIRECTION", "DIRECTION ^^^^^^^^^^ " + direction);
        }else {
            direction = ScrollDirectionDetector.DIRECTION_NO;
            if(showLog) Log.v("DIRECTION", "DIRECTION ---------- " + direction);
        }

        return direction;
    }


    public int detectDirectionChanged(int disY, boolean showLog, ChildScrollStateChangedListener orientationChangedListener){
        int direction = detectDirection(disY, showLog);
        if(orientationChangedListener != null) orientationChangedListener.onChildDirectionChange(direction);
        return direction;

    }

    public interface ChildScrollStateChangedListener {

        public enum SCROLL_POSITION {TOP, BOTTOM, MIDDLE, NO_SCROLL}

        void onChildDirectionChange(int orientation);

        void onChildPositionChange(SCROLL_POSITION position);

    }
}

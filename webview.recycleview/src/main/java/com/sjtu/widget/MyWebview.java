package com.sjtu.widget;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * [description]
 * author: yifei
 * created at 16/9/27 上午10:58
 */

public class MyWebview extends WebView implements NestedScrollingChild {
    private static final String TAG = "MyWebview";

    NestedScrollingChildHelper mNestedScrollingChildHelper;

    public MyWebview(Context context) {
        super(context);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
    }

    public MyWebview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
    }

    public MyWebview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x1 = 0;
        float x2 = 0;
        float y1 = 0;
        float y2 = 0;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            x1 = event.getX();
            y1 = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            x2 = event.getX();
            y2 = event.getY();
        }
        Log.d(TAG, "MyWebview dispatchTouchEvent \n(x1,y1):(" + x1 + "," + y1 + ") -> \n(x2,y2):(" + x2 + "," + y2 + ")");
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x1 = 0;
        float x2 = 0;
        float y1 = 0;
        float y2 = 0;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            x1 = event.getX();
            y1 = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            x2 = event.getX();
            y2 = event.getY();
        }
        Log.d(TAG, "MyWebview onTouchEvent \n(x1,y1):(" + x1 + "," + y1 + ") -> \n(x2,y2):(" + x2 + "," + y2 + ")");
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        float x1 = 0;
        float x2 = 0;
        float y1 = 0;
        float y2 = 0;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            x1 = event.getX();
            y1 = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            x2 = event.getX();
            y2 = event.getY();
        }
        Log.d(TAG, "MyWebview onInterceptTouchEvent \n(x1,y1):(" + x1 + "," + y1 + ") -> \n(x2,y2):(" + x2 + "," + y2 + ")");
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

}



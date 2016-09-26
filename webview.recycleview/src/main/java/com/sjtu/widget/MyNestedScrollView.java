package com.sjtu.widget;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

/**
 * [description]
 * author: yifei
 * created at 16/9/26 上午11:20
 */

public class MyNestedScrollView extends NestedScrollView implements ScrollDirectionDetector.ChildScrollStateChangedListener, ScrollDismissBehavior.OnScroll {
    private static final String TAG = "MyNestedScrollView";

    private static int SENSOR_DISTANCE;

    public MyNestedScrollView(Context context) {
        super(context);
    }

    public MyNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOverScrollMode(OVER_SCROLL_NEVER);
        SENSOR_DISTANCE = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
    }

    private int direction = ScrollDirectionDetector.DIRECTION_NO;

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        onScrollDismissBehavior(l - oldl, t - oldt);
        direction = ScrollDirectionDetector.detectDirection(t - oldt, true);
        Log.v(TAG, "onScrollChanged : top = " + t + "  direction = " + direction);
        setApproachLine(t);
    }

    /**
     * 计算当前切换 line 位置
     *
     * @param scrollY
     */
    private void setApproachLine(int scrollY) {
        switch (direction) {
            case ScrollDirectionDetector.DIRECTION_DOWN: {
                int disMin = -1;
                for (View item : scrollingChildList) {
                    int itemTop = item.getTop();
//                    Log.v(TAG, "1111itemTop : " + itemTop + " " + item);
                    if (scrollY <= itemTop) {
                        int dis = itemTop - scrollY;
                        if (disMin == -1 || dis < disMin) {
                            disMin = dis;
                            setCurrentSwapLine(itemTop);
                        }
                    }
                }
                if (disMin == -1) {
                    setCurrentSwapLine(-1);
                }
            }
            break;
            case ScrollDirectionDetector.DIRECTION_UP: {
                int disMin = -1;
                for (View item : scrollingChildList) {
                    int itemTop = item.getTop();
//                    Log.v(TAG, "2222itemTop : " + itemTop + " " + item);
                    if (scrollY >= itemTop) {
                        int dis = scrollY - itemTop;
                        if (disMin == -1 || dis < disMin) {
                            disMin = dis;
                            setCurrentSwapLine(itemTop);
                        }
                    }
                }
                if (disMin == -1) {
                    setCurrentSwapLine(-1);
                }
            }
            break;
        }
    }

    /**
     * 当前 scrolling 切换位置
     */
    private int currentSwapLine = -1;

    private void setCurrentSwapLine(int currentSwapLine) {
        this.currentSwapLine = currentSwapLine;
        Log.v(TAG, "currentSwapLine = " + currentSwapLine);
    }

    private List<View> scrollingChildList;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        analyzeNestedScrollingChildViews();
    }

    /**
     * nestedScrollingChild 只能嵌套在rootChild的第一层 <p>
     * 获取 implements nestedScrollingChild view list
     */
    private void analyzeNestedScrollingChildViews() {
        View rootChild = getChildAt(0);
        if (rootChild == null || !(rootChild instanceof ViewGroup)) {
            throw new IllegalArgumentException("EmbeddedScrollView root child illegal");
        }
        scrollingChildList = new ArrayList<>();
        ViewGroup root = (ViewGroup) rootChild;
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof WebView) {
                scrollingChildList.add(child);
            }
        }
    }

    private boolean firstInitSize = true;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (firstInitSize) {
            setNestedScrollViewHeight();
            firstInitSize = false;
        }
    }

    /**
     * 如果 scrollingChildList 中的 view 没有设定具体高度,默认设置成 EmbeddedScrollView 的高度
     */
    private void setNestedScrollViewHeight() {
        post(new Runnable() {
            @Override
            public void run() {
                for (View view : scrollingChildList) {
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    if (params.height == ViewGroup.LayoutParams.MATCH_PARENT || params.height == ViewGroup.LayoutParams.WRAP_CONTENT || params.height == ViewGroup.LayoutParams.FILL_PARENT) {
                        int scrollViewH = getMeasuredHeight();
                        params.height = scrollViewH + 100;
                        view.setLayoutParams(params);
                        Log.i(TAG, "setNestedScrollViewHeight = " + scrollViewH + "  view = " + view);
                    }
                }
            }
        });
    }

    @Override
    public void onScrollDismissBehavior(int dx, int dy) {
        if (dismissBehavior != null) dismissBehavior.onScroll(dx, dy);
    }

    private ScrollDismissBehavior dismissBehavior;

    @Override
    public void setScrollDismissBehavior(ScrollDismissBehavior behavior) {
        this.dismissBehavior = behavior;
        setupChildScrollDismissBehavior();
    }

    private void setupChildScrollDismissBehavior() {
        if (scrollingChildList == null || dismissBehavior == null) return;
        for (View view : scrollingChildList) {
            if (view instanceof ScrollDismissBehavior.OnScroll) {
                ((ScrollDismissBehavior.OnScroll) view).setScrollDismissBehavior(dismissBehavior);
            }
        }
    }


    public void onChildDirectionChange(int orientation) {

    }

    @Override
    public void onChildPositionChange(SCROLL_POSITION position) {

    }

    boolean consumeEvent = false;
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        int scrollY = getScrollY();
        Log.v(TAG, "onNestedPreScroll : direction = " + direction + "  currentSwapLine = " + currentSwapLine + "  dy = " + dy + "  scrollY = " + scrollY);

        if(scrollY < currentSwapLine){
            Log.v(TAG, "consumeEvent 111111");
            if(direction == ScrollDirectionDetector.DIRECTION_DOWN){
                if(currentSwapLine != -1 && (scrollY + dy) > currentSwapLine) dy = currentSwapLine - scrollY;
            }
            consumeEvent(dx, dy, consumed);
            return;
        }
        if(scrollY > currentSwapLine){
            Log.v(TAG, "consumeEvent 222222");
            if(direction == ScrollDirectionDetector.DIRECTION_UP){
                if(currentSwapLine != -1 && (scrollY + dy) < currentSwapLine) dy = currentSwapLine - scrollY;
            }
            consumeEvent(dx, dy, consumed);
            return;
        }

    }

    private void consumeEvent(int dx, int dy, int[] consumed) {
        scrollBy(dx, dy);
        consumed[0] = 0;
        consumed[1] = dy;
        consumeEvent = true;
        Log.i(TAG, "parent consumed pre : " + consumed[1]);
    }


    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.i(TAG, "parent consumed : dyConsumed = " + dyConsumed + " , dyUnconsumed = " + dyUnconsumed);
        scrollBy(dxUnconsumed, dyUnconsumed);
        consumeEvent = true;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        Log.i(TAG, "onNestedPreFling : " + " direction = " + direction + " currentSwapLine = " + currentSwapLine + " velocityY = " + velocityY + " scrollY = " + getScrollY());

        int scrollY = getScrollY();

        if(scrollY < currentSwapLine){
            Log.v(TAG, "fling 111111");
            fling((int) velocityY);
            consumeEvent = true;
            return true;
        }
        if(scrollY > currentSwapLine){
            Log.v(TAG, "fling 222222");
            fling((int) velocityY);
            consumeEvent = true;
            return true;
        }
        return false;
    }



    /**
     * 在没有实际滑动前，做越界处理
     *
     * @param scrollX
     * @param scrollY
     * @param clampedX
     * @param clampedY
     */
    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        Log.d(TAG, "onOverScrolled : " + scrollY + " " + clampedY + " isTouchUp = " + isTouchUp + "/" + hasNestedScroll + " currentSwapLine = " + currentSwapLine);
        if ((isTouchUp || !hasNestedScroll) && currentSwapLine != -1) {
            int overY = scrollY - currentSwapLine;
            if (Math.abs(overY) < SENSOR_DISTANCE) {
                if (direction == ScrollDirectionDetector.DIRECTION_DOWN && (overY > 0)) {
                    Log.e(TAG, "1111scroll to currentSwapLine = " + currentSwapLine);
                    stopScrolling(0, currentSwapLine);
                    return;
                } else if (direction == ScrollDirectionDetector.DIRECTION_UP && (overY < 0)) {
                    Log.e(TAG, "2222scroll to currentSwapLine = " + currentSwapLine);
                    stopScrolling(0, currentSwapLine);
                    return;
                }
            }
        }
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    boolean hasNestedScroll = false;
    private boolean isTouchUp;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean ret = super.onTouchEvent(ev);
        int action = ev.getAction();
        Log.v(TAG, "onTouchEvent : action = " + action + "  " + ret);
        setTouchState(ev);
        return ret;
    }


    private float lastY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        stopScrolling();
        boolean ret = super.onInterceptTouchEvent(ev);
        if (hasNestedScroll) ret = false;
        int action = ev.getAction();
        Log.v(TAG, "onInterceptTouchEvent : action = " + action + "  " + ret + "/" + hasNestedScroll);
        setTouchState(ev);
        return ret;
    }


    private void setTouchState(MotionEvent event) {

        isTouchUp = false;
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                stopScrolling();
                isTouchUp = false;
                lastY = event.getY();
                direction = ScrollDirectionDetector.DIRECTION_NO;
                break;

            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                int deltaY = (int) (lastY - y);
                if (deltaY != 0) {
                    direction = ScrollDirectionDetector.detectDirection(deltaY, true);
                }
                lastY = y;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isTouchUp = true;
                break;
        }
    }


    /**
     * 从源码分析出,连续调用两次smoothScrollBy,会终止scrolling
     */
    private void stopScrolling() {
        smoothScrollBy(0, 0);
        smoothScrollBy(0, 0);
    }

    /**
     * 从源码分析出,连续调用两次smoothScrollBy,会终止scrolling
     *
     * @param x
     * @param y
     */
    private void stopScrolling(int x, int y) {
        smoothScrollTo(x, y);
        smoothScrollTo(x, y);
    }
}

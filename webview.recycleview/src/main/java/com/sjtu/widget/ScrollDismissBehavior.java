package com.sjtu.widget;

import android.animation.Animator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;

/**
 * Created by taotao on 16/6/7.
 */
public class ScrollDismissBehavior {

    private static final String TAG = "ScrollDismissBehavior";
    public interface OnScroll{
        void setScrollDismissBehavior(ScrollDismissBehavior behavior);
        void onScrollDismissBehavior(int dx, int dy);
    }

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    private static int SENSOR_DIS;

    private static final int ANIMAL_DURATION = 300;

    private int scrolledDisY;

    private View targetView;

    public ScrollDismissBehavior(View scrollDismissView){
        this.targetView = scrollDismissView;
        SENSOR_DIS = Tools.dip2px(scrollDismissView.getContext(),20);
        Log.i(TAG, "SENSOR_DIS = " + SENSOR_DIS);
    }

    public void onScroll(int dx, int dy){
        if(scrolledDisY > 0 && dy < 0 || scrolledDisY < 0 && dy > 0){
            scrolledDisY = 0;
        }
        scrolledDisY += dy;

//        Log.v(this, "scrolledDisY = " + scrolledDisY + "  dy = " + dy );

        if(animalFinished == true){
            int visibility = targetView.getVisibility();
            if(scrolledDisY > SENSOR_DIS && visibility == View.VISIBLE){
                hide(targetView);
            }else if(-scrolledDisY > SENSOR_DIS && visibility == View.GONE){
                show(targetView);
            }
        }
    }

    private boolean animalFinished = true;

    private void hide(final View view) {
//        Log.d(this, "---hide---");
        ViewPropertyAnimator animator = view.animate();
        animator.translationY(- view.getHeight()).setInterpolator(INTERPOLATOR).setDuration(ANIMAL_DURATION);
        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                animalFinished = false;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.GONE);
                animalFinished = true;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                animalFinished = true;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }


    private void show(final View view) {
//        Log.d(this, "---show---");
        ViewPropertyAnimator animator = view.animate();
        animator.translationY(0).setInterpolator(INTERPOLATOR).setDuration(ANIMAL_DURATION);
        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view.setVisibility(View.VISIBLE);
                animalFinished = false;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animalFinished = true;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                animalFinished = true;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();

    }





}

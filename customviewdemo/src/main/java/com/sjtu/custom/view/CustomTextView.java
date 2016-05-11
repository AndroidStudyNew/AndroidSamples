package com.sjtu.custom.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.sjtu.custom.R;
import com.sjtu.custom.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义View步骤：(学习网址参考：http://blog.csdn.net/lmj623565791/article/details/24252901)
 * 1、自定义View的属性，首先在res/values/  下建立一个attrs.xml ， 在里面定义我们的属性和声明我们的整个样式。
 * 2、在View的构造方法中获得我们自定义的属性
 * [ 3、重写onMesure ]
 *  4、重写onDraw
 * 我把3用[]标出了，所以说3不一定是必须的，当然了大部分情况下还是需要重写的。
 *
 *
 * Created by CharlesZhu on 2016/4/22.
 */
public class CustomTextView extends View {

    private static final String REGULAR_PATTERN = "[`~!@#$^&*=|{}:;,\\\\[\\\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“。，、？]";
    private static final String TAG = "CustomTextView";
    private String mText;

    private String mTextContent;
    private int mTextColor;
    private int mTextSize;

    private String mSpellContent;
    private int mTextSpellColor;
    private int mTextSpellSize;

    /**
     * 绘制时控制文本绘制的范围
     */
    private Rect mSpellBound;
    private Rect mBound;
    private Paint mPaint;
    private Paint mSpellPaint;

    private int mLinePadding;
    private int mLineHeight;

    List<String> initTextToList;

    public CustomTextView(Context context) {
        this(context,null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**
     * 获得我自定义的样式属性
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /**
         * 获得自定义的样式属性
         */
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomTextView,defStyleAttr,0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CustomTextView_ctext:
                    mText = a.getString(attr);
                    break;
                case R.styleable.CustomTextView_ctextColor:
                    // 默认颜色设置为黑色
                    mTextColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.CustomTextView_ctextSpellColor:
                    // 默认颜色设置为黑色
                    mTextSpellColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.CustomTextView_ctextSize:
                    // 默认设置为16sp，TypeValue也可以把sp转化为px
                    mTextSize = a.getDimensionPixelSize(attr,(int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CustomTextView_ctextSpellSize:
                    // 默认设置为16sp，TypeValue也可以把sp转化为px
                    mTextSpellSize = a.getDimensionPixelSize(attr,(int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CustomTextView_ctextLinePadding:
                    mLinePadding = a.getDimensionPixelSize(attr,(int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    break;
            }
        }
        a.recycle();

        Log.e(TAG,"mTextSize:" + mTextSize + ", mTextSpellSize:" + mTextSpellSize + "  mLinePadding:" + mLinePadding);
        /**
         * 获取绘制文本的宽高
         */
        mPaint = new Paint();
        mSpellPaint = new Paint();
        mBound = new Rect();
        mSpellBound = new Rect();
    }

    public void seText(String str) {
        mText = str;
        invalidate();
    }

    /**
     * onMesure若调用系统提供的，系统帮我们测量的结果就是MATCH_PARENT的长度
     * 因此当设置了WRAP_CONTENT/MATCH_PARENT时，我们需要自己进行测量，即重写onMesure方法”：
     * 重写之前先了解MeasureSpec的specMode,一共三种类型：
     * EXACTLY：一般是设置了明确的值或者是MATCH_PARENT
     * AT_MOST：表示子布局限制在一个最大值内，一般为WARP_CONTENT
     * UNSPECIFIED：表示子布局想要多大就多大，很少使用
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e(TAG,"onMeasure");
        initTextToList = textToList();
        mPaint.setTextSize(mTextSize);
        mPaint.getTextBounds("测试",0,"测试".length(),mBound);
        mPaint.setColor(mTextColor);

        mSpellPaint.setTextSize(mTextSpellSize);
        mSpellPaint.getTextBounds("ceshi",0,"ceshi".length(),mSpellBound);
        mSpellPaint.setColor(mTextSpellColor);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        mLineHeight = getPaddingTop() + mBound.height() + mSpellBound.height() + getPaddingBottom() + mLinePadding;
        Log.e(TAG,"onMeasure mLineHeight:" + mLineHeight);
        int width = getPaddingLeft() + widthSize + getPaddingRight();
        int height =getPaddingTop() +  drawReal(mSpellPaint,mPaint,width,null) + getPaddingBottom() + 2 * mLinePadding;
        Log.e(TAG,"onMeasure--width--" +  width + "--height--" + height + ",mLineHeight:" + mLineHeight);
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG,"onDraw");
        drawReal(mSpellPaint,mPaint,getWidth(),canvas);
    }
    private int drawReal(Paint spellPaint, Paint paint, int widthSize, Canvas canvas) {
        if (initTextToList == null || initTextToList.size() == 0) {
            return 0;
        }
        int len = initTextToList.size();
        int row = 0;
        int templinelength = 0;
        if (canvas == null) {
            canvas = new Canvas();
        }
        int width_hanzi, width_pinyin, width;
        int startX_hanzi, startY_hanzi, startX_pinyin, startY_pinyin;
        int black_size = spellPaint.measureText(" ") > paint.measureText(" ")
                ? (int) spellPaint.measureText(" ") : (int) paint.measureText(" ");
        for (int i = 0; i < len; i++) {
            String[] strs = initTextToList.get(i).split(" ");
            width_hanzi = (int) spellPaint.measureText(strs[0]);
            width_pinyin = (int) paint.measureText(strs[1]);
            if (width_hanzi >= width_pinyin) {
                //汉字长
                width = width_hanzi;
                startX_hanzi = templinelength;
                startX_pinyin = startX_hanzi + (width_hanzi - width_pinyin) / 2;
            } else {
                //拼音长
                width = width_pinyin;
                startX_pinyin = templinelength;
                startX_hanzi = startX_pinyin + (width_pinyin - width_hanzi) / 2;

            }
            templinelength += width;
            if (templinelength > widthSize) { //换行
                row++;
                templinelength = 0;
                if (width_hanzi >= width_pinyin) {
                    //汉字长
                    width = width_hanzi;
                    startX_hanzi = templinelength;
                    startX_pinyin = startX_hanzi + (width_hanzi - width_pinyin) / 2;
                } else {
                    //拼音长
                    width = width_pinyin;
                    startX_pinyin = templinelength;
                    startX_hanzi = startX_pinyin + (width_pinyin - width_hanzi) / 2;
                }
                templinelength += width;
            }
            startY_pinyin = mLinePadding + row * mLineHeight;
            startY_hanzi = 2 * startY_pinyin - row * mLineHeight;
            canvas.drawText(strs[1] + " ", startX_pinyin, startY_pinyin, spellPaint);
            canvas.drawText(strs[0] + " ", startX_hanzi, startY_hanzi, paint);
            if (templinelength < widthSize) {//画空格
                templinelength += black_size;
            }
        }
        return (row + 1) * mLineHeight;
    }

    private List<String> textToList() {
        List<String> mTextToList = new ArrayList<>();
        if (mText != null && !mText.equals("")) {
            String[] contents = mText.split("\\)");
            //去掉了()  （）  ‘ '
            Pattern p = Pattern.compile(REGULAR_PATTERN);
            Matcher m;
            for (int i = 0; i < contents.length; i++) {
                //第一个是字符
                m = p.matcher(contents[i]);
                if (contents[i].contains("(")) {
                    if (!m.find()) {
                        mTextToList.add(contents[i].replace("(", " "));
                    } else {
                        String punctuation = contents[i].charAt(0) + "";
                        mTextToList.add(punctuation + " " + punctuation);
                        mTextToList.add(contents[i].replace(punctuation, "").replace("(", " "));
                    }
                } else {
                    mTextToList.add(contents[i] + " " + contents[i]);
                }
            }
        }
        return mTextToList;
    }

    private int maxTextSize(int mTextSizePinyin, int mTextSizeHanzi) {
        return mTextSizeHanzi > mTextSizePinyin ? mTextSizeHanzi : mTextSizePinyin;
    }

}
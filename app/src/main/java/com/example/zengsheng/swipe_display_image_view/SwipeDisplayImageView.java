package com.example.zengsheng.swipe_display_image_view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by zengsheng on 2018/3/13.
 */

public class SwipeDisplayImageView extends AppCompatImageView {

    private Context mContext;
    /**
     * 展示图片的宽高和Item高度
     */
    private int mDisplayWidth, mDisplayHeight, mItemHeight;
    /**
     * Item与背景图片滑动距离的相对比例(0 ~ 1)
     */
    private float ratio = 1;
    /**
     * 图片的滑动区域
     */
    private int mDisplayDistance;
    /**
     * 图片资源
     */
    private Drawable imageDrawable;

    public SwipeDisplayImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mItemHeight = h;
        imageDrawable = getDrawable();
        // 使用Item的宽度作为图片的宽度
        mDisplayWidth = getWidth();
        // 可以直接用手机屏幕的高度(别忘了减去状态栏高度)，不过滑动起来会相对静止，比较生硬，可以稍微减少此值以添加相对滑动的感觉
        mDisplayHeight = getScreenHeight(mContext) - getStatusBarHeight(mContext) - 100;
        // 图片滑动区域
        mDisplayDistance = mDisplayHeight - mItemHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        imageDrawable.setBounds(0, 0, mDisplayWidth, mDisplayHeight);
        canvas.save();
        canvas.translate(0, -mDisplayDistance * ratio);
        Log.d("SwipeDisplayImageView", "translateY = " + -mDisplayDistance * ratio);
        super.onDraw(canvas);
        canvas.restore();
    }

    /**
     * @param itemTop            itemView.getTop()
     * @param recyclerViewHeight recyclerView.getHeight()
     */
    public void refreshRatio(int itemTop, int recyclerViewHeight) {
        // 背景图片位移的范围
        int scope = recyclerViewHeight - mItemHeight;
        ratio = itemTop * 1.0f / scope;
        Log.d("SwipeDisplayImageView", "ratio = " + ratio);
        if (ratio < 0) {
            ratio = 0;
        }
        if (ratio > 1) {
            ratio = 1;
        }
        invalidate();
    }

    /**
     * 获取手机状态栏高度
     */
    public static Integer getStatusBarHeight(Context ctx) {
        Rect frame = new Rect();
        ((Activity) ctx).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }

    /**
     * 获取手机屏幕宽度
     */
    public static Integer getScreenWidth(Context ctx) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((Activity) ctx).getWindowManager().getDefaultDisplay()
                .getMetrics(localDisplayMetrics);
        return localDisplayMetrics.widthPixels;
    }

    /**
     * 获取手机屏幕高度
     */
    public static Integer getScreenHeight(Context ctx) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((Activity) ctx).getWindowManager().getDefaultDisplay()
                .getMetrics(localDisplayMetrics);
        return localDisplayMetrics.heightPixels;
    }
}

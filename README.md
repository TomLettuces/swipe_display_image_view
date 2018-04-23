## 仿知乎广告栏----自定义ImageView嵌入RecyclerView

#### 17年底刷知乎时发现的新型布局展示，感觉很有创意。知乎用此来投放广告，用正常列表Item的高度通过滑动展示整张手机屏幕大小的图片。下面是仿做的Gif效果图：



![ad-image-view-demo](media/15208386184845/ad-image-view-demo.gif)


### 实现分析

从效果上我们可以大致看出是一个两种ItemType的RecyclerView，而且从知乎上来看这个特殊的Item只放了图片，所以重点工作就是写一个自定义ImageView，需要的功能包括展示图片，滑动图片等，现在，我们详细分析下滑动流程，下图是滑动过程的逻辑分析：

![知乎广告栏滑动解析图](media/15208386184845/%E7%9F%A5%E4%B9%8E%E5%B9%BF%E5%91%8A%E6%A0%8F%E6%BB%91%E5%8A%A8%E8%A7%A3%E6%9E%90%E5%9B%BE.png)



### 代码解读

#### 自定义View中的核心代码：

全局变量

```java
private Context mContext;
/**
 * 展示图片的宽高和Item高度
 */
private int mDisplayWidth, mDisplayHeight, mItemHeight;
/**
 * Item与背景图片滑动距离的相对比例
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
```

```java
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
```

在onDraw中进行状态保存和位移

```java
@Override
protected void onDraw(Canvas canvas) {
    imageDrawable.setBounds(0, 0, mDisplayWidth, mDisplayHeight);
    canvas.save();
    canvas.translate(0, -mDisplayDistance * ratio);
    Log.d("SwipeDisplayImageView", "translateY = " + -mDisplayDistance * ratio);
    super.onDraw(canvas);
    canvas.restore();
}
```

在RecyclerView滑动监听中调用，用来刷新Item在图片的位置比例，核心逻辑

```java
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
```

RecyclerView添加滑动监听

```java
mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
    @Overrid
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int firstPosition = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
        int lastPosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
        // 逻辑处理的范围是从Item完全进入到完全离开
        for (int i = firstPosition; i <= lastPosition; i++) {
            RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForAdapterPosition(i);
            // 判断ItemType
            if (viewHolder.getItemViewType() == TYPE_AD) {
                View itemView = viewHolder.itemView;
                int top = itemView.getTop();
                int height = recyclerView.getHeight();

                SwipeDisplayImageView swipeDisplayImageView = itemView.findViewById(R.id.iv_swipe_display);
                swipeDisplayImageView.refreshSwipeRatio(top, height);
            }
        }
    }
});
```

xml代码

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="180dp">

    <com.example.zengsheng.advertisement.SwipeDisplayImageView
        android:id="@+id/iv_swipe_display"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scaleType="matrix"
        android:src="@drawable/pic1" />

</RelativeLayout>
```

### 总结
总体来说代码量不多，没有自定义绘制的部分，主要是需要分析清楚效果的实现过程，完成位移的逻辑处理即可，注意在xml中添加android:scaleType="matrix"这条属性。



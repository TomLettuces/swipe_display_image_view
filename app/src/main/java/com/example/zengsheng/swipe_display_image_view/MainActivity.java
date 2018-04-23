package com.example.zengsheng.swipe_display_image_view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class MainActivity extends Activity {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_AD = 1;

    private RecyclerView mRecyclerView;
    private int[] itemBackgroundColor = {R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.mRecyclerView);
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        MyRecyclerViewAdapter mRecyclerViewAdapter = new MyRecyclerViewAdapter(this);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
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
                        swipeDisplayImageView.refreshRatio(top, height);
                    }
                }
            }
        });

    }

    class MyRecyclerViewAdapter extends BaseRecyclerViewAdapter {

        public MyRecyclerViewAdapter(Context context) {
            super(context);
        }

        @Override
        public int[] getItemLayouts() {
            return new int[]{R.layout.item_normal, R.layout.item_ad};
        }

        @Override
        public int getItemCount() {
            return 10;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 5) {
                return TYPE_AD;
            } else {
                return TYPE_NORMAL;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            this.convert((BaseRecyclerViewAdapter.InnerViewHolder) holder, null, position);
        }

        @Override
        public void convert(InnerViewHolder holder, Object var2, int position) {
            if (holder.getItemViewType() == TYPE_NORMAL) {
                holder.getViewById(R.id.fl_container).setBackgroundColor(getResources().getColor(itemBackgroundColor[position % itemBackgroundColor.length]));
                holder.setText(R.id.tv_num, position + "");
            }
        }

    }
}

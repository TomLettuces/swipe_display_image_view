package com.example.zengsheng.swipe_display_image_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zengsheng on 2018/2/11.
 */

public abstract class BaseRecyclerViewAdapter<E> extends RecyclerView.Adapter {
    private Context mContext;
    private List<E> mData;
    private LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private int[] mItemLayouts;
    public static final int ITEM_TYPE_HEADER = -1;
    public static final int ITEM_TYPE_FOOTER = -2;
    public static final int ITEM_TYPE_NORMAL = 0;
    public static final int NO_ID = -1;

    public BaseRecyclerViewAdapter(Context context) {
        this(context, -1);
    }

    public BaseRecyclerViewAdapter(Context context, int layoutId) {
        this(context, (List) null, layoutId);
    }

    public BaseRecyclerViewAdapter(Context context, List<E> data) {
        this(context, data, -1);
    }

    public BaseRecyclerViewAdapter(Context context, List<E> data, int layoutId) {
        this.mContext = context;
        this.mData = data;
        if (layoutId >= 0) {
            this.mItemLayouts = new int[]{layoutId};
        }

        this.mInflater = LayoutInflater.from(context);
    }

    public void setData(List<E> data) {
        this.mData = data;
    }

    public void setDataAndUpdate(List<E> data) {
        this.setData(data);
        this.notifyDataSetChanged();
    }

    public void addData(List<E> data) {
        if (this.mData != null) {
            this.mData.addAll(data);
        } else {
            this.setData(data);
        }

    }

    public void addDataAndUpdate(List<E> data) {
        this.addData(data);
        this.notifyDataSetChanged();
    }

    public List<E> getData() {
        return this.mData;
    }

    public E getItemBean(int position) {
        E itemBean = null;
        if (this.mData != null && position >= 0 && position < this.mData.size()) {
            itemBean = this.mData.get(position);
        }

        return itemBean;
    }

    public int getItemPosition(RecyclerView.ViewHolder holder) {
        int position = -1;
        if (holder != null) {
            position = holder.getAdapterPosition();
        }

        return position;
    }

    protected Context getContext() {
        return this.mContext;
    }

    public int getItemCount() {
        return this.mData == null ? 0 : this.mData.size();
    }

    public int[] getItemLayouts() {
        return this.mItemLayouts;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseRecyclerViewAdapter<E>.InnerViewHolder viewHolder = this.createInnerViewHolder(parent, viewType);
        this.onCreateItemListeners(viewHolder);
        return viewHolder;
    }

    protected BaseRecyclerViewAdapter<E>.InnerViewHolder createInnerViewHolder(ViewGroup parent, int viewType) {
        this.mItemLayouts = this.getItemLayouts();
        if (this.mItemLayouts == null) {
            return null;
        } else if (this.mItemLayouts.length < 1) {
            return null;
        } else {
            int layoutId;
            if (this.mItemLayouts.length == 1) {
                layoutId = this.mItemLayouts[0];
            } else {
                layoutId = this.mItemLayouts[viewType];
            }

            View itemView = this.mInflater.inflate(layoutId, parent, false);
            BaseRecyclerViewAdapter<E>.InnerViewHolder viewHolder = new BaseRecyclerViewAdapter.InnerViewHolder(itemView);
            return viewHolder;
        }
    }

    protected void onCreateItemListeners(final BaseRecyclerViewAdapter<E>.InnerViewHolder holder) {
        View itemView = holder.getItemView();
        if (itemView != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (BaseRecyclerViewAdapter.this.mOnItemClickListener != null) {
                        BaseRecyclerViewAdapter.this.mOnItemClickListener.onItemClick(view, BaseRecyclerViewAdapter.this.getItemPosition(holder));
                    }

                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View view) {
                    return BaseRecyclerViewAdapter.this.mOnItemLongClickListener != null ? BaseRecyclerViewAdapter.this.mOnItemLongClickListener.onItemLongClick(view, BaseRecyclerViewAdapter.this.getItemPosition(holder)) : false;
                }
            });
        }

    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        this.convert((BaseRecyclerViewAdapter.InnerViewHolder) holder, this.mData.get(position), position);
    }

    public void addItem(E item, int position) {
        this.mData.add(position, item);
        this.notifyItemInserted(position);
    }

    public void removeItem(int position) {
        this.mData.remove(position);
        this.notifyItemRemoved(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    public abstract void convert(BaseRecyclerViewAdapter<E>.InnerViewHolder var1, E var2, int var3);

    protected LayoutInflater getLayoutInflater() {
        return this.mInflater != null ? this.mInflater : (this.mContext != null ? LayoutInflater.from(this.mContext) : null);
    }

    public class InnerViewHolder extends RecyclerView.ViewHolder implements IViewHolder {
        SparseArray<View> mViews = new SparseArray();
        View mItemView;

        public InnerViewHolder(View itemView) {
            super(itemView);
            this.mItemView = itemView;
        }

        public View getItemView() {
            return this.mItemView;
        }

        public <T extends View> T getViewById(int viewId) {
            View view = (View) this.mViews.get(viewId);
            if (view == null) {
                view = this.mItemView.findViewById(viewId);
                this.mViews.put(viewId, view);
            }

            return (T) view;
        }

        public void setText(int viewId, CharSequence text) {
            TextView tv = (TextView) this.getViewById(viewId);
            tv.setText(text);
        }

        public void setImageResource(int viewId, int resId) {
            ImageView iv = (ImageView) this.getViewById(viewId);
            iv.setImageResource(resId);
        }

        public void setImageBitmap(int viewId, Bitmap bm) {
            ImageView iv = (ImageView) this.getViewById(viewId);
            iv.setImageBitmap(bm);
        }
    }

    public interface IViewHolder {
        View getItemView();
    }

    public interface OnItemClickListener {
        void onItemClick(View var1, int var2);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View var1, int var2);
    }
}

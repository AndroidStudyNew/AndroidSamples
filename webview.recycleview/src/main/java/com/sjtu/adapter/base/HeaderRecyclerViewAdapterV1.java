package com.sjtu.adapter.base;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by yifei on 16/7/18.
 */
public class HeaderRecyclerViewAdapterV1 extends RecyclerView.Adapter {

    private static final int TYPE_HEADER = Integer.MIN_VALUE;
    private static final int TYPE_FOOTER = Integer.MIN_VALUE + 1;
    private static final int TYPE_ADAPTEE_OFFSET = 2;

    private final RecyclerView.Adapter mAdaptee;


    public HeaderRecyclerViewAdapterV1(RecyclerView.Adapter adaptee) {
        mAdaptee = adaptee;
    }

    public RecyclerView.Adapter getAdaptee() {
        return mAdaptee;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER && mAdaptee instanceof HeaderRecyclerView) {
            return ((HeaderRecyclerView) mAdaptee).onCreateHeaderViewHolder(parent, viewType);
        } else if (viewType == TYPE_FOOTER && mAdaptee instanceof FooterRecyclerView) {
            return ((FooterRecyclerView) mAdaptee).onCreateFooterViewHolder(parent, viewType);
        }
        return mAdaptee.onCreateViewHolder(parent, viewType - TYPE_ADAPTEE_OFFSET);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0 && holder.getItemViewType() == TYPE_HEADER && useHeader()) {
            ((HeaderRecyclerView) mAdaptee).onBindHeaderView(holder, position);
        } else if (position == mAdaptee.getItemCount() && holder.getItemViewType() == TYPE_FOOTER && useFooter()) {
            ((FooterRecyclerView) mAdaptee).onBindFooterView(holder, position);
        } else {
            mAdaptee.onBindViewHolder(holder, position - (useHeader() ? 1 : 0));
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = mAdaptee.getItemCount();
        if (useHeader()) {
            itemCount += 1;
        }
        if (useFooter()) {
            itemCount += 1;
        }
        return itemCount;
    }

    private boolean useHeader() {
        if (mAdaptee instanceof HeaderRecyclerView) {
            return true;
        }
        return false;
    }

    private boolean useFooter() {
        if (mAdaptee instanceof FooterRecyclerView) {
            return true;
        }
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && useHeader()) {
            return TYPE_HEADER;
        }
        if (position == mAdaptee.getItemCount() && useFooter()) {
            return TYPE_FOOTER;
        }
        if (mAdaptee.getItemCount() >= Integer.MAX_VALUE - TYPE_ADAPTEE_OFFSET) {
            new IllegalStateException("HeaderRecyclerViewAdapter offsets your BasicItemType by " + TYPE_ADAPTEE_OFFSET + ".");
        }
        return mAdaptee.getItemViewType(position) + TYPE_ADAPTEE_OFFSET;
    }


    public static interface HeaderRecyclerView {
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType);

        public void onBindHeaderView(RecyclerView.ViewHolder holder, int position);
    }

    public static interface FooterRecyclerView {
        public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType);

        public void onBindFooterView(RecyclerView.ViewHolder holder, int position);
    }

}

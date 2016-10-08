package com.sjtu.adapter.rc;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.sjtu.adapter.base.HeaderRecyclerViewAdapterV2;
import com.sjtu.webview.recycleview.R;

import java.util.List;

/**
 * Created by yifei on 16/7/18.
 */
public class HomeAdapter extends HeaderRecyclerViewAdapterV2 {

    private static final String TAG = "HomeAdapter";

    private List<String> mDatas;

    public HomeAdapter(List<String> datas) {
        this.mDatas = datas;
    }

    @Override
    public boolean useHeader() {
        return true;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        return new MyHeadView(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_head, parent, false));
    }

    @Override
    public void onBindHeaderView(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHeadView) {
            ((MyHeadView) holder).wv.loadUrl("http://www.jianshu.com/p/a00f5f2ab2f5");
        }
    }

    @Override
    public boolean useFooter() {
        return false;
    }

    @Override
    public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindFooterView(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, parent, false));
        return holder;
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder holder, int position) {
        Log.e("TTTT","position -- >" + position);
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).tv.setText(mDatas.get(position));
        }
    }

    @Override
    public int getBasicItemCount() {
        return mDatas.size();
    }

    @Override
    public int getBasicItemType(int position) {
        return position;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.id_num);
        }
    }

    class MyHeadView extends RecyclerView.ViewHolder {

        WebView wv;

        public MyHeadView(View view) {
            super(view);
            wv = (WebView) view.findViewById(R.id.wv);
            WebSettings webSettings = wv.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            wv.setVerticalScrollBarEnabled(false);
            wv.setVerticalScrollbarOverlay(false);
            wv.setHorizontalScrollBarEnabled(false);
            wv.setHorizontalScrollbarOverlay(false);
            wv.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    int height = view.getMeasuredHeight();
                    Log.d(TAG,"height:" + height);
                }
            });
        }
    }
}

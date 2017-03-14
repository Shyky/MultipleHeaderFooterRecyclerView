package com.shyky.demo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用装饰者模式实现的支持添加多个header和footer的包装Adapter
 * <p>用于包装另一个Adapter</p>
 *
 * @author Shyky
 * @version 1.1
 * @date 2017/3/2
 * @since 1.0
 */
public class MultipleHeaderFooterWrapperAdapter extends RecyclerView.Adapter {
    private static final String TAG = "MultipleHeaderFooter";
    private static final int TYPE_HEADER = 11;
    private static final int TYPE_FOOTER = 12;
    private final Context context;
    private final List<View> headerViews;
    private final List<View> footerViews;
    private final RecyclerView.Adapter adapter;

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout container;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            container = (LinearLayout) itemView;
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout container;

        public FooterViewHolder(View itemView) {
            super(itemView);
            container = (LinearLayout) itemView;
        }
    }

    public MultipleHeaderFooterWrapperAdapter(Context context, RecyclerView.Adapter adapter) {
        this(context, null, null, adapter);
    }

    public MultipleHeaderFooterWrapperAdapter(Context context, List<View> headerViews, List<View> footerViews, RecyclerView.Adapter adapter) {
        this.context = context;
//        layoutInflater = LayoutInflater.from(context);
        if (headerViews == null && footerViews == null) {
            this.headerViews = new ArrayList<>();
            this.footerViews = new ArrayList<>();
        } else {
            this.headerViews = headerViews;
            this.footerViews = footerViews;
        }
        this.adapter = adapter;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getHeaderCount()) {
            return TYPE_HEADER;
        }
        if (hasFooter() && position >= getHeaderCount() + adapter.getItemCount()) {
            return TYPE_FOOTER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                final LinearLayout headerContainer = new LinearLayout(context);
                return new HeaderViewHolder(headerContainer);
            case TYPE_FOOTER:
                final LinearLayout footerContainer = new LinearLayout(context);
                return new FooterViewHolder(footerContainer);
            default:
                return adapter.onCreateViewHolder(parent, viewType);
//            default:
//                return new NormalViewHolder(layoutInflater.inflate(R.layout.item_text, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "getItemCount = " + getItemCount());
        Log.d(TAG, "position = " + position);
        final int viewType = getItemViewType(position);
        Log.d(TAG, "viewType = " + viewType);
        switch (viewType) {
            case TYPE_HEADER:
                final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.container.addView(getHeader(position));
                break;
            case TYPE_FOOTER:
                final FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
                footerViewHolder.container.addView(getFooter(position));
                break;
            default:
                final int itemPosition = position - getHeaderCount();
                adapter.onBindViewHolder(holder, itemPosition);
//                String item = getItem(position);
//                final NormalViewHolder viewHolder = (NormalViewHolder) holder;
//                viewHolder.textView.setText(item);
//                break;
        }
    }

    @Override
    public int getItemCount() {
        return getHeaderCount() + adapter.getItemCount() + getFooterCount();
    }

//    public T getItem(int position) {
//        final int itemPosition = position - getHeaderCount();
//        return data.get(itemPosition);
//    }
//
//    public int getDataCount() {
//        return data == null || data.isEmpty() ? 0 : data.size();
//    }

    public int getHeaderCount() {
        return headerViews.size();
    }

    public boolean hasHeader() {
        return getHeaderCount() != 0;
    }

    public void addHeader(View view) {
        headerViews.add(view);
    }

    public View getHeader(int index) {
        return headerViews.get(index);
    }

    public int getFooterCount() {
        return headerViews.size();
    }

    public boolean hasFooter() {
        return getFooterCount() != 0;
    }

    public void addFooter(View view) {
        footerViews.add(view);
    }

    public View getFooter(int index) {
        final int footerIndex = index - (getHeaderCount() + adapter.getItemCount());
        return footerViews.get(footerIndex);
    }
}
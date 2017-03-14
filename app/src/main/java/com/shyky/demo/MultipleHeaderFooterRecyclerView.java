package com.shyky.demo;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义实现支持添加多个header view和footer view的RecyclerView
 *
 * @author Shyky
 * @version 1.1
 * @date 2017/3/2
 * @since 1.0
 */
public class MultipleHeaderFooterRecyclerView extends RecyclerView {
    /**
     * Listen to changes in the data set
     */
    protected InternalAdapterDataObserver dataSetObserver;
    /**
     * The adapter containing the data to be displayed by this view
     */
    protected Adapter internalAdapter;
    private ArrayList<View> headerViews;
    private ArrayList<View> footerViews;
    private final LayoutInflater layoutInflater;
    /**
     * The listener that receives notifications when an item is clicked.
     */
    private OnItemClickListener onItemClickListener;

    protected class InternalAdapterDataObserver extends AdapterDataObserver {
        protected AdapterDataObserver internalDataObserver;

        public InternalAdapterDataObserver() {
            // 通过反射获取父类观察者对象
            try {
                Field field = getDeclaredField(RecyclerView.class, "mObserver");
                if (field != null) {
                    internalDataObserver = (AdapterDataObserver) field.get(MultipleHeaderFooterRecyclerView.this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public Field getDeclaredField(Class clz, String fieldName) {
            Field field;
            for (; clz != Object.class; clz = clz.getSuperclass()) {
                try {
                    field = clz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return field;
                } catch (Exception e) {
                }
            }
            return null;
        }

        @Override
        public void onChanged() {
            if (internalDataObserver != null) {
                internalDataObserver.onChanged();
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (internalDataObserver != null) {
                internalDataObserver.onItemRangeChanged(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            if (internalDataObserver != null) {
                internalDataObserver.onItemRangeChanged(positionStart, itemCount, payload);
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (internalDataObserver != null) {
                internalDataObserver.onItemRangeInserted(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (internalDataObserver != null) {
                internalDataObserver.onItemRangeRemoved(positionStart, itemCount);
            }
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (internalDataObserver != null) {
                internalDataObserver.onItemRangeMoved(fromPosition, toPosition, itemCount);
            }
        }
    }

    /**
     * Interface definition for a callback to be invoked when an item in this
     * AdapterView has been clicked.
     */
    public interface OnItemClickListener {
        /**
         * Callback method to be invoked when an item in this AdapterView has
         * been clicked.
         * <p>
         * Implementers can call getItemAtPosition(position) if they need
         * to access the data associated with the selected item.
         *
         * @param parent   The RecyclerView where the click happened.
         * @param view     The view within the AdapterView that was clicked (this
         *                 will be a view provided by the adapter)
         * @param position The position of the view in the adapter.
         * @param id       The row id of the item that was clicked.
         */
        void onItemClick(ViewGroup parent, View view, int position, long id);
    }

    public MultipleHeaderFooterRecyclerView(Context context) {
        this(context, null);
    }

    public MultipleHeaderFooterRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultipleHeaderFooterRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        headerViews = new ArrayList<>();
        footerViews = new ArrayList<>();
        layoutInflater = LayoutInflater.from(context);
        // 设置默认的布局管理器，默认为垂直方向排列
        setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (internalAdapter != null && dataSetObserver != null) {
            internalAdapter.unregisterAdapterDataObserver(dataSetObserver);
        }
        if (headerViews.size() > 0 || footerViews.size() > 0) {
            internalAdapter = wrapHeaderListAdapterInternal(headerViews, footerViews, adapter);
        } else {
            internalAdapter = adapter;
        }
        if (internalAdapter != null) {
            dataSetObserver = new InternalAdapterDataObserver();
            internalAdapter.registerAdapterDataObserver(dataSetObserver);
        }

        super.swapAdapter(internalAdapter, true);
    }

    /**
     * 设置item点击事件
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public void addHeaderView(@LayoutRes int resId) {
        addHeaderView(layoutInflater.inflate(resId, this, false));
    }

    /**
     * 添加一个view出现在列表的底部
     * <p>注意：此方法需要在{@link #setAdapter(Adapter)}之前调用</p>
     *
     * @param view 要添加的view
     */
    public void addHeaderView(View view) {
        headerViews.add(view);
        // Wrap the adapter if it wasn't already wrapped.
        if (internalAdapter != null) {
            if (!(internalAdapter instanceof MultipleHeaderFooterWrapperAdapter22)) {
                wrapHeaderListAdapterInternal();
            }

            // In the case of re-adding a header view, or adding one later on,
            // we need to notify the observer.
            if (dataSetObserver != null) {
                dataSetObserver.onChanged();
            }
        }
    }

    public void addFooterView(@LayoutRes int resId) {
        addFooterView(layoutInflater.inflate(resId, this, false));
    }

    /**
     * 添加一个view出现在列表的底部
     *
     * @param view 要添加的view
     */
    public void addFooterView(View view) {
        footerViews.add(view);
        // Wrap the adapter if it wasn't already wrapped.
        if (internalAdapter != null) {
            if (!(internalAdapter instanceof MultipleHeaderFooterWrapperAdapter22)) {
                wrapHeaderListAdapterInternal();
            }

            // In the case of re-adding a footer view, or adding one later on,
            // we need to notify the observer.
            if (dataSetObserver != null) {
                dataSetObserver.onChanged();
            }
        }
    }

    protected WrapperAdapter wrapHeaderListAdapterInternal(List<View> headerViews, List<View> footerViews, Adapter adapter) {
        return new MultipleHeaderFooterWrapperAdapter22(getContext(), headerViews, footerViews, adapter,onItemClickListener);
    }

    protected void wrapHeaderListAdapterInternal() {
        internalAdapter = wrapHeaderListAdapterInternal(headerViews, footerViews, internalAdapter);
    }
}
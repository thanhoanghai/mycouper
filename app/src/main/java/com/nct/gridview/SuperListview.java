package com.nct.gridview;


import thh.com.mycouper.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;


public class SuperListview extends BaseSuperAbsListview {

    public SuperListview(Context context) {
        super(context);
    }


    public SuperListview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SuperListview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ListView getList(){
       return (ListView) mList;
    }
    
    public void addHeaderView(View view)
    {
    	((ListView)mList).addHeaderView(view);
    }

    @Override
    protected void initAttrs(AttributeSet attrs) {
        super.initAttrs(attrs);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.superlistview);
        try {
            mSuperListViewMainLayout = a.getResourceId(R.styleable.superlistview_superlv_mainLayoutID, R.layout.nct_view_progress_listview);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void initAbsListView(View v) {

        View listView = v.findViewById(android.R.id.list);

        if (listView instanceof ListView)
            mList = (ListView) listView;
        else
            throw new IllegalArgumentException("SuperListView works with a List!");


        if (mList!=null) {

            mList.setClipToPadding(mClipToPadding);
            // Divider
            getList().setDivider(new ColorDrawable(mDivider));
            getList().setDividerHeight((int) mDividerHeight);
            
            if(mBackroundID > 0)
            	getList().setBackgroundResource(mBackroundID);

            mList.setOnScrollListener(this);
            if (mSelector != 0)
                mList.setSelector(mSelector);

            if (mPadding != -1.0f) {
                mList.setPadding(mPadding, mPadding, mPadding, mPadding);
            } else {
                mList.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
            }
            mList.setScrollBarStyle(mScrollbarStyle);
        }
    }


    public void setupSwipeToDismiss(final SwipeDismissListViewTouchListener.DismissCallbacks listener, final boolean autoRemove) {
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener((ListView) mList, new SwipeDismissListViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return listener.canDismiss(position);
                    }

                    @Override
                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                        if (autoRemove) {
                        	//TODO:
//                            for (int position : reverseSortedPositions) {
//
//                                ((ArrayAdapter)mList.getAdapter()).remove(mList.getAdapter().getItem(position));
//                            }
//                            ((ArrayAdapter)mList.getAdapter()).notifyDataSetChanged();
                        }
                        listener.onDismiss(listView, reverseSortedPositions);
                    }
                });
        mList.setOnTouchListener(touchListener);
    }

}

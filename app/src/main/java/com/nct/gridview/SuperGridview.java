package com.nct.gridview;


import thh.com.mycouper.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;


public class SuperGridview extends BaseSuperAbsListview {

    //-------------------------------------------------------
    // Custom Grid attributes
    //-------------------------------------------------------
    private int mColumns;
    private int mHorizontalSpacing;
    private int mVerticalSpacing;


    public SuperGridview(Context context) {
        super(context);
    }

    public SuperGridview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SuperGridview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public GridView getList(){
        return (GridView) mList;
    }

    @Override
    protected void initAttrs(AttributeSet attrs) {
        super.initAttrs(attrs);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.superlistview);
        try {
            mSuperListViewMainLayout = a.getResourceId(R.styleable.superlistview_superlv_mainLayoutID, R.layout.nct_view_progress_gridview);
        } finally {
            a.recycle();
        }

        TypedArray ag = getContext().obtainStyledAttributes(attrs, R.styleable.supergridview);
        try {
            mColumns = ag.getInt(R.styleable.supergridview_supergv__columns, 1);
            mVerticalSpacing = (int) ag.getDimension(R.styleable.supergridview_supergv__verticalSpacing, 1);
            mHorizontalSpacing = (int) ag.getDimension(R.styleable.supergridview_supergv__horizontalSpacing, 1);
        } finally {
            ag.recycle();
        }
    }

    @Override
    protected void initAbsListView(View v) {

        View listView = v.findViewById(android.R.id.list);
        if (listView instanceof GridView)
            mList = (GridView) listView;
        else
            throw new IllegalArgumentException(listView.getClass().getName());

        if (mList!=null) {

            getList().setNumColumns(mColumns);
            getList().setVerticalSpacing(mVerticalSpacing);
            getList().setHorizontalSpacing(mHorizontalSpacing);
            getList().setHorizontalSpacing((int) mDividerHeight);
            getList().setVerticalSpacing((int) mDividerHeight);
            
            mList.setClipToPadding(mClipToPadding);
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

}

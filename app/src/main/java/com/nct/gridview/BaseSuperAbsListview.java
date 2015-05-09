package com.nct.gridview;


import thh.com.mycouper.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;


public abstract class BaseSuperAbsListview extends FrameLayout implements
		AbsListView.OnScrollListener {

	protected int ITEM_LEFT_TO_LOAD_MORE = 10;

	protected AbsListView mList;
	protected ViewStub mProgress;
	protected ViewStub mDisconnect;
	protected ViewStub mMoreProgress;
	protected ViewStub mEmpty;

	private boolean mIsDisconnectInflated = false;
	private OnClickListener mDisconnectListener = null;
	
	protected int mProgressId;
	protected int mDisconnectId;
	protected int mMoreProgressId;
	protected int mEmptyId;
	
	private ImageView mEmptyImg;
	private TextView mEmptyTxt;

	protected float mDividerHeight;
	protected int mDivider;
	protected boolean mClipToPadding;
	protected int mPadding;
	protected int mPaddingTop;
	protected int mPaddingBottom;
	protected int mPaddingLeft;
	protected int mPaddingRight;
	protected int mScrollbarStyle;
	protected int mBackroundID;

	protected OnMoreListener mOnMoreListener;
	protected boolean isLoadingMore;
	protected int mSelector;
	protected SwipeRefreshLayout mPtrLayout;

	protected int mSuperListViewMainLayout;

	public SwipeRefreshLayout getSwipeToRefresh() {
		return mPtrLayout;
	}

	public AbsListView getList() {
		return mList;
	}

	public BaseSuperAbsListview(Context context) {
		super(context);
		initView();
	}

	public BaseSuperAbsListview(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttrs(attrs);
		initView();
	}

	public BaseSuperAbsListview(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initAttrs(attrs);
		initView();
	}

	protected void initAttrs(AttributeSet attrs) {
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.superlistview);
		try {
			mClipToPadding = a
					.getBoolean(
							R.styleable.superlistview_superlv__listClipToPadding,
							false);
			mDivider = a.getColor(
					R.styleable.superlistview_superlv__listDivider, 0);
			mDividerHeight = a.getDimension(
					R.styleable.superlistview_superlv__listDividerHeight, 0.0f);
			mPadding = (int) a.getDimension(
					R.styleable.superlistview_superlv__listPadding, -1.0f);
			mPaddingTop = (int) a.getDimension(
					R.styleable.superlistview_superlv__listPaddingTop, 0.0f);
			mPaddingBottom = (int) a.getDimension(
					R.styleable.superlistview_superlv__listPaddingBottom, 0.0f);
			mPaddingLeft = (int) a.getDimension(
					R.styleable.superlistview_superlv__listPaddingLeft, 0.0f);
			mPaddingRight = (int) a.getDimension(
					R.styleable.superlistview_superlv__listPaddingRight, 0.0f);
			mScrollbarStyle = a.getInt(
					R.styleable.superlistview_superlv__scrollbarStyle, -1);
			mSelector = a.getResourceId(
					R.styleable.superlistview_superlv__listSelector, 0);
			mEmptyId = a.getResourceId(
					R.styleable.superlistview_superlv__empty, 0);
			mMoreProgressId = a.getResourceId(
					R.styleable.superlistview_superlv__moreProgress,
					R.layout.nct_view_more_progress);
			mProgressId = a.getResourceId(
					R.styleable.superlistview_superlv__progress,
					R.layout.nct_view_progress);
			mDisconnectId = a.getResourceId(
					R.styleable.superlistview_superlv__disconnect,
					R.layout.nct_view_disconnect);
			
			mBackroundID = a.getResourceId(
					R.styleable.superlistview_superlv__listbackround,
					0);
		} finally {
			a.recycle();
		}
	}

	private void initView() {
		if (isInEditMode()) {
			return;
		}
		View v = LayoutInflater.from(getContext()).inflate(
				mSuperListViewMainLayout, this);
		mPtrLayout = (SwipeRefreshLayout) v.findViewById(R.id.ptr_layout);
		mPtrLayout.setEnabled(false);

		// Loading progress
		mProgress = (ViewStub) v.findViewById(android.R.id.progress);
		mProgress.setLayoutResource(mProgressId);
		mProgress.inflate();
		
		// Disconnect view
		mDisconnect = (ViewStub)v.findViewById(R.id.disconnect);
		mDisconnect.setLayoutResource(mDisconnectId);
		mDisconnect.setVisibility(View.GONE);
		
		// Load more progress
		mMoreProgress = (ViewStub) v.findViewById(R.id.more_progress);
		mMoreProgress.setLayoutResource(mMoreProgressId);
		if (mMoreProgressId != 0)
			mMoreProgress.inflate();
		mMoreProgress.setVisibility(View.GONE);

		// Empty view (no data)
		mEmpty = (ViewStub) v.findViewById(R.id.empty);
		mEmpty.setLayoutResource(mEmptyId);
		if (mEmptyId != 0) {
			View view = mEmpty.inflate();
			try {
				mEmptyImg = (ImageView) view.findViewById(R.id.nct_empty_img);
				mEmptyTxt = (TextView) view.findViewById(R.id.nct_empty_txt);
			} catch (Exception e) {
				mEmptyImg = null;
				mEmptyTxt = null;
			}
		}
		mEmpty.setVisibility(View.GONE);

		initAbsListView(v);
	}

	/**
	 * Implement this method to customize the AbsListView
	 */
	protected abstract void initAbsListView(View view);

	/**
	 * Set the adapter to the listview Automativally hide the progressbar Set
	 * the refresh to false If adapter is empty, then the emptyview is shown
	 * 
	 * @param adapter
	 */
	public void setAdapter(ListAdapter adapter) {
		mList.setAdapter(adapter);
		if (mEmpty != null && mEmptyId != 0)
			mList.setEmptyView(mEmpty);
		showList();
		mPtrLayout.setRefreshing(false);
		adapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				isLoadingMore = false;
				mPtrLayout.setRefreshing(false);
				showList();
				if (mList.getAdapter().getCount() == 0 && mEmptyId != 0) {
					mEmpty.setVisibility(View.VISIBLE);
				} else if (mEmptyId != 0) {
					mEmpty.setVisibility(View.GONE);
				}
			}
		});
		if ((adapter == null || adapter.getCount() == 0) && mEmptyId != 0) {
			mEmpty.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * Remove the adapter from the listview
	 */
	public void clear() {
		mList.setAdapter(null);
	}

	/**
	 * Show the progressbar
	 */
	public void showProgress() {
		showHideIconRefress(false);
		hideList();
		hideDisconnect();
		mProgress.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Hide the progressbar
	 */
	public void hideProgress() {
		mProgress.setVisibility(View.GONE);
	}
	
	public void showDisconnect() {
		hideProgress();
		hideList();
		mEmpty.setVisibility(View.GONE);
		mPtrLayout.setRefreshing(false);
		if (mIsDisconnectInflated) {
			mDisconnect.setVisibility(View.VISIBLE);
		} else {
			mIsDisconnectInflated = true;
			View view = mDisconnect.inflate();
			view.setOnClickListener(mDisconnectListener);
		}
	}
	
	public void showHideIconRefress(Boolean status)
	{
		mPtrLayout.setRefreshing(status);
	}
	
	public void hideDisconnect() {
		mDisconnect.setVisibility(View.GONE);
	}

	/**
	 * Hide the progressbar and show the listview
	 */
	public void showList() {
		hideProgress();
		hideDisconnect();
		mList.setVisibility(View.VISIBLE);
	}
	
	/**
	 * Hide the listview
	 */
	public void hideList() {
		mList.setVisibility(View.GONE);
	}

	public void showMoreProgress() {
		mMoreProgress.setVisibility(View.VISIBLE);

	}

	public void hideMoreProgress() {
		mMoreProgress.setVisibility(View.GONE);
	}
	
	/**
	 * Set the listener when refresh is triggered and enable the
	 * SwipeRefreshLayout
	 * 
	 * @param listener
	 */
	public void setRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
		mPtrLayout.setEnabled(true);
		mPtrLayout.setOnRefreshListener(listener);
	}

	/**
	 * Set the colors for the SwipeRefreshLayout states
	 * 
	 * @param col1
	 * @param col2
	 * @param col3
	 * @param col4
	 */
	@SuppressWarnings("deprecation")
	public void setRefreshingColor(int col1, int col2, int col3, int col4) {
		mPtrLayout.setColorScheme(col1, col2, col3, col4);
	}

	/**
	 * Set the scroll listener for the listview
	 * 
	 * @param listener
	 */
	public void setOnScrollListener(AbsListView.OnScrollListener listener) {
		mList.setOnScrollListener(listener);
	}

	/**
	 * Set the onItemClickListener for the listview
	 * 
	 * @param listener
	 */
	public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
		mList.setOnItemClickListener(listener);
	}

	/**
	 * 
	 * @return the listview adapter
	 */
	public android.widget.ListAdapter getAdapter() {
		return mList.getAdapter();
	}

	public int getFirstVisiblePosition() {
		return mList.getFirstVisiblePosition();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	/**
	 * Sets the More listener
	 * 
	 * @param onMoreListener
	 * @param max
	 *            Number of items before loading more
	 */
	public void setupMoreListener(OnMoreListener onMoreListener, int max) {
		mOnMoreListener = onMoreListener;
		ITEM_LEFT_TO_LOAD_MORE = max;
	}

	public void setOnMoreListener(OnMoreListener onMoreListener) {
		mOnMoreListener = onMoreListener;
	}

	public void setNumberBeforeMoreIsCalled(int max) {
		ITEM_LEFT_TO_LOAD_MORE = max;
	}

	public boolean isLoadingMore() {
		return isLoadingMore;
	}

	/**
	 * Enable/Disable the More event
	 * 
	 * @param isLoadingMore
	 */
	public void setLoadingMore(boolean isLoadingMore) {
		this.isLoadingMore = isLoadingMore;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (((totalItemCount - firstVisibleItem - visibleItemCount) == ITEM_LEFT_TO_LOAD_MORE || (totalItemCount
				- firstVisibleItem - visibleItemCount) == 0
				&& totalItemCount > visibleItemCount)
				&& !isLoadingMore) {
			isLoadingMore = true;
			if (mOnMoreListener != null) {
				mMoreProgress.setVisibility(View.VISIBLE);
				mOnMoreListener.onMoreAsked(mList.getAdapter().getCount(),
						ITEM_LEFT_TO_LOAD_MORE, firstVisibleItem);
			}
		}
	}

	/**
	 * Remove the moreListener
	 */
	public void removeMoreListener() {
		mOnMoreListener = null;
	}

	public void setOnTouchListener(OnTouchListener listener) {
		mList.setOnTouchListener(listener);
	}
	
	public void setEmptyImg(int resId) {
		if (mEmptyImg != null) {
			mEmptyImg.setImageResource(resId);
		}
	}

	public void setEmptyTxt(int resId) {
		if (mEmptyTxt != null) {
			mEmptyTxt.setText(resId);
		}
	}
	
	public void setOnDisconnectClickListener(OnClickListener l) {
		if (mDisconnect != null) {
			mDisconnectListener = l;
		}
	}
}

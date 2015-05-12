/**
 * 
 */
package com.nct.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.View.OnClickListener;

import com.nct.adapter.BaseAdapterApp;
import com.nct.gridview.BaseSuperAbsListview;
import com.nct.gridview.OnMoreListener;
import thh.com.mycouper.R;

@SuppressLint("InlinedApi") abstract class BaseGridFragment<T> extends BaseMainFragment implements
		SwipeRefreshLayout.OnRefreshListener, OnMoreListener {
	
	public static final int PAGE_INDEX_DEFAULT = 0;
	public static final int PAGE_SIZE_DEFAULT = 20;

	protected BaseSuperAbsListview mAbsListview;
	private int mCurrentPageIndex = PAGE_INDEX_DEFAULT;
	
	protected boolean mIsRefreshData = true;
	private boolean mIsLockLoadmore = false;
	
	public BaseAdapterApp<T> mBaseAdapter;
	private boolean mIsDisableRefresh = false;
	private boolean mIsDisableLoadmore = false;
	private boolean mIsDisableLoading = false;
	
	private int mNodataImgResId = 0, mNodataTxtResId = 0;
	
	protected void setNoDataInfo(int imgResId, int txtResId) {
		this.mNodataImgResId = imgResId;
		this.mNodataTxtResId = txtResId;
	}
	
	protected void disableLoading() {
		this.mIsDisableLoading = true;
	}
	
	protected void disableRefresh() {
		this.mIsDisableRefresh = true;
	}

	protected void disableLoadmore() {
		this.mIsDisableLoadmore = true;
	}
	
	@SuppressLint("ResourceAsColor")
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		try {
			mAbsListview = (BaseSuperAbsListview) view
					.findViewById(R.id.nct_base_abslist);
		} catch (Exception e) {
			mAbsListview = null;
		}
		if (mAbsListview == null)
			return;

		if (!mIsDisableRefresh) {
			// Wow so beautiful
			mAbsListview.setRefreshingColor(android.R.color.holo_orange_light,
					android.R.color.holo_blue_light,
					android.R.color.holo_green_light,
					android.R.color.holo_red_light);
			// Setting the refresh listener will enable the refresh progressbar
			mAbsListview.setRefreshListener(this);
		}
		
		if (!mIsDisableLoadmore) {
			// I want to get loadMore triggered if I see the last item (1)
			mAbsListview.setupMoreListener(this, PAGE_SIZE_DEFAULT);
		}
		
		if (mIsDisableLoading) {
			mAbsListview.hideProgress();
		}
		
		if (mNodataImgResId != 0) {
			mAbsListview.setEmptyImg(mNodataImgResId);
		}
		if (mNodataTxtResId != 0) {
			mAbsListview.setEmptyTxt(mNodataTxtResId);
		}
		
		mAbsListview.setOnDisconnectClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mAbsListview.showProgress();
				onRefresh();
			}
		});
	}
	
	
	protected BaseAdapterApp<T> initAdapter(ArrayList<T> list) {
		return null;
	}
	
	protected Boolean isAdapterNull()
	{
		if(mBaseAdapter==null)
			return true;
		return false;
	}
	
	protected void setDataDefault()
	{
		if(mAbsListview!=null && mBaseAdapter!=null)
		{
			mAbsListview.setAdapter(mBaseAdapter);
			mAbsListview.showList();
		}
	}
	
	/**
	 * Set Data to Adapter
	 * 
	 * @param list
	 */
	protected void setData(ArrayList<T> list) {
		if (mBaseAdapter == null) {
			mBaseAdapter = initAdapter(list);
			mAbsListview.setAdapter(mBaseAdapter);
			return;
		}
		if (mIsRefreshData) {
			mBaseAdapter.clearData();
		} else {
			mAbsListview.hideMoreProgress();
			mIsRefreshData = true;
		}
		if (list != null) {
			mBaseAdapter.addData(list);
		}else
			mAbsListview.hideProgress();
	}

	/**
	 * Set Data with load more flag
	 * 
	 * @param list
	 * @param flag
	 */
	protected void setData(ArrayList<T> list, boolean flag) {
		allowLoadmore(flag);
		setData(list);
	}
	
	protected void allowLoadmore(boolean flag ) {
		if (!flag){
			mAbsListview.removeMoreListener();
			mIsLockLoadmore = true;
		} else if (mIsLockLoadmore) {
			mAbsListview.setOnMoreListener(this);
			mIsLockLoadmore = false;
		}
	}
	
	protected void resetPageIndex() {
		this.mCurrentPageIndex = PAGE_INDEX_DEFAULT;
	}
	
	protected void loadDataWithLoadmore(int pageIndex, int pageSize) {
	}
	
	@Override
	protected void loadData() {
		loadDataWithLoadmore(mCurrentPageIndex, PAGE_SIZE_DEFAULT);
	}
	
	@Override
	protected final void onLoadDataSuccess(String result) {
		if (!handleLoadingDataSuccess(result)) {
			if (mAbsListview != null) {
				mAbsListview.showDisconnect();
			}
		}
		mIsRefreshData = true;
	}
	
	protected abstract boolean handleLoadingDataSuccess(String result);
	
	@Override
	protected void onLoadDataFailure(String result) {
		if (mAbsListview != null) 
		{
//			ErrorData errorData = DataHelper.getErrorData(result);
//			if (errorData != null && errorData.data != null) {
//				if (errorData.data.code == ResultCode.DATA_NOT_EXIST) {
//					setData(null);
//					return;
//				}
//			}
			if(mCurrentPageIndex == PAGE_INDEX_DEFAULT && (mAbsListview.getAdapter() == null || mAbsListview.getAdapter().getCount()==0) ) 
				mAbsListview.showDisconnect();
			else
			{
				mAbsListview.hideMoreProgress();
				mAbsListview.showList();
				mAbsListview.showHideIconRefress(false);
			}
		}
		mIsRefreshData = true;
	}

	@Override
	public void onRefresh() {
		mAbsListview.showProgress();
		mIsRefreshData = true;
		resetPageIndex();
		loadData();
	}

	@Override
	public void onMoreAsked(int numberOfItems, int numberBeforeMore,
			int currentItemPos) {
		mIsRefreshData = false;
		mCurrentPageIndex++;
		loadData();
	}
	
	public void clearAdapter()
	{
		if(mBaseAdapter!=null)
		{	mBaseAdapter.clearData();
			mBaseAdapter.notifyDataSetChanged();
		}
	}
	public int getCountAdapter()
	{
		if(mBaseAdapter!=null)
			return mBaseAdapter.getCount();
		return 0;
	}
}

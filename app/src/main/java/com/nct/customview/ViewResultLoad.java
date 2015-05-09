package com.nct.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import thh.com.mycouper.R;


public class ViewResultLoad extends FrameLayout {

	protected ProgressBar mProgress;
	protected LinearLayout mDisconnect;
	protected TextView mEmpty;
	protected TextView mTvNointernet;
	protected ImageView mImgNetwork;

	protected int mEmptyId;
	protected int mDisconnectID;
	protected int mProgressId;

	private InterfaceNetworkListener mListenerClick;
	public interface InterfaceNetworkListener {
		public void onclickNetwork();
	}
	public void setListenerClick(InterfaceNetworkListener mListen)
	{
		mListenerClick = mListen;
	}

	public ViewResultLoad(Context context) {
		super(context);
		initView();
	}

	public ViewResultLoad(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public ViewResultLoad(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView() {
		if (isInEditMode()) {
			return;
		}
		View v = LayoutInflater.from(getContext()).inflate(
				R.layout.view_result_load, this);

		mProgress = (ProgressBar) v.findViewById(R.id.view_progress);
		mDisconnect = (LinearLayout) v.findViewById(R.id.view_disconnect);
		mEmpty = (TextView) v.findViewById(R.id.view_empty);
		
		mTvNointernet = (TextView) v.findViewById(R.id.view_disconnect_tv);
		mImgNetwork = (ImageView) v.findViewById(R.id.view_disconnect_img);
		mImgNetwork.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mListenerClick!=null)
					mListenerClick.onclickNetwork();
			}
		});
	}

	public void showProgress() {
		mProgress.setVisibility(View.VISIBLE);
		mDisconnect.setVisibility(View.GONE);
		mEmpty.setVisibility(View.GONE);
	}

	public void showDisconnect() {
		mDisconnect.setVisibility(View.VISIBLE);
		mProgress.setVisibility(View.GONE);
		mEmpty.setVisibility(View.GONE);
	}

	public void showEmpty() {
		mDisconnect.setVisibility(View.GONE);
		mProgress.setVisibility(View.GONE);
		mEmpty.setVisibility(View.VISIBLE);
	}

	public void hideAll() {
		mDisconnect.setVisibility(View.GONE);
		mProgress.setVisibility(View.GONE);
		mEmpty.setVisibility(View.GONE);
	}
	
	public void setTextEmpty(String text)
	{
		if(mEmpty!=null)
			mEmpty.setText(text);
	}
	public void setTextNoInternet(String text)
	{
		if(mTvNointernet!=null)
			mTvNointernet.setText(text);
	}
}

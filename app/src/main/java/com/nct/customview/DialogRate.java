package com.nct.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nct.customview.DialogCustom.FinishDialogConfirmListener;

import thh.com.mycouper.R;


public class DialogRate extends Dialog implements
		View.OnClickListener {

	private FinishDialogConfirmListener mListener;
	public void setListenerFinishedDialog(FinishDialogConfirmListener t) {
		mListener = t;
	}
	
	private TextView tvTitle;
	private String textTitle="";

	private Button bntLater;
	private Button bntRate;
	private Button bntNo;
	private int numberBnt = 3;
	private ImageView imgLogo;
	private int idIconLogo = -1;
	private String titleBnt1="",titleBnt2="",titleBnt3="";
	
	public DialogRate(Context context) {
		super(context, R.style.ThemeDialogCustom);
	}
	public DialogRate(Context context,int numberButton,String textBt1,String textBt2,String textBt3,String title) {
		super(context, R.style.ThemeDialogCustom);
		

		titleBnt1 = textBt1;
		titleBnt2 = textBt2;
		titleBnt3 = textBt3;
		numberBnt = numberButton;
		textTitle = title;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_rate);
		
		bntRate = (Button) findViewById(R.id.dialog_rate_bt_rate_now);
		bntRate.setOnClickListener(this);
		bntLater = (Button) findViewById(R.id.dialog_rate_bt_later);
		bntLater.setOnClickListener(this);
		bntNo = (Button) findViewById(R.id.dialog_rate_bt_no);
		bntNo.setOnClickListener(this);
		if(numberBnt<3)
			bntNo.setVisibility(View.GONE);
		if(numberBnt<2)
			bntLater.setVisibility(View.GONE);
		
		if(!TextUtils.isEmpty(titleBnt1))
			bntRate.setText(titleBnt1);
		if(!TextUtils.isEmpty(titleBnt2))
			bntLater.setText(titleBnt2);
		if(!TextUtils.isEmpty(titleBnt3))
			bntNo.setText(titleBnt3);
		
		tvTitle = (TextView) findViewById(R.id.dialog_rate_tvtitle);
		if(!TextUtils.isEmpty(textTitle))
		{
			tvTitle.setText(textTitle);
		}
	}

	public void exitDialog() {
		this.dismiss();
	}

	public void showDialog(int index) {
		setSelectItem(index);
		this.show();
	}

	public void setSelectItem(int i) {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_rate_bt_rate_now:
			if (mListener != null)
				mListener.onFinishConfirmDialog(0);
			this.dismiss();
			break;
		case R.id.dialog_rate_bt_later:
			if (mListener != null)
				mListener.onFinishConfirmDialog(1);
			this.dismiss();
			break;
		case R.id.dialog_rate_bt_no:
			if (mListener != null)
				mListener.onFinishConfirmDialog(2);
			this.dismiss();
			break;
		default:
			break;
		}
		this.dismiss();
	}
}

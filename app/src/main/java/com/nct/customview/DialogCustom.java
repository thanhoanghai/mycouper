package com.nct.customview;



import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import thh.com.mycouper.R;

public class DialogCustom extends Dialog implements
		View.OnClickListener {

	public interface FinishDialogConfirmListener {
		void onFinishConfirmDialog(int i);
	}

	private FinishDialogConfirmListener mListener;

	public void setListenerFinishedDialog(FinishDialogConfirmListener t) {
		mListener = t;
	}

	private Button bntCancel;
	private Button bntOk;
	private TextView tvTitle,tvDes;

	private int typeDialog = 0;
	private int TYPE_DIALOG_OK = 1;
	//private int TYPE_DIALOG_OK_CANCEL = 2;
	
	private String sTitle="",sDes="";
	
	public DialogCustom(Context context) {
        this(context, R.style.ThemeDialogCustom);
    }


	public DialogCustom(Context context, int type) {
		super(context, R.style.ThemeDialogCustom);
		typeDialog = type;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_custom);

		tvTitle = (TextView) findViewById(R.id.dialog_custom_title);
		tvDes = (TextView) findViewById(R.id.dialog_custom_des);
		bntCancel = (Button) findViewById(R.id.dialog_custom_bnt_cancel);
		bntOk = (Button) findViewById(R.id.dialog_custom_bnt_ok);
		bntCancel.setOnClickListener(this);
		bntOk.setOnClickListener(this);
		if (typeDialog == TYPE_DIALOG_OK) {
			bntCancel.setVisibility(View.GONE);
		}
		setTextViewText();
	}
	
	public void setText(String title, String des)
	{
		sTitle = title;
		sDes = des;
		setTextViewText();
	}
	
	private void setTextViewText()
	{
		if(tvTitle!=null)
		{
			if(!TextUtils.isEmpty(sTitle))
			{
				tvTitle.setText(sTitle);
				tvTitle.setVisibility(View.VISIBLE);
			}else
				tvTitle.setVisibility(View.GONE);
		}
		
		if(tvDes != null)
		{
			if(!TextUtils.isEmpty(sDes))
			{
				tvDes.setText(sDes);
				tvDes.setVisibility(View.VISIBLE);
			}else
				tvDes.setVisibility(View.GONE);
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
		case R.id.dialog_custom_bnt_cancel:
			if (mListener != null)
				mListener.onFinishConfirmDialog(0);
			this.dismiss();
			break;
		case R.id.dialog_custom_bnt_ok:
			if (mListener != null)
				mListener.onFinishConfirmDialog(1);
			this.dismiss();
			break;
		default:
			break;
		}
		this.dismiss();
	}
}

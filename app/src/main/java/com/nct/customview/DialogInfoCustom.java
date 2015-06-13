package com.nct.customview;



import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nct.constants.GlobalInstance;
import com.nct.model.UserObject;

import java.util.List;

import thh.com.mycouper.R;

public class DialogInfoCustom extends Dialog implements
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
	private LinearLayout ly1, ly2, ly3, ly4, ly5;
	private TextView txt1, txt2, txt3, txt4, txt5;

	private int typeDialog = 0;
	private int TYPE_DIALOG_OK = 1;

	private String sTitle="", sDes="";

	private UserObject userObject;

	public DialogInfoCustom(Context context) {
        this(context, R.style.ThemeDialogCustom);
    }


	public DialogInfoCustom(Context context, int type) {
		super(context, R.style.ThemeDialogCustom);
		typeDialog = type;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_info_custom);

		userObject = GlobalInstance.getInstance().userInfo;

		tvTitle = (TextView) findViewById(R.id.dialog_custom_title);
		tvDes = (TextView) findViewById(R.id.dialog_custom_des);
		bntCancel = (Button) findViewById(R.id.dialog_custom_bnt_cancel);
		bntOk = (Button) findViewById(R.id.dialog_custom_bnt_ok);
		bntCancel.setOnClickListener(this);
		bntOk.setOnClickListener(this);
		if (typeDialog == TYPE_DIALOG_OK) {
			bntCancel.setVisibility(View.GONE);
		}
		ly1 = (LinearLayout)findViewById(R.id.conttent1);
		txt1 = (TextView)findViewById(R.id.txt_text1);
		ly2 = (LinearLayout)findViewById(R.id.conttent2);
		txt2 = (TextView)findViewById(R.id.txt_text2);
		ly3 = (LinearLayout)findViewById(R.id.conttent3);
		txt3 = (TextView)findViewById(R.id.txt_text3);
		ly4 = (LinearLayout)findViewById(R.id.conttent4);
		txt4 = (TextView)findViewById(R.id.txt_text4);
		ly5 = (LinearLayout)findViewById(R.id.conttent5);
		txt5 = (TextView)findViewById(R.id.txt_text5);
		setTextViewText();
	}
	
	public void setText(String title, String des)
	{
		sTitle = title;
		sDes = des;
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

		if(userObject!=null){
		txt1.setText(userObject.first_name);
		txt2.setText(userObject.last_name);
		txt3.setText(userObject.birthday);
		txt4.setText(userObject.user_email);
		txt5.setText(userObject.phone);
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

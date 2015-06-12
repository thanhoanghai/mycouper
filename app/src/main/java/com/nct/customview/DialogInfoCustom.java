package com.nct.customview;



import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
	private LinearLayout ly1, ly2, ly3, ly4, ly5, ly6;
	private TextView txt1, txt2, txt3, txt4, txt5, txt6;

	private int typeDialog = 0;
	private int TYPE_DIALOG_OK = 1;

	private String sTitle="", sDes="";

	private List<String> listContent;
	private final int mMaxContent = 6;

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
		ly6 = (LinearLayout)findViewById(R.id.conttent6);
		txt6 = (TextView)findViewById(R.id.txt_text6);
		setTextViewText();
	}
	
	public void setText(String title, String des, List<String> listContent)
	{
		sTitle = title;
		sDes = des;
		this.listContent = listContent;
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

		if(listContent != null && listContent.size() > 0){
			for (int i = 0; i < listContent.size(); i++){
				if(i < mMaxContent){
					switch (i){
						case 0:
							if(listContent.get(i) != null){
								if(ly1 != null)
									ly1.setVisibility(View.VISIBLE);
								if(txt1 != null)
									txt1.setText(listContent.get(i));
							}
							break;
						case 1:
							if(listContent.get(i) != null){
								if(ly2 != null)
									ly2.setVisibility(View.VISIBLE);
								if(txt2 != null)
									txt2.setText(listContent.get(i));
							}
							break;
						case 2:
							if(listContent.get(i) != null){
								if(ly3 != null)
									ly3.setVisibility(View.VISIBLE);
								if(txt3 != null)
									txt3.setText(listContent.get(i));
							}
							break;
						case 3:
							if(listContent.get(i) != null){
								if(ly4 != null)
									ly4.setVisibility(View.VISIBLE);
								if(txt4 != null)
									txt4.setText(listContent.get(i));
							}
							break;
						case 4:
							if(listContent.get(i) != null){
								if(ly5 != null)
									ly5.setVisibility(View.VISIBLE);
								if(txt5 != null)
									txt5.setText(listContent.get(i));
							}
							break;
						case 5:
							if(listContent.get(i) != null){
								if(ly6 != null)
									ly6.setVisibility(View.VISIBLE);
								if(txt6 != null)
									txt6.setText(listContent.get(i));
							}
							break;
					}
				}else
					break;
			}
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

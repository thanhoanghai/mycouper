package com.nct.customview;



import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nct.constants.Constants;

import thh.com.mycouper.R;

public class DialogNation extends Dialog implements
		View.OnClickListener {

	public interface FinishDialogConfirmListener {
		void onFinishConfirmDialog(int i);
	}

	private FinishDialogConfirmListener mListener;

	public void setListenerFinishedDialog(FinishDialogConfirmListener t) {
		mListener = t;
	}

	private TextView bntCancel;

	public DialogNation(Context context) {
		super(context, R.style.ThemeDialogCustom);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_nation);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_nation_tv_cancel:
			if (mListener != null)
				mListener.onFinishConfirmDialog(0);
			this.dismiss();
			break;
		default:
			break;
		}
		this.dismiss();
	}
}

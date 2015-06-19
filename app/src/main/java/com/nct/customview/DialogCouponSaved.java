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
import android.widget.ListView;
import android.widget.TextView;

import com.nct.adapter.DialogNationAdapter;
import com.nct.model.CountryObject;

import java.util.ArrayList;

import thh.com.mycouper.R;

public class DialogCouponSaved extends Dialog implements
		View.OnClickListener {

	public interface FinishDialogConfirmListener {
		void onFinishConfirmDialog(int i);
	}
	private FinishDialogConfirmListener mListener;
	public void setListenerFinishedDialog(FinishDialogConfirmListener t) {
		mListener = t;
	}

	private TextView tvSerial,tvValidFrome,tvValidTo;
	private String sSerial,sValidFrome,sValidTo;
	private Button bntOk;

	private Context mContext;

	public DialogCouponSaved(Context context, String seri,String from,String to) {
		super(context, R.style.ThemeDialogCustom);
		mContext = context;
		sSerial = seri;
		sValidFrome = from;
		sValidTo = to;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_coupon_saved);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);


		tvSerial = (TextView) findViewById(R.id.dialog_coupoin_saved_tv_serials);
		tvValidFrome = (TextView) findViewById(R.id.dialog_coupoin_saved_tv_validfrome);
		tvValidTo = (TextView) findViewById(R.id.dialog_coupoin_saved_tv_validto);

		if(!TextUtils.isEmpty(sSerial))
			tvSerial.setText(sSerial);
		if(!TextUtils.isEmpty(sValidFrome))
			tvValidFrome.setText(mContext.getString(R.string.valid_from) + " : " + sValidFrome);
		if(!TextUtils.isEmpty(sValidTo))
			tvValidTo.setText(mContext.getString(R.string.valid_to) + " : " + sValidTo);

		bntOk = (Button) findViewById(R.id.dialog_coupoin_saved_bt_ok);
		bntOk.setOnClickListener(this);

	}

	public void setTitle(String seri,String from,String to)
	{
		sSerial = seri;
		sValidFrome = from;
		sValidTo = to;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_coupoin_saved_bt_ok:
			if (mListener != null)
				mListener.onFinishConfirmDialog(0);
			break;
		default:
			break;
		}
		this.dismiss();
	}
}

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
import android.widget.ListView;
import android.widget.TextView;

import com.nct.adapter.DialogNationAdapter;
import com.nct.constants.Constants;
import com.nct.model.CountryObject;

import org.w3c.dom.Text;

import java.util.ArrayList;

import thh.com.mycouper.R;

public class DialogNation extends Dialog implements
		View.OnClickListener {

	public interface FinishDialognation {
		void onFinishConfirmDialog(String idCountry);
	}

	private FinishDialognation mListener;

	public void setListenerFinishedDialog(FinishDialognation t) {
		mListener = t;
	}

	private TextView bntCancel;
	private ListView lv;
	private DialogNationAdapter adapter;

	public DialogNation(Context context,ArrayList<CountryObject> list) {
		super(context, R.style.ThemeDialogCustom);
		adapter = new DialogNationAdapter(context,list);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_nation);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		bntCancel = (TextView) findViewById(R.id.dialog_nation_tv_cancel);
		bntCancel.setOnClickListener(this);

		lv = (ListView) findViewById(R.id.dialog_nation_lv);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				clickItem(position);
			}
		});
	}

	private void clickItem(int pos)
	{
		adapter.setIndexSelect(pos);
		mListener.onFinishConfirmDialog(adapter.getIDCountry(pos));
		this.dismiss();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_nation_tv_cancel:
			break;
		default:
			break;
		}
		this.dismiss();
	}
}

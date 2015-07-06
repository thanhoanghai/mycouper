package com.nct.customview;



import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.nct.adapter.DialogNationAdapter;
import com.nct.adapter.DialogPosNameAdapter;
import com.nct.model.CountryObject;

import java.util.ArrayList;

import thh.com.mycouper.R;

public class DialogPosName extends Dialog implements
		View.OnClickListener {

	public interface FinishDialognation {
		void onFinishConfirmDialog(int index);
	}

	private FinishDialognation mListener;

	public void setListenerFinishedDialog(FinishDialognation t) {
		mListener = t;
	}

	private TextView tvtitle;
	private TextView bntCancel;
	private ListView lv;
	private DialogPosNameAdapter adapter;
	private String nameTitle;
	public DialogPosName(Context context, ArrayList<String> list,String name) {
		super(context, R.style.ThemeDialogCustom);
		adapter = new DialogPosNameAdapter(context,list);
		nameTitle = name;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_pos_name);
		getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		tvtitle = (TextView) findViewById(R.id.dialog_pos_name_tv_title);

		bntCancel = (TextView) findViewById(R.id.dialog_pos_name_tv_cancel);
		bntCancel.setOnClickListener(this);

		lv = (ListView) findViewById(R.id.dialog_pos_name_lv);
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
		if(adapter!=null) {
			adapter.setIndexSelect(pos);
			if(mListener!=null)
				mListener.onFinishConfirmDialog(pos);
			this.dismiss();
		}
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

	public void setIndexItem(int pos)
	{
		if(adapter!=null)
			adapter.setIndexSelect(pos);
	}
}

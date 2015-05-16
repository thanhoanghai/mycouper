package com.nct.mv;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.TextHttpResponseHandler;
import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nct.dataloader.DataHelper;
import com.nct.dataloader.DataLoader;
import com.nct.dataloader.URLProvider;
import com.nct.model.StatusObject;
import com.nct.utils.Debug;
import com.nct.utils.Utils;

import org.apache.http.Header;
import org.w3c.dom.Text;

import thh.com.mycouper.R;

public class AtChangePass extends AtBase {
	private static final String tag = "AtChangePass";

	private Button bntClose,bntChange;

	private EditText edtCurrentpass,edtNewpass,edtConfirmpass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_change_pass);

		initTopbar(getString(R.string.change_pass));
		setTopbarBtLeftImage(R.drawable.icon_back);
		setTopbarLeftBtListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		setTopbarBtRightVisible(View.INVISIBLE);

		edtCurrentpass = (EditText) findViewById(R.id.change_pass_edt_currentpass);
		edtNewpass = (EditText) findViewById(R.id.change_pass_edt_newpass);
		edtConfirmpass = (EditText) findViewById(R.id.change_pass_edt_confirm);

		bntChange = (Button)findViewById(R.id.change_pass_bt_change);
		bntChange.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updateNewPass();
			}
		});
		bntClose = (Button) findViewById(R.id.change_pass_bt_close);
		bntClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void updateNewPass()
	{
		String newp = edtNewpass.getText().toString();
		String confp = edtConfirmpass.getText().toString();
		if(TextUtils.isEmpty(newp) || TextUtils.isEmpty(confp))
		{
			Debug.toast(AtChangePass.this,getString(R.string.change_pass_enter_new_password));
			return;
		}
		if(newp.equals(confp))
		{
			showDialogLoading();
			Utils.keyBoardForceHide(AtChangePass.this);
			DataLoader.postParam(URLProvider.getParamsChangpassword(GlobalInstance.getInstance().getUserID(),newp), new TextHttpResponseHandler() {
				@Override
				public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
					hideDialogLoading();
				}

				@Override
				public void onSuccess(int i, Header[] headers, String s) {
					hideDialogLoading();
					StatusObject object = DataHelper.getStatusObject(s);
					if(object.statusCode == Constants.STATUS_CODE_OK)
					{
						Debug.toast(AtChangePass.this,getString(R.string.change_pass_your_pass_have_been_update));
					}
					Debug.toast(AtChangePass.this,s);
				}
			});
		}else
		{
			Debug.toast(AtChangePass.this, getString(R.string.change_pass_confirm_pass_not_match));
		}

	}
}

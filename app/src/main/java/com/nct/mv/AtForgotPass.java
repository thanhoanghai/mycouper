package com.nct.mv;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.TextHttpResponseHandler;
import com.nct.dataloader.DataHelper;
import com.nct.dataloader.DataLoader;
import com.nct.dataloader.URLProvider;
import com.nct.model.StatusObject;
import com.nct.utils.Debug;
import com.nct.utils.Utils;

import org.apache.http.Header;

import thh.com.mycouper.R;

public class AtForgotPass extends AtBase {

    private static final String tag = "AtForgotPass";

	private EditText edtEmail;
	private Button btResend;

	private TextView btAlready,btLogin;
    private String email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_forgot_pass);

		edtEmail = (EditText) findViewById(R.id.forgot_edt_email);
        edtEmail.setText("hoang.test.001@gmail.com");
		btResend = (Button) findViewById(R.id.forgot_bt_resend);
        btResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendForgotPass();
            }
        });

		btAlready = (TextView) findViewById(R.id.forgot_bt_already);
		btAlready.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btLogin = (TextView) findViewById(R.id.forgot_bt_login);
		btLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

    private void sendForgotPass()
    {
        Utils.keyBoardForceHide(AtForgotPass.this);
        showDialogLoading();
        email = edtEmail.getText().toString();
        if(!TextUtils.isEmpty(email)) {
            DataLoader.postParam(URLProvider.getParamForgotPass(email), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    hideDialogLoading();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    Debug.logData(tag, s);
                    StatusObject item = DataHelper.getStatusObject(s);
                    Debug.toast(AtForgotPass.this,item.errorMessage);
                    hideDialogLoading();
                }
            });
        }
    }
}

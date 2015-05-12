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

public class AtSignUp extends AtBase {

    private Button bntSigup;
    private TextView btAlready,btLogin;
    private EditText edtEmail,edtPass,edtConfirm;

    private String sEmail,sPass,sConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_sign_up);

        edtEmail = (EditText) findViewById(R.id.signup_edt_email);
        edtPass = (EditText) findViewById(R.id.signup_edt_password);
        edtConfirm = (EditText) findViewById(R.id.signup_edt_confirm);

        bntSigup = (Button) findViewById(R.id.signup_bt_signup);
        bntSigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signNewAccount();
            }
        });

        btAlready = (TextView) findViewById(R.id.signup_bt_already);
        btAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btLogin = (TextView) findViewById(R.id.signup_bt_login);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void signNewAccount()
    {
        sEmail = edtEmail.getText().toString();
        sPass = edtPass.getText().toString();
        sConfirm = edtConfirm.getText().toString();

        if(!TextUtils.isEmpty(sPass) && !TextUtils.isEmpty(sEmail) && !TextUtils.isEmpty(sConfirm) && sPass.equals(sConfirm))
        {
            Utils.keyBoardForceHide(AtSignUp.this);
            showDialogLoading();
            DataLoader.postParam(URLProvider.getSignUp(sEmail, sPass), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    hideDialogLoading();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    hideDialogLoading();
                    StatusObject item = DataHelper.getStatusObject(s);
                    Debug.toast(AtSignUp.this,item.errorMessage);
                }
            });
        }
    }
}

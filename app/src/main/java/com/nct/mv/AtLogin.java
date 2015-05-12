package com.nct.mv;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.TextHttpResponseHandler;
import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nct.dataloader.DataHelper;
import com.nct.dataloader.DataLoader;
import com.nct.dataloader.URLProvider;
import com.nct.model.UserData;
import com.nct.utils.Debug;
import com.nct.utils.Utils;

import org.apache.http.Header;

import thh.com.mycouper.R;

public class AtLogin extends AtBase {

    private static final String tag = "AtLogin";

    private EditText edtUsername, edtPass;
    private Button btLogin, btLoginFace;
    private TextView btForgot, btSignup;
    private String sEmail,sPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_login);

        edtUsername = (EditText) findViewById(R.id.login_edt_username);
        edtUsername.setText("hoang.test.001@gmail.com");
        edtPass = (EditText) findViewById(R.id.login_edt_password);
        edtPass.setText("123456");

        btLogin = (Button) findViewById(R.id.login_bt_login);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin();
            }
        });
        btLoginFace = (Button) findViewById(R.id.login_bt_login_fb);
        btLoginFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btForgot = (TextView) findViewById(R.id.login_bt_forgotpass);
        btForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.gotoScreenForgotPass(AtLogin.this);
            }
        });
        btSignup = (TextView) findViewById(R.id.login_bt_signup);
        btSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.gotoScreenSignUp(AtLogin.this);
            }
        });
    }

    private void checkLogin()
    {
        sEmail = edtUsername.getText().toString();
        sPass = edtPass.getText().toString();
        getLogin();
    }

    private void getLogin()
    {
        
        showDialogLoading();
        DataLoader.postParam(URLProvider.getParamLogin(sEmail, sPass), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                hideDialogLoading();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                UserData object = DataHelper.getUserData(s);
                if(object!=null && object.statusCode == Constants.STATUS_CODE_OK)
                {
                    GlobalInstance.getInstance().userInfo = object.data;
                    Debug.logData(tag,object.data.user_email);
                }
                hideDialogLoading();
                Utils.gotoScreenMain(AtLogin.this);
            }
        });
    }
}

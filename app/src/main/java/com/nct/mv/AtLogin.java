package com.nct.mv;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nct.dataloader.DataHelper;
import com.nct.dataloader.DataLoader;
import com.nct.dataloader.URLProvider;
import com.nct.model.UserData;
import com.nct.utils.Debug;

import com.nct.utils.Pref;
import com.nct.utils.Device;
import com.nct.utils.Utils;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

import org.apache.http.Header;

import thh.com.mycouper.R;

public class AtLogin extends AtBase {

    private static final String tag = "AtLogin";

    private EditText edtUsername, edtPass;
    private Button btLogin, btLoginFace;
    private TextView btForgot, btSignup;
    private String sEmail,sPass;
    private SimpleFacebook mSimpleFacebook;

    private enum TYPE_LOGIN {LoginEmail, LoginFacebook};

    private TYPE_LOGIN typeLogin = TYPE_LOGIN.LoginEmail;

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(AtLogin.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Debug.logError(tag, "result code " + resultCode);
        mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_login);

        new Device(this);

        setupFonts();

        edtUsername = (EditText) findViewById(R.id.login_edt_username);
        edtUsername.setText("hoang.test.001@gmail.com");
        edtPass = (EditText) findViewById(R.id.login_edt_password);
        edtPass.setText("1234567xx");

        btLogin = (Button) findViewById(R.id.login_bt_login);
        btLogin.setTypeface(mRoboLight);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeLogin = TYPE_LOGIN.LoginEmail;
                String result = checkLogin();
                if (result.equals(""))
                    getLogin();
                else
                    Debug.toast(AtLogin.this, result);
            }
        });
        btLoginFace = (Button) findViewById(R.id.login_bt_login_fb);
        btLoginFace.setTypeface(mRoboLight);
        btLoginFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                typeLogin = TYPE_LOGIN.LoginFacebook;
                loginFacebook();
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

        String jsonLogin = Pref.getStringObject(Constants.ID_SAVE_LOGIN,AtLogin.this);
        if(!TextUtils.isEmpty(jsonLogin))
        {
            handleLogin(jsonLogin);
        }

    }

    private String checkLogin(){
        String result = "";
        sEmail = edtUsername.getText().toString();
        sPass = edtPass.getText().toString();
        if(TextUtils.isEmpty(sEmail)){
            return result = getResources().getString(R.string.login_message_user_is_empty);
        }
        if(TextUtils.isEmpty(sPass)){
            return result = getResources().getString(R.string.login_message_pass_is_empty);
        }
        return result;
    }

    private void getLogin()
    {
        RequestParams params;
        if(typeLogin == TYPE_LOGIN.LoginEmail)
            params = URLProvider.getParamLogin(sEmail, sPass);
        else
            params = URLProvider.getParamLoginFacebook(sEmail);

        Utils.keyBoardForceHide(AtLogin.this);
        showDialogLoading();
        DataLoader.postParam(params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                hideDialogLoading();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Debug.logData(tag,s);
                Pref.SaveStringObject(Constants.ID_SAVE_LOGIN, s, AtLogin.this);
                handleLogin(s);
                hideDialogLoading();

            }
        });
    }

    private void handleLogin(String s)
    {
        UserData object = DataHelper.getUserData(s);
        if (object != null && object.statusCode == Constants.STATUS_CODE_OK) {
            GlobalInstance.getInstance().userInfo = object.data;
            Utils.gotoScreenMain(AtLogin.this);
            Debug.toastDebug(AtLogin.this,GlobalInstance.getInstance().getSessionID());
            finish();
        }else
            Debug.toast(AtLogin.this,object.errorMessage);
    }

    private void loginFacebook()
    {
        mSimpleFacebook.login(onLoginListener);
    }
    final OnLoginListener onLoginListener = new OnLoginListener() {
        @Override
        public void onFail(String reason) {
            Debug.logError(tag, "Failed to login");
        }

        @Override
        public void onException(Throwable throwable) {
            Debug.logError(tag, "Bad thing happened");
        }

        @Override
        public void onThinking() {
            // show progress bar or something to the user while login is
            // happening
            Debug.logError(tag, "onThinking");
        }

        @Override
        public void onLogin() {
            // change the state of the button or do whatever you want
            Debug.logError(tag, "login succeed");
            getProfileFacebook();
        }

        @Override
        public void onNotAcceptingPermissions(Permission.Type type) {
            Debug.toast(
                    AtLogin.this,
                    String.format("You didn't accept %s permissions"));
        }
    };

    private void getProfileFacebook()
    {
        showDialogLoading();
        SimpleFacebook.getInstance().getProfile(new OnProfileListener() {
            @Override
            public void onThinking() {
            }

            @Override
            public void onException(Throwable throwable) {
            }

            @Override
            public void onFail(String reason) {
                hideDialogLoading();
            }

            @Override
            public void onComplete(Profile response) {
                Debug.logError(tag, "info onComplete");
                //fbuserid = response.getId();
                // fbavatar = "https://graph.facebook.com/" + fbuserid+
                // "/picture?type=normal";
                //fbfullname = response.getLastName() + response.getFirstName();// /response.getName();
                //fbaccesskey = mSimpleFacebook.getSession().getAccessToken();
                sEmail = response.getEmail();
                //indexLogin = Constants.TYPE_USER_FACEBOOK;
                //callLogin(Constants.TYPE_LOGIN_ARRAY[indexLogin]);
                getLogin();
            }
        });
    }
}

package com.nct.mv;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.TextHttpResponseHandler;
import com.nct.constants.GlobalInstance;
import com.nct.dataloader.DataHelper;
import com.nct.dataloader.DataLoader;
import com.nct.dataloader.URLProvider;
import com.nct.model.StatusObject;
import com.nct.model.UserObject;
import com.nct.utils.Debug;
import com.nct.utils.Utils;

import org.apache.http.Header;
import org.json.JSONObject;

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
                String result = checkInvalidValue();
                if(result.equals(""))
                    signNewAccount();
                else
                    Debug.toast(AtSignUp.this, result);
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

    private void signNewAccount(){
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
                String mUserID = "";
                try {
                    JSONObject object = new JSONObject(s);
                    JSONObject obj = object.getJSONObject("data");
                    mUserID = obj.optString("user_id");

                }catch (Exception e){
                    e.printStackTrace();
                }

                StatusObject item = DataHelper.getStatusObject(s);
                Debug.toast(AtSignUp.this,item.errorMessage);
                UserObject data = new UserObject("", sEmail);
                GlobalInstance.getInstance().userInfo = data;
                Utils.gotoScreenMain(AtSignUp.this);
                finish();

//                {"statusCode":"200","data":{"user_id":268}}
            }
        });
    }

    private String checkInvalidValue(){
        String result = "";
        sEmail = edtEmail.getText().toString();
        sPass = edtPass.getText().toString();
        sConfirm = edtConfirm.getText().toString();
        if(TextUtils.isEmpty(sEmail))
            return result = getResources().getString(R.string.login_message_user_is_empty);
        if(!Utils.isValidEmail(sEmail))
            return result = getResources().getString(R.string.login_message_user_invalid);
        if(TextUtils.isEmpty(sPass))
            return result = getResources().getString(R.string.login_message_pass_is_empty);
        if(!sConfirm.equals(sPass))
            return result = getResources().getString(R.string.login_message_confirm_pass_incorrect);

        return result;
    }
}

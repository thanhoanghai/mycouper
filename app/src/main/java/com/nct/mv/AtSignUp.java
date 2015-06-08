package com.nct.mv;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.nct.model.StatusObject;
import com.nct.model.UserObject;
import com.nct.utils.Debug;
import com.nct.utils.Pref;
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

                int statusCode = 0;
                String errorMessage = null;
                String user_id = "";
                try {
                    JSONObject object = new JSONObject(s);
                    statusCode = object.optInt("statusCode");
                    JSONObject json = object.getJSONObject("data");
                    user_id = json.optString("user_id");
                    errorMessage = object.optString("errorMessage");
                }catch (Exception e){
                    e.printStackTrace();
                }
                hideDialogLoading();
                if(statusCode == Constants.API_REQUEST_STATUS_SUCCESS){
                    Pref.SaveStringObject(Constants.ID_SAVE_LOGIN, s, AtSignUp.this);
                    StatusObject item = DataHelper.getStatusObject(s);
                    Debug.toast(AtSignUp.this, getResources().getString(R.string.signup_success));
                    UserObject data = new UserObject(user_id, sEmail);
                    GlobalInstance.getInstance().userInfo = data;
                    Utils.gotoScreenMain(AtSignUp.this);
                    finish();
                }else{
                    if(errorMessage != null && !errorMessage.equals(""))
                        Debug.toast(AtSignUp.this, errorMessage);
                    else
                        Debug.toast(AtSignUp.this, getResources().getString(R.string.signup_failed));
                }
//                {"statusCode":"500","error":"user_email","errorMessage":"User email already exist"}
//                {"statusCode":"200","data":{"user_id":273}}
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

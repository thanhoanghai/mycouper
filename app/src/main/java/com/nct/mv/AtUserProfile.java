package com.nct.mv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.TextHttpResponseHandler;
import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nct.customview.DialogCustom;
import com.nct.customview.PopupActionItem;
import com.nct.customview.QuickAction;
import com.nct.dataloader.DataLoader;
import com.nct.dataloader.URLProvider;
import com.nct.model.UserObject;
import com.nct.utils.CouperDateTextWatcher;
import com.nct.utils.Debug;
import com.nct.utils.Pref;
import com.nct.utils.Utils;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import thh.com.mycouper.R;

public class AtUserProfile extends AtBase {

    private final String[] titleGender = {"Mrs", "Ms", "Mr"};

	private static final String tag = "AtUserProfile";

	private EditText tvFistname,tvLastname,tvBirth,tvPhone;
    private TextView tvLastLogin, tvcivility;
	private TextView tvEmail,tvPass;
	private UserObject userObject;
    private LinearLayout conTentGender;

	private Button bntLogout;
    private boolean isEdit = false;
    private boolean isEditing = false;

    private String firstName, lastName, birthDay, civility, phoneNumber;

    private QuickAction quickAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_user_profile);

		setLanguge();
		initTopbar(getString(R.string.user_account));
		setTopbarBtLeftImage(R.drawable.icon_back);
		setTopbarBtRightVisible(View.GONE);
		setTopbarLeftBtListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setTopbarTxtRightVisible(View.VISIBLE);
        setTopbarTxtRightTitle(getResources().getString(R.string.edit));
        setTopbarRightTxtListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditing) {
                    if (!isEdit) {
                        isEdit = true;
                        setTopbarTxtRightTitle(getResources().getString(R.string.done));
                        setControlsEnable(isEdit);
                    } else {
                        isEditing = true;
                        isEdit = false;
                        setTopbarTxtRightTitle(getResources().getString(R.string.edit));
                        setControlsEnable(isEdit);
                        updateProfile();
                    }
                }
            }
        });

        civility = "2";
        innitPopup();

		userObject = GlobalInstance.getInstance().userInfo;

		tvEmail = (TextView) findViewById(R.id.user_account_email);
		tvPass = (TextView) findViewById(R.id.user_account_password);

		tvFistname = (EditText) findViewById(R.id.user_account_firstname);
		tvLastname = (EditText) findViewById(R.id.user_account_lastname);
		tvBirth = (EditText) findViewById(R.id.user_account_birthday);
        conTentGender = (LinearLayout) findViewById(R.id.contentcivility);
		tvcivility = (TextView) findViewById(R.id.user_account_civility);
        tvcivility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quickAction.show(v);
            }
        });
        conTentGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quickAction.show(tvcivility);
            }
        });
		tvPhone = (EditText) findViewById(R.id.user_account_phone);
		tvLastLogin = (TextView) findViewById(R.id.user_account_lastlogin);

        setControlsEnable(isEdit);

        tvBirth.addTextChangedListener(new CouperDateTextWatcher(tvBirth));

		bntLogout = (Button) findViewById(R.id.user_account_bt_logout);
		bntLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                showDialogConfirmDelete();
			}
		});

		tvPass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utils.gotoScreenChangepass(AtUserProfile.this);
			}
		});

		setData();
	}

    private void showDialogConfirmDelete(){
        DialogCustom dialog = new DialogCustom(AtUserProfile.this);
        dialog.setText(getString(R.string.confirm),getString(R.string.do_you_want_logout));
        dialog.setListenerFinishedDialog(new DialogCustom.FinishDialogConfirmListener() {
            @Override
            public void onFinishConfirmDialog(int i) {
                if (i == 1){
                    Pref.SaveStringObject(Constants.ID_SAVE_LOGIN,"",AtUserProfile.this);
                    Intent intent = new Intent(AtUserProfile.this, AtLogin.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Debug.toast(AtUserProfile.this, getResources().getString(R.string.logout_success));
                    finish();
                }
            }
        });
        dialog.show();
    }

    private void setControlsEnable(boolean isEnable){
        tvFistname.setEnabled(isEnable);
        tvLastname.setEnabled(isEnable);
        tvBirth.setEnabled(isEnable);
        tvcivility.setClickable(isEnable);
        conTentGender.setClickable(isEnable);
//        tvcivility.setEnabled(isEnable);
        tvPhone.setEnabled(isEnable);
    }

	private void setData()
	{
        civility = userObject.civility;
        if(civility == null || civility.equals(""))
            civility = "2";
		tvFistname.setText(userObject.first_name);
		tvLastname.setText(userObject.last_name);
		tvBirth.setText(userObject.birthday);

		tvPhone.setText(userObject.phone);
		tvLastLogin.setText(userObject.last_login_time);
		tvEmail.setText(userObject.user_email);
        String temp = "";
        if(civility.equals("2")){//MALE
            temp = titleGender[0];
            civility = "2";
        }else if(civility.equals("3")){//FEMALE
            temp = titleGender[1];
            civility = "3";
        }else if(civility.equals("4")){//FEMALE
            temp = titleGender[2];
            civility = "4";
        }else{
            temp = titleGender[0];
            civility = "2";
        }
        tvcivility.setText(temp);
	}

    private void updateProfile(){
        Utils.keyBoardForceHide(AtUserProfile.this);
        showDialogLoading();

        firstName = tvFistname.getText().toString();
        lastName = tvLastname.getText().toString();
        birthDay = tvBirth.getText().toString();
        phoneNumber = tvPhone.getText().toString();

        DataLoader.postParam(URLProvider.updateProfile(GlobalInstance.getInstance().userInfo.user_id, firstName, lastName, phoneNumber, civility, birthDay),
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        isEditing = false;
                        hideDialogLoading();
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        isEditing = false;
                        hideDialogLoading();
                        String lastUpdate = "" + System.currentTimeMillis();

                        int statusCode = 0;
                        try {
                            JSONObject object = new JSONObject(s);
                            statusCode = object.optInt("statusCode");
                            if (statusCode == Constants.API_REQUEST_STATUS_SUCCESS) {
                                UserObject itemUser = new UserObject(GlobalInstance.getInstance().userInfo.user_id, GlobalInstance.getInstance().userInfo.user_email,
                                        firstName, lastName, birthDay, phoneNumber, civility, lastUpdate);
                                userObject = itemUser;
                                GlobalInstance.getInstance().userInfo = itemUser;
                                Debug.toast(AtUserProfile.this, getResources().getString(R.string.user_account_update_success));
                            } else
                                Debug.toast(AtUserProfile.this, getResources().getString(R.string.user_account_update_failed));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void innitPopup(){
        quickAction = new QuickAction(AtUserProfile.this, QuickAction.VERTICAL);
        quickAction.setStatusBackround(false);
        List<PopupActionItem> listItem = new ArrayList<PopupActionItem>();
        for (int i = 0; i < titleGender.length; i++){
            listItem.add(new PopupActionItem(i, titleGender[i], null));
            quickAction.addActionItem(listItem.get(i));
        }

        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                switch (actionId){
                    case 0:
                        civility = "2";
                        break;
                    case 1:
                        civility = "3";
                        break;
                    case 2:
                        civility = "4";
                        break;
                }

                tvcivility.setText(titleGender[actionId]);
            }
        });


    }

}

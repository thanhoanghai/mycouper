package com.nct.mv;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import thh.com.mycouper.R;

public class AtSignUp extends AtBase {

    private Button bntSigup;
    private TextView btAlready,btLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_sign_up);

        bntSigup = (Button) findViewById(R.id.signup_bt_signup);
        bntSigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
}

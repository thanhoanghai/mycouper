package com.nct.dataloader;


import com.loopj.android.http.RequestParams;

public class URLProvider {


    public static final String PROVIDER = "http://mycouper.com/api/";


    public static RequestParams getParamLogin(String email,String password)
    {
        RequestParams params = new RequestParams();
        params.put("ac", "login");
        params.put("email", email);
        params.put("password", password);
        return params;
    }

    public static RequestParams getParamForgotPass(String email)
    {
        RequestParams params = new RequestParams();
        params.put("ac", "reset_password");
        params.put("email", email);
        return params;
    }

}
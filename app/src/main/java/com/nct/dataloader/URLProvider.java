package com.nct.dataloader;


import com.loopj.android.http.RequestParams;

public class URLProvider {

    private static final String TAG = "URLProvider";
    public static final String PROVIDER = "http://mycouper.com/api/";


    public static RequestParams getParamLogin(String email,String password)
    {
        RequestParams params = new RequestParams();
        params.put("ac", "login");
        params.put("email", email);
        params.put("password", password);
        return params;
    }

}
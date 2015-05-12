package com.nct.dataloader;


import com.loopj.android.http.RequestParams;

public class URLProvider {


    public static final String PROVIDER = "http://mycouper.com/api/";
    public static String getCompanyMemberCard() {
        String client = PROVIDER;
        try {
            client += "?ac=get_company_member_card" ;
            return client;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return client;
    }



    public static String getMemberCardForUser(String user_id) {
        String client = PROVIDER;
        try {
            client += "?ac=get_member_card_for_user" ;
            client += "&user_id=" + user_id ;
            return client;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return client;
    }

    public static RequestParams getParamLogin(String email,String password)
    {
        RequestParams params = new RequestParams();
        params.put("ac", "login");
        params.put("email", email);
        params.put("password", password);
        return params;
    }

    public static RequestParams getParamLoginFacebook(String email)
    {
        RequestParams params = new RequestParams();
        params.put("ac", "login");
        params.put("facebook", email);
        return params;
    }

    public static RequestParams getParamForgotPass(String email)
    {
        RequestParams params = new RequestParams();
        params.put("ac", "reset_password");
        params.put("email", email);
        return params;
    }

    public static RequestParams getSignUp(String user_email,String password)
    {
        RequestParams params = new RequestParams();
        params.put("ac", "create_user");
        params.put("user_email", user_email);
        params.put("password", password);
        return params;
    }

}
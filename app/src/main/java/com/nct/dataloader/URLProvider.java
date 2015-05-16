package com.nct.dataloader;


import com.loopj.android.http.RequestParams;
import com.nct.utils.Debug;

import java.io.File;
import java.io.FileNotFoundException;

public class URLProvider {

    public static final String tag = "URLProvider";
    public static final String PROVIDER = "http://mycouper.com/api/";


    public static RequestParams getParamsCreateCardWithCompanyByCategory(String user_id,String company_id,String member_card_name,String member_card_number,String front_of_the_card,String back_of_the_card,String description,String card_number_type)
    {
        RequestParams params = new RequestParams();
        params.put("ac", "create_member_card_by_category");
        params.put("user_id", user_id);
        params.put("company_id", company_id);
        params.put("member_card_name", member_card_name);
        params.put("member_card_number", member_card_number);
        params.put("front_of_the_card", front_of_the_card);
        params.put("back_of_the_card", back_of_the_card);
        params.put("description", description);
        params.put("card_number_type", description);

        return params;
    }


    public static RequestParams getParamsChangpassword(String user_id,String new_password)
    {
        RequestParams params = new RequestParams();
        params.put("ac", "change_user_info");
        params.put("user_id", user_id);
        params.put("new_password", new_password);
        return params;
    }


    public static final String PROVIDER_UPLOAD_IMAGE = "http://media.mycouper.com/api.php?task=up";
    public static RequestParams getParamsUploadImage(String pathImage) {
        File myFile = new File(pathImage);
        RequestParams params = new RequestParams();
        try {
            params.put("file", myFile);
        } catch(FileNotFoundException e) {}
        return params;
    }


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
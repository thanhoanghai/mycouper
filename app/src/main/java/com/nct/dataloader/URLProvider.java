package com.nct.dataloader;


import android.content.Context;
import android.webkit.MimeTypeMap;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.nct.utils.HttpsUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class URLProvider {

    public static final String tag = "URLProvider";
    public static final String PROVIDER = "http://mycouper.com/api/";

    public static RequestParams getParamsCreateCardWithCompanyByCategory(String user_id, String company_id, String member_card_name, String member_card_number, String front_of_the_card, String back_of_the_card, String description, String card_number_type)
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
        params.put("card_number_type", card_number_type);

        return params;
    }

    public static RequestParams getParamsUpdateEcouponActivie(String coupon_id)
    {
        RequestParams params = new RequestParams();
        params.put("ac", "update_ecoupon_active");
        params.put("coupon_id", coupon_id);

        return params;
    }
    public static RequestParams getParamsUpdateEcouponDelete(String coupon_id)
    {
        RequestParams params = new RequestParams();
        params.put("ac", "update_ecoupon_deleted");
        params.put("coupon_id", coupon_id);

        return params;
    }

    public static RequestParams getParamsCreateCardWithUser(String user_id, String company_name, String member_card_name, String member_card_number, String front_of_the_card, String back_of_the_card, String description, String card_number_type)
    {
        RequestParams params = new RequestParams();
        params.put("ac", "create_member_card_by_user");
        params.put("user_id", user_id);
        params.put("company_name", company_name);
        params.put("member_card_name", member_card_name);
        params.put("member_card_number", member_card_number);
        params.put("front_of_the_card", front_of_the_card);
        params.put("back_of_the_card", back_of_the_card);
        params.put("description", description);
        params.put("card_number_type", card_number_type);

        return params;
    }

    public static RequestParams getParamsUpdateCard(String user_id, String cardID, String note, String company_name, String member_card_name, String front_of_the_card, String back_of_the_card, String description)
    {
        RequestParams params = new RequestParams();
        params.put("ac", "change_member_card");
        params.put("member_card_id", cardID);
        params.put("note", note);
        params.put("member_card_name", member_card_name);
        params.put("description", description);
        params.put("front_of_the_card", front_of_the_card);
        params.put("back_of_the_card", back_of_the_card);
        params.put("company_name", company_name);
        params.put("user_id", user_id);
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


    public static RequestParams getParamsDeleteMemberCard(int member_card_id)
    {
        RequestParams params = new RequestParams();
        params.put("ac", "delete_member_card");
        params.put("member_card_id", member_card_id);
        return params;
    }


    public static final String PROVIDER_UPLOAD_IMAGE = "http://media.mycouper.com/api.php?task=up";
    public static RequestParams getParamsUploadImage(String pathImage) {
        File myFile = new File(pathImage);
        RequestParams params = new RequestParams();
        try {
            params.put("file", myFile);
//            params.put("file",new FileInputStream(myFile));
        } catch(FileNotFoundException e) {}
        return params;
    }

    public static String postPhoto(String nameImage, ByteArrayOutputStream baosphoto){
        String result = null;

        String mime_type = "multipart/form-data";
        HttpClient httpClient = HttpsUtils.getInstance().sslClient(new DefaultHttpClient());
        HttpPost httppost = new HttpPost(PROVIDER_UPLOAD_IMAGE);
        try {
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart(new FormBodyPart("file", new ByteArrayBody(baosphoto.toByteArray(), "image/jpeg", nameImage)));
            httppost.setEntity(reqEntity);

            HttpResponse response = httpClient.execute(httppost);
            result = EntityUtils.toString(response.getEntity()).trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getEcouponBymemberCompany(String company_id,String user_id)
    {
        String client = PROVIDER;
        try {
            client += "?ac=get_ecoupon_by_member" ;
            client += "&company_id=" + company_id;
            client += "&user_id=" + user_id;
            return client;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return client;
    }


    public static String getNewNumberNewCoupon(String user_id)
    {
        String client = PROVIDER;
        try {
            client += "?ac=get_new_number_news_coupon" ;
            client += "&user_id=" + user_id;
            return client;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return client;
    }


    public static String getPosCompany(String company_id)
    {
        String client = PROVIDER;
        try {
            client += "?ac=get_pos" ;
            client += "&company_id=" + company_id;
            return client;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return client;
    }

    public static String getMemberCardByCountry(String country_id) {
        String client = PROVIDER;
        try {
            client += "?ac=get_company_member_card_by_country" ;
            client += "&country_id=" + country_id;
            return client;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return client;
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

    public static String getListNation() {
        String client = PROVIDER;
        try {
            client += "?ac=get_all_country" ;
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

    public static RequestParams updateProfile(String user_id, String first_name, String last_name, String phone,
                                              String civility, String birthday)
    {
        RequestParams params = new RequestParams();
        params.put("ac", "change_user_info");
        params.put("user_id", user_id);
        params.put("first_name", first_name);
        params.put("last_name", last_name);
        params.put("phone", phone);
        params.put("civility", civility);
        params.put("birthday", birthday);
        return params;
    }

}
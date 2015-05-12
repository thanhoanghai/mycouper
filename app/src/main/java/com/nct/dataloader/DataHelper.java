/**
 * 
 */
package com.nct.dataloader;


import com.google.gson.Gson;
import com.nct.model.CompanyData;
import com.nct.model.MemberCardData;
import com.nct.model.StatusObject;
import com.nct.model.UserData;

public class DataHelper {


    public static CompanyData getCompanyData(String result) {
        CompanyData data;
        try {
            Gson gson = new Gson();
            data = gson.fromJson(result, CompanyData.class);
            return data;
        } catch (Exception ex) {
            data = null;
        }
        return data;
    }

    public static MemberCardData getMemberCardData(String result) {
        MemberCardData data;
        try {
            Gson gson = new Gson();
            data = gson.fromJson(result, MemberCardData.class);
            return data;
        } catch (Exception ex) {
            data = null;
        }
        return data;
    }

    public static UserData getUserData(String result) {
        UserData data;
        try {
            Gson gson = new Gson();
            data = gson.fromJson(result, UserData.class);
            return data;
        } catch (Exception ex) {
            data = null;
        }
        return data;
    }
    public static StatusObject getStatusObject(String result) {
        StatusObject data;
        try {
            Gson gson = new Gson();
            data = gson.fromJson(result, StatusObject.class);
            return data;
        } catch (Exception ex) {
            data = null;
        }
        return data;
    }


}

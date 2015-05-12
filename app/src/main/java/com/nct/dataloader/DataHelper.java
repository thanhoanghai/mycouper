/**
 * 
 */
package com.nct.dataloader;


import com.google.gson.Gson;
import com.nct.model.UserData;

public class DataHelper {

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


}

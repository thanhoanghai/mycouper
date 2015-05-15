package com.nct.constants;

import com.nct.model.UserObject;

public class GlobalInstance {

	private static GlobalInstance mGlobalInstance;
	public String userID = "";
	public String idLanguage = "vi";
    public UserObject userInfo;

	public static GlobalInstance getInstance() {
		if (mGlobalInstance == null) {
			mGlobalInstance = new GlobalInstance();
		}
		return mGlobalInstance;
	}


}

package com.nct.constants;

import com.nct.model.MemberCardObject;
import com.nct.model.StoresData;
import com.nct.model.StoresObject;
import com.nct.model.UserObject;

public class GlobalInstance {

	private static GlobalInstance mGlobalInstance;
	public String idLanguage = "en";
    public UserObject userInfo;
	public StoresData storesInfo;
	public MemberCardObject memberCard;


	public static GlobalInstance getInstance() {
		if (mGlobalInstance == null) {
			mGlobalInstance = new GlobalInstance();
		}
		return mGlobalInstance;
	}

	public String getUserID()
	{
		if(userInfo!=null)
			return userInfo.user_id;
		return "";
	}
	public String getSessionID()
	{
		if(userInfo!=null)
			return userInfo.session_id;
		return "";
	}


}

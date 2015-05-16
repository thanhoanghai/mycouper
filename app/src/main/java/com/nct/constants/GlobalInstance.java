package com.nct.constants;

import com.nct.model.MemberCardObject;
import com.nct.model.UserObject;

public class GlobalInstance {

	private static GlobalInstance mGlobalInstance;
	public String idLanguage = "vi";
    public UserObject userInfo;
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


}

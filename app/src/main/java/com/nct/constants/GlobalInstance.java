package com.nct.constants;

public class GlobalInstance {

	private static GlobalInstance mGlobalInstance;
	public String userID = "";
	public String idLanguage = "";

	public static GlobalInstance getInstance() {
		if (mGlobalInstance == null) {
			mGlobalInstance = new GlobalInstance();
		}
		return mGlobalInstance;
	}


}

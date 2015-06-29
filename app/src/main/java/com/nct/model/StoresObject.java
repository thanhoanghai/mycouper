package com.nct.model;

import com.google.gson.annotations.SerializedName;

public class StoresObject extends StatusObject {

    @SerializedName("company_id")
	public String company_id;
    @SerializedName("company_name")
	public String company_name;
    @SerializedName("company_logo")
	public String company_logo;
    @SerializedName("session_id")
    public String session_id;
    @SerializedName("session_id_code")
    public String session_id_code;
//    @SerializedName("coupon_category")
//    public String coupon_category;
//    @SerializedName("stamp_category")
//    public String stamp_category;
}
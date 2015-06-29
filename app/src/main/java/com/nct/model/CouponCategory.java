package com.nct.model;

import com.google.gson.annotations.SerializedName;

public class CouponCategory {

    @SerializedName("cc_id")
	public String cc_id;
    @SerializedName("card_name")
	public String card_name;
    @SerializedName("qrcode_id")
	public String qrcode_id;
    @SerializedName("last_update")
    public String last_update;
}
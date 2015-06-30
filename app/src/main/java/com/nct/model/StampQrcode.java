package com.nct.model;

import com.google.gson.annotations.SerializedName;

public class StampQrcode extends StampInfoCategory {

    @SerializedName("stamp_pos_id")
    public String stamp_pos_id;
    @SerializedName("pos_id")
    public String pos_id;
    @SerializedName("pos_name")
    public String pos_name;
    @SerializedName("qrcode")
    public String qrcode;
    @SerializedName("last_update")
    public String last_update;
}
package com.nct.model;

import com.google.gson.annotations.SerializedName;

public class StatusObject {

	@SerializedName("statusCode")
	public int statusCode;
	@SerializedName("errorMessage")
	public String errorMessage;
	@SerializedName("error")
	public String error;

}
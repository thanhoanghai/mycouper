package com.nct.model;

public class UserObject {

	public String user_id;
	public String first_name;
	public String last_name;
    public String birthday;
    public String user_email;
    public String avatar;
    public String phone;
    public String civility;
    public String create_time;
    public String last_update;
    public String last_login_time;

    public UserObject(String user_id, String user_email){
        this.user_id = user_id;
        this.user_email = user_email;
    }
}
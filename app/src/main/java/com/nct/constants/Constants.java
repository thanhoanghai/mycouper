package com.nct.constants;


import android.os.Environment;

public class Constants {
	
	public static final String ID_GOOGLE_ANALYTICS = "UA-51832900-15";
	
	public static boolean DEBUG_FLOW = true;
	public static boolean DEBUG_URL = true;
	public static boolean DEBUG_DATA = true;
	public static boolean DEBUG_ERROR = true;
	public static boolean DEBUG_TOAST = false;
	
	public static int SCREEN_WIDTH = 480;
	public static int SCREEN_HEIGHT = 800;

	public static final String API_GOOGLEKEY = "AIzaSyB8mtKr923llplPGMNN30mdPDQACTpXfxQ";
	
	
	public static final String TAB_1 = "TAB_1";
	public static final String TAB_2 = "TAB_2";
	public static final String TAB_3 = "TAB_3";


    public static final String ID_LANGUAGE[] = {"en","vi"};
    public static final String SAVE_ID_LANGUAGE = "SAVE_ID_LANGUAGE";

	public static final String ID_SAVE_LOGIN = "ID_SAVE_LOGIN";
	public static final String TAB_CREATE_CARD = "TAB_CREATE_CARD";

    public static final String TAB_CAMERA_CAMERA = "TAB_CAMERA_CAMERA";

    public static final String FONT_ROBO_BOLD = "fonts/Roboto-Bold.ttf";
    public static final String FONT_ROBO_LIGHT = "fonts/Roboto-Light.ttf";
    public static final String FONT_ROBO_REGULAR = "fonts/Roboto-Regular.ttf";
    public static final String FONT_ROBO_MEDIUM = "fonts/Roboto-Medium.ttf";

    public static final String CALENDAR_DATE_FORMATTER = "yyyy-MM-dd";
    public static final String CALENDAR_DATE_FORMATTER_DDMMYYYY = "dd-MM-yyyy";

    /**
     * Folder is used to save down load image in SDCard.
     */
    public static final String SDCARD_CACHE_PREFIX = Environment
            .getExternalStorageDirectory() + "/MyCouper/";

    /**
     * Default file path of photo has taken from camera.
     * */
    public static final String SDCARD_TAKE_PHOTO_CACHE_PREFIX = SDCARD_CACHE_PREFIX
            + "/photo.jpg";

    public static final String SDCARD_TAKE_AUDIO_CACHE_PREFIX = SDCARD_CACHE_PREFIX
            + "/audio.3gp";

    public static final String SDCARD_TAKE_VIDEO_CACHE_PREFIX = SDCARD_CACHE_PREFIX
            + "/video.mp4";

    public static final String JPG = ".jpg";
    public static final String MP4 = ".mp4";

    public static final int BITMAP_MAX_SIZE = 960;

    public static final int CB_IMAGE_HEIGHT = 200;
    public static final int CB_IMAGE_WIDTH = 300;

	public static final int API_DELAY_TIME = 1000;
	
	public static String DEVICES_ID = "";
	public static String TOKEN_KEY = "";
	public static String DEVICE_INFOR = "";
	public static final String VERSION_NAME_WHEN_ERROR = "1.0.0";


    public static int STATUS_CODE_OK = 200;
	public static final String FACEBOOK_ID = "751574654961717";
	public static final String FACEBOOK_NAMESPACE = "mycouper";


	public static final int POP_UP_ID_USER_PROFILE = 1;
	public static final int POP_UP_ID_SETTING = 2;
	public static final int POP_UP_ID_CONTACT = 3;
	public static final int POP_UP_ID_SYNCHRONIZE = 4;
	public static final int POP_UP_ID_HELP = 5;
	public static final int POP_UP_ID_COPYRIGHT = 6;
    public static final int POP_UP_ID_USER_LANGUAGE = 7;
    public static final int POP_UP_ID_TUTORIALS = 8;
    public static final int POP_UP_ID_ASK= 9;
    public static final int POP_UP_ID_INFORMATION= 10;
    public static final int POP_UP_ID_STORE= 11;

    // type code
    public static final int POP_UP_ID_CODE_ID = 1;
    public static final int POP_UP_ID_CODE_BARCODE = 2;
    public static final int POP_UP_ID_CODE_QRCODE = 3;

	public static final String TYPE_CARD_SCAN_CODE[] = {"ID","qrcode","barcode"};
	public static final int TYPE_CREATE_CARD_SEARCH = 0;
	public static final int TYPE_CREATE_CARD_INFO = 1;
	public static final int TYPE_CREATE_CARD_IMAGE = 2;
	public static final int TYPE_CREATE_CARD_SUCCESS = 3;

    public static final String KEY_BUNDLE_BOOLEAN_VALUE = "KEY_BUNDLE_BOOLEAN_VALUE";
    public static final String KEY_BUNDLE_OBJECT_VALUE = "KEY_BUNDLE_OBJECT_VALUE";
    public static final String KEY_BUNDLE_CARD_INFO_COMPANYNAME = "KEY_BUNDLE_CARD_INFO_COMPANYNAME";
    public static final String KEY_BUNDLE_CARD_INFO_COMPANYID = "KEY_BUNDLE_CARD_INFO_COMPANYID";
    public static final String KEY_BUNDLE_CARD_INFO_COMPANYLOGO = "KEY_BUNDLE_CARD_INFO_COMPANYLOGO";
    public static final String KEY_BUNDLE_CARD_INFO_CARDCODE = "KEY_BUNDLE_CARD_INFO_CARDCODE";
    public static final String KEY_BUNDLE_CARD_INFO_CARDNAME = "KEY_BUNDLE_CARD_INFO_CARDNAME";
    public static final String KEY_BUNDLE_CARD_INFO_CARDDES = "KEY_BUNDLE_CARD_INFO_CARDDES";
    public static final String KEY_BUNDLE_CARD_INFO_TYPE_CODE = "KEY_BUNDLE_CARD_INFO_TYPE_CODE";
    public static final String KEY_BUNDLE_CARD_EDIT_CARD = "KEY_BUNDLE_CARD_EDIT_CARD";
    public static final String KEY_BUNDLE_CARD_COMPANY_CATEGORY = "KEY_BUNDLE_CARD_COMPANY_CATEGORY";

	public static String AT_PREVIEW_ID_LINK_IMAGE = "AT_PREVIEW_ID_LINK_IMAGE";
    public static String AT_INTRODUCE_INDEX = "AT_INTRODUCE_INDEX";

    //Status api
    public static final int API_REQUEST_STATUS_SUCCESS = 200;
    public static final int API_REQUEST_STATUS_FAILED = 500;


    public static final String ID_COUNTRY_DEFAUL_VIETNAME = "14";

    public static final String MAIL_SUPPORT = "sale@mycouper.com";
    public static final String PHONE_SUPPORT = "+84902292626";
}
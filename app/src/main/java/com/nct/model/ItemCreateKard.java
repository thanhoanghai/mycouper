package com.nct.model;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by nghidv on 5/18/2015.
 */
public class ItemCreateKard {
    public static Bitmap bitmapFront = null;
    public static File frontFile = null;
    public static Bitmap bitmapBack = null;
    public static File backFile = null;
    public static String mCardID = null;
    public static String frontUrl = null;
    public static String backUrl = null;

    public static void clear(){
        bitmapFront = null;
        frontFile = null;
        bitmapBack = null;
        backFile = null;
        mCardID = null;
        frontUrl = null;
        backUrl = null;
    }
}

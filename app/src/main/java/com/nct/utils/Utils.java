package com.nct.utils;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.nct.constants.Constants;
import com.nct.mv.AtCardDetail;
import com.nct.mv.AtChangePass;

import com.nct.mv.AtCreateCard;
import com.nct.mv.AtForgotPass;
import com.nct.mv.AtLanguage;
import com.nct.mv.AtLogin;
import com.nct.mv.AtMain;
import com.nct.mv.AtPreviewImage;
import com.nct.mv.AtSignUp;
import com.nct.mv.AtUserProfile;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import thh.com.mycouper.R;

public class Utils {

	private static final String TAG = "Utils";

	public static void gotoScreenLanguage(Context mContext) {
		Intent search = new Intent(mContext,AtLanguage.class);
		mContext.startActivity(search);
	}

	public static void gotoScreenPreviewImage(Context mContext,String linkImage) {
		Intent search = new Intent(mContext, AtPreviewImage.class);
		search.putExtra(Constants.AT_PREVIEW_ID_LINK_IMAGE,linkImage);
		mContext.startActivity(search);
	}

	public static void gotoScreenCardDetail(Context mContext) {
		Intent search = new Intent(mContext, AtCardDetail.class);
		mContext.startActivity(search);
	}

	public static void gotoScreenChangepass(Context mContext) {
		Intent search = new Intent(mContext, AtChangePass.class);
		mContext.startActivity(search);
	}


	public static void gotoScreenUserProfile(Context mContext) {
		Intent search = new Intent(mContext, AtUserProfile.class);
		mContext.startActivity(search);
	}


	public static void gotoScreenCreateCard(Context mContext, boolean isEdit) {
		Intent intent = new Intent(mContext, AtCreateCard.class);
        if(isEdit)
            intent.putExtra(Constants.KEY_BUNDLE_CARD_EDIT_CARD, isEdit);
		mContext.startActivity(intent);
	}


	public static void gotoScreenMain(Context mContext) {
		Intent search = new Intent(mContext, AtMain.class);
		mContext.startActivity(search);
	}

	public static void gotoScreenForgotPass(Context mContext) {
		Intent search = new Intent(mContext, AtForgotPass.class);
		mContext.startActivity(search);
	}

	public static void gotoScreenSignUp(Context mContext) {
		Intent search = new Intent(mContext, AtSignUp.class);
		mContext.startActivity(search);
	}

	public static void gotoScreenLogin(Context mContext) {
		Intent search = new Intent(mContext, AtLogin.class);
		mContext.startActivity(search);
	}

	public static boolean isNetwork3g(Context mContext) {
		try {
			ConnectivityManager manager = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			boolean is3g = manager.getNetworkInfo(
					ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
			return is3g;
		} catch (Exception ex) {
			return false;
		}
	}

	public static boolean isNetworkAvailable(Context mContext) {
		try {
			if (mContext != null) {
				ConnectivityManager cm = (ConnectivityManager) mContext
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				if (cm.getActiveNetworkInfo() != null
						&& cm.getActiveNetworkInfo().isAvailable()
						&& cm.getActiveNetworkInfo().isConnected()) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception ex) {
		}
		return false;
	}

	/**
	 * Create a MD5-Hash and Dump as a Hex String
	 */
	public static String md5(String s) {
		if (s != null) {
			try {
				MessageDigest digest = java.security.MessageDigest
						.getInstance("MD5");
				digest.update(s.getBytes());
				byte messageDigest[] = digest.digest();

				StringBuffer hexString = new StringBuffer();
				for (int i = 0; i < messageDigest.length; i++) {
					String hex = Integer.toHexString(0xFF & messageDigest[i]);
					if (hex.length() == 1)
						hexString.append('0');
					hexString.append(hex);
				}
				return hexString.toString();

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			return "";
		} else
			return "";
	}

	public static DisplayImageOptions getDisplayImageOptions(int idImage) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(idImage).showImageForEmptyUri(idImage)
				.showImageOnFail(idImage).cacheInMemory(true).cacheOnDisk(true)
				.considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
				.build();
		return options;
	}

	public static String showHashKeyFacebook(Context mContext) {
		try {
			String PACKAGE_NAME = mContext.getPackageName();
			PackageInfo info = mContext.getPackageManager().getPackageInfo(
					PACKAGE_NAME, PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.e("KeyHash:",
						"key is: "
								+ Base64.encodeToString(md.digest(),
										Base64.DEFAULT));
				return Base64.encodeToString(md.digest(),
						Base64.DEFAULT);
			}
		} catch (NameNotFoundException e) {
			Log.e("error", "error name not found");
		} catch (NoSuchAlgorithmException e) {
			Log.e("error", "error no algorithm");
		}
		return "";
	}

	public static void updateDeviceInfo(Context context, String username) {
		try {
			String serviceName = Context.TELEPHONY_SERVICE;
			TelephonyManager m_telephonyManager = (TelephonyManager) context
					.getSystemService(serviceName);
			String imei = m_telephonyManager.getDeviceId();

			if (TextUtils.isEmpty(imei)) {
				WifiManager wimanager = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				imei = Utils.md5(wimanager.getConnectionInfo().getMacAddress());
			} else {
				imei = Utils.md5(imei);
			}

			Constants.DEVICES_ID = imei;
			Constants.DEVICE_INFOR = context.getString(R.string.deviceInfo,
					imei, Build.VERSION.SDK_INT, getVersionName(context),
					username);
		} catch (Exception ex) {
			ex.printStackTrace();
			Constants.DEVICE_INFOR = context.getString(R.string.deviceInfo,
					"0123456789", Build.VERSION.SDK_INT,
					getVersionName(context), username);
		}

		Debug.logError(TAG, Constants.DEVICE_INFOR);

	}

	public static String getVersionName(Context context) {
		PackageManager packageManager = context.getPackageManager();
		if (packageManager != null) {
			try {
				PackageInfo packageInfo = packageManager.getPackageInfo(
						context.getPackageName(), 0);
				String versionName = packageInfo.versionName;
				return versionName;
			} catch (PackageManager.NameNotFoundException e) {
				return Constants.VERSION_NAME_WHEN_ERROR;
			}
		}
		return Constants.VERSION_NAME_WHEN_ERROR;
	}

	public static String convertNumberToK(String sNumber) {
		String listen = "0";
		if (sNumber != null) {
			if (sNumber.length() > 3) {
				long listenCount = Long.parseLong(sNumber);
				if (listenCount < 1000000)
					return (listenCount / 1000) + "K";
				else
					return (listenCount / 1000000) + "M";
			} else if (sNumber.length() > 0)
				listen = sNumber;
		}
		return listen;
	}

	public static void openGooglePlayApp(Context mContext, String namePack) {
		try {
			mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse("market://details?id=" + namePack)));
		} catch (android.content.ActivityNotFoundException anfe) {
			mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse("http://play.google.com/store/apps/details?id="
							+ namePack)));
		}
	}

	public static String convertLongToDay(long time) {
		Date date = new Date(time);
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
		String dateText = df2.format(date);
		return dateText;
	}

	public static void keyBoardForceShow(Context mContext) {
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}

	public static void keyBoardForceHide(Context mContext) {
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	}

	public static String thousandsSeparator(int val) {
		BigDecimal value = new BigDecimal(val);
		DecimalFormat df = new DecimalFormat("#,##0");
		return df.format(value);
	}

	public static void openLinkWeb(Context mContext, String link) {
		try {
			Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
			mContext.startActivity(myIntent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void shareLinkOnline(Context mContext, String link) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(android.content.Intent.EXTRA_TEXT, link);
		mContext.startActivity(Intent.createChooser(intent,
				"Share Your Friends"));
	}

    public final static boolean isValidEmail(String target) {
        if (target == null || target.equals("")) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static Bitmap scaleDownBitmap(String imgPath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);
        options.inSampleSize = calculateInSampleSize(options , Constants.CB_IMAGE_WIDTH, Constants.CB_IMAGE_HEIGHT);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgPath, options);

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        return calculateInSampleSize(width, height, reqWidth, reqHeight);
    }

    public static int calculateInSampleSize(int curWidth, int curHeight, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = curHeight;
        final int width =  curWidth;

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


	public static float pxFromDp(final Context context, final float dp) {
		return dp * context.getResources().getDisplayMetrics().density;
	}

	public static Date dateTimeFromString(String dateString) {
		return dateTimeFromString(dateString, Constants.CALENDAR_DATE_FORMATTER);
	}

	public static Date dateTimeFromString(String dateString, String format) {
		if(dateString==null) return null;
		ParsePosition pos = new ParsePosition(0);
		SimpleDateFormat simpledateformat = new SimpleDateFormat(format);
		Date stringDate = simpledateformat.parse(dateString, pos);
		return stringDate;
	}

	public static String formatDateTime(Date dateTime) {
		if(dateTime == null) return "";
		SimpleDateFormat formatter = new SimpleDateFormat(Constants.CALENDAR_DATE_FORMATTER_DDMMYYYY, Locale.getDefault());

		return formatter.format(dateTime);
	}

}

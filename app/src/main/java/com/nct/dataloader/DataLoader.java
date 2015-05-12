package com.nct.dataloader;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nct.utils.Debug;

public class DataLoader {

	private static final String TAG = "DataLoader";
	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void get(String url, TextHttpResponseHandler responseHandler) {
        Debug.logURL(TAG, url);
		client.get(url, responseHandler);
	}

	public static void post(String url, TextHttpResponseHandler responseHandler) {
		client.post(url, responseHandler);
	}
    public static void postParam(RequestParams param, TextHttpResponseHandler responseHandler) {
        client.post(URLProvider.PROVIDER,param, responseHandler);
    }

}

// public static String getLinkWithToken(String link)
// {
// return link + GlobalInstance.getInstance().getAccessToken();
// }
//
// public static void getToken(final OnRequestAccessTokenListener listener) {
// String link = URLProvider.getLinkToken();
// Debug.logError(TAG, link);
// client.get(link, new TextHttpResponseHandler() {
// @Override
// public void onSuccess(int arg0, Header[] arg1, String arg2) {
// Debug.logData(TAG, arg2);
// TokenData object = DataHelper.getTokenData(arg2);
// if (object != null) {
// GlobalInstance.getInstance().setAccessToken(object.data);
// listener.onSuccess();
// }
// }
// @Override
// public void onFailure(int arg0, Header[] arg1, String arg2,
// Throwable arg3) {
// // TODO Auto-generated method stub
// listener.onFailure();
// }
// });
// }
// public interface OnRequestAccessTokenListener {
// void onSuccess();
// void onFailure();
// }

package com.nct.mv;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.nct.utils.Utils;

import thh.com.mycouper.R;

public class AtScan extends Activity implements ZBarScannerView.ResultHandler {
	private ZBarScannerView mScannerView;

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		mScannerView = new ZBarScannerView(this);
		setContentView(mScannerView);
	}

	@Override
	public void onResume() {
		super.onResume();
		mScannerView.setResultHandler(this);
		mScannerView.startCamera();
	}

	@Override
	public void onPause() {
		super.onPause();
		mScannerView.stopCamera();
	}

	@Override
	public void handleResult(Result rawResult) {
		Toast.makeText(this, "Contents = " + rawResult.getContents() +
				", Format = " + rawResult.getBarcodeFormat().getName(), Toast.LENGTH_SHORT).show();
		mScannerView.startCamera();
	}
}

}

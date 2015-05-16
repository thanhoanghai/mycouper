package com.nct.mv;

import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.nct.fragment.CameraFragment;
import com.nct.fragment.CameraMainFragment;
import com.nct.fragment.EffectCameraFragment;
import com.nct.fragment.ViewClickable;

import thh.com.mycouper.R;

public class MainHomeCameraActivity extends FragmentActivity implements ViewClickable, CameraMainFragment.OnImagePickedListener {

	private FragmentManager fmManager;
	private FragmentTransaction fmTransaction;
	
	private Dialog mProgress = null;	

	//camera flash
	private CameraMainFragment CameraMainFragment;
	private String currentFlashMode;
	private TextView flashTextButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.main_home_camera_activity);
		
		//hide content while waiting for camera nitial - solve sony xperia neol problem
		getWindow().getDecorView().findViewById(android.R.id.content).setVisibility(View.INVISIBLE);
		
		fmManager = getSupportFragmentManager();		
		fmManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {		
			@Override
			public void onBackStackChanged() {
				updateTopBarNavigation();
			}			
		});
		
		flashTextButton = (TextView) findViewById(R.id.photo_top_bar_camera_flash);
		
		//put camera fragment
		CameraMainFragment = new CameraMainFragment();
		CameraMainFragment.setPreviewListener(onPreviewListener);
		putFragment(CameraMainFragment);									
	}	
	
	/**
	 * update top bar step
	 */
	private void updateTopBarNavigation() {		
		View topBarCamera = findViewById(R.id.photo_top_bar_camera);
		View topBarEffect = findViewById(R.id.photo_top_bar_effect);
		
		try {
			Fragment fm = fmManager.findFragmentById(R.id.camera_content_cover);
			
			if (fm instanceof CameraMainFragment) {
				topBarCamera.setVisibility(View.VISIBLE);
			} else {
				topBarCamera.setVisibility(View.GONE);
			}
			
			if (fm instanceof EffectCameraFragment) {
				topBarEffect.setVisibility(View.VISIBLE);
			} else {
				topBarEffect.setVisibility(View.GONE);
			}						
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		View topbar = findViewById(R.id.camera_top_bar);
		topbar.bringToFront();
	}
	
	/**
	 * put a fragment with UI
	 */
	public void putFragment(Fragment fm) {
		fmTransaction = fmManager.beginTransaction();
//		fmTransaction.setCustomAnimations(R.anim.push_left_in, 0,
//				0, R.anim.push_right_out);
		fmTransaction.add(R.id.camera_content_cover, fm);		
		fmTransaction.addToBackStack(null);
		fmTransaction.commit();
	}
	
	public void popFragment() {
		fmManager.popBackStack();
	}

	public void showProgressDialog() {		
		if (mProgress == null) {
			mProgress = new Dialog(MainHomeCameraActivity.this,
					android.R.style.Theme_Translucent_NoTitleBar);
			mProgress.setContentView(R.layout.progressdialog);
			mProgress.setCancelable(false);
		}

		mProgress.show();
	}

	public void dismissProgressDialog() {
		if (mProgress != null && mProgress.isShowing()) {
			mProgress.dismiss();
		}
	}
	
	/*
	 * listener for receiving camera start preview and update flash mode 
	 */
	CameraFragment.OnPreviewListener onPreviewListener = new CameraFragment.OnPreviewListener() {
		@Override
		public void onPreview() {
			Fragment fmCamera = fmManager.findFragmentById(R.id.camera_content_cover);			
			if (fmCamera instanceof CameraMainFragment) {
				CameraMainFragment = ((CameraMainFragment)fmCamera); 				
				List<String> modes = CameraMainFragment.getSupportedFlashModes();
				
				if( modes != null &&
						modes.contains(Camera.Parameters.FLASH_MODE_AUTO) &&
						modes.contains(Camera.Parameters.FLASH_MODE_OFF) &&
						modes.contains(Camera.Parameters.FLASH_MODE_ON) ) {
					currentFlashMode = Camera.Parameters.FLASH_MODE_AUTO;					
					flashTextButton.setText("Auto");
					flashTextButton.setVisibility(View.VISIBLE);
					CameraMainFragment.setFlashMode(currentFlashMode);
				} else {
					flashTextButton.setVisibility(View.GONE);
				}
				
				// show/hide switch camera button
				View switchButton  = findViewById(R.id.photo_top_bar_camera_switch);
				if(Camera.getNumberOfCameras() > 1) {
					switchButton.setVisibility(View.VISIBLE);
				} else {
					switchButton.setVisibility(View.GONE);
				}
			}	
			
			//show content after camera initial - solve sony xperia neol problem
			getWindow().getDecorView().findViewById(android.R.id.content).setVisibility(View.VISIBLE);
		}
	};
	
	/*
	 * change camera flash mode
	 */
	private void changeFlashMode() {
		if( Camera.Parameters.FLASH_MODE_AUTO.equals(currentFlashMode) ) {
			currentFlashMode = Camera.Parameters.FLASH_MODE_OFF;					
			flashTextButton.setText("Off");
			CameraMainFragment.setFlashMode(currentFlashMode);
		} else if( Camera.Parameters.FLASH_MODE_OFF.equals(currentFlashMode) ) {
			currentFlashMode = Camera.Parameters.FLASH_MODE_ON;					
			flashTextButton.setText("On");
			CameraMainFragment.setFlashMode(currentFlashMode);
		} else if( Camera.Parameters.FLASH_MODE_ON.equals(currentFlashMode) ) {
			currentFlashMode = Camera.Parameters.FLASH_MODE_AUTO;					
			flashTextButton.setText("Auto");
			CameraMainFragment.setFlashMode(currentFlashMode);
		}
	}
	
	@Override
	public void onViewClick(View v) {
		switch (v.getId()) {
		case R.id.photo_top_bar_camera_flash:
			changeFlashMode();
			break;
		case R.id.photo_top_bar_camera_switch:
			Fragment fmCamera = fmManager.findFragmentById(R.id.camera_content_cover);			
			if (fmCamera instanceof CameraMainFragment) {
				((CameraMainFragment)fmCamera).switchCamera();
			}
			break;
		case R.id.photo_top_bar_effect_cancel:
			onBackPressed();
			break;
		case R.id.photo_top_bar_effect_apply:
			Fragment fmEffect = fmManager.findFragmentById(R.id.camera_content_cover);			
			if (fmEffect instanceof EffectCameraFragment) {
				/*
				 * return result to parent activity
				 * call getResultUri to write result bitmap to file
				 * default is the file that we use for take new photo Constant.SDCARD_TAKE_PHOTO_CACHE_PREFIX
				 */				
				Uri resultUri = ((EffectCameraFragment)fmEffect).getResultUri();
				Intent resultIntent = new Intent();
				resultIntent.setData(resultUri);
				setResult(RESULT_OK, resultIntent);
				finish();
			}			 			
			break;
		default:
			try {
				Fragment fm = fmManager.findFragmentById(R.id.camera_content_cover);
				if (fm instanceof ViewClickable) {
					((ViewClickable) fm).onViewClick(v);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}

	@Override
	public void onBackPressed() {
		Fragment fm = fmManager.findFragmentById(R.id.camera_content_cover);
		if (fm instanceof CameraMainFragment) {
			finish();			
		} else {
			super.onBackPressed();
		}
	}
	
	@Override
	public void onImagePicked(Uri uri) {
		Log.i("NghiDo", "" + uri);
		EffectCameraFragment effectFragment = new EffectCameraFragment();
		Bundle bundle = new Bundle();		
		bundle.putParcelable(EffectCameraFragment.EXT_DATA_URI, uri);		
		effectFragment.setArguments(bundle);
		putFragment(effectFragment);
	}
}

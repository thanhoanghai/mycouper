package com.nct.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nct.fragment.CameraFragment.OnPreviewListener;

import java.util.List;

import thh.com.mycouper.R;

public class CameraMainFragment extends Fragment implements ViewClickable {
	
	private static final int REQUEST_CODE_PICK_LIBRARY = 0;
	private static final int REQUEST_CODE_PICK_INSTAGRAM = 1;
	
	private CameraFragment cameraFragment;
	private Uri resultFileUri;
	
	OnPreviewListener onPreviewListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.camera_main_fragment, null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);				                    
                        
        cameraFragment = (CameraFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.camera_fragment);
        cameraFragment.setPreviewListener(onPreviewListener);
                
        //hide pick menu
        togglePickType(false);
	}
		
	/**
	 * get image from library
	 */
	private void getFromLibrary() {
		Intent intentPhotoLibrary = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intentPhotoLibrary.setType("image/*");
		startActivityForResult(intentPhotoLibrary, REQUEST_CODE_PICK_LIBRARY);
	}
	
	/**
	 * get iamge from instagram
	 */
	private void getFromInstagram() {
//		Intent intentInsta = new Intent(getActivity(), InstagramActivity.class);		
//		startActivityForResult(intentInsta, REQUEST_CODE_PICK_INSTAGRAM);
	}		
	
	private void togglePickType() {
		View v = getView().findViewById(R.id.camera_pick_type_cover);
		if( v.getVisibility() == View.VISIBLE ) {
			v.setVisibility(View.GONE);
		} else {
			v.setVisibility(View.VISIBLE);
		}
	}
	
	private void togglePickType(boolean isShow) {
		View v = getView().findViewById(R.id.camera_pick_type_cover);
		if( !isShow ) {
			v.setVisibility(View.GONE);
		} else {
			v.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onViewClick(View v) {
		switch (v.getId()) {
		case R.id.camera_close:
			getActivity().finish();
			break;
		case R.id.camera_pick_type:
			togglePickType();
			break;
		case R.id.camera_library:
			togglePickType(false);
			getFromLibrary();
			break;
		case R.id.camera_capture:
			togglePickType(false);
			captureImage();
			break;
		case R.id.camera_instagram:
			togglePickType(false);
			getFromInstagram();
			break;		
		default:
			break;
		}		
	}			
	
	private void captureImage() {
		cameraFragment.captureImage(new CameraFragment.OnCaptureListener() {			
			@Override
			public void onCapture(Uri uri) {
				resultFileUri = uri;
				fireResult();
			}
		});
	}

	public void switchCamera() {
		cameraFragment.switchCamera();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if( resultCode == Activity.RESULT_OK ) {
			switch ( requestCode ) {
			case REQUEST_CODE_PICK_INSTAGRAM:				
			case REQUEST_CODE_PICK_LIBRARY:
				resultFileUri = data.getData();				
				fireResult();
				break;
			default:
				break;
			}
		}			
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * send result image uri to parent 
	 */
	void fireResult() {
		if( getActivity() instanceof OnImagePickedListener ) {
			((OnImagePickedListener)getActivity()).onImagePicked(resultFileUri);						
		}
	}
	
	public void setPreviewListener(OnPreviewListener onPreviewListener) {
		this.onPreviewListener = onPreviewListener;
	}
	
	public void setFlashMode(String mode) {
		cameraFragment.setFlashMode(mode);
	}
	
	public List<String> getSupportedFlashModes() {
		return cameraFragment.getSupportedFlashModes();
	}
	
	/**
	 * interface for parent activity to implement for receiving taken image
	 * @author nvhau
	 *
	 */
	public static interface OnImagePickedListener {
		void onImagePicked(Uri uri);
	}
	
//	public static final String EXT_EDIT_URL_DATA = "CameraMainFragment_ext_edit_url_data";
//	
//	private static final int REQUEST_CODE_PICK_LIBRARY = 0;
//	private static final int REQUEST_CODE_PICK_INSTAGRAM = 1;
//	
//	private CameraFragment cameraFragment;
//	private Uri resultFileUri;   
//	
//	private LinearLayout contentType;
//    
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {	
//		View view = inflater.inflate(R.layout.camera_main_fragment, null);
//		
//		innitUI(view);
//		return view;
//	}
//	
//	private void innitUI(View view){
//		contentType = (LinearLayout) view.findViewById(R.id.camera_pick_type_cover);
//	}
//	
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {	
//		super.onActivityCreated(savedInstanceState);
//				       
//        Bundle bundle = getArguments();
//        if( bundle != null ) {        	
//    		resultFileUri = Uri.parse(bundle.getString(EXT_EDIT_URL_DATA));
//    		fireResult();        	
//        }
//        
//      //check if device have 1 or 2 camera to show swtich camera button
//        View switchButton = getView().findViewById(R.id.camera_switch);
//        if( Camera.getNumberOfCameras() > 1 ) {
//        	switchButton.setVisibility(View.VISIBLE);
//        } else {
//        	switchButton.setVisibility(View.GONE);
//        }
//                       
//        cameraFragment = (CameraFragment) getFragmentManager().findFragmentById(R.id.camera_fragment);
//        cameraFragment.setPreviewListener(onPreviewListener);
//	}
//	
//	/*
//	 * listener for receiving camera start preview and update main UI 
//	 */
//	CameraFragment.OnPreviewListener onPreviewListener = new CameraFragment.OnPreviewListener() {		
//		@Override
//		public void onPreview() {						
//			//show content after camera initial - solve sony xperia neol problem
//			getActivity().getWindow().getDecorView().findViewById(android.R.id.content).setVisibility(View.VISIBLE);
//		}		
//	};
//	
//	/**
//	 * get image from library
//	 */
//	private void getFromLibrary() {
//		Intent intentPhotoLibrary = new Intent(Intent.ACTION_PICK,
//				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//		intentPhotoLibrary.setType("image/*");
//		startActivityForResult(intentPhotoLibrary, REQUEST_CODE_PICK_LIBRARY);
//	}
//	
//	@Override
//	public void onViewClick(View v) {
//		switch (v.getId()) {
//		case R.id.camera_pick_type:
//			if(contentType.getVisibility() == View.VISIBLE)
//				contentType.setVisibility(View.GONE);
//			else
//				contentType.setVisibility(View.VISIBLE);
//			break;
//		case R.id.camera_capture:			
//			captureImage();
//			break;
//		case R.id.camera_switch:
//			switchCamera();
//			break;
//		case R.id.camera_back:
//			((CameraMainActivity)getActivity()).onBackPressed();
//			break;
//		case R.id.camera_library:
//			getFromLibrary();
//			break;
//		case R.id.camera_instagram:
//			getFromLibrary();
//			break;
//		case R.id.camera_close:
//			((CameraMainActivity)getActivity()).finish();
//			break;
//		default:
//			break;
//		}	
//	}
//	
//	private void captureImage() {
//		cameraFragment.captureImage(new CameraFragment.OnCaptureListener() {			
//			@Override
//			public void onCapture(Uri uri) {
//				resultFileUri = uri;
//				fireResult();
//			}
//		});
//	}
//
//	private void switchCamera() {
//		cameraFragment.switchCamera();
//	}
//	
//	/**
//	 * send result image uri to parent 
//	 */
//	void fireResult() {
//		if( getActivity() instanceof OnImagePickedListener ) {
//			((OnImagePickedListener)getActivity()).onImagePicked(resultFileUri);						
//		}
//	}
//
//	/**
//	 * interface for parent activity to implement for receiving taken image
//	 * @author NghiDo
//	 *
//	 */
//	public static interface OnImagePickedListener {
//		void onImagePicked(Uri uri);
//	}
}

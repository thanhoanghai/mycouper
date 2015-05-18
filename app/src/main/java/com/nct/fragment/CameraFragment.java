package com.nct.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;

import com.nct.constants.Constants;
import com.nct.customview.CameraPreview;
import com.nct.utils.BitmapUtils;
import com.nct.utils.Device;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import thh.com.mycouper.R;

public class CameraFragment extends Fragment {
	
private static final String TAG = "CameraFragment";
	
	/*
	 * camera type
	 */
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	
	private static Camera mCamera;
	private MediaRecorder mMediaRecorder;
    private CameraPreview mPreview;
    private int mCurrentCameraId;
	private int result = 0;
    
    private int mKardPreviewFrameHeight;
    
    OnCaptureListener onCaptureListener;    
    OnPreviewListener onPreviewListener;
    
    private Dialog mProgress = null;
    
    /*
     * we are recording video or not
     */
    private boolean isRecording = false;
    private boolean isCapturing = false;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.camera_fragment, null);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mKardPreviewFrameHeight = 0;
        
        //caculate zoom percent
		final LinearLayout preview = (LinearLayout) getView().findViewById(R.id.camera_preview_cover);
		if (preview.getViewTreeObserver().isAlive()) {
			preview.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	              @SuppressWarnings("deprecation") // We use the new method when supported
	              @SuppressLint("NewApi") // We check which build version we are using.
	              @Override
	              public void onGlobalLayout() {
	                  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
	                  	preview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
	                  } else {
	                  	preview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
	                  }
	                              				
	                  mKardPreviewFrameHeight = preview.getHeight();	   
	                  
	                  /*
	                   * wait 500 milli for the view to be drawn before show preview
	                   * sony devices problem
	                   */	                  	                  
	                  preview.postDelayed(new Runnable() {
						@Override
						public void run() {						
							// try to open first available camera
							if (!showPreview(Camera.CameraInfo.CAMERA_FACING_BACK)) {
								showPreview(Camera.CameraInfo.CAMERA_FACING_FRONT);
							}
						}
	                  }, 100);
	              }
	          });
		}					
	}
	
	@Override
	public void onResume() {
		super.onResume();		
		//try to open first available camera
        if( !showPreview(Camera.CameraInfo.CAMERA_FACING_BACK) ) {
        	showPreview(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }                
	}
	
	@Override
	public void onPause() {
		releaseMediaRecorder();
		releaseCamera();		
		super.onPause();
	}		
	
	void showProgressDialog() {		
		if (mProgress == null) {
			mProgress = new Dialog(getActivity(),
					android.R.style.Theme_Translucent_NoTitleBar);
			mProgress.setContentView(R.layout.progressdialog);
			mProgress.setCancelable(false);
		}

		mProgress.show();
	}

	void dismissProgressDialog() {
		if (mProgress != null && mProgress.isShowing()) {
			mProgress.dismiss();
		}
	}
	
	/**
	 * @param facing front/back
	 * @return
	 */
	private boolean showPreview(int facing) {
		releaseCamera();		
		mCamera = getCameraInstance(facing);
		if( mCamera == null || mKardPreviewFrameHeight == 0) {
			/*
			 * dont show preview if the frame height still not available
			 * recall on preview frame's onGlobalLayout(...)
			 */			
			return false;
		}
		
		int maxPreviewSize = 640*640;
		int previewWidth = 0;
		int previewHeigth = 0;
		Parameters parameters = mCamera.getParameters();
		ArrayList<Size> listSize = (ArrayList<Size>) parameters.getSupportedPreviewSizes();
		for( Size size : listSize ) {
			int tmp = size.width * size.height;
			if( tmp < maxPreviewSize ||
					tmp < Device.getDeviceWidth() * Device.getDeviceHeight() ) {
				previewWidth = size.width;
				previewHeigth = size.height;				
				break;
			}
		}
		parameters.setPreviewSize(previewWidth, previewHeigth);				
		mCamera.setParameters(parameters);
		
		mPreview = new CameraPreview(getActivity(), mCamera);		
		int previewScaleW = previewWidth;
		int previewScaleH = previewHeigth;		
		if( Device.getDeviceWidth() * previewWidth < mKardPreviewFrameHeight * previewHeigth ) {
			//crop height
			previewScaleW = mKardPreviewFrameHeight;
			previewScaleH = previewScaleW * previewHeigth / previewWidth;
		} else {
			//crop width
			previewScaleH = Device.getDeviceWidth();
			previewScaleW = previewScaleH * previewWidth / previewHeigth;
		}
						
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(previewScaleH, previewScaleW);
		mPreview.setLayoutParams(lp);
		
        LinearLayout preview = (LinearLayout) getView().findViewById(R.id.camera_preview_cover);
        preview.removeAllViews();
        preview.addView(mPreview);
        
        if( onPreviewListener != null ) {
        	onPreviewListener.onPreview();
        }
        
        isRecording = false;
        
        return true;
	}
	
	private void releaseCamera() {
		if( mCamera != null ) {
			mCamera.release();
			mCamera = null;
		}
	}
	
	/**
	 * change camera front/back
	 */
	public void switchCamera() {		
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		Camera.getCameraInfo(mCurrentCameraId, cameraInfo);
		if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
			showPreview(Camera.CameraInfo.CAMERA_FACING_FRONT);
		} else {
			showPreview(Camera.CameraInfo.CAMERA_FACING_BACK);
		}
	}
	
	/**
	 * 
	 * @param facing front/back
	 * @return
	 */
	private Camera getCameraInstance(int facing) {
		int cameraCount = 0;
		Camera cam = null;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras();
		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			if (cameraInfo.facing == facing) {
				try {
					cam = Camera.open(camIdx);
					setCameraDisplayOrientation(getActivity(), camIdx, cam);
					
					//enable auto focus
					Camera.Parameters params = cam.getParameters();
					List<String> focusModes = params.getSupportedFocusModes();
					if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
						params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
						cam.setParameters(params);
					}

					mCurrentCameraId = camIdx;
				} catch (RuntimeException e) {
					Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
				}
			}
		}

		return cam;
	}

	private void setCameraDisplayOrientation(FragmentActivity activity,
			int cameraId, android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}

	/**
	 * capture image and finish
	 */
	public void captureImage(OnCaptureListener onCaptureListener) {
		if(!isCapturing) {
			this.onCaptureListener = onCaptureListener;
			mCamera.autoFocus(autoFocusCallback);
			isCapturing = true;
		}
	}

	AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			showProgressDialog();
			mCamera.takePicture(null, null, mPictureCallback);
		}
	};

	private PictureCallback mPictureCallback = new PictureCallback() {
	    @Override
	    public void onPictureTaken(byte[] data, Camera camera) {
	    	dismissProgressDialog();
	    	String fileName = Calendar.getInstance().getTimeInMillis() + Constants.JPG;
	        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE, fileName);
	        try {
	            Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);
	            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
	    		android.hardware.Camera.getCameraInfo(mCurrentCameraId, info);
				int camDegree = result;
				if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					camDegree = (360 - camDegree) % 360; // compensate the mirror
				}

	            Bitmap bitmap = BitmapUtils.rotate(realImage, camDegree);

	            FileOutputStream fos = new FileOutputStream(pictureFile);
	            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
	            fos.close();
	        } catch (FileNotFoundException e) {
	            Log.d(TAG, "File not found: " + e.getMessage());
	        } catch (IOException e) {
	            Log.d(TAG, "Error accessing file: " + e.getMessage());
	        }

	        if( onCaptureListener != null ) {
	        	onCaptureListener.onCapture(getOutputMediaFileUri(MEDIA_TYPE_IMAGE, fileName));
	        }

	        if( mCamera!= null ) {
				mCamera.startPreview();
			}

	        isCapturing = false;
	    }
	};

	@SuppressLint("NewApi")
	private boolean prepareVideoRecorder(boolean useLowProfile){
		String fileName = Calendar.getInstance().getTimeInMillis() + Constants.MP4;
	    mMediaRecorder = new MediaRecorder();

	    // Step 1: Unlock and set camera to MediaRecorder
	    mCamera.unlock();
	    mMediaRecorder.setCamera(mCamera);

	    // Step 2: Set sources
	    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
	    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

	    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
	    if(useLowProfile) {
	    	mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
	    } else {
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
				// try to get an valid profile
				if(CamcorderProfile.hasProfile(mCurrentCameraId, CamcorderProfile.QUALITY_480P)) {
					mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
				} else {
					mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
				}
			} else {
				mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
			}
	    }

	    // Step 4: Set output file
	    mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO, fileName).toString());

	    // Step 5: Set the preview output
	    mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

	    //set rotation hint
	    android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(mCurrentCameraId, info);
		mMediaRecorder.setOrientationHint(info.orientation);

	    // Step 6: Prepare configured MediaRecorder
	    try {
	        mMediaRecorder.prepare();
	    } catch (IllegalStateException e) {
	        Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    } catch (IOException e) {
	        Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    }
	    return true;
	}

	private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

	public boolean isVideoRecording() {
		return isRecording;
	}

	/**
	 * start/stop record video and return if operation success or fail
	 * @param onCaptureListener
	 * @return
	 */
	public boolean recordVideo( OnCaptureListener onCaptureListener ) {
		this.onCaptureListener = onCaptureListener;
		boolean success = true;
		String fileName = Calendar.getInstance().getTimeInMillis() + Constants.MP4;
		if (isRecording) {
            // stop recording and release camera
            mMediaRecorder.stop();  // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock();         // take camera access back from MediaRecorder

            // inform the user that recording has stopped
            isRecording = false;

            //return result to parent
            if( this.onCaptureListener != null ) {
            	this.onCaptureListener.onCapture(getOutputMediaFileUri(MEDIA_TYPE_VIDEO, fileName));
	        }
        } else {
            // initialize video camera
        	// use try-catch to make sure we can get the camera work
        	try {
	            if (prepareVideoRecorder(false)) {
	                // Camera is available and unlocked, MediaRecorder is prepared,
	                // now you can start recording
	                mMediaRecorder.start();

	                // inform the user that recording has started
	                isRecording = true;
	            } else {
	            	success = false;
	                // prepare didn't work, release the camera
	                releaseMediaRecorder();
	                // inform user
	            }
        	} catch (Exception e) {
        		// try to record again with low profile
        		releaseMediaRecorder();
        		if (prepareVideoRecorder(true)) {
	                mMediaRecorder.start();
	                isRecording = true;
	            } else {
	            	success = false;
	                releaseMediaRecorder();
	            }
        	}
        }

		return success;
	}

	/** Create a file Uri for saving an image or video */
	private Uri getOutputMediaFileUri(int type, String fileName){
	      return Uri.fromFile(getOutputMediaFile(type, fileName));
	}

	/** Create a File for saving an image or video */
	private File getOutputMediaFile(int type, String fileName){
	    File dir = new File(Constants.SDCARD_CACHE_PREFIX);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
//	        mediaFile = new File(Constant.SDCARD_TAKE_PHOTO_CACHE_PREFIX);
        	mediaFile = new File(Constants.SDCARD_CACHE_PREFIX + "/" + "image" + fileName);
	    } else if(type == MEDIA_TYPE_VIDEO) {
//	        mediaFile = new File(Constant.SDCARD_TAKE_VIDEO_CACHE_PREFIX);
	        mediaFile = new File(Constants.SDCARD_CACHE_PREFIX + "/" + "video" + fileName);
	    } else {
	        return null;
	    }

	    return mediaFile;
	}

	//flash mode
	public List<String> getSupportedFlashModes() {
		if( mCamera != null ) {
			Camera.Parameters parameters = mCamera.getParameters();
			return parameters.getSupportedFlashModes();
		}
		return null;
	}

	public void setFlashMode(String mode) {
		if( mCamera != null ) {
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setFlashMode(mode);
			mCamera.setParameters(parameters);
		}
	}
	
	public void setPreviewListener(OnPreviewListener onPreviewListener) {
		this.onPreviewListener = onPreviewListener;
	}
	
	/**
	 * interface for receiving captured image file
	 *
	 */
	public static interface OnCaptureListener {
		void onCapture(Uri uri);
	}
	
	/**
	 * interface for receiving preview start
	 *
	 */
	public static interface OnPreviewListener {
		void onPreview();
	}

}

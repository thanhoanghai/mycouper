package com.nct.mv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.edmodo.cropper.CropImageView;
import com.javacodegeeks.androidcameraexample.CameraPreview;
import com.nct.constants.Constants;
import com.nct.customview.TfTextView;
import com.nct.model.ItemCreateKard;
import com.nct.utils.Debug;
import com.nct.utils.Utils;

import thh.com.mycouper.R;

public class AtCamera extends Activity implements OnClickListener {

    private static final int REQUEST_CODE_PICK_LIBRARY = 0;
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;

	private Camera mCamera;
	private CameraPreview mPreview;
	private PictureCallback mPicture;
	private ImageView capture, switchCamera, closeCamera, btnGallery;
    private TfTextView btnFlast;
	private Context myContext;
	private LinearLayout cameraPreview;
    LinearLayout lyCropImage;
    CropImageView cropImageView;
    TfTextView txtCancel, txtRetake, txtUse;
    LinearLayout progressLayout;

	private boolean cameraFront = false;

    private Uri resultFileUri;
    Bitmap croppedImage;
    Bitmap bitmapCamera;
    private boolean isFrontBitmap = false;
    private int mCurrentCameraId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_camera);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		myContext = this;
        if(getIntent() != null)
            isFrontBitmap = getIntent().getBooleanExtra(Constants.KEY_BUNDLE_BOOLEAN_VALUE, false);
		initialize();
	}

	private int findFrontFacingCamera() {
		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				cameraId = i;
                mCurrentCameraId = cameraId;
				cameraFront = true;
				break;
			}
		}
		return cameraId;
	}

	private int findBackFacingCamera() {
		int cameraId = -1;
		//Search for the back facing camera
		//get the number of cameras
		int numberOfCameras = Camera.getNumberOfCameras();
		//for every camera check
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
				cameraId = i;
                mCurrentCameraId = cameraId;
				cameraFront = false;
				break;
			}
		}
		return cameraId;
	}

	public void onResume() {
		super.onResume();
		if (!hasCamera(myContext)) {
			Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
			toast.show();
			finish();
		}
		if (mCamera == null) {
			//if the front facing camera does not exist
			if (findFrontFacingCamera() < 0) {
				Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
				switchCamera.setVisibility(View.GONE);
			}			
			mCamera = Camera.open(findBackFacingCamera());
			mPicture = getPictureCallback();
			mPreview.refreshCamera(mCamera);
		}
	}

	public void initialize() {
		cameraPreview = (LinearLayout) findViewById(R.id.camera_preview);
		mPreview = new CameraPreview(myContext, mCamera);
		cameraPreview.addView(mPreview);

		capture = (ImageView) findViewById(R.id.button_capture);
        capture.setOnClickListener(this);
		switchCamera = (ImageView) findViewById(R.id.button_ChangeCamera);
		switchCamera.setOnClickListener(this);
        closeCamera = (ImageView) findViewById(R.id.imageView_close);
        closeCamera.setOnClickListener(this);
        btnGallery = (ImageView) findViewById(R.id.button_gallery);
        btnGallery.setOnClickListener(this);
        btnFlast = (TfTextView) findViewById(R.id.btn_flast);
        btnFlast.setOnClickListener(this);

        lyCropImage = (LinearLayout) findViewById(R.id.lyCropImage);
        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        // Sets initial aspect ratio to 10/10, for demonstration purposes
        cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);

        txtCancel = (TfTextView) findViewById(R.id.txtCancel);
        txtCancel.setOnClickListener(this);
        txtRetake = (TfTextView) findViewById(R.id.txtRetake);
        txtRetake.setOnClickListener(this);
        txtUse = (TfTextView) findViewById(R.id.txtUse);
        txtUse.setOnClickListener(this);
        progressLayout = (LinearLayout) findViewById(R.id.progress_layout);
	}

	public void chooseCamera() {
		//if the camera preview is the front
		if (cameraFront) {
			int cameraId = findBackFacingCamera();
			if (cameraId >= 0) {
				//open the backFacingCamera
                mCamera = Camera.open(cameraId);
                //set a picture callback
                mPicture = getPictureCallback();
                //refresh the preview
				mPreview.refreshCamera(mCamera);
			}
		} else {
			int cameraId = findFrontFacingCamera();
			if (cameraId >= 0) {
				//open the backFacingCamera
                mCamera = Camera.open(cameraId);
				//set a picture callback
                mPicture = getPictureCallback();
				//refresh the preview
				mPreview.refreshCamera(mCamera);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		//when on Pause, release camera in order to be used from other applications
		releaseCamera();
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_capture:
                progressLayout.setVisibility(View.VISIBLE);
                mCamera.takePicture(null, null, mPicture);
                closeCamera.setClickable(false);
                capture.setClickable(false);
                break;
            case R.id.button_ChangeCamera:
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    //release the old camera instance
                    releaseCamera();
                    //switch camera, from the front and the back and vice versa
                    chooseCamera();
                } else {
                    Debug.toast(this, "Sorry, your phone has only one camera!");
                }
                break;
            case R.id.imageView_close:
                finish();
                break;
            case R.id.button_gallery:
                getFromLibrary();
                break;
            case R.id.btn_flast:

                break;
            case R.id.txtCancel:
                cropImageView.setImageBitmap(bitmapCamera);
                break;
            case R.id.txtRetake:
                lyCropImage.setVisibility(View.GONE);
                break;
            case R.id.txtUse:
                if(isFrontBitmap)
                    ItemCreateKard.bitmapFront = cropImageView.getCroppedImage();
                else
                    ItemCreateKard.bitmapBack = cropImageView.getCroppedImage();
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                break;
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( resultCode == Activity.RESULT_OK ) {
            switch ( requestCode ) {
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

    private boolean hasCamera(Context context) {
		//check if the device has camera
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}

    private PictureCallback mPictureCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String fileName = Calendar.getInstance().getTimeInMillis() + Constants.JPG;
            File pictureFile = getOutputMediaFile(fileName);
            try {
                Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);
                android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
                android.hardware.Camera.getCameraInfo(mCurrentCameraId, info);

                FileOutputStream fos = new FileOutputStream(pictureFile);
                realImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                e.getMessage();
            } catch (IOException e) {
                e.getMessage();
            }

            if( mCamera!= null ) {
                mCamera.startPreview();
            }
        }
    };

    private PictureCallback getPictureCallback() {
		PictureCallback picture = new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				//make a new picture file
//				File pictureFile = getOutputMediaFile();
//
//				if (pictureFile == null) {
//					return;
//				}
//				try {
//					//write the file
//					FileOutputStream fos = new FileOutputStream(pictureFile);
//					fos.write(data);
//					fos.close();
//				} catch (FileNotFoundException e) {
//				} catch (IOException e) {
//				}

                String fileName = Calendar.getInstance().getTimeInMillis() + Constants.JPG;
                File pictureFile = getOutputMediaFile(fileName);
                try {
                    Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);
                    android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
                    android.hardware.Camera.getCameraInfo(mCurrentCameraId, info);

                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    realImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.getMessage();
                } catch (IOException e) {
                    e.getMessage();
                }

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getOutputMediaFileUri(fileName));
                    setPreviewBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                lyCropImage.setVisibility(View.VISIBLE);
//                bitmapCamera = Utils.scaleDownBitmap(pictureFile.getPath());
                progressLayout.setVisibility(View.GONE);
                capture.setClickable(true);
                closeCamera.setClickable(true);
//                cropImageView.setImageBitmap(bitmapCamera);
				//refresh camera to continue preview
				mPreview.refreshCamera(mCamera);
			}
		};
		return picture;
	}

    private void setPreviewBitmap(Bitmap bitmap) {
        bitmapCamera = getResizeBitmap(bitmap);
        cropImageView.setImageBitmap(bitmapCamera);
    }

    private int mBitmapWidth, mBitmapHeight;

    /**
     * resize input bitmap to save memory
     * @return
     */
    private Bitmap getResizeBitmap(Bitmap bitmap) {
        mBitmapWidth = bitmap.getWidth();
        mBitmapHeight = bitmap.getHeight();

        if(mBitmapWidth > Constants.BITMAP_MAX_SIZE || mBitmapHeight > Constants.BITMAP_MAX_SIZE) {
            if(mBitmapWidth > mBitmapHeight) {
                // scale base on width
                mBitmapHeight = mBitmapHeight * Constants.BITMAP_MAX_SIZE / mBitmapWidth;
                mBitmapWidth = Constants.BITMAP_MAX_SIZE;
            } else {
                // scale base on height
                mBitmapWidth = mBitmapWidth * Constants.BITMAP_MAX_SIZE / mBitmapHeight;
                mBitmapHeight = Constants.BITMAP_MAX_SIZE;
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, mBitmapWidth, mBitmapHeight, true);
        }

        return bitmap;
    }

	//make picture and save to a folder
	private static File getOutputMediaFile() {
		//make a new file directory inside the "sdcard" folder
		File mediaStorageDir = new File("/sdcard/", "JCG Camera");
		//if this "JCGCamera folder does not exist
		if (!mediaStorageDir.exists()) {
			//if you cannot make this folder return
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		//take the current timeStamp
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		//and make a media file:
		mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		return mediaFile;
	}

    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(String fileName){
        return Uri.fromFile(getOutputMediaFile(fileName));
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(String fileName){
        File dir = new File(Constants.SDCARD_CACHE_PREFIX);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File mediaFile;
        mediaFile = new File(Constants.SDCARD_CACHE_PREFIX + "/" + "image" + fileName);

        return mediaFile;
    }

    /**
     * send result image uri to parent
     */
    void fireResult() {
        try {
            bitmapCamera = MediaStore.Images.Media.getBitmap(getContentResolver(), resultFileUri);
            lyCropImage.setVisibility(View.VISIBLE);
            cropImageView.setImageBitmap(bitmapCamera);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	private void releaseCamera() {
		// stop and release camera
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}
}
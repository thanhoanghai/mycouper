package com.nct.mv;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.edmodo.cropper.CropImageView;
import com.nct.constants.Constants;
import com.nct.customview.Preview;
import com.nct.customview.TfTextView;
import com.nct.utils.BitmapUtils;
import com.nct.utils.Utils;

import thh.com.mycouper.R;

public class AtCamera extends Activity implements OnClickListener {

    // Static final constants
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;
    private static final int REQUEST_CODE_PICK_LIBRARY = 0;

    private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
    private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";
    // Instance variables
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;

	ImageView image;
	Activity context;
	Preview preview;
	Camera camera;
    ImageView exitButton;
	ImageView fotoButton;
	LinearLayout progressLayout;

    LinearLayout lyCropImage;
    CropImageView cropImageView;
    TfTextView txtCancel, txtRetake, txtUse;

	String path = "/sdcard/KutCamera/cache/images/";

    private Uri resultFileUri;
    Bitmap croppedImage;
    Bitmap bitmapCamera;

    private int mCurrentCameraId;
    private int result = 0;

    // Saves the state upon rotating the screen/restarting the activity
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
    }

    // Restores the state upon rotating the screen/restarting the activity
    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.at_camera);
		
		context=this;
		
		fotoButton = (ImageView) findViewById(R.id.imageView_foto);
        fotoButton.setOnClickListener(this);
		exitButton = (ImageView) findViewById(R.id.imageView_close);
        exitButton.setOnClickListener(this);
		image = (ImageView) findViewById(R.id.imageView_photo);
        image.setOnClickListener(this);
		progressLayout = (LinearLayout) findViewById(R.id.progress_layout);

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
		
		preview = new Preview(this, (SurfaceView) findViewById(R.id.KutCameraFragment));

		FrameLayout frame = (FrameLayout) findViewById(R.id.preview);
		frame.addView(preview);
		preview.setKeepScreenOn(true);

	}

	@Override
	protected void onResume() {
		super.onResume();
		// TODO Auto-generated method stub
		if(camera==null){
		camera = Camera.open();
		camera.startPreview();
		camera.setErrorCallback(new ErrorCallback() {
			public void onError(int error, Camera mcamera) {

				camera.release();
				camera = Camera.open();
				Log.d("Camera died", "error camera");
			}
		});
		}
		if (camera != null) {
			if (Build.VERSION.SDK_INT >= 14)
				setCameraDisplayOrientation(context,
						CameraInfo.CAMERA_FACING_BACK, camera);
			preview.setCamera(camera);
		}
	}
	
	private void setCameraDisplayOrientation(Activity activity, int cameraId,
			Camera camera) {
		CameraInfo info = new CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
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

		if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}


	
	Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
				
			try{
			    camera.takePicture(mShutterCallback, null, mPictureCallback);
			}catch(Exception e){
				
			}

		}
	};

    ShutterCallback mShutterCallback = new ShutterCallback() {

        @Override
        public void onShutter() {
            // TODO Auto-generated method stub

        }
    };

    public void takeFocusedPicture() {
        camera.autoFocus(mAutoFocusCallback);

    }

    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // Log.d(TAG, "onPictureTaken - raw");
        }
    };

    public static final int MEDIA_TYPE_IMAGE = 1;
    private PictureCallback mPictureCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            String fileName = Calendar.getInstance().getTimeInMillis() + Constants.JPG;
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE, fileName);
            try {
                Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(mCurrentCameraId, info);
                int camDegree = result;
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    camDegree = (360 - camDegree) % 360; // compensate the mirror
                }

                Bitmap bitmap = BitmapUtils.rotate(realImage, camDegree);

                FileOutputStream fos = new FileOutputStream(pictureFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d("Camera", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("Camera", "Error accessing file: " + e.getMessage());
            }

            resultFileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE, fileName);

            if( camera!= null ) {
                camera.startPreview();
            }

            fotoButton.setClickable(true);
            camera.startPreview();
            progressLayout.setVisibility(View.GONE);
            exitButton.setClickable(true);

            fireResult();
        }
    };

    PictureCallback jpegCallback = new PictureCallback() {
        @SuppressWarnings("deprecation")
        public void onPictureTaken(byte[] data, Camera camera) {
            String mPath = "";
            FileOutputStream outStream = null;
            Calendar c = Calendar.getInstance();
            File videoDirectory = new File(path);
            mPath = path + c.getTime().getSeconds() + ".jpg";
            if (!videoDirectory.exists()) {
                videoDirectory.mkdirs();
            }
            try {

                // Write to SD Card
                outStream = new FileOutputStream(mPath);
                outStream.write(data);
                outStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }


            bitmapCamera = Utils.scaleDownBitmap(mPath);
//            final BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 5;
//
//            options.inPurgeable = true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
//
//            options.inInputShareable = true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
//
//
//            realImage = BitmapFactory.decodeByteArray(data, 0, data.length, options);

            ExifInterface exif = null;
            try {
                exif = new ExifInterface(path + c.getTime().getSeconds()
                        + ".jpg");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

//            try {
//                Log.d("EXIF value",
//                        exif.getAttribute(ExifInterface.TAG_ORIENTATION));
//                if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
//                        .equalsIgnoreCase("1")) {
//                    realImage = rotate(realImage, 90);
//                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
//                        .equalsIgnoreCase("8")) {
//                    realImage = rotate(realImage, 90);
//                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
//                        .equalsIgnoreCase("3")) {
//                    realImage = rotate(realImage, 90);
//                } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION)
//                        .equalsIgnoreCase("0")) {
//                    realImage = rotate(realImage, 90);
//                }
//            } catch (Exception e) {
//
//            }

            lyCropImage.setVisibility(View.VISIBLE);
            cropImageView.setImageBitmap(bitmapCamera, exif);
//            image.setImageBitmap(realImage);

            fotoButton.setClickable(true);
            camera.startPreview();
            progressLayout.setVisibility(View.GONE);
            exitButton.setClickable(true);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_foto:
                try {
                    takeFocusedPicture();
                } catch (Exception e) {

                }
                exitButton.setClickable(false);
                fotoButton.setClickable(false);
                progressLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.imageView_close:
                System.gc();
                this.finish();
                break;
            case R.id.imageView_photo:
                getFromLibrary();
                break;
            case R.id.txtCancel:
                cropImageView.setImageBitmap(bitmapCamera);
                break;
            case R.id.txtRetake:
                lyCropImage.setVisibility(View.GONE);
                break;
            case R.id.txtUse:
                croppedImage = cropImageView.getCroppedImage();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                croppedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.KEY_BUNDLE_CARD_INFO_BITMAP, byteArray);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                break;
        }
    }

    /**
     * change camera front/back
     */
    public void switchCamera() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(mCurrentCameraId, cameraInfo);
//        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
//            showPreview(Camera.CameraInfo.CAMERA_FACING_FRONT);
//        } else {
//            showPreview(Camera.CameraInfo.CAMERA_FACING_BACK);
//        }
    }

    public static Bitmap rotate(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, false);
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

    /**
     * get image from library
     */
    private void getFromLibrary() {
        Intent intentPhotoLibrary = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intentPhotoLibrary.setType("image/*");
        startActivityForResult(intentPhotoLibrary, REQUEST_CODE_PICK_LIBRARY);
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type, String fileName){
        File dir = new File(Constants.SDCARD_CACHE_PREFIX);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(Constants.SDCARD_CACHE_PREFIX + "/" + "image" + fileName);
        } else {
            return null;
        }

        return mediaFile;
    }

    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(int type, String fileName){
        return Uri.fromFile(getOutputMediaFile(type, fileName));
    }

}

package com.nct.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;

import com.nct.background.ImageLoader;
import com.nct.background.ImageLoader.OnImageLoadingFinishListener;
import com.nct.constants.Constants;
import com.nct.customview.SpanZoomImageView;
import com.nct.utils.BitmapUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import thh.com.mycouper.R;

public class EffectCameraFragment extends Fragment {
	
	/*
	 * Effect image
	 */
	public static final String EXT_DATA_URI = "KBKardEffect24Fragment_ext_data_uri";
	
	private Uri imageUri;
	private SpanZoomImageView kardImage;

	//defaulte image byte
	private byte[] defaultBitmapByte;
	private int mBitmapWidth, mBitmapHeight;
	
	private int mKardPreviewFrameHeight;
	//kard height percent of preview frame
	private int kardPreviewHeightPercent = 90;
	
	private Dialog mProgress = null;
	
	/*
	 * native libraries for do image effect
	 */
	static { 	
	    System.loadLibrary("yuv420sp2rgb");
	} 
		
	/*
	 * do image effact function
	 */
	private native void rgbappliedeffect(byte[] in, int width, int height, byte[] out, byte[] overlay,  int effect, float brightness);
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.effect_camera_fragment, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);		
		Bundle bundle = getArguments();
		if( bundle != null ) {
			imageUri = bundle.getParcelable(EXT_DATA_URI);
		}		
			
		kardImage = (SpanZoomImageView) getView().findViewById(R.id.camera_image);

		if( imageUri != null ) {
			//load new image			
			if(imageUri.getScheme().contains("http")) {
				//image from web - instagram
				showProgressDialog();
				ImageLoader.getInstance().loadImageCallback(imageUri.toString(), 0, BitmapUtils.ROUND_NONE, 0, 0, mImageLoadedListener);
			} else {
				//local image
				try {															
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
					setPreviewBitmap(bitmap);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}	    
			}
		} 
				
		kardImage.setEnableEdit(false);
					
		//caculate zoom percent
		final FrameLayout preview = (FrameLayout) getView().findViewById(R.id.camera_preview_cover);
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
                }
            });
        }
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
	
	private OnImageLoadingFinishListener mImageLoadedListener = new OnImageLoadingFinishListener() {
		@Override
		public void onLoadingFinished(String url, Bitmap resultBitmap) {
			dismissProgressDialog();
			setPreviewBitmap(resultBitmap);
		}
	};
	
	/**
	 * resize input bitmap and set to preview frame
	 * @param bitmap
	 */
	private void setPreviewBitmap(Bitmap bitmap) {
		bitmap = getResizeBitmap(bitmap);
		kardImage.setImageBitmap(bitmap);
	}

	/**
	 * write result bitmap to file, same as we take a new photo
	 * @return
	 */
	public Uri getResultUri() {
		Uri resultFileUri = null;
		
		File dir = new File(Constants.SDCARD_CACHE_PREFIX);
		if (!dir.exists()) {
			dir.mkdirs();
		}
        File pictureFile = new File(Constants.SDCARD_TAKE_PHOTO_CACHE_PREFIX);
        try{
	        FileOutputStream fOut = new FileOutputStream(pictureFile);
	
	        Bitmap bitmap = ((BitmapDrawable)kardImage.getDrawable()).getBitmap();
	        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		    fOut.flush();
		    fOut.close();		    
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        resultFileUri = Uri.fromFile(pictureFile);
        
        return resultFileUri;
	}
	
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
	
}

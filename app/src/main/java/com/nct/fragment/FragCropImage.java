package com.nct.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.nct.customview.TfTextView;
import com.nct.model.ItemCreateKard;
import com.nct.mv.AtCamera;
import com.nct.utils.BitmapUtils;
import com.theartofdev.edmodo.cropper.CropImageView;

import thh.com.mycouper.R;

/**
 * Created by nghidv on 5/23/2015.
 */
public class FragCropImage extends Fragment implements View.OnClickListener {

    public static final String KEY_BUNDLE_PHOTO_GALLERY = "KEY_BUNDLE_PHOTO_GALLERY";
    public static final String KEY_BUNDLE_PHOTO_IS_GALLERY = "KEY_BUNDLE_PHOTO_IS_GALLERY";

    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;

    private CropImageView cropImageView;
    private TfTextView txtCancel, txtRetake, txtUse;

    public static byte[] imageToShow=null;
    private String imgPath = null;
    private Bitmap bitmapCamera;

    private boolean isLibrary = false;
    private int bitmapWidth = 0;
    private int bitmapHeight = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            isLibrary = getArguments().getBoolean(KEY_BUNDLE_PHOTO_IS_GALLERY, false);
            imgPath = getArguments().getString(KEY_BUNDLE_PHOTO_GALLERY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.frag_crop_image, container, false);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        bitmapWidth = metrics.widthPixels;
        bitmapHeight = metrics.heightPixels - 70;

        cropImageView = (CropImageView) v.findViewById(R.id.CropImageView);
        cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);
        cropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
        cropImageView.setFixedAspectRatio(false);

        txtCancel = (TfTextView) v.findViewById(R.id.txtCancel);
        txtCancel.setOnClickListener(this);
        txtRetake = (TfTextView) v.findViewById(R.id.txtRetake);
        txtRetake.setOnClickListener(this);
        txtUse = (TfTextView) v.findViewById(R.id.txtUse);
        txtUse.setOnClickListener(this);

        if(imgPath != null){
            getBitmap(imgPath);
        }else if(imageToShow != null){
            showImageCapture();
        }else
            Toast.makeText(getActivity(), "Get photo error!", Toast.LENGTH_LONG).show();
        return v;
    }

    private void showImageCapture(){
        BitmapFactory.Options opts=new BitmapFactory.Options();

        opts.inPurgeable=true;
        opts.inInputShareable=true;
        opts.inMutable=false;
        opts.inSampleSize=2;

        bitmapCamera = BitmapFactory.decodeByteArray(imageToShow, 0, imageToShow.length, opts);
        imageToShow=null;
        if(bitmapCamera != null)
            cropImageView.setImageBitmap(bitmapCamera);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtCancel:
                if (bitmapCamera != null) {
                    cropImageView.setImageBitmap(bitmapCamera);
                }
                break;
            case R.id.txtRetake:
                ((AtCamera)getActivity()).actionBackKey();
                break;
            case R.id.txtUse:
                if(bitmapCamera != null){
                    if(((AtCamera)getActivity()).isFrontBitmap)
                        ItemCreateKard.bitmapFront = cropImageView.getCroppedImage();
                    else
                        ItemCreateKard.bitmapBack = cropImageView.getCroppedImage();
                }
                Intent resultIntent = new Intent();
                getActivity().setResult(Activity.RESULT_OK, resultIntent);
                getActivity().finish();
                break;
        }
    }

    private void getBitmap(String path){
        try {
            bitmapCamera = BitmapUtils.scaleDownBitmap(path, bitmapWidth, bitmapHeight);
            if (bitmapCamera != null) {
                cropImageView.setImageBitmap(bitmapCamera);
            }
        }catch (OutOfMemoryError e){
            e.getMessage();
            Log.e("NghiDo_", "out of memory");
        }
    }
}

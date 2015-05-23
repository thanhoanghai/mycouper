package com.nct.mv;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import com.nct.constants.Constants;
import com.nct.customview.TfTextView;
import com.nct.model.ItemCreateKard;
import com.nct.utils.BitmapUtils;
import com.nct.utils.Debug;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import thh.com.mycouper.R;

public class AtWacCamera extends Activity implements View.OnClickListener {

    public static final String GET_PICTURE_FROM_GALLERY = "GET_PICTURE_FROM_GALLERY";

    private static final int SELECT_PICTURE = 1;
    private static final int TAKE_PICTURE = 2;
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 10;

    CropImageView cropImageView;
    TfTextView txtCancel, txtRetake, txtUse, txtGallery;

    Bitmap bitmapCamera;

    private boolean isLibrary = false;
    private boolean isFrontBitmap = false;
    private int bitmapWidth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.at_wac_camera);

        if(getIntent() != null){
            isFrontBitmap = getIntent().getBooleanExtra(Constants.KEY_BUNDLE_BOOLEAN_VALUE, false);
            isLibrary = getIntent().getBooleanExtra(GET_PICTURE_FROM_GALLERY, false);
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        bitmapWidth = metrics.widthPixels;

        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES, DEFAULT_ASPECT_RATIO_VALUES);
        cropImageView.setCropShape(CropImageView.CropShape.RECTANGLE);
        cropImageView.setFixedAspectRatio(false);

        txtGallery = (TfTextView) findViewById(R.id.txtGallery);
        txtGallery.setOnClickListener(this);
        txtCancel = (TfTextView) findViewById(R.id.txtCancel);
        txtCancel.setOnClickListener(this);
        txtRetake = (TfTextView) findViewById(R.id.txtRetake);
        txtRetake.setOnClickListener(this);
        txtUse = (TfTextView) findViewById(R.id.txtUse);
        txtUse.setOnClickListener(this);
        if(isLibrary){
            getImageFromGarelly();
        }else{
            takePhoto();
        }
    }

    Uri mImageUri = null;
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String imgName = System.currentTimeMillis() + ".jpg";
        File photo = new File(Environment.getExternalStorageDirectory(), imgName);
        mImageUri = Uri.fromFile(photo);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageUri);
        try {
            intent.putExtra("return-data", true);
            startActivityForResult(intent, TAKE_PICTURE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void getImageFromGarelly() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn nguồn ảnh"), SELECT_PICTURE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtCancel:
                cropImageView.setImageBitmap(bitmapCamera);
                break;
            case R.id.txtGallery:
                getImageFromGarelly();
                break;
            case R.id.txtRetake:
                takePhoto();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case TAKE_PICTURE:
                    if (mImageUri != null) {
                        getBitmap(mImageUri.getPath());
                    }else
                        Debug.toast(this, "Capture photo error!");
                    break;
                case SELECT_PICTURE:
                    if (data != null){
                        String imgPath = null;
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
                            imgPath = getRealPathFromURI(this, data.getData());
                        }else {
                            imgPath = getImagePath(data.getData());
                        }
                        getBitmap(imgPath);
                    }else
                        Debug.toast(this, "Get photo from gallery failed!");
                    break;
            }
        }else
            this.finish();
    }

    private void getBitmap(String path){
        try {
            bitmapCamera = BitmapUtils.scaleDownBitmap(path, bitmapWidth, bitmapWidth/2);
            if (bitmapCamera != null) {
                cropImageView.setImageBitmap(bitmapCamera);
            }
        }catch (OutOfMemoryError e){
            e.getMessage();
            Log.e("NghiDo_", "out of memory");
        }
    }

    /**
     * API < KITKAT(19)
     * @param context
     * @param contentUri
     * @return
     */
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            if (cursor == null) {
                return contentUri.getPath();
            } else {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * API > KITKAT(19)
     * @param uri
     * @return
     */
    public String getImagePath(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
}

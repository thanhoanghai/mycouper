package com.nct.mv;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import com.nct.constants.Constants;
import com.nct.fragment.FragCropImage;
import com.nct.fragment.FragMainCamera;
import com.nct.utils.Debug;

import java.util.HashMap;
import java.util.Stack;

import thh.com.mycouper.R;

/**
 * Created by nghidv on 5/23/2015.
 */
public class AtCamera extends FragmentActivity implements FragMainCamera.Contract {

    private static final int SELECT_PICTURE = 1;

    private HashMap<String, Stack<Fragment>> mStacks;
    private String mCurrentTab = Constants.TAB_CAMERA_CAMERA;

    private FragMainCamera std=null;
    private FragMainCamera ffc=null;
    private FragMainCamera current=null;
    private boolean hasTwoCameras=(Camera.getNumberOfCameras() > 1);

    private boolean singleShot=false;
    private boolean isFrontCamera = false;
    public boolean isFrontBitmap = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.at_camera);

        if(getIntent() != null)
            isFrontBitmap = getIntent().getBooleanExtra(Constants.KEY_BUNDLE_BOOLEAN_VALUE, false);

        initSaveFragment();

        if (hasTwoCameras) {
            if (std == null) {
                std= FragMainCamera.newInstance(false);
            }
            current = std;
        }
        else {
            current = FragMainCamera.newInstance(false);
        }
        pushFragments(Constants.TAB_CAMERA_CAMERA, current, false, true);
    }

    private void initSaveFragment()
    {
        mStacks = new HashMap<String, Stack<Fragment>>();
        mStacks.put(Constants.TAB_CAMERA_CAMERA, new Stack<Fragment>());
    }

    public void getImageFromGarelly() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn nguồn ảnh"), SELECT_PICTURE);
    }

    @Override
    public void onBackPressed() {
        actionBackKey();
    }

    public void actionBackKey() {
        if (mStacks.get(mCurrentTab).size() == 1) {
            super.onBackPressed(); // or call finish..
        } else {
            popFragments();
        }
    }
    public void popFragments() {
        Fragment fragment = mStacks.get(mCurrentTab).elementAt(mStacks.get(mCurrentTab).size() - 2);

		/* pop current fragment from stack.. */
        mStacks.get(mCurrentTab).pop();
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    public void pushFragments(String tag, Fragment fragment, boolean shouldAnimate, boolean shouldAdd) {
        if (shouldAdd)
            mStacks.get(tag).push(fragment);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }
    
    public void changeCamera(){
        if (isFrontCamera) {
            if(std == null) {
                std=FragMainCamera.newInstance(false);
            }
            current=std;
        }
        else {
            if(ffc == null) {
                ffc = FragMainCamera.newInstance(true);
            }
            current=ffc;
        }
        isFrontCamera = !isFrontCamera;
        pushFragments(Constants.TAB_CAMERA_CAMERA, current, false, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case SELECT_PICTURE:
                    if (data != null){
                        String imgPath = null;
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
                            imgPath = getRealPathFromURI(this, data.getData());
                        }else {
                            imgPath = getImagePath(data.getData());
                        }
                        if(imgPath != null){
                            FragCropImage fragment = new FragCropImage();
                            Bundle bundle = new Bundle();
                            bundle.putString(FragCropImage.KEY_BUNDLE_PHOTO_GALLERY, imgPath);
                            bundle.putBoolean(FragCropImage.KEY_BUNDLE_PHOTO_IS_GALLERY, true);
                            fragment.setArguments(bundle);
                            pushFragments(Constants.TAB_CAMERA_CAMERA, fragment, true, true);
                        }else{
                            Debug.toast(this, "Get photo from gallery failed!");
                        }
                    }else
                        Debug.toast(this, "Get photo from gallery failed!");
                    break;
            }
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_CAMERA && current != null) {
            current.takePicture();
            return(true);
        }

        return(super.onKeyDown(keyCode, event));
    }

    @Override
    public boolean isSingleShotMode() {
        return(singleShot);
    }

    @Override
    public void setSingleShotMode(boolean mode) {
        singleShot=mode;
    }

}

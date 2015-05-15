package com.nct.fragment;

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
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nct.constants.Constants;
import com.nct.customview.TfTextView;
import com.nct.mv.AtCreateCard;
import com.nct.utils.Utils;
import com.soundcloud.android.crop.Crop;
import com.soundcloud.android.crop.CropImageActivity;

import java.io.File;
import java.net.URI;

import thh.com.mycouper.R;

public class FragCreateCardImage extends BaseMainFragment {

    private static final int SELECT_PICTURE = 1;
    private static final int TAKE_PICTURE = 2;
    private static final int REQUEST_CODE_CROP_IMAGE = 3;

    private enum MODE_CAMERA_FACE {Front, Back};
    private MODE_CAMERA_FACE modeFace;

    private TfTextView txtCompanyName;
    private Button bntNext;
    private RelativeLayout btnFrontFace;
    private RelativeLayout btnBackFace;

    private String mCompanyName;
    private String mCardCode;
    private String mCardName;
    private String mCardDes;

    public static FragCreateCardImage newInstance() {
        FragCreateCardImage f = new FragCreateCardImage();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mCompanyName = getArguments().getString(Constants.KEY_BUNDLE_CARD_INFO_COMPANYNAME);
            mCardCode = getArguments().getString(Constants.KEY_BUNDLE_CARD_INFO_CARDCODE);
            mCardName = getArguments().getString(Constants.KEY_BUNDLE_CARD_INFO_CARDNAME);
            mCardDes = getArguments().getString(Constants.KEY_BUNDLE_CARD_INFO_CARDDES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.frag_create_card_image, container, false);

        initTopbar(v,"Card Image");
        setTopbarBtLeftImage(R.drawable.icon_back);
        setTopbarLeftBtListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AtCreateCard)getActivity()).actionBackKey();
            }
        });

        txtCompanyName = (TfTextView) v.findViewById(R.id.create_card_companymane);
        if(mCompanyName == null)
            mCompanyName = "";
        txtCompanyName.setText(mCompanyName);

        btnFrontFace = (RelativeLayout) v.findViewById(R.id.btn_front_camera);
        btnFrontFace.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                modeFace = MODE_CAMERA_FACE.Front;
                takePhoto();
            }
        });

        btnBackFace = (RelativeLayout) v.findViewById(R.id.btn_back_camera);
        btnBackFace.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                modeFace = MODE_CAMERA_FACE.Back;
                takePhoto();
            }
        });

        bntNext = (Button) v.findViewById(R.id.frag_create_card_image_bt_next);
        bntNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AtCreateCard)getActivity()).changeFragment(Constants.TYPE_CREATE_CARD_SUCCESS, new FragCreateCardSuccess());
            }
        });

        return v;
    }

    //    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode){
                case TAKE_PICTURE:
                    if (mImageUri != null) {
                        beginCrop(mImageUri);
                    }
                    break;
                case SELECT_PICTURE:
                    if (data != null){
                        beginCrop(data.getData());
                    }
                    break;
                case REQUEST_CODE_CROP_IMAGE:
                    if (data == null) return;
//                    String path = data.getStringExtra(CropImage.IMAGE_PATH);
//                    if (path == null) {
//                        return;
//                    }
//
//                    try {
//                        Bitmap photo = MoMoAvatar.scaleDownBitmap(path);
//                        if (photo != null) {
//                            File f  = MoMoAvatar.saveAvatarInSDCard(this, photo);
//                            if (f != null){
//                                mAvatarFile = f;
//                                MoMoReq.processGetLinkAvatar(this);
//                            }
//                        }
//                    }catch (OutOfMemoryError e){
//                        LogUtils.f("out of memory");
//                    }
                    break;
                case Crop.REQUEST_PICK:
                    beginCrop(data.getData());
                    break;
                case Crop.REQUEST_CROP:
                    handleCrop(resultCode, data);
                    break;
            }
        }
    }

    /**
     * Chụp hình bằng camera của máy
     */
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

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(getActivity());
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == getActivity().RESULT_OK) {
            Uri uri = result.getData();
            Bitmap photo = Utils.scaleDownBitmap(uri.getPath());
//            resultView.setImageURI(Crop.getOutput(result));
            Toast.makeText(getActivity(), "true", Toast.LENGTH_SHORT).show();
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(getActivity(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
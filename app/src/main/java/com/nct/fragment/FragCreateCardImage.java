package com.nct.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nct.constants.Constants;
import com.nct.customview.TfTextView;
import com.nct.mv.AtCamera;
import com.nct.mv.AtCreateCard;
import com.nct.utils.Utils;
import com.soundcloud.android.crop.Crop;
import com.soundcloud.android.crop.CropImageActivity;

import java.io.File;
import java.net.URI;

import thh.com.mycouper.R;

public class FragCreateCardImage extends BaseMainFragment implements OnClickListener {

    private static final int SELECT_PICTURE = 1;
    private static final int TAKE_PICTURE = 2;
    private static final int REQUEST_CODE_CROP_IMAGE = 3;

    private enum MODE_CAMERA_FACE {Front, Back};
    private MODE_CAMERA_FACE modeFace;

    RelativeLayout lyBtnCameraFront, lyBtnCameraBack, lyImageFront, lyImageBack;
    ImageView imgeFront, imageBack;
    ImageView btnFront, btnBack;


    private TfTextView txtCompanyName;
    private Button bntNext;
    private ImageButton btnFrontFace;
    private ImageButton btnBackFace;

    private String mCompanyName;
    private String mCardCode;
    private String mCardName;
    private String mCardDes;

    private Bitmap mBitmapFront, mBitmapBack;

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

        lyBtnCameraFront = (RelativeLayout) v.findViewById(R.id.btn_front_camera);
        lyImageFront = (RelativeLayout) v.findViewById(R.id.lyImageFront);
        lyBtnCameraBack = (RelativeLayout) v.findViewById(R.id.btn_back_camera);
        lyImageBack = (RelativeLayout) v.findViewById(R.id.lyImagBack);

        imgeFront = (ImageView) v.findViewById(R.id.front_Camera);
        btnFront = (ImageView) v.findViewById(R.id.image_camera_front);
        btnFront.setOnClickListener(this);

        imageBack = (ImageView) v.findViewById(R.id.image_back);
        btnBack = (ImageView) v.findViewById(R.id.image_camera_back);
        btnBack.setOnClickListener(this);

        txtCompanyName = (TfTextView) v.findViewById(R.id.create_card_companymane);
        if(mCompanyName == null)
            mCompanyName = "";
        txtCompanyName.setText(mCompanyName);

        btnFrontFace = (ImageButton) v.findViewById(R.id.front_Camera);
        btnFrontFace.setOnClickListener(this);

        btnBackFace = (ImageButton) v.findViewById(R.id.back_Camera);
        btnBackFace.setOnClickListener(this);

        bntNext = (Button) v.findViewById(R.id.frag_create_card_image_bt_next);
        bntNext.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.image_camera_front:
            case R.id.front_Camera:
                modeFace = MODE_CAMERA_FACE.Front;
                intent = new Intent(getActivity(), AtCamera.class);
                getActivity().startActivityForResult(intent, TAKE_PICTURE);
                break;
            case R.id.image_camera_back:
            case R.id.back_Camera:
                modeFace = MODE_CAMERA_FACE.Back;
                intent = new Intent(getActivity(), AtCamera.class);
                getActivity().startActivityForResult(intent, TAKE_PICTURE);
                break;
            case R.id.frag_create_card_image_bt_next:
                ((AtCreateCard)getActivity()).changeFragment(Constants.TYPE_CREATE_CARD_SUCCESS, new FragCreateCardSuccess());
                break;
        }
    }

    public void activityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode){
                case TAKE_PICTURE:
                    byte[] byteArray = data.getByteArrayExtra(Constants.KEY_BUNDLE_CARD_INFO_BITMAP);
                    if(modeFace == MODE_CAMERA_FACE.Front){
                        mBitmapFront = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        if(mBitmapFront != null){
                            lyBtnCameraFront.setVisibility(View.GONE);
                            lyImageFront.setVisibility(View.VISIBLE);
                            imgeFront.setImageBitmap(mBitmapFront);
                        }else{
                            lyBtnCameraFront.setVisibility(View.VISIBLE);
                            lyImageFront.setVisibility(View.GONE);
                        }
                    }else{
                        mBitmapBack = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        if(mBitmapBack != null){
                            lyBtnCameraBack.setVisibility(View.GONE);
                            lyImageBack.setVisibility(View.VISIBLE);
                            imageBack.setImageBitmap(mBitmapBack);
                        }else{
                            lyBtnCameraBack.setVisibility(View.VISIBLE);
                            lyImageBack.setVisibility(View.GONE);
                        }
                    }
                    break;

            }
        }
    }

}
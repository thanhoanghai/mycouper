package com.nct.fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nct.constants.GlobalInstance;
import com.nct.customview.DialogCustom;
import com.nct.customview.DialogRate;
import com.nct.constants.Constants;
import com.nct.customview.TfTextView;
import com.nct.model.ItemCreateKard;
import com.nct.mv.AtCreateCard;
import com.nct.mv.AtWacCamera;

import thh.com.mycouper.R;

public class FragCreateCardImage extends BaseMainFragment implements OnClickListener {

    private static final int SELECT_PICTURE = 1;
    private static final int TAKE_PICTURE = 2;
    private static final int REQUEST_CODE_CROP_IMAGE = 3;

    private enum MODE_CAMERA_FACE {Front, Back};
    private MODE_CAMERA_FACE modeFace;

    private RelativeLayout mLLInfoCard;
    private LinearLayout mLLInputName;
    private ImageView imageView;

    RelativeLayout lyBtnCameraFront, lyBtnCameraBack, lyImageFront, lyImageBack;
    ImageView imgeFront, imageBack;
    ImageView btnFront, btnBack;


    private TfTextView txtCompanyName, txtCompanyDes;
    private Button bntNext;
    private ImageButton btnFrontFace;
    private ImageButton btnBackFace;

    private String mCompanyName = "";
    private String mCardCode = "";
    private String mCardName = "";
    private String mCardDes = "";
    private String mTypeCode = "";
    private String mCompanyID = "";
    private String mCompanyLogo = "";

    private boolean isEditCard = false;
    private boolean isOther = false;

    private Bitmap mBitmapFront, mBitmapBack;

    public static FragCreateCardImage newInstance() {
        FragCreateCardImage f = new FragCreateCardImage();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            isEditCard = getArguments().getBoolean(Constants.KEY_BUNDLE_CARD_EDIT_CARD, false);
            mCompanyName = getArguments().getString(Constants.KEY_BUNDLE_CARD_INFO_COMPANYNAME);
            mCardCode = getArguments().getString(Constants.KEY_BUNDLE_CARD_INFO_CARDCODE);
            mCardName = getArguments().getString(Constants.KEY_BUNDLE_CARD_INFO_CARDNAME);
            mCardDes = getArguments().getString(Constants.KEY_BUNDLE_CARD_INFO_CARDDES);
            mTypeCode = getArguments().getString(Constants.KEY_BUNDLE_CARD_INFO_TYPE_CODE);
            isOther = getArguments().getBoolean(Constants.KEY_BUNDLE_BOOLEAN_VALUE, false);
            if(!isOther){
                mCompanyID = getArguments().getString(Constants.KEY_BUNDLE_CARD_INFO_COMPANYID);
                mCompanyLogo = getArguments().getString(Constants.KEY_BUNDLE_CARD_INFO_COMPANYLOGO);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.frag_create_card_image, container, false);

        initTopbar(v,getString(R.string.frag_createcard));
        setTopbarBtLeftImage(R.drawable.icon_back);
        setTopbarLeftBtListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AtCreateCard) getActivity()).actionBackKey();
            }
        });
        setTopBarbtRightVisible(View.INVISIBLE);

        mLLInfoCard = (RelativeLayout) v.findViewById(R.id.frag_create_card_info_linear_img);
        mLLInputName = (LinearLayout) v.findViewById(R.id.frag_create_card_info_linear_input);
        imageView = (ImageView) v.findViewById(R.id.frag_create_card_info_img);
        txtCompanyName = (TfTextView) v.findViewById(R.id.frag_create_card_info_tv_title);
        txtCompanyDes = (TfTextView) v.findViewById(R.id.frag_create_card_info_tv_des);

        if(isOther){
            mLLInfoCard.setVisibility(View.GONE);
            mLLInputName.setVisibility(View.VISIBLE);
            txtCompanyName = (TfTextView) v.findViewById(R.id.create_card_companymane);
            if(mCompanyName == null)
                mCompanyName = "";
            txtCompanyName.setText(mCompanyName);
        }else{
            mLLInfoCard.setVisibility(View.VISIBLE);
            mLLInputName.setVisibility(View.GONE);
            txtCompanyDes.setText(mCardDes);
            if(mCompanyLogo != null)
                displayImage(imageView, mCompanyLogo);
            if(mCompanyName != null)
                txtCompanyName.setText(mCompanyName);
        }

        lyBtnCameraFront = (RelativeLayout) v.findViewById(R.id.btn_front_camera);
        lyImageFront = (RelativeLayout) v.findViewById(R.id.lyImageFront);
        lyBtnCameraBack = (RelativeLayout) v.findViewById(R.id.btn_back_camera);
        lyImageBack = (RelativeLayout) v.findViewById(R.id.lyImagBack);

        imgeFront = (ImageView) v.findViewById(R.id.image_front);
        btnFront = (ImageView) v.findViewById(R.id.image_camera_front);
        btnFront.setOnClickListener(this);

        imageBack = (ImageView) v.findViewById(R.id.image_back);
        btnBack = (ImageView) v.findViewById(R.id.image_camera_back);
        btnBack.setOnClickListener(this);

        btnFrontFace = (ImageButton) v.findViewById(R.id.front_Camera);
        btnFrontFace.setOnClickListener(this);

        btnBackFace = (ImageButton) v.findViewById(R.id.back_Camera);
        btnBackFace.setOnClickListener(this);

        bntNext = (Button) v.findViewById(R.id.frag_create_card_image_bt_next);
        bntNext.setOnClickListener(this);

        if(isEditCard){
            lyBtnCameraFront.setVisibility(View.GONE);
            lyImageFront.setVisibility(View.VISIBLE);
            lyBtnCameraBack.setVisibility(View.GONE);
            lyImageBack.setVisibility(View.VISIBLE);
            if(ItemCreateKard.frontUrl != null && !ItemCreateKard.frontUrl.equals("") && !ItemCreateKard.frontUrl.equals("NULL")){
                lyBtnCameraFront.setVisibility(View.GONE);
                lyImageFront.setVisibility(View.VISIBLE);
                displayImage(imgeFront, ItemCreateKard.frontUrl);
            }else{
                lyBtnCameraFront.setVisibility(View.VISIBLE);
                lyImageFront.setVisibility(View.GONE);
            }
            if(ItemCreateKard.backUrl != null && !ItemCreateKard.frontUrl.equals("") && !ItemCreateKard.frontUrl.equals("NULL")){
                lyBtnCameraBack.setVisibility(View.GONE);
                lyImageBack.setVisibility(View.VISIBLE);
                displayImage(imageBack, ItemCreateKard.backUrl);
            }else{
                lyBtnCameraBack.setVisibility(View.VISIBLE);
                lyImageBack.setVisibility(View.GONE);
            }
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AtCreateCard)getActivity()).setWindowSoftInputMode(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_camera_front:
            case R.id.front_Camera:
                modeFace = MODE_CAMERA_FACE.Front;
//                gotoCamera();
                showsSelectDialog();
                break;
            case R.id.image_camera_back:
            case R.id.back_Camera:
                modeFace = MODE_CAMERA_FACE.Back;
//                gotoCamera();
                showsSelectDialog();
                break;
            case R.id.frag_create_card_image_bt_next:
                ((AtCreateCard)getActivity()).savePhoto(mBitmapFront, mBitmapBack);
                break;
        }
    }

    public void callCreateKard(String fontUrl, String backUrl){
        if(isEditCard){
            String note = "";
            if(fontUrl.equals("") && ItemCreateKard.frontUrl != null)
                fontUrl = ItemCreateKard.frontUrl;
            if(backUrl.equals("") && ItemCreateKard.backUrl != null)
                backUrl = ItemCreateKard.backUrl;
            ((AtCreateCard) getActivity()).updateCard(GlobalInstance.getInstance().userInfo.user_id, ItemCreateKard.mCardID, note, mCompanyName, mCardName, fontUrl, backUrl, mCardDes);
        }else{
            String mCompany = "";
            if(!isOther){
                if(mCompanyID != null)
                    mCompany = mCompanyID;
            }else
                mCompany = mCompanyName;
            ((AtCreateCard) getActivity()).createNewCard(GlobalInstance.getInstance().userInfo.user_id, mCompany, mCardName, mCardCode, fontUrl, backUrl, mCardDes, mTypeCode, isOther);
        }
    }

    public void showSuccess(){
        FragCreateCardSuccess fm = new FragCreateCardSuccess();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.KEY_BUNDLE_CARD_EDIT_CARD, isEditCard);
        bundle.putString(Constants.KEY_BUNDLE_CARD_INFO_COMPANYID, mCompanyID);
        bundle.putString(Constants.KEY_BUNDLE_CARD_INFO_COMPANYNAME, mCompanyName);
        if(!isOther)
            bundle.putString(Constants.KEY_BUNDLE_CARD_INFO_COMPANYLOGO, mCompanyLogo);
        bundle.putString(Constants.KEY_BUNDLE_CARD_INFO_CARDCODE, mCardCode);
        bundle.putString(Constants.KEY_BUNDLE_CARD_INFO_CARDNAME, mCardName);
        bundle.putString(Constants.KEY_BUNDLE_CARD_INFO_CARDDES, mCardDes);
        bundle.putBoolean(Constants.KEY_BUNDLE_BOOLEAN_VALUE, isOther);
        fm.setArguments(bundle);
        ((AtCreateCard)getActivity()).changeFragment(Constants.TYPE_CREATE_CARD_IMAGE, fm);
    }

    public void activityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode){
                case TAKE_PICTURE:
                    if(modeFace == MODE_CAMERA_FACE.Front){
                        mBitmapFront = ItemCreateKard.bitmapFront;
                        if(mBitmapFront != null){
                            lyBtnCameraFront.setVisibility(View.GONE);
                            lyImageFront.setVisibility(View.VISIBLE);
                            imgeFront.setImageBitmap(mBitmapFront);
//                            String fileName = "mycouper_front_photo.png";
                            // save bitmap
//                            ItemCreateKard.frontFile = BitmapUtils.saveBitmapInSDCard(getActivity(), fileName, mBitmapFront);
                        }else{
                            lyBtnCameraFront.setVisibility(View.VISIBLE);
                            lyImageFront.setVisibility(View.GONE);
                        }
                    }else{
                        mBitmapBack = ItemCreateKard.bitmapBack;
                        if(mBitmapBack != null){
                            lyBtnCameraBack.setVisibility(View.GONE);
                            lyImageBack.setVisibility(View.VISIBLE);
                            imageBack.setImageBitmap(mBitmapBack);
//                            String fileName = "mycouper_back_photo.png";
//                            ItemCreateKard.backFile = BitmapUtils.saveBitmapInSDCard(getActivity(), fileName, mBitmapBack);
                        }else{
                            lyBtnCameraBack.setVisibility(View.VISIBLE);
                            lyImageBack.setVisibility(View.GONE);
                        }
                    }
                    break;

            }
        }
    }

    private void showsSelectDialog() {
        DialogRate dialog = new DialogRate(getActivity(), 2, "Gallery", "Camera", "", "");
        dialog.setListenerFinishedDialog(new DialogCustom.FinishDialogConfirmListener() {
            @Override
            public void onFinishConfirmDialog(int i) {
                Intent intent = new Intent(getActivity(), AtWacCamera.class);
                if(modeFace == MODE_CAMERA_FACE.Front)
                    intent.putExtra(Constants.KEY_BUNDLE_BOOLEAN_VALUE, true);
                else
                    intent.putExtra(Constants.KEY_BUNDLE_BOOLEAN_VALUE, false);
                if(i == 0)
                    intent.putExtra(AtWacCamera.GET_PICTURE_FROM_GALLERY, true);
                else
                    intent.putExtra(AtWacCamera.GET_PICTURE_FROM_GALLERY, false);
                getActivity().startActivityForResult(intent, TAKE_PICTURE);
            }
        });
        dialog.show();
    }

}
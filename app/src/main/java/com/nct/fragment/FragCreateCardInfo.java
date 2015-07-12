package com.nct.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nct.customview.PopupActionItem;
import com.nct.customview.QuickAction;
import com.nct.customview.TfTextView;
import com.nct.model.CompanyObject;
import com.nct.model.ItemCreateKard;
import com.nct.model.MemberCardObject;
import com.nct.mv.AtCreateCard;
import com.nct.utils.Debug;
import com.nct.utils.Utils;

import info.vividcode.android.zxing.CaptureActivity;
import info.vividcode.android.zxing.CaptureActivityIntents;
import info.vividcode.android.zxing.CaptureResult;
import thh.com.mycouper.R;

public class FragCreateCardInfo extends BaseMainFragment {

    private final String TYPE_CARD_SCAN_CODE[] = {"ID", "barcode", "qrcode"};
    private final String TYPE_QRCODE = "QR_CODE";

    private Button bntNext;
    private Button bntScan;
    private LinearLayout mLLInfoCard, mLLInputName;
    private ImageView imageView;
    private TfTextView txtCompanyName;
    private EditText edtCardCode, edtCardName, edtCardDes;
    private EditText edtCompanyName;

    private LinearLayout mLLTypeCode;
    private TfTextView txtTypeCode;
    private QuickAction quickAction;
    private PopupActionItem bntID;
    private PopupActionItem bntBarcode;
    private PopupActionItem bntQrcode;

    private boolean isEditCard = false;
    private boolean isOther = false;

    private CompanyObject itemCompany;
    private MemberCardObject memberCard;

    private String mCompanyName;
    private String mCompanyID;
    private String mCompanyLogo;
    private String mCardCode;
    private String mCardName;
    private String mCardDes;
    private String mTypeCode = TYPE_CARD_SCAN_CODE[0];

    public static FragCreateCardInfo newInstance() {
        FragCreateCardInfo f = new FragCreateCardInfo();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            isEditCard = getArguments().getBoolean(Constants.KEY_BUNDLE_CARD_EDIT_CARD, false);
            if(!isEditCard){
                isOther = getArguments().getBoolean(Constants.KEY_BUNDLE_BOOLEAN_VALUE, false);
                itemCompany = (CompanyObject)getArguments().getSerializable(Constants.KEY_BUNDLE_OBJECT_VALUE);
            }else{
                memberCard = GlobalInstance.getInstance().memberCard;
                ItemCreateKard.mCardID = "" + memberCard.member_card_id;
                ItemCreateKard.frontUrl = memberCard.front_of_the_card;
                ItemCreateKard.backUrl = memberCard.back_of_the_card;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.frag_create_card_info, container, false);

        initTopbar(v,getString(R.string.frag_createcard));

        setTopbarBtLeftImage(R.drawable.icon_back);
        setTopbarLeftBtListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AtCreateCard) getActivity()).actionBackKey();
            }
        });
        setTopBarbtRightVisible(View.INVISIBLE);

        innitControl(v);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AtCreateCard)getActivity()).setWindowSoftInputMode(true);
    }

    private void innitControl(View v){
        mLLInfoCard = (LinearLayout) v.findViewById(R.id.frag_create_card_info_linear_img);
        mLLInputName = (LinearLayout) v.findViewById(R.id.frag_create_card_info_linear_input);
        mLLTypeCode = (LinearLayout) v.findViewById(R.id.lyTypeCode);

        imageView = (ImageView) v.findViewById(R.id.frag_create_card_info_img);
        txtCompanyName = (TfTextView) v.findViewById(R.id.frag_create_card_info_tv_title);
        if(isOther){
            mLLInfoCard.setVisibility(View.GONE);
            mLLInputName.setVisibility(View.VISIBLE);
            edtCompanyName = (EditText) v.findViewById(R.id.create_card_companymane);
        }else{
            mLLInfoCard.setVisibility(View.VISIBLE);
            mLLInputName.setVisibility(View.GONE);
            if(isEditCard){
                mCompanyLogo = memberCard.company_logo;
                displayImage(imageView, mCompanyLogo);
                mCompanyID = memberCard.company_id;
                if(memberCard.company_name != null && !memberCard.company_name.equals("NULL"))
                    txtCompanyName.setText(memberCard.company_name);
                else
                    txtCompanyName.setText("");
            }else{
                if(itemCompany != null){
                    mCompanyLogo = itemCompany.company_logo;
                    displayImage(imageView, mCompanyLogo);
                    mCompanyID = itemCompany.company_id;
                    if(itemCompany.company_name != null && !itemCompany.company_name.equals("NULL"))
                        txtCompanyName.setText(itemCompany.company_name);
                    else
                        txtCompanyName.setText("");
                }
            }
        }

        innitPopup();

        edtCardCode = (EditText) v.findViewById(R.id.create_card_cardcode);
        edtCardName = (EditText) v.findViewById(R.id.create_card_cardname);
        edtCardDes = (EditText) v.findViewById(R.id.create_card_carddescription);
        txtTypeCode = (TfTextView) v.findViewById(R.id.create_card_typecode);
        txtTypeCode.setText("ID");
        txtTypeCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                quickAction.show(v);
            }
        });

        LinearLayout contentTypeCode = (LinearLayout) v.findViewById(R.id.contentTypeCode);
        contentTypeCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                quickAction.show(txtTypeCode);
            }
        });

        bntScan = (Button) v.findViewById(R.id.frag_create_card_info_bt_scan);
        bntScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent captureIntent = new Intent(getActivity(), CaptureActivity.class);
                // Using `CaptureActivityIntents`, set parameters to an intent.
                // (There is no requisite parameter to set to an intent.)
                // For instance, `setPromptMessage` method set prompt message displayed on `CaptureActivity`.
                CaptureActivityIntents.setPromptMessage(captureIntent, "Code scanning...");
                // Start activity.
                startActivityForResult(captureIntent, 1);
            }
        });

        if(isEditCard){
            if(memberCard.member_card_number != null)
                edtCardCode.setText(memberCard.member_card_number);
            if(memberCard.member_card_name != null && !memberCard.member_card_name.equals("NULL"))
                edtCardName.setText(memberCard.member_card_name);
            if(memberCard.description != null && !memberCard.description.equals("NULL"))
                edtCardDes.setText(memberCard.description);
        }else{
            edtCardCode.setEnabled(true);
            bntScan.setClickable(true);
            txtTypeCode.setClickable(true);
        }

        bntNext = (Button) v.findViewById(R.id.frag_create_card_info_bt_next);
        bntNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = checkParam();
                if(result.equals("")) {
                    FragCreateCardImage fm = new FragCreateCardImage();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.KEY_BUNDLE_CARD_EDIT_CARD, isEditCard);
                    bundle.putString(Constants.KEY_BUNDLE_CARD_INFO_COMPANYID, mCompanyID);
                    bundle.putString(Constants.KEY_BUNDLE_CARD_INFO_COMPANYNAME, mCompanyName);
                    if(!isOther)
                        bundle.putString(Constants.KEY_BUNDLE_CARD_INFO_COMPANYLOGO, mCompanyLogo);
                    bundle.putString(Constants.KEY_BUNDLE_CARD_INFO_CARDCODE, mCardCode);
                    bundle.putString(Constants.KEY_BUNDLE_CARD_INFO_CARDNAME, mCardName);
                    bundle.putString(Constants.KEY_BUNDLE_CARD_INFO_CARDDES, mCardDes);
                    bundle.putString(Constants.KEY_BUNDLE_CARD_INFO_TYPE_CODE, mTypeCode);
                    bundle.putBoolean(Constants.KEY_BUNDLE_BOOLEAN_VALUE, isOther);
                    fm.setArguments(bundle);
                    ((AtCreateCard)getActivity()).changeFragment(Constants.TYPE_CREATE_CARD_IMAGE, fm);

                    Utils.keyBoardForceHide(getActivity());

                }else
                    Debug.toast(getActivity(), result);
            }
        });
    }

    private void innitPopup(){
        quickAction = new QuickAction(getActivity(), QuickAction.VERTICAL);
        quickAction.setStatusBackround(false);
        bntID = new PopupActionItem(Constants.POP_UP_ID_CODE_ID, "ID", null);
        bntBarcode = new PopupActionItem(Constants.POP_UP_ID_CODE_BARCODE, "Barcode", null);
        bntQrcode = new PopupActionItem(Constants.POP_UP_ID_CODE_QRCODE, "Qrcode", null);

        quickAction.addActionItem(bntID);
        quickAction.addActionItem(bntBarcode);
        quickAction.addActionItem(bntQrcode);

        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                String mtype = "ID";
                switch (actionId){
                    case Constants.POP_UP_ID_CODE_ID:
                        mTypeCode = TYPE_CARD_SCAN_CODE[0];
                        mtype = "ID";
                        break;
                    case Constants.POP_UP_ID_CODE_BARCODE:
                        mTypeCode = TYPE_CARD_SCAN_CODE[1];
                        mtype = "Barcode";
                        break;
                    case Constants.POP_UP_ID_CODE_QRCODE:
                        mTypeCode = TYPE_CARD_SCAN_CODE[2];
                        mtype = "Qrcode";
                        break;
                }
                txtTypeCode.setText(mtype);
            }
        });
    }

    private String checkParam(){
        String result = "";
        mCardCode = edtCardCode.getText().toString();
        mCardName = edtCardName.getText().toString();
        mCardDes = edtCardDes.getText().toString();
        if(isOther)
            mCompanyName = edtCompanyName.getText().toString();
        else
            mCompanyName = txtCompanyName.getText().toString();

        if(TextUtils.isEmpty(mCompanyName))
            return result = getResources().getString(R.string.frag_createcard_info_caompanyname_is_empty);
        if(TextUtils.isEmpty(mCardCode))
            return result = getResources().getString(R.string.frag_createcard_info_cardcode_is_empty);
        //if(TextUtils.isEmpty(mCardName))
            //return result = getResources().getString(R.string.frag_createcard_info_cardname_requiret);

        return result;
    }
    // QR_CODE
    public void activityResult(int requestCode, int resultCode, Intent intent){
        if (resultCode == getActivity().RESULT_OK) {
            CaptureResult res = null;
            String format = null;
            try{
                res = CaptureResult.parseResultIntent(intent);
            }catch (Exception e){
                e.printStackTrace();
            }
            if(res != null){
                mCardCode = res.getContents();
                format = res.getFormatName();
                if(mCardCode != null)
                    edtCardCode.setText(mCardCode);
                else
                    mCardCode = "";
                // Handle successful scan
                String typeCode = "";
                if(mCardCode != null){
                    if(format != null && format.equals(TYPE_QRCODE)) {
                        mTypeCode = TYPE_CARD_SCAN_CODE[2];
                        typeCode = "Qrcode";
                    }else{
                        mTypeCode = TYPE_CARD_SCAN_CODE[1];
                        typeCode = "Barcode";
                    }
                    txtTypeCode.setText(typeCode);
                }
            }
        } else if (resultCode == getActivity().RESULT_CANCELED) {
            // Handle cancel
            mCardCode = "";
        }
    }
}
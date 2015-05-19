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
import com.nct.customview.TfTextView;
import com.nct.model.CompanyObject;
import com.nct.model.MemberCardObject;
import com.nct.mv.AtCreateCard;
import com.nct.utils.Debug;
import thh.com.mycouper.R;

public class FragCreateCardInfo extends BaseMainFragment {

    private final String TYPE_CARD_SCAN_CODE[] = {"ID","qrcode","barcode"};
    private final String TYPE_QRCODE = "QR_CODE";

    private Button bntNext;
    private Button bntScan;
    private LinearLayout mLLInfoCard, mLLInputName;
    private ImageView imageView;
    private TfTextView txtCompanyName;
    private EditText edtCardCode, edtCardName, edtCardDes;
    private EditText edtCompanyName;

    private boolean isOther = false;
    private CompanyObject itemCompany;
    private MemberCardObject memberCard;

    private String mCompanyName;
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
            isOther = getArguments().getBoolean(Constants.KEY_BUNDLE_BOOLEAN_VALUE, false);
            itemCompany = (CompanyObject)getArguments().getSerializable(Constants.KEY_BUNDLE_OBJECT_VALUE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.frag_create_card_info, container, false);

        initTopbar(v,"create card info");

        setTopbarBtLeftImage(R.drawable.icon_back);
        setTopbarLeftBtListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AtCreateCard) getActivity()).actionBackKey();
            }
        });

        innitControl(v);

        return v;
    }

    private void innitControl(View v){
        mLLInfoCard = (LinearLayout) v.findViewById(R.id.frag_create_card_info_linear_img);
        mLLInputName = (LinearLayout) v.findViewById(R.id.frag_create_card_info_linear_input);

        if(isOther){
            mLLInfoCard.setVisibility(View.GONE);
            mLLInputName.setVisibility(View.VISIBLE);
            edtCompanyName = (EditText) v.findViewById(R.id.create_card_companymane);
        }else{
            mLLInfoCard.setVisibility(View.VISIBLE);
            mLLInputName.setVisibility(View.GONE);
            imageView = (ImageView) v.findViewById(R.id.frag_create_card_info_img);
            txtCompanyName = (TfTextView) v.findViewById(R.id.frag_create_card_info_tv_title);
            if(itemCompany != null){
                displayImage(imageView, itemCompany.company_logo);
                if(itemCompany.company_name != null)
                    txtCompanyName.setText(itemCompany.company_name);
                else
                    txtCompanyName.setText("");
            }
        }

        edtCardCode = (EditText) v.findViewById(R.id.create_card_cardcode);
        edtCardName = (EditText) v.findViewById(R.id.create_card_cardname);
        edtCardDes = (EditText) v.findViewById(R.id.create_card_carddescription);

        bntScan = (Button) v.findViewById(R.id.frag_create_card_info_bt_scan);
        bntScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
//                intent.putExtra("SCAN_MODE", "BAR_CODE_MODE");
                startActivityForResult(intent, 0);
            }
        });

        bntNext = (Button) v.findViewById(R.id.frag_create_card_info_bt_next);
        bntNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = checkParam();
                if(result.equals("")) {
                    FragCreateCardImage fm = new FragCreateCardImage();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.KEY_BUNDLE_CARD_INFO_COMPANYNAME, mCompanyName);
                    bundle.putString(Constants.KEY_BUNDLE_CARD_INFO_CARDCODE, mCardCode);
                    bundle.putString(Constants.KEY_BUNDLE_CARD_INFO_CARDNAME, mCardName);
                    bundle.putString(Constants.KEY_BUNDLE_CARD_INFO_CARDDES, mCardDes);
                    bundle.putString(Constants.KEY_BUNDLE_CARD_INFO_TYPE_CODE, mTypeCode);
                    bundle.putBoolean(Constants.KEY_BUNDLE_BOOLEAN_VALUE, isOther);
                    bundle.putSerializable(Constants.KEY_BUNDLE_OBJECT_VALUE, itemCompany);
                    fm.setArguments(bundle);
                    ((AtCreateCard)getActivity()).changeFragment(Constants.TYPE_CREATE_CARD_IMAGE, fm);
                }else
                    Debug.toast(getActivity(), result);
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
        if(TextUtils.isEmpty(mCardName))
            return result = getResources().getString(R.string.frag_createcard_info_cardname_requiret);

        return result;
    }
    // QR_CODE
    public void activityResult(int requestCode, int resultCode, Intent intent){
        if (resultCode == getActivity().RESULT_OK) {
            mCardCode = intent.getStringExtra("SCAN_RESULT");
            if(mCardCode != null)
                edtCardCode.setText(mCardCode);
            else
                mCardCode = "";
            // Handle successful scan
            String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
            if(format != null){
                if(format.equals(TYPE_QRCODE))
                    mTypeCode = TYPE_CARD_SCAN_CODE[1];
                else
                    mTypeCode = TYPE_CARD_SCAN_CODE[2];
            }
        } else if (resultCode == getActivity().RESULT_CANCELED) {
            // Handle cancel
            mCardCode = "";
        }
    }
}
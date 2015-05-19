package com.nct.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nct.constants.Constants;
import com.nct.customview.TfTextView;
import com.nct.model.CompanyObject;

import thh.com.mycouper.R;

public class FragCreateCardSuccess extends BaseMainFragment {

    private RelativeLayout mLLInfoCard;
    private LinearLayout mLLInputName;
    private ImageView imageView;
    private TfTextView txtCompanyName, txtCompanyDes;

    private Button bntNext;

    private String mCompanyName;
    private String mCardCode;
    private String mCardName;
    private String mCardDes;
    private boolean isOther = false;
    private CompanyObject itemCompany;

    public static FragCreateCardSuccess newInstance() {
        FragCreateCardSuccess f = new FragCreateCardSuccess();
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
            isOther = getArguments().getBoolean(Constants.KEY_BUNDLE_BOOLEAN_VALUE, false);
            itemCompany = (CompanyObject)getArguments().getSerializable(Constants.KEY_BUNDLE_OBJECT_VALUE);
        }
        if(mCardDes == null)
            mCardDes = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.frag_create_card_success, container, false);

        mLLInfoCard = (RelativeLayout) v.findViewById(R.id.frag_create_card_info_linear_img);
        mLLInputName = (LinearLayout) v.findViewById(R.id.frag_create_card_info_linear_input);

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
            imageView = (ImageView) v.findViewById(R.id.frag_create_card_info_img);
            txtCompanyName = (TfTextView) v.findViewById(R.id.frag_create_card_info_tv_title);
            txtCompanyDes = (TfTextView) v.findViewById(R.id.frag_create_card_info_tv_des);
            txtCompanyDes.setText(mCardDes);
            if(itemCompany != null){
                displayImage(imageView, itemCompany.company_logo);
                if(itemCompany.company_name != null)
                    txtCompanyName.setText(itemCompany.company_name);
                else
                    txtCompanyName.setText("");
            }
        }

        return v;
    }
}
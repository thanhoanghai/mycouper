package com.nct.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nct.adapter.FragCompanyAdapter;
import com.nct.constants.Constants;
import com.nct.dataloader.DataHelper;
import com.nct.dataloader.DataLoader;
import com.nct.dataloader.URLProvider;
import com.nct.model.CompanyData;
import com.nct.model.CompanyObject;
import com.nct.mv.AtCreateCard;
import com.nct.utils.Utils;

import java.util.ArrayList;

import thh.com.mycouper.R;

public class FragCreateCardInfo extends BaseMainFragment {

    private Button bntNext;

    public static FragCreateCardInfo newInstance() {
        FragCreateCardInfo f = new FragCreateCardInfo();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        bntNext = (Button) v.findViewById(R.id.frag_create_card_info_bt_next);
        bntNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AtCreateCard)getActivity()).changeFragment(Constants.TYPE_CREATE_CARD_IMAGE);
            }
        });

        return v;
    }
}
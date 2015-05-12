package com.nct.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.nct.constants.Constants;
import com.nct.mv.AtCreateCard;

import thh.com.mycouper.R;

public class FragCreateCardImage extends BaseMainFragment {

    private Button bntNext;

    public static FragCreateCardImage newInstance() {
        FragCreateCardImage f = new FragCreateCardImage();
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
        View v = inflater.inflate(R.layout.frag_create_card_image, container, false);

        initTopbar(v,"Card Image");
        setTopbarBtLeftImage(R.drawable.icon_back);
        setTopbarLeftBtListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AtCreateCard)getActivity()).actionBackKey();
            }
        });

        bntNext = (Button) v.findViewById(R.id.frag_create_card_image_bt_next);
        bntNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AtCreateCard)getActivity()).changeFragment(Constants.TYPE_CREATE_CARD_SUCCESS);
            }
        });

        return v;
    }
}
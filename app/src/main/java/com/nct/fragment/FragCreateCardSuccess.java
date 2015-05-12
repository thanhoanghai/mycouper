package com.nct.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import thh.com.mycouper.R;

public class FragCreateCardSuccess extends BaseMainFragment {

    private Button bntNext;

    public static FragCreateCardSuccess newInstance() {
        FragCreateCardSuccess f = new FragCreateCardSuccess();
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
        View v = inflater.inflate(R.layout.frag_create_card_success, container, false);


        return v;
    }
}
package com.nct.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.nct.adapter.FragMemberCardAdapter;
import com.nct.constants.Constants;
import com.nct.model.CardObject;
import com.nct.utils.Utils;

import java.util.ArrayList;

import thh.com.mycouper.R;

public class FragCreateCardSearch extends BaseGridFragment<CardObject> {


    private Button bntAddmore;

    public static FragCreateCardSearch newInstance() {
        FragCreateCardSearch f = new FragCreateCardSearch();
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
        View v = inflater.inflate(R.layout.frag_create_card_search, container, false);

        initTopbar(v, getString(R.string.frag_createcard));
        setTopBarbtRightVisible(View.INVISIBLE);
        setTopbarBtLeftImage(R.drawable.icon_back);
        setTopbarLeftBtListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bntAddmore = (Button) v.findViewById(R.id.frag_create_card_bt_other);
        bntAddmore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.gotoScreenCreateCard(getActivity());
            }
        });

        return v;
    }

    @SuppressLint("InflateParams")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isAdapterNull()) {
            DelayTimeStart(Constants.API_DELAY_TIME);
        } else
            setDataDefault();

    }

    public void DelayTimeFinish() {
        loadData();
    }

    @Override
    protected void loadData() {
        ArrayList<CardObject> list = new ArrayList<CardObject>();
        list.add(new CardObject());
        list.add(new CardObject());
        list.add(new CardObject());
        list.add(new CardObject());
        list.add(new CardObject());
        list.add(new CardObject());
        list.add(new CardObject());
        list.add(new CardObject());
        list.add(new CardObject());
        list.add(new CardObject());
        list.add(new CardObject());
        list.add(new CardObject());
        list.add(new CardObject());
        list.add(new CardObject());
        list.add(new CardObject());

        setData(list, true);
    }


    @Override
    protected void loadDataWithLoadmore(int pageindex, int pagesize) {
        //DataLoader.get(URLProvider.getMVGenres(genres,topic, pageindex),
        //mTextHttpResponseHandler);
    }

    @Override
    protected boolean handleLoadingDataSuccess(String result) {

        return true;
    }

    @Override
    protected FragMemberCardAdapter initAdapter(ArrayList<CardObject> list) {
        FragMemberCardAdapter adapter = new FragMemberCardAdapter(getActivity(), list);
        return adapter;
    }


}
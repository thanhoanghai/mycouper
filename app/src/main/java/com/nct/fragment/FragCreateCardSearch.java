package com.nct.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nct.adapter.FragCompanyAdapter;
import com.nct.adapter.FragMemberCardAdapter;
import com.nct.constants.Constants;
import com.nct.dataloader.DataHelper;
import com.nct.dataloader.DataLoader;
import com.nct.dataloader.URLProvider;
import com.nct.model.CardObject;
import com.nct.model.CompanyData;
import com.nct.model.CompanyObject;
import com.nct.model.MemberCardObject;
import com.nct.mv.AtCreateCard;
import com.nct.utils.Utils;

import java.util.ArrayList;

import thh.com.mycouper.R;

public class FragCreateCardSearch extends BaseGridFragment<CompanyObject> {

    private EditText edtSearch;
    private ImageView bntClear;

    private TextView bntCancel;

    private Button bntOther;
    private CompanyData data;

    private Boolean isActiveSearch;

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

        disableLoadmore();

        isActiveSearch = false;

        initTopbar(v, getString(R.string.frag_createcard));
        setTopBarbtRightVisible(View.INVISIBLE);
        setTopbarBtLeftImage(R.drawable.icon_back);
        setTopbarLeftBtListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AtCreateCard)getActivity()).actionBackKey();
            }
        });

        bntOther = (Button) v.findViewById(R.id.frag_create_card_bt_other);
        bntOther.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragCreateCardInfo fm = new FragCreateCardInfo();
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.KEY_BUNDLE_BOOLEAN_VALUE, true);
                fm.setArguments(bundle);
                ((AtCreateCard)getActivity()).changeFragment(Constants.TYPE_CREATE_CARD_INFO, fm);
            }
        });

        bntCancel = (TextView) v.findViewById(R.id.frag_create_card_tv_cancel);
        bntCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.keyBoardForceHide(getActivity());
            }
        });

        bntClear = (ImageView) v.findViewById(R.id.frag_create_card_bnt_clear);
        bntClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText("");
            }
        });

        edtSearch = (EditText) v.findViewById(R.id.frag_create_card_edt_search);
        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                String text = edtSearch.getText().toString();
                if(text.length() > 0) {
                    bntClear.setVisibility(View.VISIBLE);
                    isActiveSearch = true;
                }
                else
                    bntClear.setVisibility(View.INVISIBLE);
                if(isActiveSearch)
                    ((FragCompanyAdapter)mBaseAdapter).setFilter(text);
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
        }else
            setDataDefault();

    }

    public void DelayTimeFinish() {
        loadData();
    }

    @Override
    protected void loadData() {
        DataLoader.get(URLProvider.getCompanyMemberCard(),
                mTextHttpResponseHandler);
    }

    @Override
    protected boolean handleLoadingDataSuccess(String result) {
        data = DataHelper.getCompanyData(result);
        setData(data.data,false);
        return true;
    }

    @Override
    protected FragCompanyAdapter initAdapter(ArrayList<CompanyObject> list) {
        FragCompanyAdapter adapter = new FragCompanyAdapter(getActivity(), list);
        return adapter;
    }

}
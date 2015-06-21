package com.nct.mv;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.nct.constants.Constants;
import com.nct.customview.PopupActionItem;
import com.nct.customview.QuickAction;
import com.nct.fragment.FragHome;
import com.nct.fragment.FragMemberCard;
import com.nct.utils.Utils;

import thh.com.mycouper.R;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import java.util.Stack;
import java.util.HashMap;

public class AtMain extends AtBase {

    private HashMap<String, Stack<Fragment>> mStacks;
    private String mCurrentTab;
    private TabHost mTabHost;


    private QuickAction quickAction;
    private PopupActionItem bntUserProfile;
    private PopupActionItem bntLanguage;
    private PopupActionItem bntSetting;
    private PopupActionItem bntContact;
    private PopupActionItem bntSynchronize;
    private PopupActionItem bntHelp;
    private PopupActionItem bntCopyRight;


    private Boolean isResetLanguage = false;

    @Override
    protected void onResume() {
        super.onResume();
        if(isResetLanguage)
        {
            initListPopUpMenu();
            setTopbarTitle(getString(R.string.frag_membercard));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_main);

        setLanguge();

        initListPopUpMenu();

        initTopbar(getString(R.string.frag_membercard));
        setTopbarRighttBtListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                quickAction.show(v);
            }
        });


        mStacks = new HashMap<String, Stack<Fragment>>();
        mStacks.put(Constants.TAB_1, new Stack<Fragment>());
        mStacks.put(Constants.TAB_2, new Stack<Fragment>());
        mStacks.put(Constants.TAB_3, new Stack<Fragment>());

        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mTabHost.setOnTabChangedListener(listener);

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }
        initTabs();
    }

    private void initTabs() {
        TabHost.TabSpec spec = mTabHost.newTabSpec(Constants.TAB_1);
        mTabHost.setCurrentTab(-3);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.tab_realcontent);
            }
        });
        spec.setIndicator(createTabView(this, R.drawable.tab_1));
        mTabHost.addTab(spec);

        spec = mTabHost.newTabSpec(Constants.TAB_2);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.tab_realcontent);
            }
        });
        spec.setIndicator(createTabView(this, R.drawable.tab_2));
        mTabHost.addTab(spec);

        spec = mTabHost.newTabSpec(Constants.TAB_3);
        spec.setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String tag) {
                return findViewById(R.id.tab_realcontent);
            }
        });

        spec.setIndicator(createTabView(this, R.drawable.tab_3));
        mTabHost.addTab(spec);
    }

    private static View createTabView(final Context context,
                                      final int iconResource) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_tabs_pager_fragment_item, null);
        ImageView iconTab = (ImageView) view.findViewById(R.id.tabIcon);
        //iconTab.setBackgroundResource(iconResource);
        iconTab.setImageResource(iconResource);
//         if (iconResource == R.drawable.tab_utinity) {
//         View lineView = (View) view.findViewById(R.id.lineView);
//         lineView.setVisibility(View.GONE);
//         }
        return view;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }

    TabHost.OnTabChangeListener listener = new TabHost.OnTabChangeListener() {
        public void onTabChanged(String tabId) {
            /* Set current tab.. */
            mCurrentTab = tabId;
            if (mStacks.get(tabId).size() == 0) {
                if (tabId.equals(Constants.TAB_1)) {
                    pushFragments(tabId, new FragMemberCard(), false, true);
                } else if (tabId.equals(Constants.TAB_2)) {
                    pushFragments(tabId, new FragHome(), false, true);
                } else if (tabId.equals(Constants.TAB_3)) {
                    pushFragments(tabId, new FragHome(), false, true);
                }
            } else {
                pushFragments(tabId, mStacks.get(tabId).lastElement(), false,
                        false);
            }
        }
    };

    public void setCurrentTab(int val) {
        mTabHost.setCurrentTab(val);
    }

    public void pushFragments(String tag, Fragment fragment,
                              boolean shouldAnimate, boolean shouldAdd) {
        if (shouldAdd)
            mStacks.get(tag).push(fragment);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        if (shouldAnimate)
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        ft.replace(R.id.tab_realcontent, fragment);
        ft.commit();
    }


    private void initListPopUpMenu()
    {
        quickAction = new QuickAction(AtMain.this, QuickAction.VERTICAL);
        quickAction.setStatusBackround(false);
        bntUserProfile = new PopupActionItem(Constants.POP_UP_ID_USER_PROFILE, getString(R.string.user_profile), null);
        bntLanguage = new PopupActionItem(Constants.POP_UP_ID_USER_LANGUAGE, getString(R.string.language), null);
        bntSetting = new PopupActionItem(Constants.POP_UP_ID_SETTING, "Setting", null);
        bntContact = new PopupActionItem(Constants.POP_UP_ID_CONTACT, "Contact", null);
        bntSynchronize = new PopupActionItem(Constants.POP_UP_ID_SYNCHRONIZE , "Synchronize", null);
        bntHelp = new PopupActionItem(Constants.POP_UP_ID_HELP , "Help", null);
        bntCopyRight = new PopupActionItem(Constants.POP_UP_ID_COPYRIGHT , "Copyright", null);

        quickAction.addActionItem(bntUserProfile);
        quickAction.addActionItem(bntLanguage);
        quickAction.addActionItem(bntSetting);
        quickAction.addActionItem(bntContact);
        quickAction.addActionItem(bntSynchronize);
        quickAction.addActionItem(bntHelp);
        quickAction.addActionItem(bntCopyRight);

        quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
            @Override
            public void onItemClick(QuickAction source, int pos, int actionId) {
                if (actionId == Constants.POP_UP_ID_USER_PROFILE) {
                    Utils.gotoScreenUserProfile(AtMain.this);
                }else
                if (actionId == Constants.POP_UP_ID_USER_LANGUAGE) {
                    Utils.gotoScreenLanguage(AtMain.this);
                    isResetLanguage = true;
                }
            }
        });

    }

    public void popFragments() {
        Fragment fragment = mStacks.get(mCurrentTab).elementAt(
                mStacks.get(mCurrentTab).size() - 2);

		/* pop current fragment from stack.. */
        mStacks.get(mCurrentTab).pop();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        ft.replace(R.id.tab_realcontent, fragment);
        ft.commit();
    }

}
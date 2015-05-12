package com.nct.mv;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.nct.adapter.AtMainViewpagerAdapter;
import com.nct.constants.Constants;
import com.nct.constants.GlobalInstance;
import com.nct.customview.ActionItem;
import com.nct.customview.PagerSlidingTabStrip;
import com.nct.customview.QuickAction;
import com.nct.fragment.FragHome;
import com.nct.fragment.FragMemberCard;

import thh.com.mycouper.R;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TabHost;

import java.util.Locale;
import java.util.Stack;
import java.util.HashMap;

public class AtMain extends AtBase {

    private HashMap<String, Stack<Fragment>> mStacks;
    private String mCurrentTab;
    private TabHost mTabHost;


    private QuickAction quickAction;
    private ActionItem bntUserProfile;
    private ActionItem bntSetting;
    private ActionItem bntContact;
    private ActionItem bntSynchronize;
    private ActionItem bntHelp;
    private ActionItem bntCopyRight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.at_main);

        setLanguge();

        quickAction = new QuickAction(AtMain.this, QuickAction.VERTICAL);
        bntUserProfile = new ActionItem(Constants.POP_UP_ID_USER_PROFILE, "User profile", null);
        bntSetting = new ActionItem(Constants.POP_UP_ID_SETTING, "Setting", null);
        bntContact = new ActionItem(Constants.POP_UP_ID_CONTACT, "Contact", null);
        bntSynchronize = new ActionItem(Constants.POP_UP_ID_SYNCHRONIZE , "Synchronize", null);
        bntHelp = new ActionItem(Constants.POP_UP_ID_HELP , "Help", null);
        bntCopyRight = new ActionItem(Constants.POP_UP_ID_COPYRIGHT , "Copyright", null);

        quickAction.addActionItem(bntUserProfile);
        quickAction.addActionItem(bntSetting);
        quickAction.addActionItem(bntContact);
        quickAction.addActionItem(bntSynchronize);
        quickAction.addActionItem(bntHelp);
        quickAction.addActionItem(bntCopyRight);

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

}
package com.bsecure.apha.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsecure.apha.AccociateMain;
import com.bsecure.apha.R;
import com.bsecure.apha.adapters.ViewPagerAdapter;


public class Home extends ParentFragment {

    private OnFragmentInteractionListener mListener;

    private View layout;

    private TabLayout tl_settings;

    private AccociateMain activity;

    private ViewPager vp_notifications;

    private ViewPagerAdapter adapter = null;
    String arecode;
    public Home() {
        // Required empty public constructor
    }

    public static Home newInstance() {
        return new Home();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (AccociateMain)context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle mArgs = getArguments();

        if (mArgs != null) {
            //arecode =mArgs.getString("areacode");
        }
        layout = inflater.inflate(R.layout.fragment_home, container, false);

        tl_settings = (TabLayout) layout.findViewById(R.id.tl_settings);
        vp_notifications = (ViewPager) layout.findViewById(R.id.vp_notifications);

        String[] tabs = {getString(R.string.apha), getString(R.string.vips),getString(R.string.news)};

       // setTabsFromPagerAdapter(tabs, tl_settings);

        adapter = new ViewPagerAdapter(getChildFragmentManager());

        setupViewPager(vp_notifications);

        setupWithViewPager(vp_notifications);
        setupTabLayout(tabs,tl_settings);
        return layout;
    }

    private void setupTabLayout(String[] tabs, TabLayout tl_settings) {
        Typeface typeface = Typeface.createFromAsset(activity.getAssets(), "Lato-Bold.ttf");
        for (int i = 0, count = tabs.length; i < count; i++) {

            if (i==0) {
                TextView textView1 = (TextView) View.inflate(activity, R.layout.template_custom_tab, null);
                textView1.setText(getString(R.string.apha));
                textView1.setTypeface(typeface);
                tl_settings.addTab(tl_settings.newTab().setCustomView(textView1));
                tl_settings.getTabAt(0).setCustomView(textView1);
            }else if (i==1) {

                TextView textView2 = (TextView) View.inflate(activity, R.layout.templete_custom_tabtwo, null);
                textView2.setText(getString(R.string.vips));
                textView2.setTypeface(typeface);
                tl_settings.addTab(tl_settings.newTab().setCustomView(textView2));
                tl_settings.getTabAt(1).setCustomView(textView2);

            }else {

                TextView textView3 = (TextView) View.inflate(activity, R.layout.custom_tabe_three, null);
                textView3.setText(getString(R.string.news));
                textView3.setTypeface(typeface);
                tl_settings.addTab(tl_settings.newTab().setCustomView(textView3));
                tl_settings.getTabAt(2).setCustomView(textView3);
            }
        }

    }

    public void setupWithViewPager(@NonNull ViewPager viewPager) {
        final PagerAdapter adapter = viewPager.getAdapter();
        if (adapter == null) {
            throw new IllegalArgumentException("ViewPager does not have a PagerAdapter set");
        }
        // First we'll add Tabs, using the adapter's page titles
       // setTabsFromPagerAdapter(adapter);

        // Now we'll add our page change listener to the ViewPager
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tl_settings));

        // Now we'll add a tab selected listener to set ViewPager's current item
        tl_settings.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        // Make sure we reflect the currently set ViewPager item
        if (adapter.getCount() > 0) {
            final int curItem = viewPager.getCurrentItem();
            if (tl_settings.getSelectedTabPosition() != curItem) {
                //selectTab(tl_accounts.getTabAt(curItem));
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter.addFrag(APHSFragment.newInstance(), "");
        adapter.addFrag(VIPAPHSFragment.newInstance(), "");
        adapter.addFrag(NewsFragment.newInstance(), "");

        viewPager.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    public void setTabsFromPagerAdapter(@NonNull String[] tabs, TabLayout tabLayout) {

        for (int i = 0, count = tabs.length; i < count; i++) {

            TextView textView = (TextView) View.inflate(activity, R.layout.template_custom_tab, null);
            textView.setText(tabs[i]);
            tabLayout.addTab(tabLayout.newTab().setCustomView(textView));

        }
    }

    @Override
    public String getFragmentName() {
        return "APHA";
    }
}

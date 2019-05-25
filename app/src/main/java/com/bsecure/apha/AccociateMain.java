package com.bsecure.apha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bsecure.apha.fragments.APHMembersFragment;
import com.bsecure.apha.fragments.APHSROLESFragment;
import com.bsecure.apha.fragments.Home;
import com.bsecure.apha.fragments.HomeMembers;
import com.bsecure.apha.fragments.ParentFragment;
import com.bsecure.apha.fragments.ShowVIPFragment;
import com.bsecure.apha.fragments.VIPSFragment;
import com.bsecure.apha.models.MemberModel;
import com.bsecure.apha.otp.MPayment;
import com.bsecure.apha.otp.SendOtpScreen;
import com.bsecure.apha.utils.SharedValues;

import java.util.List;
import java.util.Stack;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AccociateMain extends AppCompatActivity {
    private FragmentManager manager = null;
    DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Stack<ParentFragment> fragStack = null;
    private Home homeFragment;
    private HomeMembers homeMembers;
    private APHSROLESFragment aphsrolesFragment = null;
    private APHMembersFragment aphMembersFragment = null;
    private ShowVIPFragment showVIPFragment = null;
    private VIPSFragment vipsFragment = null;
    private ProfileView profileView = null;
    private ActionBar actionBar = null;
    private Toolbar toolbar;
    private NavigationView navigationView = null;
    private View drawerHeader;
    private IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filter = new IntentFilter("com.acc.app.SESSION");
        registerReceiver(mBroadcastReceiver, filter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        fragStack = new Stack<ParentFragment>();

        getSupportActionBar().getThemedContext();

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,

                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                supportInvalidateOptionsMenu();
            }

        };

        drawerLayout.addDrawerListener(mDrawerToggle);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerToggle.setDrawerIndicatorEnabled(true);

        manager = getSupportFragmentManager();

        if (savedInstanceState != null) {
            temp();
        }

        if (SharedValues.getValue(this, "approval_status").equalsIgnoreCase("0")) {
            getredAlert("Your Approval Is Pending \nPlease Contact Admin", "0");
        } else if (SharedValues.getValue(this, "paid_status").equalsIgnoreCase("0")) {
            getredAlert("Your Membership Payment Is Pending \nPlease Contact Admin", "0");
        } else if (SharedValues.getValue(this, "subscription_status").equalsIgnoreCase("0")) {
            getredAlert("Your App Subscription Is Pending \nPlease Contact Admin", "1");
        }
        homeFragment = new Home();
        Bundle bundle = new Bundle();
        // bundle.putString("areacode", areacode);
        homeFragment.setArguments(bundle);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.container, homeFragment, "homeFragment");

        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
        fragStack.push(homeFragment);

        navigationView = (NavigationView) findViewById(R.id.navigation);
        String member_number = SharedValues.getValue(this, "member_number");
        if (member_number.equalsIgnoreCase("2")) {
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.m_dp_approvel).setVisible(true);
        } else {
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.m_dp_approvel).setVisible(false);
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                onNavigationDrawerItemSelected(menuItem.getItemId());
                return true;
            }
        });
        drawerHeader = navigationView.inflateHeaderView(R.layout.header_top);

        ((TextView) drawerHeader.findViewById(R.id.user_name)).setText("APHA");
    }

    public void onNavigationDrawerItemSelected(int menuId) {
        int count = fragStack.size();
        while (count > 1) {

            ParentFragment pf = fragStack.remove(count - 1);

            FragmentTransaction trans = manager.beginTransaction();
            trans.remove(pf);
            trans.commit();
            profileView = null;
            homeMembers = null;
            count--;
        }

        switch (menuId) {

            case R.id.m_home:
                if (homeFragment == null)
                    homeFragment = new Home();
                swiftFragments(homeFragment, "homeFragment");
                break;

            case R.id.m_delprofile:
                if (profileView == null)
                    profileView = new ProfileView();
                swiftFragments(profileView, "profileview");
//                Intent prf = new Intent(this, ProfileView.class);
//                startActivity(prf);
                break;
            case R.id.user_guide:
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Alert!")
                        .setContentText("Comming soon")
                        .setConfirmText("Ok")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.m_dp_approvel:
                if (homeMembers == null)
                    homeMembers = new HomeMembers();
                swiftFragments(homeMembers, "homeMembers");
                break;
            default:
                break;
        }

    }

    private void swiftFragments(ParentFragment frag, String tag) {
        FragmentTransaction trans = manager.beginTransaction();
        if (frag.isAdded() && frag.isVisible()) {
            return;
        } else if (frag.isAdded() && frag.isHidden()) {
            trans.hide(fragStack.get(fragStack.size() - 1));
            trans.show(frag);

        } else if (!frag.isAdded()) {
            try {

                ParentFragment pf = fragStack.get(fragStack.size() - 1);
                trans.hide(pf);

                trans.add(R.id.container, frag, tag);
                trans.show(frag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        trans.commitAllowingStateLoss();
        trans = null;
        // getSupportActionBar().setTitle(frag.getFragmentName());
        if (!(frag instanceof Home))
            fragStack.push((ParentFragment) frag);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mBroadcastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                SharedValues.saveValue(getApplicationContext(), "member_id", "");
                getApplicationContext().deleteDatabase("Associate_new.db");
                Intent sc = new Intent(getApplicationContext(), SendOtpScreen.class);
                startActivity(sc);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public boolean isDrawerOpen() {
        return false;// mNavigationDrawerFragment.isDrawerOpen();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {

            if (fragStack.size() > 1) {

                ParentFragment pf = fragStack.peek();

                if (pf.back() == true)
                    return true;

                fragStack.pop();

                FragmentTransaction trans = manager.beginTransaction();
                trans.remove(pf);

                ParentFragment pf1 = fragStack.get(fragStack.size() - 1);
                trans.show(pf1);
                trans.commit();

                // getSupportActionBar().setTitle(pf1.getFragmentName());
                return true;
            }


            // return false;
            return super.onKeyDown(keyCode, event);

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerLayout != null)
                    drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void temp() {
    }

    void getredAlert(String text, final String codition) {
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Alert!")
                .setContentText(text)
                .setConfirmText("Ok")

                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        if (codition.equalsIgnoreCase("1")) {
                            Intent pay = new Intent(getApplicationContext(), MPayment.class);
                            startActivity(pay);
                        }
                    }
                })
                .show();
    }

    public void getRolesList(String dest_type, String Id) {
        Bundle bundle = new Bundle();
        bundle.putString("dest_key", dest_type);
        bundle.putString("_id", Id);
        aphsrolesFragment = new APHSROLESFragment();
        aphsrolesFragment.setArguments(bundle);
        swiftFragments(aphsrolesFragment, "aphsrolesFragment");
    }

    public void getRolesMemberData(List<MemberModel> matchesList, int position, String id) {
        Bundle bundle = new Bundle();
        bundle.putString("dest_key", matchesList.get(position).getId());
        bundle.putString("district_id", id);
        aphMembersFragment = new APHMembersFragment();
        aphMembersFragment.setArguments(bundle);
        swiftFragments(aphMembersFragment, "aphMembersFragment");
    }

    public void getVipView(String id, String name) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("name", name);
        vipsFragment = new VIPSFragment();
        vipsFragment.setArguments(bundle);
        swiftFragments(vipsFragment, "vipsFragment");

    }

    public void showSectors(List<MemberModel> matchesList, String id, int pos) {
        Bundle bundle = new Bundle();
        bundle.putString("sector_id", matchesList.get(pos).getId());
        bundle.putString("district_id", id);
        bundle.putString("name", matchesList.get(pos).getName());
        showVIPFragment = new ShowVIPFragment();
        showVIPFragment.setArguments(bundle);
        swiftFragments(showVIPFragment, "showVIPFragment");
    }
}

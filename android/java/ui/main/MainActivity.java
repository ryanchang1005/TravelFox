package com.travelfox.ryan.ui.main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.travelfox.ryan.R;
import com.travelfox.ryan.base.BaseActivity;
import com.travelfox.ryan.ui.discoverPlace.DiscoverPlaceFragment;
import com.travelfox.ryan.ui.me.MeFragment;
import com.travelfox.ryan.ui.myTravel.MyTravelFragment;
import com.travelfox.ryan.ui.publicTravel.PublicTravelFragment;

public class MainActivity extends BaseActivity {

    BottomNavigationView bottomNavigationView;

    // 當前Fragment資訊
    Fragment currentFragment;
    Fragment mDiscoverPlaceFragment, mPublicTravelFragment, mMyTravelFragment, mMeFragment;
    Class currentFragmentClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.main_activity);
        super.onCreate(savedInstanceState);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            item.setChecked(true);
            if (item.getItemId() == R.id.discoverPlaceItem) {
                showFragment(mDiscoverPlaceFragment);
            } else if (item.getItemId() == R.id.publicTravelItem) {
                showFragment(mPublicTravelFragment);
            } else if (item.getItemId() == R.id.myTravelItem) {
                showFragment(mMyTravelFragment);
            } else if (item.getItemId() == R.id.meItem) {
                showFragment(mMeFragment);
            }
            return false;
        });

        // init Fragment
        mDiscoverPlaceFragment = DiscoverPlaceFragment.getInstance();
        mPublicTravelFragment = PublicTravelFragment.getInstance();
        mMyTravelFragment = MyTravelFragment.getInstance();
        mMeFragment = MeFragment.getInstance();

        showFragment(mDiscoverPlaceFragment);
    }

    private void showFragment(Fragment fragment) {
        if (fragment.getClass() == currentFragmentClass) {
            return;
        }

        if (fragment.isAdded()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(currentFragment)
                    .show(fragment)
                    .commit();
        } else {
            FragmentTransaction ft = getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frameLayout, fragment);
            if (currentFragment != null) {
                ft.hide(currentFragment);
            }
            ft.commit();
        }

        currentFragment = fragment;
        currentFragmentClass = fragment.getClass();
    }
}
package com.travelfox.ryan.ui.init;

import android.os.Bundle;
import android.os.Handler;

import com.travelfox.ryan.R;
import com.travelfox.ryan.base.BaseActivity;
import com.travelfox.ryan.ui.main.MainActivity;
import com.travelfox.ryan.ui.login.LoginActivity;
import com.travelfox.ryan.utils.PrefUtils;

public class InitActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_activity);


        new Handler().postDelayed(() -> {
            // 判斷是否已登入, 有accessToken代表已登入
            String accessToken = getPrefUtils().getString(PrefUtils.ACCESS_TOKEN);
            if (accessToken == null) {
                openActivity(LoginActivity.class);
            } else {
                openActivity(MainActivity.class);
            }
            finish();
        }, 1200);
    }
}
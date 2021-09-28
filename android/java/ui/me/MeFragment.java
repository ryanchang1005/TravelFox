package com.travelfox.ryan.ui.me;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.travelfox.ryan.BuildConfig;
import com.travelfox.ryan.R;
import com.travelfox.ryan.api.response.GetUserDetailResponse;
import com.travelfox.ryan.base.BaseFragment;
import com.travelfox.ryan.utils.PrefUtils;

public class MeFragment extends BaseFragment {

    public static Fragment getInstance() {
        return new MeFragment();
    }

    TextView tvDisplayName, tvLoginPlatform, tvVersionCode;

    @Override
    protected int getLayoutResource() {
        return R.layout.me_fragment;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        tvDisplayName = findViewById(R.id.tvDisplayName);
        tvLoginPlatform = findViewById(R.id.tvLoginPlatform);
        tvVersionCode = findViewById(R.id.tvVersionCode);
        findViewById(R.id.btnFavoritePlace).setOnClickListener(view -> {

        });
        findViewById(R.id.btnFavoriteTravel).setOnClickListener(view -> {

        });
        findViewById(R.id.btnLogout).setOnClickListener(view -> logoutDialog());

        tvVersionCode.setText(BuildConfig.VERSION_NAME);

        getUserDetail();
    }

    private void getUserDetail() {
        String userId = getPrefUtils().getString(PrefUtils.USER_ID);
        getPlannerService().getUserDetail(userId, (statusCode, responseBody, throwable) -> {
            GetUserDetailResponse rsp = (GetUserDetailResponse) handleBasicOnResponse(statusCode, responseBody, throwable, GetUserDetailResponse.class);
            if (rsp != null) {
                tvDisplayName.setText(rsp.display_name);
                tvLoginPlatform.setText(rsp.login_platform);
            }
        });
    }

    private void logoutDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("登出")
                .setMessage("確定要登出嗎?")
                .setPositiveButton("確定", (dialogInterface, i) -> logout())
                .setNegativeButton("取消", null)
                .show();
    }
}

package com.travelfox.ryan.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.travelfox.ryan.R;
import com.travelfox.ryan.api.ErrorCode;
import com.travelfox.ryan.api.PlannerService;
import com.travelfox.ryan.api.response.BaseResponse;
import com.travelfox.ryan.api.response.ErrorResponse;
import com.travelfox.ryan.ui.login.LoginActivity;
import com.travelfox.ryan.utils.PrefUtils;
import com.google.gson.Gson;


public class BaseActivity extends AppCompatActivity {

    View activeToDisplayLayout;
    View progressLayout;

    PlannerService mPlannerService;
    PrefUtils mPrefUtils;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activeToDisplayLayout = findViewById(R.id.activeToDisplayLayout);
        progressLayout = findViewById(R.id.progressLayout);

        mPrefUtils = new PrefUtils(this);
        mPlannerService = new PlannerService(mPrefUtils, this);
    }

    public void showProgressView() {
        activeToDisplayLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
    }

    public void dismissProgressView() {
        activeToDisplayLayout.setVisibility(View.VISIBLE);
        progressLayout.setVisibility(View.GONE);
    }

    public PlannerService getPlannerService() {
        return mPlannerService;
    }

    public PrefUtils getPrefUtils() {
        return mPrefUtils;
    }

    public void openActivity(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }

    public void toast(int resId) {
        toast(getString(resId));
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void logout() {
        getPrefUtils().clear();
        openActivity(LoginActivity.class);
        finishAffinity();
    }

    public BaseResponse handleBasicOnResponse(
            int statusCode, String responseBody, Throwable throwable, Class<?> clazz
    ) {
        try {
            if (200 <= statusCode && statusCode <= 299) {
                return (BaseResponse) new Gson().fromJson(responseBody, clazz);
            } else {
                ErrorResponse errorResponse = new Gson().fromJson(responseBody, ErrorResponse.class);
                switch (errorResponse.code) {
                    case ErrorCode.INVALID_TOKEN:
                        toast(R.string.invalid_token);
                        logout();
                        break;
                    case ErrorCode.LOGIN_FAILED:
                        toast(R.string.invalid_token);
                        break;
                    default:
                        toast(errorResponse.message + "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

package com.travelfox.ryan.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.travelfox.ryan.R;
import com.travelfox.ryan.api.PlannerService;
import com.travelfox.ryan.api.response.BaseResponse;
import com.travelfox.ryan.utils.PrefUtils;

public abstract class BaseFragment extends Fragment {

    View activeToDisplayLayout;
    View progressLayout;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    protected abstract int getLayoutResource();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutResource(), container, false);
    }

    @Override
    public void onViewStateRestored(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        activeToDisplayLayout = findViewById(R.id.activeToDisplayLayout);
        progressLayout = findViewById(R.id.progressLayout);
    }

    protected <T extends View> T findViewById(int resId) {
        return getView().findViewById(resId);
    }

    private BaseActivity getBaseActivity() {
        FragmentActivity activity = getActivity();
        if (activity instanceof BaseActivity) {
            return (BaseActivity) activity;
        }
        return null;
    }

    public void showProgressView() {
        activeToDisplayLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
    }

    public void dismissProgressView() {
        if (activeToDisplayLayout != null) {
            activeToDisplayLayout.setVisibility(View.VISIBLE);
        }
        if (progressLayout != null) {
            progressLayout.setVisibility(View.GONE);
        }
    }

    public void toast(int resId) {
        toast(getString(resId));
    }

    public void toast(String text) {
        if (getBaseActivity() != null) {
            getBaseActivity().toast(text);
        }
    }

    public void hideKeyboard() {
        if (getBaseActivity() != null) {
            getBaseActivity().hideKeyboard();
        }
    }

    public void openActivity(Class<?> clz) {
        startActivity(new Intent(getActivity(), clz));
    }

    public void finishAffinity() {
        if (getBaseActivity() != null) {
            getBaseActivity().finishAffinity();
        }
    }

    public PrefUtils getPrefUtils() {
        if (getBaseActivity() != null) {
            return getBaseActivity().getPrefUtils();
        }
        return null;
    }

    public PlannerService getPlannerService() {
        if (getBaseActivity() != null) {
            return getBaseActivity().getPlannerService();
        }
        return null;
    }

    public void logout() {
        if (getBaseActivity() != null) {
            getBaseActivity().logout();
        }
    }

    public BaseResponse handleBasicOnResponse(
            int statusCode, String responseBody, Throwable throwable, Class<?> clazz
    ) {
        if (getBaseActivity() != null) {
            return getBaseActivity().handleBasicOnResponse(statusCode, responseBody, throwable, clazz);
        }
        return null;
    }
}

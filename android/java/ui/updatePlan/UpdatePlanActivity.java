package com.travelfox.ryan.ui.updatePlan;

import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.travelfox.ryan.R;
import com.travelfox.ryan.base.BaseActivity;
import com.travelfox.ryan.api.request.UpdatePlanRequest;
import com.travelfox.ryan.api.response.UpdatePlanResponse;

public class UpdatePlanActivity extends BaseActivity {

    long planId;
    String planName;

    EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.plan_transfer_activity);
        super.onCreate(savedInstanceState);

        planId = getIntent().getLongExtra("planId", -1);
        planName = getIntent().getStringExtra("planName");

        etName = findViewById(R.id.etName);
        findViewById(R.id.btnUpdate).setOnClickListener(view -> updatePlan());

        etName.setHint(planName);
        etName.setText(planName);

        // 顯示鍵盤
        etName.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etName, InputMethodManager.SHOW_IMPLICIT);
    }


    private void updatePlan() {
        hideKeyboard();
        String name = etName.getText().toString();
        if (name.isEmpty()) {
            toast("請輸入");
            return;
        }
        UpdatePlanRequest request = new UpdatePlanRequest();
        request.name = name;
        getPlannerService().updatePlan(planId, request, (statusCode, responseBody, throwable) -> {
            UpdatePlanResponse rsp = (UpdatePlanResponse) handleBasicOnResponse(statusCode, responseBody, throwable, UpdatePlanResponse.class);
            if (rsp != null) {
                toast("修改計畫成功");
                finish();
            }
        });
    }
}
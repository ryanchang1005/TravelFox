package com.travelfox.ryan.ui.placeSelect;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.travelfox.ryan.R;
import com.travelfox.ryan.api.response.GetPlaceListResponse;
import com.travelfox.ryan.base.BaseActivity;
import com.travelfox.ryan.base.DefaultTextWatcher;

public class PlaceSelectActivity extends BaseActivity {
    String tag;

    EditText etKeyword;
    View btnClear;
    RecyclerView rvKeywordMatch;

    KeywordMatchAdapter mKeywordMatchAdapter;

    long lastTypeTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.place_select_activity);
        super.onCreate(savedInstanceState);

        tag = getIntent().getStringExtra("tag");

        btnClear = findViewById(R.id.btnClear);
        etKeyword = findViewById(R.id.etKeyword);
        rvKeywordMatch = findViewById(R.id.rvKeywordMatch);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        btnClear.setOnClickListener(v -> clearKeyword());
        etKeyword.addTextChangedListener(new DefaultTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                btnClear.setVisibility(s.toString().trim().isEmpty() ? View.INVISIBLE : View.VISIBLE);

                // 打完文字後1秒才去執行查詢, 如在這1秒內還繼續打字的話剛的查詢會被取消
                lastTypeTime = System.currentTimeMillis();
                Message msg = Message.obtain();
                msg.obj = lastTypeTime;
                mHandler.sendMessageDelayed(msg, 600);
            }
        });

        rvKeywordMatch.setLayoutManager(new LinearLayoutManager(this));
        rvKeywordMatch.setAdapter(mKeywordMatchAdapter = new KeywordMatchAdapter(this));
        mKeywordMatchAdapter.setOnItemClick((v, place) -> {
            Intent intent = new Intent();
            intent.putExtra("place", place);
            intent.putExtra("tag", tag);
            setResult(RESULT_OK, intent);
            finish();
        });

        getPlaceList("");
    }

    private void clearKeyword() {
        etKeyword.setText("");
    }

    private void getPlaceList(String keyword) {
        showProgressView();
        getPlannerService().getPlaceList(keyword, (statusCode, responseBody, throwable) -> {
            GetPlaceListResponse rsp = (GetPlaceListResponse) handleBasicOnResponse(statusCode, responseBody, throwable, GetPlaceListResponse.class);
            if (rsp != null) {
                mKeywordMatchAdapter.setData(rsp.results, keyword);
            }
            dismissProgressView();
        });
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            long time = (long) msg.obj;
            if (time == lastTypeTime) {

                // 代表距離上次打完字後1秒內沒有在打字了, 這邊再去查詢關鍵字
                getPlaceList(etKeyword.getText().toString().trim());
            } else {
//                L.d("typing");
            }
        }
    };
}
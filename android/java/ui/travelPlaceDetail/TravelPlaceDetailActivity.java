package com.travelfox.ryan.ui.travelPlaceDetail;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.travelfox.ryan.R;
import com.travelfox.ryan.api.request.UpdateTravelPlaceExpenseRequest;
import com.travelfox.ryan.api.request.UpdateTravelPlaceRemarksRequest;
import com.travelfox.ryan.api.response.DeleteTravelPlaceResponse;
import com.travelfox.ryan.api.response.UpdateTravelPlaceExpenseResponse;
import com.travelfox.ryan.api.response.UpdateTravelPlaceRemarksResponse;
import com.travelfox.ryan.base.BaseActivity;
import com.travelfox.ryan.entity.TravelPlace;
import com.travelfox.ryan.ui.updateText.UpdateTextActivity;
import com.travelfox.ryan.utils.StrUtils;

public class TravelPlaceDetailActivity extends BaseActivity {

    ActivityResultLauncher<Intent> updateTextActivityResultLauncher;

    // extra
    TravelPlace travelPlace;
    String travelId;

    // flag
    boolean isDeleted = false;

    TextView tvName;
    TextView tvAddress;
    TextView tvRemarks;
    TextView tvExpense;
    ImageView ivCreatorAvatar;
    TextView tvCreatorName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.travel_place_detail_activity);
        super.onCreate(savedInstanceState);

        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvRemarks = findViewById(R.id.tvRemarks);
        tvExpense = findViewById(R.id.tvExpense);
        tvCreatorName = findViewById(R.id.tvCreatorName);
        ivCreatorAvatar = findViewById(R.id.ivCreatorAvatar);
        tvRemarks.setOnClickListener(view -> {
            Intent intent = new Intent(this, UpdateTextActivity.class);
            intent.putExtra("updateTarget", "remarks");
            intent.putExtra("title", "修改旅程景點備註/心得");
            intent.putExtra("text", travelPlace.remarks);
            updateTextActivityResultLauncher.launch(intent);
        });
        tvExpense.setOnClickListener(view -> {
            Intent intent = new Intent(this, UpdateTextActivity.class);
            intent.putExtra("updateTarget", "expense");
            intent.putExtra("title", "修改旅程景點花費");
            intent.putExtra("text", travelPlace.expense);
            updateTextActivityResultLauncher.launch(intent);
        });
        findViewById(R.id.btnDeleteTravelPlace).setOnClickListener(view -> {
            deleteTravelPlace();
        });
        findViewById(R.id.btnGoogleMap).setOnClickListener(view -> {
            String geoUri = StrUtils.format("http://maps.google.com/maps?q=%s %s", travelPlace.name, travelPlace.address);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
            startActivity(intent);
        });


        initStartActivityForResult();

        travelPlace = getIntent().getParcelableExtra("travelPlace");
        travelId = getIntent().getStringExtra("travelId");

        tvName.setText(travelPlace.name);
        tvAddress.setText(travelPlace.address);
        tvRemarks.setText(travelPlace.remarks);
        tvExpense.setText(String.valueOf(travelPlace.expense));
        Glide.with(this).load(travelPlace.creator.avatar_url).circleCrop().into(ivCreatorAvatar);
        tvCreatorName.setText(travelPlace.creator.name);
    }

    private void saveTravelPlaceState(){
        Intent intent = new Intent();
        intent.putExtra("travelPlace", travelPlace);
        intent.putExtra("isDeleted", isDeleted);
        setResult(RESULT_OK, intent);
    }

    private void initStartActivityForResult() {
        updateTextActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String updateTarget = data.getStringExtra("updateTarget");
                        String text = data.getStringExtra("text");
                        if (updateTarget.equals("remarks")) {
                            updateTravelPlaceRemarks(text);
                        } else if (updateTarget.equals("expense")) {
                            updateTravelPlaceExpense(text);
                        }
                    }
                }
        );
    }

    private void updateTravelPlaceRemarks(String remarks) {
        // 修改旅程景點備註
        UpdateTravelPlaceRemarksRequest request = new UpdateTravelPlaceRemarksRequest();
        request.place_id = travelPlace.id;
        request.remarks = remarks;

        getPlannerService().updateTravelPlaceRemarks(request, travelId, (statusCode, responseBody, throwable) -> {
            UpdateTravelPlaceRemarksResponse rsp = (UpdateTravelPlaceRemarksResponse) handleBasicOnResponse(statusCode, responseBody, throwable, UpdateTravelPlaceRemarksResponse.class);
            if (rsp != null) {
                travelPlace.remarks = remarks;
                tvRemarks.setText(remarks);

                saveTravelPlaceState();
            }
        });
    }

    private void updateTravelPlaceExpense(String expense) {
        // 修改旅程景點花費
        UpdateTravelPlaceExpenseRequest request = new UpdateTravelPlaceExpenseRequest();
        request.place_id = travelPlace.id;
        request.expense = expense;

        getPlannerService().updateTravelPlaceExpense(request, travelId, (statusCode, responseBody, throwable) -> {
            UpdateTravelPlaceExpenseResponse rsp = (UpdateTravelPlaceExpenseResponse) handleBasicOnResponse(statusCode, responseBody, throwable, UpdateTravelPlaceExpenseResponse.class);
            if (rsp != null) {
                travelPlace.expense = expense;
                tvExpense.setText(expense);

                saveTravelPlaceState();
            }
        });
    }

    private void deleteTravelPlace() {
        getPlannerService().deleteTravelPlace(travelId, travelPlace.id, (statusCode, responseBody, throwable) -> {
            DeleteTravelPlaceResponse rsp = (DeleteTravelPlaceResponse) handleBasicOnResponse(statusCode, responseBody, throwable, DeleteTravelPlaceResponse.class);
            if (statusCode == 204) {
                isDeleted = true;
                saveTravelPlaceState();
                finish();
            }
        });
    }
}
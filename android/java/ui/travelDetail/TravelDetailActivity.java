package com.travelfox.ryan.ui.travelDetail;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.travelfox.ryan.R;
import com.travelfox.ryan.api.request.CreateTravelPlaceRequest;
import com.travelfox.ryan.api.request.UpdateTravelNameRequest;
import com.travelfox.ryan.api.request.UpdateTravelRemarksRequest;
import com.travelfox.ryan.api.request.UpdateTravelTimeRequest;
import com.travelfox.ryan.api.response.CreateTravelPlaceResponse;
import com.travelfox.ryan.api.response.UpdateTravelNameResponse;
import com.travelfox.ryan.api.response.UpdateTravelRemarksResponse;
import com.travelfox.ryan.api.response.UpdateTravelTimeResponse;
import com.travelfox.ryan.base.BaseActivity;
import com.travelfox.ryan.entity.Place;
import com.travelfox.ryan.entity.PlaceTag;
import com.travelfox.ryan.entity.Travel;
import com.travelfox.ryan.entity.TravelPlace;
import com.travelfox.ryan.ui.placeSelect.PlaceSelectActivity;
import com.travelfox.ryan.ui.travelPlaceDetail.TravelPlaceDetailActivity;
import com.travelfox.ryan.ui.updateText.UpdateTextActivity;
import com.travelfox.ryan.ui.updateTravelTime.UpdateTravelTimeActivity;
import com.travelfox.ryan.utils.StrUtils;
import com.travelfox.ryan.utils.TimeUtils;

public class TravelDetailActivity extends BaseActivity {

    ActivityResultLauncher<Intent> placeSelectActivityResultLauncher;
    ActivityResultLauncher<Intent> updateTextActivityResultLauncher;
    ActivityResultLauncher<Intent> travelPlaceDetailActivityResultLauncher;
    ActivityResultLauncher<Intent> updateTravelTimeActivityResultLauncher;

    String travelId;
    Travel travel;

    TextView tvName;
    TextView tvFromTimeAndToTime;
    RecyclerView rvTravelPlaceAttraction, rvTravelPlaceLive;
    TravelPlaceAdapter travelPlaceAttractionAdapter, travelPlaceLiveAdapter;
    TextView tvRemarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.travel_detail_activity);
        super.onCreate(savedInstanceState);

        travelId = getIntent().getStringExtra("travelId");

        tvName = findViewById(R.id.tvName);
        tvFromTimeAndToTime = findViewById(R.id.tvFromTimeAndToTime);
        rvTravelPlaceAttraction = findViewById(R.id.rvTravelPlaceAttraction);
        rvTravelPlaceLive = findViewById(R.id.rvTravelPlaceLive);
        tvRemarks = findViewById(R.id.tvRemarks);
        findViewById(R.id.btnCreateTravelPlaceLive).setOnClickListener(view -> openPlaceSelectByTag(PlaceTag.LIVE));
        findViewById(R.id.btnCreateTravelPlaceAttraction).setOnClickListener(view -> openPlaceSelectByTag(PlaceTag.ATTRACTION));
        findViewById(R.id.btnShare).setOnClickListener(view -> toast("Share"));
        findViewById(R.id.btnEditTogether).setOnClickListener(view -> toast("Edit together"));
        findViewById(R.id.btnShare).setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("TravelPlanner", travel.getClipboardText(this));
            clipboard.setPrimaryClip(clip);
            toast("已複製到剪貼簿");
        });
        tvName.setOnClickListener(view -> {
            Intent intent = new Intent(this, UpdateTextActivity.class);
            intent.putExtra("updateTarget", "name");
            intent.putExtra("title", "修改旅程名稱");
            intent.putExtra("text", travel.name);
            updateTextActivityResultLauncher.launch(intent);
        });
        tvRemarks.setOnClickListener(view -> {
            Intent intent = new Intent(this, UpdateTextActivity.class);
            intent.putExtra("updateTarget", "remarks");
            intent.putExtra("title", "修改旅程備註/心得");
            intent.putExtra("text", travel.remarks);
            updateTextActivityResultLauncher.launch(intent);
        });
        tvFromTimeAndToTime.setOnClickListener(view -> {
            Intent intent = new Intent(this, UpdateTravelTimeActivity.class);
            intent.putExtra("fromTime", travel.from_time);
            intent.putExtra("toTime", travel.to_time);
            updateTravelTimeActivityResultLauncher.launch(intent);
        });

        initStartActivityForResult();

        // 住宿
        rvTravelPlaceLive.setLayoutManager(new LinearLayoutManager(this));
        rvTravelPlaceLive.setAdapter(travelPlaceLiveAdapter = new TravelPlaceAdapter());
        travelPlaceLiveAdapter.setOnItemClick(mOnItemClickListener);

        // 景點
        rvTravelPlaceAttraction.setLayoutManager(new LinearLayoutManager(this));
        rvTravelPlaceAttraction.setAdapter(travelPlaceAttractionAdapter = new TravelPlaceAdapter());
        travelPlaceAttractionAdapter.setOnItemClick(mOnItemClickListener);

        getTravelDetail();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getTravelDetail();
    }

    private void initStartActivityForResult() {
        placeSelectActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Return from PlaceSelectActivity, Create TravelPlace
                        Intent data = result.getData();
                        Place place = data.getParcelableExtra("place");
                        String tag = data.getStringExtra("tag");
                        createTravelPlace(place, tag);
                    }
                }
        );
        updateTextActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Return from UpdateTextActivity
                        // Update 'Travel.name' or 'Travel.remarks'
                        Intent data = result.getData();
                        String updateTarget = data.getStringExtra("updateTarget");
                        String text = data.getStringExtra("text");
                        if (updateTarget.equals("name")) {
                            updateTravelName(text);
                        } else if (updateTarget.equals("remarks")) {
                            updateTravelRemarks(text);
                        }
                    }
                }
        );
        travelPlaceDetailActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Return from TravelPlaceDetail
                        Intent data = result.getData();
                        boolean isDeleted = data.getBooleanExtra("isDeleted", false);
                        TravelPlace travelPlace = data.getParcelableExtra("travelPlace");

                        if (isDeleted) {
                            // Deleted from TravelPlace
                            travel.deleteTravelPlace(travelPlace);
                        } else {
                            // Update from TravelPlace
                            travel.updateTravelPlace(travelPlace);
                        }

                        displayTravel(travel);
                    }
                }
        );
        updateTravelTimeActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Return from UpdateTravelTimeActivity
                        Intent data = result.getData();
                        updateTravelTime(data.getStringExtra("fromTime"), data.getStringExtra("toTime"));
                    }
                }
        );
    }

    private void openPlaceSelectByTag(String tag) {
        Intent intent = new Intent(this, PlaceSelectActivity.class);
        intent.putExtra("tag", tag);
        placeSelectActivityResultLauncher.launch(intent);
    }

    private void getTravelDetail() {
        getPlannerService().getTravelDetail(travelId, (statusCode, responseBody, throwable) -> {
            Travel responseTravel = (Travel) handleBasicOnResponse(statusCode, responseBody, throwable, Travel.class);
            if (responseTravel != null) {
                // set travel variable
                travel = responseTravel;

                // name, from_time, to_time, Live, Attraction
                displayTravel(travel);

                // 隱藏Progress
                dismissProgressView();
            }
        });
    }

    private void displayTravel(Travel travel) {
        tvName.setText(travel.name);
        tvFromTimeAndToTime.setText(StrUtils.format("%s ~ %s", TimeUtils.ISO8601ToDisplayDate(travel.from_time), TimeUtils.ISO8601ToDisplayDate(travel.to_time)));
        tvRemarks.setText(travel.remarks == null ? "" : travel.remarks);

        // Live
        travelPlaceLiveAdapter.setData(travel.live_list);

        // Attraction
        travelPlaceAttractionAdapter.setData(travel.attraction_list);
    }

    private void createTravelPlace(Place place, String tag) {
        CreateTravelPlaceRequest request = new CreateTravelPlaceRequest();
        request.place_id = place.id;
        request.tag = tag;

        getPlannerService().createTravelPlace(request, travel.id, (statusCode, responseBody, throwable) -> {
            CreateTravelPlaceResponse rsp = (CreateTravelPlaceResponse) handleBasicOnResponse(statusCode, responseBody, throwable, CreateTravelPlaceResponse.class);
            if (rsp != null) {
                getTravelDetail();
            }
        });
    }

    private void updateTravelName(String name) {
        UpdateTravelNameRequest request = new UpdateTravelNameRequest();
        request.name = name;

        getPlannerService().updateTravelName(request, travel.id, (statusCode, responseBody, throwable) -> {
            UpdateTravelNameResponse rsp = (UpdateTravelNameResponse) handleBasicOnResponse(statusCode, responseBody, throwable, UpdateTravelNameResponse.class);
            if (rsp != null) {
                travel.name = rsp.name;
                tvName.setText(rsp.name);
            }
        });
    }

    private void updateTravelTime(String fromTime, String toTime) {
        UpdateTravelTimeRequest request = new UpdateTravelTimeRequest();
        request.from_time = fromTime;
        request.to_time = toTime;

        getPlannerService().updateTravelTime(request, travel.id, (statusCode, responseBody, throwable) -> {
            UpdateTravelTimeResponse rsp = (UpdateTravelTimeResponse) handleBasicOnResponse(statusCode, responseBody, throwable, UpdateTravelTimeResponse.class);
            if (rsp != null) {
                travel.from_time = rsp.from_time;
                travel.to_time = rsp.to_time;
                tvFromTimeAndToTime.setText(StrUtils.format("%s ~ %s", TimeUtils.ISO8601ToDisplayDate(travel.from_time), TimeUtils.ISO8601ToDisplayDate(travel.to_time)));
            }
        });
    }

    private void updateTravelRemarks(String remarks) {
        UpdateTravelRemarksRequest request = new UpdateTravelRemarksRequest();
        request.remarks = remarks;

        getPlannerService().updateTravelRemarks(request, travel.id, (statusCode, responseBody, throwable) -> {
            UpdateTravelRemarksResponse rsp = (UpdateTravelRemarksResponse) handleBasicOnResponse(statusCode, responseBody, throwable, UpdateTravelRemarksResponse.class);
            if (rsp != null) {
                travel.remarks = rsp.remarks;
                tvRemarks.setText(rsp.remarks);
            }
        });
    }

    TravelPlaceAdapter.OnItemClickListener mOnItemClickListener = (v, travelPlace) -> {
        Intent intent = new Intent(this, TravelPlaceDetailActivity.class);
        intent.putExtra("travelPlace", travelPlace);
        intent.putExtra("travelId", travelId);
        travelPlaceDetailActivityResultLauncher.launch(intent);
    };
}
package com.travelfox.ryan.ui.placeDetail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.travelfox.ryan.R;
import com.travelfox.ryan.base.BaseActivity;
import com.travelfox.ryan.entity.Place;
import com.travelfox.ryan.utils.StrUtils;

public class PlaceDetailActivity extends BaseActivity {

    String placeId;
    Place place;

    TextView tvName, tvAddress, tvTag;
    CardView cardSave;
    TextView tvSaveText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.place_detail_activity);
        super.onCreate(savedInstanceState);

        placeId = getIntent().getStringExtra("placeId");
        tvName = findViewById(R.id.tvName);
        tvAddress = findViewById(R.id.tvAddress);
        tvTag = findViewById(R.id.tvTag);
        cardSave = findViewById(R.id.cardSave);
        tvSaveText = findViewById(R.id.tvSaveText);
        findViewById(R.id.cardSave).setOnClickListener(view -> {

        });
        findViewById(R.id.cardGoogleIt).setOnClickListener(view -> {
            String geoUri = StrUtils.format("http://maps.google.com/maps?q=%s %s", place.name, place.address);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
            startActivity(intent);
        });

        getPlaceDetail();
    }

    private void getPlaceDetail() {
        getPlannerService().getPlaceDetail(placeId, (statusCode, responseBody, throwable) -> {
            dismissProgressView();
            Place responsePlace = (Place) handleBasicOnResponse(statusCode, responseBody, throwable, Place.class);
            if (responsePlace != null) {
                place = responsePlace;
                displayPlace(place);
            }
        });
    }

    private void displayPlace(Place place) {
        tvName.setText(place.name);
        tvAddress.setText(place.address);

        // Tag
        String tagText = place.getTagsText();
        if (tagText != null) {
            tvTag.setText(tagText);
        }

        // 收藏
        if (place.is_saved) {
            cardSave.setCardBackgroundColor(getColor(R.color.button_background));
            tvSaveText.setText("已收藏");
        } else {
            cardSave.setCardBackgroundColor(getColor(R.color.background));
            tvSaveText.setText("收藏");
        }
    }
}
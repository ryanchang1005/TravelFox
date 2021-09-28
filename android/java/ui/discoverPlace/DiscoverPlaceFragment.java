package com.travelfox.ryan.ui.discoverPlace;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.SphericalUtil;
import com.travelfox.ryan.R;
import com.travelfox.ryan.api.response.GetPlaceListResponse;
import com.travelfox.ryan.base.BaseFragment;
import com.travelfox.ryan.entity.Place;
import com.travelfox.ryan.ui.placeDetail.PlaceDetailActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DiscoverPlaceFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    public static Fragment getInstance() {
        return new DiscoverPlaceFragment();
    }

    public static final LatLng DEFAULT_TAIPEI_101 = new LatLng(25.0338041, 121.5645561);

    EditText etKeyword;
    GoogleMap mMap;

    @Override
    protected int getLayoutResource() {
        return R.layout.discover_place_fragment;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        etKeyword = findViewById(R.id.etKeyword);
        etKeyword.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                onKeywordSearch(etKeyword.getText().toString());  // EditText on enter
                return true;
            }
            return false;
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));

        // Move camera to default position
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_TAIPEI_101, 12f));

        // Delay search near places
        new Handler().postDelayed(this::searchByCameraVisibleRegion, 1000);
    }

    @Override
    public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
        Place place = (Place) marker.getTag();

        Intent intent = new Intent(getActivity(), PlaceDetailActivity.class);
        intent.putExtra("placeId", place.id);
        startActivity(intent);
        return false;
    }

    private void addMarker(Place place) {
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.parseDouble(place.lat), Double.parseDouble(place.lng)))
                .icon(BitmapDescriptorFactory.fromBitmap(createMarkerBitmap(place.name))));
        marker.setTag(place);
    }

    private Bitmap createMarkerBitmap(String title) {
        View markerLayout = getLayoutInflater().inflate(R.layout.google_map_marker_layout, null);
        TextView tvMarkerTitle = markerLayout.findViewById(R.id.tvMarkerTitle);
        tvMarkerTitle.setText(title);

        markerLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        markerLayout.layout(0, 0, markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight());

        final Bitmap bitmap = Bitmap.createBitmap(markerLayout.getMeasuredWidth(), markerLayout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerLayout.draw(canvas);
        return bitmap;
    }

    private void onKeywordSearch(String keyword) {
        hideKeyboard();
        searchByCameraVisibleRegion();
    }

    private void searchByCameraVisibleRegion() {
        CameraPosition cameraPosition = mMap.getCameraPosition();

        // 中心點
        LatLng target = cameraPosition.target;

        // 可視距離, 寬, 高
        VisibleRegion viewPort = mMap.getProjection().getVisibleRegion();
        double viewPortHeight = SphericalUtil.computeDistanceBetween(viewPort.nearLeft, viewPort.farLeft);
        double viewPortWidth = SphericalUtil.computeDistanceBetween(viewPort.nearLeft, viewPort.nearRight);

        // 套用半徑 = min(寬, 高) / 2
        int distance = (int) (Math.min(viewPortHeight, viewPortWidth) / 2);
        getPlaceListByLatLngAndDistance(target.latitude, target.longitude, distance);
    }

    private void getPlaceListByLatLngAndDistance(double latitude, double longitude, int meter) {
        getPlannerService().getPlaceListByLatLngAndDistance(latitude, longitude, meter, (statusCode, responseBody, throwable) -> {
            GetPlaceListResponse rsp = (GetPlaceListResponse) handleBasicOnResponse(statusCode, responseBody, throwable, GetPlaceListResponse.class);
            if (rsp != null) {
                displayPlaceOnMap(rsp.results);
            }
        });
    }

    private void displayPlaceOnMap(List<Place> placeList) {
        mMap.clear();
        for (Place place : placeList) {
            addMarker(place);
        }
    }
}

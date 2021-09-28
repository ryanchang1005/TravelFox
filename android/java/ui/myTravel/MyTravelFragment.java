package com.travelfox.ryan.ui.myTravel;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.travelfox.ryan.R;
import com.travelfox.ryan.ui.createTravel.CreateTravelActivity;
import com.travelfox.ryan.ui.travelDetail.TravelDetailActivity;
import com.travelfox.ryan.api.response.GetSelfTravelListResponse;
import com.travelfox.ryan.base.BaseFragment;
import com.travelfox.ryan.entity.Travel;
import com.travelfox.ryan.utils.FakeUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MyTravelFragment extends BaseFragment {

    public static Fragment getInstance() {
        return new MyTravelFragment();
    }

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView rvTravel;
    TravelAdapter mTravelAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.my_travel_fragment;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        mSwipeRefreshLayout = findViewById(R.id.refresh_layout);
        rvTravel = findViewById(R.id.rvTravel);
        findViewById(R.id.btnCreateTravel).setOnClickListener(view -> openActivity(CreateTravelActivity.class));
        mSwipeRefreshLayout.setOnRefreshListener(this::getSelfTravelList);

        rvTravel.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvTravel.setAdapter(mTravelAdapter = new TravelAdapter());

        getSelfTravelList();
    }

    private void getSelfTravelList() {
        mTravelAdapter.setData(new ArrayList<>());
        getPlannerService().getSelfTravelList((statusCode, responseBody, throwable) -> {
            mSwipeRefreshLayout.setRefreshing(false);
            GetSelfTravelListResponse rsp = (GetSelfTravelListResponse) handleBasicOnResponse(statusCode, responseBody, throwable, GetSelfTravelListResponse.class);
            if (rsp != null) {
                mTravelAdapter.setData(rsp.results);
            }
        });
    }

    public class TravelAdapter extends RecyclerView.Adapter<TravelAdapter.ViewHolder> {
        List<Travel> travelList;

        public TravelAdapter() {
            travelList = new ArrayList<>();
        }

        public void setData(List<Travel> travelList) {
            this.travelList = travelList;
            notifyDataSetChanged();
        }

        @NonNull
        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_travel, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
            holder.setTravel(travelList.get(position));
        }

        @Override
        public int getItemCount() {
            return travelList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivBackground;
            TextView tvName;

            public ViewHolder(@NonNull @NotNull View itemView) {
                super(itemView);
                ivBackground = itemView.findViewById(R.id.ivBackground);
                tvName = itemView.findViewById(R.id.tvName);

                itemView.setOnClickListener(view ->
                        startActivity(
                                new Intent(getActivity(), TravelDetailActivity.class)
                                        .putExtra("travelId", travelList.get(getAdapterPosition()).id)
                        )
                );
            }

            public void setTravel(Travel travel) {
                tvName.setText(travel.name);
                ivBackground.setImageResource(FakeUtils.getSampleImage(travel.id));
            }
        }
    }
}

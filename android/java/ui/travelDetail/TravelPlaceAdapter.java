package com.travelfox.ryan.ui.travelDetail;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.travelfox.ryan.R;
import com.travelfox.ryan.entity.TravelPlace;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TravelPlaceAdapter extends RecyclerView.Adapter<TravelPlaceAdapter.ViewHolder> {

    List<TravelPlace> travelPlaceList;
    OnItemClickListener mOnItemClickListener;

    public TravelPlaceAdapter() {
        travelPlaceList = new ArrayList<>();
    }

    public void setData(List<TravelPlace> travelPlaceList) {
        this.travelPlaceList = travelPlaceList;
        notifyDataSetChanged();
    }

    public void setOnItemClick(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @NonNull
    @NotNull
    @Override
    public TravelPlaceAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new TravelPlaceAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_travel_place, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TravelPlaceAdapter.ViewHolder holder, int position) {
        holder.setTravelPlace(travelPlaceList.get(position));
    }

    @Override
    public int getItemCount() {
        return travelPlaceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvAddress;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);

            itemView.setOnClickListener(view -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, travelPlaceList.get(getAdapterPosition()));
                }
            });
        }

        @SuppressLint("DefaultLocale")
        public void setTravelPlace(TravelPlace travelPlace) {
            tvName.setText(travelPlace.name);
            tvAddress.setText(travelPlace.address);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, TravelPlace travelPlace);
    }
}


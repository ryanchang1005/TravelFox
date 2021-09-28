package com.travelfox.ryan.ui.placeSelect;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.travelfox.ryan.R;
import com.travelfox.ryan.entity.Place;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class KeywordMatchAdapter extends RecyclerView.Adapter<KeywordMatchAdapter.ViewHolder> {

    OnItemClick mOnItemClick;

    List<Place> placeList;
    String keyword;
    int keywordMatchHighlightColor;

    public KeywordMatchAdapter(Context context) {
        placeList = new ArrayList<>();
        keywordMatchHighlightColor = context.getColor(R.color.button_background);
    }

    public void setOnItemClick(OnItemClick mOnItemClick) {
        this.mOnItemClick = mOnItemClick;
    }

    public void setData(List<Place> placeList, String keyword) {
        this.placeList = placeList;
        this.keyword = keyword;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public KeywordMatchAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new KeywordMatchAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_keyword_match, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull KeywordMatchAdapter.ViewHolder holder, int position) {
        holder.setPlace(placeList.get(position));
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvAddress;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            itemView.setOnClickListener(view -> {
                if (mOnItemClick != null) {
                    mOnItemClick.onClick(view, placeList.get(getAdapterPosition()));
                }
            });
        }

        public void setPlace(Place place) {
            tvName.setText(getKeywordMatchHighLightText(place.name));
            tvAddress.setText(getKeywordMatchHighLightText(place.address));
        }

        private SpannableString getKeywordMatchHighLightText(String text) {
            // highlight符合的關鍵字
            SpannableString str = new SpannableString(text);
            if (keyword == null || keyword.isEmpty()) return str;
            int start = text.toLowerCase().indexOf(keyword.toLowerCase());
            int end = start + keyword.length();
            if (start != -1) {
                str.setSpan(new ForegroundColorSpan(keywordMatchHighlightColor), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            return str;
        }
    }

    public interface OnItemClick {
        void onClick(View v, Place place);
    }
}


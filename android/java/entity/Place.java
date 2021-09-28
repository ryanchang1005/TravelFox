package com.travelfox.ryan.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.travelfox.ryan.api.response.BaseResponse;

import java.util.ArrayList;
import java.util.List;

public class Place extends BaseResponse implements Parcelable {
    public String id;
    public String name; // 名稱
    public String address; // 地址
    public String lat; // 經度
    public String lng; // 緯度
    public String distance; // meter
    public boolean is_saved; // 收藏
    public List<String> tags; // 標籤

    public Place() {
        tags = new ArrayList<>();
    }

    protected Place(Parcel in) {
        id = in.readString();
        name = in.readString();
        address = in.readString();
        lat = in.readString();
        lng = in.readString();
        distance = in.readString();
        is_saved = in.readByte() != 0;
        tags = in.createStringArrayList();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeString(lat);
        parcel.writeString(lng);
        parcel.writeString(distance);
        parcel.writeByte((byte) (is_saved ? 1 : 0));
        parcel.writeStringList(tags);
    }

    public String getTagsText() {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String tag : tags) {
            if (sb.length() != 0) {
                sb.append("/");
            }
            sb.append(tag);
        }
        return sb.toString();
    }
}

package com.travelfox.ryan.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class TravelPlace implements Parcelable {
    public String id;
    public String name;
    public String address;
    public String lat;
    public String lng;
    public int order;
    public String remarks;
    public String expense;
    public String tag;
    public User creator;

    public TravelPlace(){

    }

    protected TravelPlace(Parcel in) {
        id = in.readString();
        name = in.readString();
        address = in.readString();
        lat = in.readString();
        lng = in.readString();
        order = in.readInt();
        remarks = in.readString();
        expense = in.readString();
        tag = in.readString();
        creator = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<TravelPlace> CREATOR = new Creator<TravelPlace>() {
        @Override
        public TravelPlace createFromParcel(Parcel in) {
            return new TravelPlace(in);
        }

        @Override
        public TravelPlace[] newArray(int size) {
            return new TravelPlace[size];
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
        parcel.writeInt(order);
        parcel.writeString(remarks);
        parcel.writeString(expense);
        parcel.writeString(tag);
        parcel.writeParcelable(creator, i);
    }

    public void copy(TravelPlace newTravelPlace) {
        this.remarks = newTravelPlace.remarks;
        this.expense = newTravelPlace.expense;
    }
}

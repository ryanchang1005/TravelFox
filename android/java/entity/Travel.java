package com.travelfox.ryan.entity;

import android.content.Context;

import com.travelfox.ryan.api.response.BaseResponse;
import com.travelfox.ryan.utils.StrUtils;
import com.travelfox.ryan.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Travel extends BaseResponse {
    public String id;
    public String name;
    public String from_time;
    public String to_time;
    public User creator;
    public boolean is_liked;
    public int comment_count;
    public int like_count;
    public List<TravelPlace> live_list;
    public List<TravelPlace> food_list;
    public List<TravelPlace> attraction_list;
    public List<TravelPlace> o_mi_ya_ga_list;
    public List<TravelPlace> traffic_list;
    public String remarks;

    public Travel() {
        live_list = new ArrayList<>();
        food_list = new ArrayList<>();
        attraction_list = new ArrayList<>();
        o_mi_ya_ga_list = new ArrayList<>();
        traffic_list = new ArrayList<>();
    }

    public List<TravelPlace> getTravelPlaceByTag(String tag) {
        if (tag == null) return null;

        switch (tag) {
            case PlaceTag.LIVE:
                return live_list;
            case PlaceTag.FOOD:
                return food_list;
            case PlaceTag.ATTRACTION:
                return attraction_list;
            case PlaceTag.O_MI_YA_GA:
                return o_mi_ya_ga_list;
            case PlaceTag.TRAFFIC:
                return traffic_list;
            default:
                return null;
        }
    }

    public String getClipboardText(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("\n");
        sb.append(StrUtils.format("%s ~ %s", TimeUtils.ISO8601ToDisplayDate(from_time), TimeUtils.ISO8601ToDisplayDate(to_time)));
        sb.append("\n------\n");

        sb.append("\n------住宿------\n");
        for (TravelPlace travelPlace : live_list) {
            sb.append(travelPlace.name);
            sb.append("\n");
        }

        sb.append("\n------美食------\n");
        for (TravelPlace travelPlace : food_list) {
            sb.append(travelPlace.name);
            sb.append("\n");
        }

        sb.append("\n------景點------\n");
        for (TravelPlace travelPlace : attraction_list) {
            sb.append(travelPlace.name);
            sb.append("\n");
        }

        sb.append("\n------伴手禮------\n");
        for (TravelPlace travelPlace : o_mi_ya_ga_list) {
            sb.append(travelPlace.name);
            sb.append("\n");
        }

        return sb.toString();
    }

    public void deleteTravelPlace(TravelPlace travelPlace) {
        // Delete TravelPlace from list(by tag)
        List<TravelPlace> list = getTravelPlaceByTag(travelPlace.tag);
        if (list == null) return;
        Iterator<TravelPlace> it = list.iterator();
        while (it.hasNext()) {
            if (it.next().id.equals(travelPlace.id)) {
                it.remove();
                break;
            }
        }
    }

    public void updateTravelPlace(TravelPlace travelPlace) {
        // Update TravelPlace from list(by tag)
        List<TravelPlace> list = getTravelPlaceByTag(travelPlace.tag);
        if (list == null) return;
        for (TravelPlace tp : list) {
            if (tp.id.equals(travelPlace.id)) {
                tp.copy(travelPlace);
                break;
            }
        }
    }
}

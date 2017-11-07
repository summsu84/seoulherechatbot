package com.chat.seoul.here.module.map.inf;


import com.chat.seoul.here.module.model.BaseModel;
import com.chat.seoul.here.module.model.place.PlaceModel;

import java.util.ArrayList;

/**
 * Created by JJW on 2016-10-17.
 */
public interface PlaceFinderListener {
    void onPlaceFinderStart();              //PlaceFinder 시작
    void onPlaceFinderSuccess(ArrayList<PlaceModel> items);            //Place Finder 성공
    void onPlaceFinderSuccess(ArrayList<BaseModel> items, int placeType);           //Base Type 과 PlaceType 리스너
    void onPlaceFinderFailed();
}

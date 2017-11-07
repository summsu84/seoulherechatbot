package com.chat.seoul.here.module.adapter;

import com.chat.seoul.here.module.model.festival.FestivalModel;
import com.chat.seoul.here.module.model.place.PlaceModel;

import java.util.ArrayList;

/**
 * Created by JJW on 2017-09-05
 * 채팅 뷰페이저의 자세히 보기 클릭시, 해당 뷰 페이저의 Chat Adapter에 있는 전체 PlaceList를 현시하기 위한 Interface
 */

public interface ViewPagerAdapterButtonListener {

    void onViewPagerAdapterAllListPlaceButtonClicked(ArrayList<PlaceModel> viewPagerPlaceItems);         //전체 보기 클릭

    void onViewPagerAdapterPlaceDetailButtonClicked(PlaceModel viewPagerPlaceItems);                    //상세 보기

    void onViewPagerAdapterFestivalDetailButtonClicked(FestivalModel festivalModel);                        //행사 정보 상세 보기

    void onViewPagerAdapterAllListFestivalButtonClicked(ArrayList<FestivalModel> viewPagerFestivalItems);   //행사 정보 전체 보기
}

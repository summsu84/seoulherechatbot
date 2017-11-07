package com.chat.seoul.here.module.adapter;

import com.chat.seoul.here.module.model.festival.FestivalModel;
import com.chat.seoul.here.module.model.place.PlaceModel;

import java.util.ArrayList;

/**
 * Created by JJW on 2017-04-18.
 */

public interface ChatAdapterButtonListener {
    void onChatAdapterButtonClicked(String buttonMessage, int meesageIntentType);

    void onChatAdapterFindPlace();
/*
    void onChatAdapterFindCurrentPlace();
    void onChatAdapterFindDestPlace();*/

    void onChatAdapterResponsePlaceSubButton();
    void onChatAdapterSendMessage(String msg);

    void onChatAdapterFindOriginDestPathPlace();

    //UI 버튼을 클릭시 호출되는 리스너너
   void onChatAdapterUIButtonClicked(String msg);


    //명소 정보 클릭 시
    void onChatAdapterViewPagerAdapterAllListPlaceButtonClicked(ArrayList<PlaceModel> viewPagerPlaceItems);
    void onChatAdapterViewPagerAdapterPlaceDetailButtonClicked(PlaceModel viewPagerPlaceItem);

    //행사 정보 클릭 시
    void onChatAdapterViewPagerAdapterFestivalDetailButtonClicked(FestivalModel festivalModel);
    void onChatAdapterViewPagerAdapterAllListFestivalButtonClicked(ArrayList<FestivalModel> viewPagerFestivalItems);
}

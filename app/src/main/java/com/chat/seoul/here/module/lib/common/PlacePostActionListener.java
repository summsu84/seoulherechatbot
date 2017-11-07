package com.chat.seoul.here.module.lib.common;

import com.chat.seoul.here.module.model.ChatMessage;

/**
 * Created by JJW on 2017-09-25.
 * API.AI로 부터 메시지를 받은 후, Place 또는 Festival Action 또는 Intent에 따라서
 * UI 작업 또는 추가 메세지 전송을 위한 인터페이스
 */
public interface PlacePostActionListener {
    //리스너의 종류에 따라서 리스너를 나눈다.
    //1. 메시지 생성 및 디스플레이스 Listener
    void onDisplayMessage(ChatMessage message);
    void onSendMessage(String method, String lat, String lon, String url, String version);
    void onDisplayMessage(String s, boolean b, int uiType, int intentType, int actionType);
    void onRequestPathPlaceSearch();
}

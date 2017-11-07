package com.chat.seoul.here.module.model;

import android.support.v4.view.ViewPager;

import com.chat.seoul.here.module.adapter.ViewPagerAdapter;
import com.chat.seoul.here.module.model.festival.FestivalModel;
import com.chat.seoul.here.module.model.place.PlaceModel;

import java.util.ArrayList;

import static com.chat.seoul.here.module.lib.conf.Common.CHAT_MESSAGE_FESTIVAL;
import static com.chat.seoul.here.module.lib.conf.Common.CHAT_MESSAGE_PLACE;

/**
 *  Chatting 메시지 포맷
 */
public class ChatMessage {
    private String type;
    private long id;
    private boolean isMe;
    private String message;
    private Long userId;
    private String dateTime;
    private boolean isSpeech;
    private boolean isViewPager;

    private int messageActionType;              //action_recommendPlaceSearch
    private int meesageIntentType;
    private int PLACE_TYPE;
    private int messageUiType;

    private ArrayList<PlaceModel> placeModelArrayList;
    private ArrayList<FestivalModel> festivalModelArrayList;

    private ViewPagerAdapter chatMessageViewPager;          //view Pager가 있는 대화인 경우..
    private ViewPager resultViewPager;
    private ViewPager showViewPager = null;


    public ChatMessage(boolean isMe, String message, String dateTime, boolean isSpeech) {
        this.isMe = isMe;
        this.message = message;
        this.dateTime = dateTime;
        this.isSpeech = isSpeech;
        this.isViewPager = false;
        this.chatMessageViewPager = null;
        this.messageActionType = -1;
        this.messageUiType = 0;
    }

    public ChatMessage()
    {
        this.message = "";
        this.dateTime = "";
        this.isSpeech = true;
        this.chatMessageViewPager = null;
        this.messageActionType = -1;
        this.messageUiType = 0;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public boolean getIsme() {
        return isMe;
    }
    public void setMe(boolean isMe) {
        this.isMe = isMe;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDate() {
        return dateTime;
    }

    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }


    public boolean isViewPager()
    {
        return isViewPager;
    }
    public void setViewPager(boolean isViewPager)
    {
        this.isViewPager = isViewPager;
    }

    public boolean isSpeech() {
        return isSpeech;
    }

    public void setSpeech(boolean speech) {
        isSpeech = speech;
    }

    public ViewPagerAdapter getChatMessageViewPager() {
        return chatMessageViewPager;
    }

    public void setChatMessageViewPager(ViewPagerAdapter chatMessageViewPager) {
        this.chatMessageViewPager = chatMessageViewPager;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "isMe=" + isMe +
                ", message='" + message + '\'' +
                ", viewpager list =" + placeModelArrayList +
                '}';
    }

    public void setActionType(int responseActionType) {
        this.messageActionType = responseActionType;
    }

    public int getActionType()
    {
        return this.messageActionType;
    }

    //봇 Action에 따른 버튼 종류를 가져온다.
    public int getButtonType() {
        return messageActionType;
    }

    public int getMeesageIntentType() {
        return meesageIntentType;
    }

    public void setMeesageIntentType(int meesageIntentType) {
        this.meesageIntentType = meesageIntentType;
    }

    public int getMessageUiType() {
        return messageUiType;
    }

    public void setMessageUiType(int messageUiType) {
        this.messageUiType = messageUiType;
    }

    public ArrayList<PlaceModel> getPlaceModelArrayList() {
        return placeModelArrayList;
    }

    public void setPlaceModelArrayList(ArrayList<PlaceModel> placeModelArrayList) {
        this.PLACE_TYPE = CHAT_MESSAGE_PLACE;
        this.placeModelArrayList = placeModelArrayList;
    }

    public ArrayList<FestivalModel> getFestivalModelArrayList() {
        return festivalModelArrayList;
    }

    public void setFestivalModelArrayList(ArrayList<FestivalModel> festivalModelArrayList) {
        this.PLACE_TYPE = CHAT_MESSAGE_FESTIVAL;
        this.festivalModelArrayList = festivalModelArrayList;
    }

    public int getPLACE_TYPE() {
        return PLACE_TYPE;
    }

    public void setPLACE_TYPE(int PLACE_TYPE) {
        this.PLACE_TYPE = PLACE_TYPE;
    }

    public void setResultViewPager(ViewPager resultViewPager) {
        this.resultViewPager = resultViewPager;
    }

    public void setChatMessageShowViewPager(ViewPager viewPager) {
        this.showViewPager = viewPager;
    }

    public ViewPager getChatMessageShowViewPager() {
        return this.showViewPager;
    }
}

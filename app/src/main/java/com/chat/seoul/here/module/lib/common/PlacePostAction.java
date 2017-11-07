package com.chat.seoul.here.module.lib.common;

import android.content.Context;

import static com.chat.seoul.here.module.lib.conf.Common.ACTION_DEST_PLACE_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_DEST_PLACE_SEARCH_NO_ENTITY;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_DETAIL_RECOMMEND_PLACE_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_FESTIVAL_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_INPUT_LATLON;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_PATH_SEARCH_NO_ENTITY;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_POSITIVE_ANSWER;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_RECOMMEND_PLACE_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_UNKNOWN;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_END;
import static com.chat.seoul.here.module.lib.conf.Common.MSG_END;
import static com.chat.seoul.here.module.lib.conf.Common.MSG_RESPONSE_RECOMMEND_PLACE;
import static com.chat.seoul.here.module.lib.conf.Common.MSG_RESPONSE_SEARCH_FESTIVAL;
import static com.chat.seoul.here.module.lib.conf.Common.UI_TEXT_BUTTON;

/**
 * Created by JJW on 2017-09-25.
 * API.AI로 부터 메시지를 받은 후, Place 또는 Festival Action 또는 Intent에 따라서
 * UI 작업 또는 추가 메세지 전송을 위한 클래스
 */

public class PlacePostAction {

    private Context ctx;
    private PlacePostActionListener listener;

    public PlacePostAction(Context ctx, PlacePostActionListener listener)
    {
        this.ctx = ctx;
        this.listener = listener;
    }


    /**
     *  API.AI 로 부터 받은 메시지 이후 다음 추가로 Action (메시지 전송 등) 해야 하는 경우 처리 한다.
     * @param responseActionType
     */
    public void generatePostAction(int responseActionType, int intentType)
    {
        //Action 타입을 보고 디바이스에서 후속 처리 해야 하는 것이 있는지 판단한다.
        //예를들어, 현재 위치 검색에서는 GPS를 통해 내부적으로 GPS 정보를 보내야 한다.
        if(responseActionType == ACTION_INPUT_LATLON){
            //GPS로부터 현재 위치를 가져온다.
            listener.onDisplayMessage(MSG_RESPONSE_SEARCH_FESTIVAL, false, UI_TEXT_BUTTON, intentType, responseActionType);
        }else if(responseActionType == ACTION_DEST_PLACE_SEARCH_NO_ENTITY)
        {
             listener.onDisplayMessage(MSG_RESPONSE_SEARCH_FESTIVAL, false, UI_TEXT_BUTTON, intentType, responseActionType);
        }else if(responseActionType == ACTION_POSITIVE_ANSWER)
        {
            //대화 종료.......
            intentType = INTENT_END;
            listener.onDisplayMessage(MSG_END, false, UI_TEXT_BUTTON, intentType, responseActionType);
        }
        /*else if(responseActionType == ACTION_NEGATIVE_ANSWER)
        {
            //대화 종료..
            intentType = INTENT_END;
            listener.onDisplayMessage(MSG_END, false, UI_TEXT_BUTTON, intentType, responseActionType);
        }*/
        // 추천 장소 검색 이 후 Action
        else if(responseActionType == ACTION_RECOMMEND_PLACE_SEARCH || responseActionType == ACTION_DETAIL_RECOMMEND_PLACE_SEARCH)
        {
            //대화 종료..
            intentType = INTENT_END;
            listener.onDisplayMessage(MSG_RESPONSE_RECOMMEND_PLACE, false, UI_TEXT_BUTTON, intentType, responseActionType);
        }

        /**
         *  행사 검색 PostAction 제거 한다.
         */
        else if(responseActionType == ACTION_FESTIVAL_SEARCH)
        {
            //Festival 요청을 한다.
            String url = "https://seoulherechat.herokuapp.com/request/festival";
            //행사 검색 이후 메인 메뉴로 이동 유도..
            intentType = INTENT_END;
            listener.onDisplayMessage(MSG_END, false, UI_TEXT_BUTTON, intentType, responseActionType);
        }
        /**
         *  문장에 목적지가 들어 있는 경우 해당 POST ACTION을 수행한다..
         */
        else if(responseActionType == ACTION_DEST_PLACE_SEARCH)
        {
            //Festival 요청을 한다.
            String url = "https://seoulherechat.herokuapp.com/request/festival";
        }

        else if(responseActionType == ACTION_UNKNOWN)
        {

        }
        else if(responseActionType == ACTION_PATH_SEARCH_NO_ENTITY)
        {

            listener.onRequestPathPlaceSearch();
        }
    }

}

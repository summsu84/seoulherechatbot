package com.chat.seoul.here.module.lib.conf;

/**
 * Created by JJW on 2017-08-08.
 */

public class Common {

    //API.AI Action
    public static final int ACTION_NO_TYPE = -1;
    public static final int ACTION_RECOMMEND_PLACE_SEARCH = 0;
    public static final int ACTION_CURRENT_PLACE_SEARCH = 1;
    public static final int ACTION_CURRENT_PLACE_SEARCH_INCOMPLETE = 12;
    public static final int ACTION_DEST_PLACE_SEARCH = 2;
    public static final int ACTION_DEST_PLACE_SEARCH_NO_ENTITY = 3;
    public static final int ACTION_DEST_PLACE_SEARCH_NO_ENTITY_INCOMPLETE = 4;
    //Path 검색
    public static final int ACTION_PATH_SEARCH = 5;
    public static final int ACTION_PATH_SEARCH_NO_ENTITY = 30;
    public static final int ACTION_PATH_SEARCH_NO_ENTITY_INCOMPLETE = 31;           //대화형 경로 검색관련해서 대화가 모두 완료되지 않은 변수
    public static final int ACTION_HELP = 6;
    public static final int ACTION_HELLO = 7;
    public static final int ACTION_UNKNOWN = 8;
    public static final int ACTION_INPUT_LATLON = 13;           //현재 위치 검색 이 후 에서 사용됨

    //Festival Action
    public static final int ACTION_FESTIVAL_SEARCH = 9;
    public static final int ACTION_FESTIVAL_SEARCH_INCOMPLETE = 14;

    public static final int ACTION_POSITIVE_ANSWER = 10;
    public static final int ACTION_NEGATIVE_ANSWER = 11;

    public static final int ACTION_DETAIL_RECOMMEND_PLACE_SEARCH_INCOMPLETE = 40;
    public static final int ACTION_DETAIL_RECOMMEND_PLACE_SEARCH = 45;


    //Intent
    public static final int INTENT_NO_TYPE = -1;
    public static final int INTENT_RECOMMEND_PLACE_SEARCH = 0;
    public static final int INTENT_CURRENT_PLACE_SEARCH = 1;
    public static final int INTENT_DEST_PLACE_SEARCH = 2;
    public static final int INTENT_DEST_PLACE_SEARCH_NO_ENTITY = 3;
    public static final int INTENT_DEST_PLACE_SEARCH_NO_ENTITY_INCOMPLETE = 4;
    public static final int INTENT_PATH_PLACE_SEARCH = 30;
    public static final int INTENT_PATH_PLACE_SEARCH_NO_ENTITY = 31;
    public static final int INTENT_PATH_SEARCH = 5;
    public static final int INTENT_HELP = 6;
    public static final int INTENT_HELLO = 7;
    public static final int INTENT_UNKNOWN = 8;
    public static final int INTENT_POSITIVE_ANSWER = 9;
    public static final int INTENT_NEGATIVE_ANSWER = 10;
    public static final int INTENT_NEGATIVE_ANSWER_WITHOUT_CONTEXT = 100;


    //Festival Action
    public static final int INTENT_FESTIVAL_SEARCH = 9;
    public static final int INTENT_WEB_REQ_FESTIVAL_SEARCH = 10;           //Festval 검색을 요구 하는 의도 (WEB)
    public static final int INTENT_WEB_CURRENT_PLACE_SEARCH = 12;           //Web의 결과 메시지를 생성하기 위한 Intent 종류
    public static final int INTENT_ASK_PROFILE = 11;
    public static final int INTENT_END = 20;
    public static final int INTENT_HELLO_PLACE_SUBMENU = 30;

    public static final int INTENT_DETAIL_RECOMMEND_PLACE_SEARCH = 100;

    //UI Type
    public static final int UI_TEXT_ONLY = 0;
    public static final int UI_TEXT_CARD = 1;
    public static final int UI_CARD_ONLY= 2;
    public static final int UI_TEXT_BUTTON = 3;
    public static final int UI_TEXT_CARD_BUTTON = 4;
    public static final int UI_IMAGE = 5;
    public static final int UI_TEXT_IMAGE = 6;
    public static final int UI_IMAGE_BUTTON = 10;
    public static final int UI_TEXT_BUTTON_IMAGE =7;
    public static final int UI_TEXT_CARD_IMAGE = 8;
    public static final int UI_TEXT_CARD_BUTTON_IMAGE = 9;

    //HTTP REQUEDST ACTIOn

    public static final int HTTP_ACTION_PLACE_DET = 0;




    //DEVICE RequestType


    public static final int PAGE_FULLY_NOT_FOUND = -1;
    public static final int PAGE_NOT_FOUND = 0;
    public static final int PAGE_FOUND = 1;
    public static final int LIST_VIEW_COUNT = 3;

    //URI
    public static final String URI_REQUEST_PLACE_CURRENT = "";
    public static final String URI_REQUEST_PLACE_REFRESH = "";
    public static final String URI_REQUEST_PLACE_RECOMMEND = "";
    public static final String URI_REQUEST_PLACE_DEST = "https://seoulherechat.herokuapp.com/request/place/dest";
    public static final String URI_REQUEST_PLACE_PATH = "";

    //Sigin Activity
    public static final int GPS_RESULT_SUCESS = 10000;
    public static final int GPS_RESULT_FAILED = 10001;

    //Bot Message
    public static final String MSG_RESPONSE_HELLO = "안녕하세요, 서울?여긴? 챗봇입니다. 무엇을 도와 드릴까요?";
    public static final String MSG_RESPONSE_PLACE_SUBMENU = "아래 버튼을 클릭 하시거나, 명소 검색관련하여 질문을 해보세요..";
    public static final String MSG_RESPONSE_SEARCHING = "잠시만 기다려 주세요. 검색 중입니다.";
    public static final String MSG_RESPONSE_SEARCHING_RESULT = "요청하신 내용을 검색 하였습니다.";
    public static final String MSG_RESPONSE_CURRENT_PLACE = "현재 위치 주변 명소를 찾았습니다. 자세한 정보를 보실려면 '자세히 보기', 전체 보시려면 '전체 보기'를 눌러주세요.";
    public static final String MSG_RESPONSE_DEST_PLACE = "주변 명소를 찾았습니다. 자세한 정보를 보실려면 '자세히 보기', 전체 보시려면 '전체 보기'를 눌러주세요.";
    public static final String MSG_END = "서울?여긴? 챗봇 검색 결과가 만족 스럽습니까? 메뉴로 가시려면 메뉴를 입력하시거나 버튼을 눌러 주세요";
    public static final String MSG_RESPONSE_RECOMMEND_PLACE = "추천 명소가 만족 스럽습니까? 메뉴로 가시려면 메뉴를 입력하시거나 버튼을 눌러 주세요 (예측모델을 통한 상세추천검색을 하시고자하면 상세추천검색 버튼을 눌러주세요)";
    public static final String MSG_RESPONSE_SEARCH_FESTIVAL = "검색 하신 주변에 문화 행사를 검색 해보시겠습니까?";
    public static final String MSG_RESPONSE_LOCATION_NO_SEOUL = "검색하신 장소가 올바르지 않거나, 서울 지역이 아닙니다. 서울 지역을 입력하시거나, 아래 검색하기 버튼을 클릭 해주세요";
    public static final String MSG_RESPONSE_LOCATION_NO_USE = "현재 위치를 사용하셔야 합니다. 메뉴로 이동합니다.";

    //Button Message
    //public static final String[] MSG_BUTTON_HELLO = {"추천명소", "주변 명소", "목적지 주변 명소", "행사 정보"};           //Intro
    public static final String[] MSG_BUTTON_HELLO = {"명소 검색하기", "행사 검색하기", "도움말", "ABOUT"};           //Intro
    public static final String[] MSG_BUTTON_HELP = {"추천명소", "주변 명소", "목적지 주변 명소", "행사 정보"};           //Intro
    public static final String[] MSG_BUTTON_PLACE_SUBMENU = {"추천명소", "주변명소", "목적지 주변 명소", "경로 주변 명소"};

    public static final String[] MSG_BUTTON_PLACE_SEARCH_RESULT = {"네", "아니요"};
    public static final String[] MSG_BUTTON_DESTINATION_SEARCH = {"검색하기"};
    public static final String[] MSG_BUTTON_CURRENT_LOCATION_USE = {"사용하기", "사용하지 않기"};
    public static final String[] MSG_UNKNOWN = {"의도 요청하기"};
    public static final String[] MSG_BUTTON_MENU = {"메뉴"};
    public static final String[] MSG_BUTTON_MENU_DETAIL_SEARCH = {"메뉴", "상세추천장소검색"};

    //PLACE TYPE
    public static int CHAT_MESSAGE_PLACE = 0;
    public static int CHAT_MESSAGE_FESTIVAL = 1;
}

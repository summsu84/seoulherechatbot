package com.chat.seoul.here.module.lib.common;

import com.chat.seoul.here.module.model.ChatMessage;
import com.chat.seoul.here.module.model.festival.FestivalModel;
import com.chat.seoul.here.module.model.place.PlaceCoordModel;
import com.chat.seoul.here.module.model.place.PlaceImageModel;
import com.chat.seoul.here.module.model.place.PlaceModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ai.api.model.Metadata;
import ai.api.model.Result;

import static com.chat.seoul.here.module.lib.conf.Common.ACTION_CURRENT_PLACE_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_CURRENT_PLACE_SEARCH_INCOMPLETE;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_DEST_PLACE_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_DEST_PLACE_SEARCH_NO_ENTITY;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_DEST_PLACE_SEARCH_NO_ENTITY_INCOMPLETE;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_DETAIL_RECOMMEND_PLACE_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_DETAIL_RECOMMEND_PLACE_SEARCH_INCOMPLETE;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_FESTIVAL_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_FESTIVAL_SEARCH_INCOMPLETE;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_HELLO;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_HELP;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_INPUT_LATLON;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_NEGATIVE_ANSWER;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_NO_TYPE;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_PATH_SEARCH_NO_ENTITY;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_PATH_SEARCH_NO_ENTITY_INCOMPLETE;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_POSITIVE_ANSWER;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_RECOMMEND_PLACE_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_UNKNOWN;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_ASK_PROFILE;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_CURRENT_PLACE_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_DEST_PLACE_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_DEST_PLACE_SEARCH_NO_ENTITY;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_DETAIL_RECOMMEND_PLACE_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_FESTIVAL_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_HELLO;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_HELP;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_NEGATIVE_ANSWER;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_NEGATIVE_ANSWER_WITHOUT_CONTEXT;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_PATH_PLACE_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_PATH_PLACE_SEARCH_NO_ENTITY;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_POSITIVE_ANSWER;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_RECOMMEND_PLACE_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_UNKNOWN;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_WEB_CURRENT_PLACE_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.UI_TEXT_BUTTON;
import static com.chat.seoul.here.module.lib.conf.Common.UI_TEXT_BUTTON_IMAGE;
import static com.chat.seoul.here.module.lib.conf.Common.UI_TEXT_CARD;
import static com.chat.seoul.here.module.lib.conf.Common.UI_TEXT_ONLY;

/**
 * Created by JJW on 2017-09-15.
 * Place 정보를 파싱한다.
 */

public class PlaceParser {
    private volatile static PlaceParser uniqueInstance;

    private PlaceParser() {
    }


    public static PlaceParser getInstance() {

        if (uniqueInstance == null) {

            synchronized (PlaceParser.class) {

                if (uniqueInstance == null) {

                    uniqueInstance = new PlaceParser();

                }

            }

        }
        return uniqueInstance;
    }

    public ChatMessage parseFromWebAPI(String response) {


        boolean isPlaceList = true;         //true : placeList, false : festivalList
        //data가 있는 경우..
        boolean isPlaceListExist = true;

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            JSONObject data = jsonObject.getJSONObject("data");

            if(data.has("placeList"))
            {
                System.out.println(">>Place is exsist");
                if (data.getJSONArray("placeList").length() > 0) {
                    return parsePlaceListFromWebAPI(data);
                }
            }else
            {
                System.out.println(">>Place is not exsist");

            }

            if(data.has("festivalList"))
            {
                System.out.println(">>festivalList is exsist");
                if (data.getJSONArray("festivalList").length() > 0) {
                    return parseFestivalListFroMWebAPI(data);
                }
            }else
            {
                System.out.println(">>festivalList is not exsist");

            }
/*
            if (data.get("placeList") != null) {
                System.out.println(">>>>PlaceList is not null!!!");

            } else if (data.get("festivalList") != null) {
                return parseFestivalListFroMWebAPI(data);
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(122);//dummy
        chatMessage.setMessage("데이터가 존재 하지 않습니다.");
        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatMessage.setMe(false);

        return chatMessage;
    }
/*
        chatMessage.setMessageUiType(messageUiType);
        chatMessage.setActionType(responseActionType);
        chatMessage.setMeesageIntentType(responseIntentType);*/


    public ChatMessage parsePlaceListFromWebAPI(JSONObject data )
    {
        ArrayList<PlaceModel> placeModelArrayList = new ArrayList<PlaceModel>();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(122);//dummy
        chatMessage.setMessage("아래와 같이 검색 하였습니다.");
        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatMessage.setMe(false);


        int responseActionType = 0;
        int messageUiType = 0;
        int responseIntentType = 0;
        boolean isPlaceListExist = false;
        try {
            JSONArray placeList = data.getJSONArray("placeList");
            JSONObject returnAction = null;
            JSONObject returnIntent = null;
            JSONObject placeSource = null;
            String sourceX_Coord = null;
            String sourceY_Coord = null;
            double sourceAround = 0;

            //Action 처리
            if (data.has("returnActionValue")) {
                returnAction = data.getJSONObject("returnActionValue");
                String responseAction = returnAction.get("action").toString();
                ResponseActionClass responseActionClass = getResponseActionType(responseAction, false);

                responseActionType = responseActionClass.responseActionType;
                messageUiType = responseActionClass.messaegUiType;

            } else {
                responseActionType = ACTION_NO_TYPE;
            }

            if (data.has("returnIntentValue")) {
                returnIntent = data.getJSONObject("returnIntentValue");
                String responseIntent = returnIntent.get("intent").toString();
                responseIntentType = getResponseIntentType(responseIntent);
            } else {
                responseIntentType = 0;
            }


            //PlaceSource 처리리
            if (data.has("placeSource")) {
               placeSource = data.getJSONObject("placeSource");

                if (placeSource.has("x_coord")) {
                    sourceX_Coord = placeSource.get("x_coord").toString().replaceAll("\"", "");
                }
                if (placeSource.has("y_coord")) {
                    sourceY_Coord = placeSource.get("y_coord").toString().replaceAll("\"", "");
                }
                if (placeSource.has("round")) {
                    sourceAround = Double.parseDouble(placeSource.get("round").toString());
                }
            }



            if (placeList.length() > 0) {
                isPlaceListExist = true;
                for (int i = 0; i < placeList.length(); i++) {
                    JSONObject placeItem = placeList.getJSONObject(i);

                    String placeId = null;
                    String code = null;
                    String placeName = null;
                    String placeDesc = null;
                    String seatCnt = null;
                    String phone = null;
                    String openHour = null;
                    String closeDay = null;
                    String openDay = null;
                    String homepage = null;
                    String addr = null;
                    String entrFee = null;
                    int viewcnt = 0;
                    int like = 0;


                    if (placeItem.has("PLACE_ID")) {
                        placeId = placeItem.get("PLACE_ID").toString().replaceAll("\"", "");
                    }

                    if (placeItem.has("CODE")) {
                        code = placeItem.get("CODE").toString().replaceAll("\"", "");
                    }


                    if (placeItem.has("PLACE_NAME")) {
                        placeName = placeItem.get("PLACE_NAME").toString();
                    }
                    if (placeItem.has("PLACE_DESC")) {
                        placeDesc = placeItem.get("PLACE_DESC").toString();
                    }
                    if (placeItem.has("SEAT_CNT")) {
                        seatCnt = placeItem.get("SEAT_CNT").toString();
                    }
                    if (placeItem.has("PHONE")) {
                        phone = placeItem.get("PHONE").toString();
                    }
                    if (placeItem.has("OPEN_HOUR")) {
                        openHour = placeItem.get("OPEN_HOUR").toString();
                    }
                    if (placeItem.has("CLOSE_DAY")) {
                        closeDay = placeItem.get("CLOSE_DAY").toString();
                    }
                    if (placeItem.has("OPEN_DAY")) {
                        openDay = placeItem.get("OPEN_DAY").toString();
                    }
                    if (placeItem.has("HOMEPAGE")) {
                        homepage = placeItem.get("HOMEPAGE").toString();
                    }
                    if (placeItem.has("ADDR")) {
                        addr = placeItem.get("ADDR").toString();
                    }
                    if (placeItem.has("ENTR_FEE")) {
                        entrFee = placeItem.get("ENTR_FEE").toString();
                    }

                    //view count
                    if (placeItem.has("VIEW_CNT")) {
                        viewcnt = (int) placeItem.get("VIEW_CNT");
                    }
                    //like
                    if (placeItem.has("LIKE")) {
                        like = (int) placeItem.get("LIKE");
                    }

                    int commentCnt = 0;
                    //Comment Cnt
                    if (placeItem.has("COMMENT_COUNT")) {
                        commentCnt = (int) placeItem.get("COMMENT_COUNT");
                    }

                    PlaceModel info = new PlaceModel(placeId, code, placeName, placeDesc, seatCnt, phone, openHour, closeDay, openDay, homepage, addr, entrFee, viewcnt, like, commentCnt, sourceX_Coord, sourceY_Coord, sourceAround);

                    if (placeItem.has("MAIN_IMG")) {
                        PlaceImageModel placeImageModel = new PlaceImageModel();
                        String placeMainImg = null;
                        placeMainImg = placeItem.get("MAIN_IMG").toString();
                        placeImageModel.setMAIN_IMG(placeMainImg);
                        //imageModelArrayList.add(placeImageModel);
                        info.getPLACE_IMAGE_LIST().add(placeImageModel);

                    }

                    PlaceCoordModel coordModel = new PlaceCoordModel();
                    double x_coord = Double.parseDouble(placeItem.get("X_COORD").toString());
                    double y_coord = Double.parseDouble(placeItem.get("Y_COORD").toString());

                    coordModel.setX_COORD(x_coord);
                    coordModel.setY_COORD(y_coord);
                    info.setPLACE_COORD(coordModel);
                    //Action을 검출 한다.
                    //info.setResponseAction(responseActionType);
                    placeModelArrayList.add(info);
                }
            } else {
                isPlaceListExist = false;
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

        if (isPlaceListExist == true) {
            chatMessage.setPlaceModelArrayList(placeModelArrayList);
            chatMessage.setViewPager(true);
        } else {
            chatMessage.setViewPager(false);
            chatMessage.setMessage("요청하신 위치 주변에 명소가 존재 하지 않습니다.");
        }

        return chatMessage;
    }

    public ChatMessage parseFestivalListFroMWebAPI(JSONObject data)
    {
        ArrayList<FestivalModel> festivalModelArrayList = new ArrayList<FestivalModel>();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(122);//dummy
        chatMessage.setMessage("아래와 같이 검색 하였습니다.");
        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatMessage.setMe(false);


        int responseActionType = 0;
        int messageUiType = 0;
        int responseIntentType = 0;
        boolean isFestivalExist = false;
        try {
            JSONArray placeList = data.getJSONArray("festivalList");
            JSONObject returnAction = null;
            JSONObject returnIntent = null;
            JSONObject placeSource = null;
            String sourceX_Coord = null;
            String sourceY_Coord = null;
            double sourceAround = 0;

            //Action 처리
            if (data.has("returnActionValue")) {
                returnAction = data.getJSONObject("returnActionValue");
                String responseAction = returnAction.get("action").toString();
                ResponseActionClass responseActionClass = getResponseActionType(responseAction, false);

                responseActionType = responseActionClass.responseActionType;
                messageUiType = responseActionClass.messaegUiType;

            } else {
                responseActionType = ACTION_NO_TYPE;
            }

            if (data.has("returnIntentValue")) {
                returnIntent = data.getJSONObject("returnIntentValue");
                String responseIntent = returnIntent.get("intent").toString();
                responseIntentType = getResponseIntentType(responseIntent);
            } else {
                responseIntentType = 0;
            }


            //PlaceSource 처리리
            if (data.has("placeSource")) {
                placeSource = data.getJSONObject("placeSource");

                if (placeSource.has("x_coord")) {
                    sourceX_Coord = placeSource.get("x_coord").toString().replaceAll("\"", "");
                }
                if (placeSource.has("y_coord")) {
                    sourceY_Coord = placeSource.get("y_coord").toString().replaceAll("\"", "");
                }
                if (placeSource.has("round")) {
                    sourceAround = Double.parseDouble(placeSource.get("round").toString());
                }
            }



            if (placeList.length() > 0) {
                isFestivalExist = true;
                for (int i = 0; i < placeList.length(); i++) {
                    JSONObject festivalItem = placeList.getJSONObject(i);



                    String festivalId = null;
                    String festivalName = null;
                    String festivalClass = null;
                    String festivalTarget = null;
                    String festivalPlace = null;
                    String festivalStartDate = null;
                    String festivalEndDate = null;
                    String festivalPayment = null;
                    String festivalXCoord = null;
                    String festivalYCoord = null;
                    String festivalArea = null;
                    String festivalLink = null;

                    //나중에 3항연산자로 변경
                    if (festivalItem.has("FESTIVAL_ID")) {
                        festivalId = festivalItem.get("FESTIVAL_ID").toString();
                    }
                    if (festivalItem.has("FESTIVAL_NAME")) {
                        festivalName = festivalItem.get("FESTIVAL_NAME").toString();
                    }
                    if (festivalItem.has("FESTIVAL_CLASS")) {
                        festivalClass = festivalItem.get("FESTIVAL_CLASS").toString();
                    }
                    if (festivalItem.has("FESTIVAL_TARGET")) {
                        festivalTarget = festivalItem.get("FESTIVAL_TARGET").toString();
                    }
                    if (festivalItem.has("FESTIVAL_PLACE")) {
                        festivalPlace = festivalItem.get("FESTIVAL_PLACE").toString();
                    }
                    if (festivalItem.has("FESTIVAL_START_DATE")) {
                        festivalStartDate = festivalItem.get("FESTIVAL_START_DATE").toString();
                    }
                    if (festivalItem.has("FESTIVAL_END_DATE")) {
                        festivalEndDate = festivalItem.get("FESTIVAL_END_DATE").toString();
                    }
                    if (festivalItem.has("FESTIVAL_PAYMENT")) {
                        festivalPayment = festivalItem.get("FESTIVAL_PAYMENT").toString();
                    }
                    if (festivalItem.has("FESTIVAL_X_COORD")) {
                        festivalXCoord = festivalItem.get("FESTIVAL_X_COORD").toString();
                    }
                    if (festivalItem.has("FESTIVAL_Y_COORD")) {
                        festivalYCoord = festivalItem.get("FESTIVAL_Y_COORD").toString();
                    }
                    if (festivalItem.has("FESTIVAL_AREA")) {
                        festivalArea = festivalItem.get("FESTIVAL_AREA").toString();
                    }
                    if (festivalItem.has("FESTIVAL_LINK")) {
                        festivalLink = festivalItem.get("FESTIVAL_LINK").toString();
                    }

                    FestivalModel info = new FestivalModel(festivalId, festivalName, festivalClass, festivalTarget, festivalPlace, festivalStartDate, festivalEndDate, festivalPayment, festivalXCoord, festivalYCoord, festivalArea, festivalLink, sourceX_Coord, sourceY_Coord, sourceAround);

                    festivalModelArrayList.add(info);

                }
            } else {
                isFestivalExist = false;
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }


        if (isFestivalExist == true) {
            chatMessage.setFestivalModelArrayList(festivalModelArrayList);
            chatMessage.setViewPager(true);
        } else {
            chatMessage.setViewPager(false);
            chatMessage.setMessage("요청하신 위치 주변에 명소가 존재 하지 않습니다.");
        }

        return chatMessage;
    }



    public int parseFromAPIAI(Map<String, JsonElement> tmp, String intentName, String iResponseAction, boolean isActionInCompleted, ChatMessage chatMessage, HashMap<String, JsonElement> params) {

        String responseAction = iResponseAction;
        ResponseActionClass responseActionClass = getResponseActionType(responseAction, isActionInCompleted);
        int responseActionType = responseActionClass.responseActionType;
        int messageUiType = responseActionClass.messaegUiType;
        int responseIntentType = getResponseIntentType(intentName);

        if (tmp != null && tmp.size() > 0) {
            //data가 있는 경우..
            if (tmp.get("placeList") != null) {
                System.out.println(">>>>PlaceList is not null!!!");
                if (tmp.get("placeList").isJsonArray()) {
                    return parsePlaceListFromAPIAI(tmp, responseActionType, messageUiType, responseIntentType, chatMessage);
                }
            } else if (tmp.get("festivalList") != null) {
                System.out.println(">>>>festivalList is not null!!!");
                if (tmp.get("festivalList").isJsonArray()) {
                    return parseFestivalListFromAPIAI(tmp, responseActionType, messageUiType, responseIntentType, chatMessage);
                }

            }
        }
        chatMessage.setMessageUiType(messageUiType);
        chatMessage.setActionType(responseActionType);
        chatMessage.setMeesageIntentType(responseIntentType);


        return responseActionType;
    }



    /**
     * API.AI 로부터 명소 정보를 파싱 한다.
     *
     * */

    public int parsePlaceListFromAPIAI(Map<String, JsonElement> tmp, int responseActionType, int messageUiType, int responseIntentType, ChatMessage chatMessage) {
        ArrayList<PlaceModel> placeModelArrayList = null;
        boolean isPlaceListExist = false;
        //API.AI Action 부터 파악

        System.out.println(">>[PlaeParser] chatMessage : " + chatMessage.getMessage() + ", action : " + responseActionType + ", ui type : " + messageUiType + ", intent : " + responseIntentType);
        //PlaceList parsing
        if (responseActionType == ACTION_RECOMMEND_PLACE_SEARCH || responseActionType == ACTION_DEST_PLACE_SEARCH_NO_ENTITY || responseActionType == ACTION_INPUT_LATLON || responseActionType == ACTION_DETAIL_RECOMMEND_PLACE_SEARCH) {

            if (tmp != null && tmp.size() > 0) {
                //data가 있는 경우..
                placeModelArrayList = new ArrayList<PlaceModel>();
                JsonArray jsonArray = tmp.get("placeList").getAsJsonArray();

                String sourceX_Coord = null;
                String sourceY_Coord = null;
                double sourceAround = 0;

                //PlaceSource 체크 하기
                if(tmp.get("placeSource") != null) {
                    JsonObject placeSource = tmp.get("placeSource").getAsJsonObject();

                    if (placeSource.has("x_coord")) {
                        sourceX_Coord = placeSource.get("x_coord").toString().replaceAll("\"", "");
                    }
                    if (placeSource.has("y_coord")) {
                        sourceY_Coord = placeSource.get("y_coord").toString().replaceAll("\"", "");
                    }
                    if (placeSource.has("round")) {
                        sourceAround = Double.parseDouble(placeSource.get("round").toString());
                    }
                }

                //placeList가 존재하는지 확인한다.
                if (jsonArray.size() > 0) {

                    isPlaceListExist = true;
                    for (JsonElement jo : jsonArray) {
                        JsonObject placeItem = jo.getAsJsonObject();
                        String placeId = null;
                        String placeName = null;
                        String placeDesc = null;
                        String seatCnt = null;
                        String phone = null;
                        String openHour = null;
                        String closeDay = null;
                        String openDay = null;
                        String homepage = null;
                        String addr = null;
                        String entrFee = null;
                        String code = null;
                        int viewcnt = 0;
                        int like = 0;

                        if (placeItem.has("PLACE_ID")) {
                            placeId = placeItem.get("PLACE_ID").getAsString();
                        }
                        if (placeItem.has("CODE")) {
                            code = placeItem.get("CODE").getAsString();
                        }
                        if (placeItem.has("PLACE_NAME")) {
                            placeName = placeItem.get("PLACE_NAME").getAsString();
                        }
                        if (placeItem.has("PLACE_DESC")) {
                            placeDesc = placeItem.get("PLACE_DESC").getAsString();
                        }
                        if (placeItem.has("SEAT_CNT")) {
                            seatCnt = placeItem.get("SEAT_CNT").getAsString();
                        }
                        if (placeItem.has("PHONE")) {
                            phone = placeItem.get("PHONE").getAsString();
                        }
                        if (placeItem.has("OPEN_HOUR")) {
                            openHour = placeItem.get("OPEN_HOUR").getAsString();
                        }
                        if (placeItem.has("CLOSE_DAY")) {
                            closeDay = placeItem.get("CLOSE_DAY").getAsString();
                        }
                        if (placeItem.has("OPEN_DAY")) {
                            openDay = placeItem.get("OPEN_DAY").getAsString();
                        }
                        if (placeItem.has("HOMEPAGE")) {
                            homepage = placeItem.get("HOMEPAGE").getAsString();
                        }
                        if (placeItem.has("ADDR")) {
                            addr = placeItem.get("ADDR").getAsString();
                        }
                        if (placeItem.has("ENTR_FEE")) {
                            entrFee = placeItem.get("ENTR_FEE").getAsString();
                        }
                        if (placeItem.has("VIEW_CNT")) {
                            viewcnt = placeItem.get("VIEW_CNT").getAsInt();
                        }
                        if (placeItem.has("LIKE")) {
                            like = placeItem.get("LIKE").getAsInt();
                        }


                        int commentCnt = 0;
                        //Comment Cnt
                        if (placeItem.has("COMMENT_COUNT")) {
                            commentCnt = placeItem.get("COMMENT_COUNT").getAsInt();
                        }

                        PlaceModel info = new PlaceModel(placeId, code, placeName, placeDesc, seatCnt, phone, openHour, closeDay, openDay, homepage, addr, entrFee, viewcnt, like, commentCnt, sourceX_Coord, sourceY_Coord, sourceAround);

                        if (placeItem.has("MAIN_IMG")) {
                            PlaceImageModel placeImageModel = new PlaceImageModel();
                            String placeMainImg = null;
                            placeMainImg = placeItem.get("MAIN_IMG").getAsString();
                            placeImageModel.setMAIN_IMG(placeMainImg);
                            //imageModelArrayList.add(placeImageModel);
                            info.getPLACE_IMAGE_LIST().add(placeImageModel);

                        }

                        PlaceCoordModel coordModel = new PlaceCoordModel();
                        double x_coord = Double.parseDouble(placeItem.get("X_COORD").toString());
                        double y_coord = Double.parseDouble(placeItem.get("Y_COORD").toString());

                        coordModel.setX_COORD(x_coord);
                        coordModel.setY_COORD(y_coord);
                        info.setPLACE_COORD(coordModel);

                        info.setResponseAction(responseActionType);
                        placeModelArrayList.add(info);

                    }
                }
            }

            //Place가 존재 하는 경우..
            if (isPlaceListExist == true) {
                chatMessage.setPlaceModelArrayList(placeModelArrayList);
                chatMessage.setViewPager(true);
            } else {
                chatMessage.setViewPager(false);
                chatMessage.setMessage("요청하신 위치 주변에 명소가 존재 하지 않습니다.");
            }
        }
        chatMessage.setMessageUiType(messageUiType);
        chatMessage.setActionType(responseActionType);
        chatMessage.setMeesageIntentType(responseIntentType);               //intentType 설정

        return responseActionType;
    }

    //Festival Parsing
    public ChatMessage parseFestivalData(JSONObject data) {
        JSONArray festivalList = null;
        boolean isFestivalExist = true;
        JSONObject returnAction = null;
        JSONObject returnIntent = null;
        JSONObject placeSource = null;
        String sourceX_Coord = null;
        String sourceY_Coord = null;
        double sourceAround = 0;

        ArrayList<FestivalModel> festivalModelArrayList = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(122);//dummy
        chatMessage.setMessage("아래와 같이 문화 행사를 검색 하였습니다.");
        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatMessage.setMe(false);

        try {
            festivalList = data.getJSONArray("festivalList");

            if (data.has("placeSource")) {
                placeSource = data.getJSONObject("placeSource");


                if (placeSource.has("x_coord")) {
                    sourceX_Coord = placeSource.get("x_coord").toString().replaceAll("\"", "");
                }
                if (placeSource.has("y_coord")) {
                    sourceY_Coord = placeSource.get("y_coord").toString().replaceAll("\"", "");
                }
                if (placeSource.has("round")) {
                    sourceAround = Double.parseDouble(placeSource.get("round").toString());
                }
            }

            if (festivalList.length() > 0) {
                isFestivalExist = true;
                for (int i = 0; i < festivalList.length(); i++) {
                    JSONObject festivalItem = festivalList.getJSONObject(i);

                    String festivalId = null;
                    String festivalName = null;
                    String festivalClass = null;
                    String festivalTarget = null;
                    String festivalPlace = null;
                    String festivalStartDate = null;
                    String festivalEndDate = null;
                    String festivalPayment = null;
                    String festivalXCoord = null;
                    String festivalYCoord = null;
                    String festivalArea = null;
                    String festivalLink = null;

                    //나중에 3항연산자로 변경
                    if (festivalItem.has("FESTIVAL_ID")) {
                        festivalId = festivalItem.get("FESTIVAL_ID").toString().replaceAll("\"", "");
                    }
                    if (festivalItem.has("FESTIVAL_NAME")) {
                        festivalName = festivalItem.get("FESTIVAL_NAME").toString().replaceAll("\"", "");
                    }
                    if (festivalItem.has("FESTIVAL_CLASS")) {
                        festivalClass = festivalItem.get("FESTIVAL_CLASS").toString().replaceAll("\"", "");
                    }
                    if (festivalItem.has("FESTIVAL_TARGET")) {
                        festivalTarget = festivalItem.get("FESTIVAL_TARGET").toString().replaceAll("\"", "");
                    }
                    if (festivalItem.has("FESTIVAL_PLACE")) {
                        festivalPlace = festivalItem.get("FESTIVAL_PLACE").toString().replaceAll("\"", "");
                    }
                    if (festivalItem.has("FESTIVAL_START_DATE")) {
                        festivalStartDate = festivalItem.get("FESTIVAL_START_DATE").toString().replaceAll("\"", "");
                    }
                    if (festivalItem.has("FESTIVAL_END_DATE")) {
                        festivalEndDate = festivalItem.get("FESTIVAL_END_DATE").toString().replaceAll("\"", "");
                    }
                    if (festivalItem.has("FESTIVAL_PAYMENT")) {
                        festivalPayment = festivalItem.get("FESTIVAL_PAYMENT").toString().replaceAll("\"", "");
                    }
                    if (festivalItem.has("FESTIVAL_X_COORD")) {
                        festivalXCoord = festivalItem.get("FESTIVAL_X_COORD").toString();
                    }
                    if (festivalItem.has("FESTIVAL_Y_COORD")) {
                        festivalYCoord = festivalItem.get("FESTIVAL_Y_COORD").toString();
                    }
                    if (festivalItem.has("FESTIVAL_AREA")) {
                        festivalArea = festivalItem.get("FESTIVAL_AREA").toString().replaceAll("\"", "");
                    }
                    if (festivalItem.has("FESTIVAL_LINK")) {
                        festivalLink = festivalItem.get("FESTIVAL_LINK").toString().replaceAll("\"", "");
                    }

                    FestivalModel info = new FestivalModel(festivalId, festivalName, festivalClass, festivalTarget, festivalPlace, festivalStartDate, festivalEndDate, festivalPayment, festivalXCoord, festivalYCoord, festivalArea, festivalLink, sourceX_Coord, sourceY_Coord, sourceAround);

                    festivalModelArrayList.add(info);
                }
            } else {
                isFestivalExist = false;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isFestivalExist == true) {
            chatMessage.setFestivalModelArrayList(festivalModelArrayList);
            chatMessage.setViewPager(true);
        } else {
            chatMessage.setViewPager(false);
            chatMessage.setMessage("요청하신 위치 주변에 명소가 존재 하지 않습니다.");
        }

        //chatMessage.setMessageUiType(messageUiType);
        //chatMessage.setActionType(responseActionType);
        //chatMessage.setMeesageIntentType(responseIntentType);               //intentType 설정


        return chatMessage;

    }


    /**
     *  API.AI 로부터 행상 정보를 파싱 한다.
     * @param tmp
     * @param responseActionType
     * @param messageUiType
     * @param responseIntentType
     * @param chatMessage
     * @return
     */
    public int parseFestivalListFromAPIAI(Map<String, JsonElement> tmp, int responseActionType, int messageUiType, int responseIntentType, ChatMessage chatMessage) {

        ArrayList<FestivalModel> festivalModelArrayList = null;
        boolean isFestivalExist = false;


        System.out.println(">>[PlaeParser] chatMessage : " + chatMessage.getMessage() + ", action : " + responseActionType + ", ui type : " + messageUiType + ", intent : " + responseIntentType);

        //PlaceList parsing
        if (responseActionType == ACTION_POSITIVE_ANSWER || responseActionType == ACTION_FESTIVAL_SEARCH) {

            if (tmp != null && tmp.size() > 0) {
                //data가 있는 경우..
                festivalModelArrayList = new ArrayList<FestivalModel>();
                JsonArray jsonArray = tmp.get("festivalList").getAsJsonArray();
                JsonObject placeSource = tmp.get("placeSource").getAsJsonObject();

                String sourceX_Coord = null;
                String sourceY_Coord = null;
                double sourceAround = 0;
                if (placeSource.has("x_coord")) {
                    sourceX_Coord = placeSource.get("x_coord").toString().replaceAll("\"", "");;
                }
                if (placeSource.has("y_coord")) {
                    sourceY_Coord = placeSource.get("y_coord").toString().replaceAll("\"", "");;
                }
                if (placeSource.has("round")) {
                    sourceAround = Double.parseDouble(placeSource.get("round").toString());
                }

                //placeList가 존재하는지 확인한다.
                if (jsonArray.size() > 0) {

                    isFestivalExist = true;
                    for (JsonElement jo : jsonArray) {
                        JsonObject festivalItem = jo.getAsJsonObject();
                        String festivalId = null;
                        String festivalName = null;
                        String festivalClass = null;
                        String festivalTarget = null;
                        String festivalPlace = null;
                        String festivalStartDate = null;
                        String festivalEndDate = null;
                        String festivalPayment = null;
                        String festivalXCoord = null;
                        String festivalYCoord = null;
                        String festivalArea = null;
                        String festivalLink = null;

                        //나중에 3항연산자로 변경
                        if (festivalItem.has("FESTIVAL_ID")) {
                            festivalId = festivalItem.get("FESTIVAL_ID").toString().replaceAll("\"", "");
                        }
                        if (festivalItem.has("FESTIVAL_NAME")) {
                            festivalName = festivalItem.get("FESTIVAL_NAME").toString().replaceAll("\"", "");
                        }
                        if (festivalItem.has("FESTIVAL_CLASS")) {
                            festivalClass = festivalItem.get("FESTIVAL_CLASS").toString().replaceAll("\"", "");
                        }
                        if (festivalItem.has("FESTIVAL_TARGET")) {
                            festivalTarget = festivalItem.get("FESTIVAL_TARGET").toString().replaceAll("\"", "");
                        }
                        if (festivalItem.has("FESTIVAL_PLACE")) {
                            festivalPlace = festivalItem.get("FESTIVAL_PLACE").toString().replaceAll("\"", "");
                        }
                        if (festivalItem.has("FESTIVAL_START_DATE")) {
                            festivalStartDate = festivalItem.get("FESTIVAL_START_DATE").toString().replaceAll("\"", "");
                        }
                        if (festivalItem.has("FESTIVAL_END_DATE")) {
                            festivalEndDate = festivalItem.get("FESTIVAL_END_DATE").toString().replaceAll("\"", "");
                        }
                        if (festivalItem.has("FESTIVAL_PAYMENT")) {
                            festivalPayment = festivalItem.get("FESTIVAL_PAYMENT").toString().replaceAll("\"", "");
                        }
                        if (festivalItem.has("FESTIVAL_X_COORD")) {
                            festivalXCoord = festivalItem.get("FESTIVAL_X_COORD").toString().replaceAll("\"", "");
                        }
                        if (festivalItem.has("FESTIVAL_Y_COORD")) {
                            festivalYCoord = festivalItem.get("FESTIVAL_Y_COORD").toString().replaceAll("\"", "");
                        }
                        if (festivalItem.has("FESTIVAL_AREA")) {
                            festivalArea = festivalItem.get("FESTIVAL_AREA").toString().replaceAll("\"", "");
                        }
                        if (festivalItem.has("FESTIVAL_LINK")) {
                            festivalLink = festivalItem.get("FESTIVAL_LINK").toString().replaceAll("\"", "");
                        }

                        FestivalModel info = new FestivalModel(festivalId, festivalName, festivalClass, festivalTarget, festivalPlace, festivalStartDate, festivalEndDate, festivalPayment, festivalXCoord, festivalYCoord, festivalArea, festivalLink, sourceX_Coord, sourceY_Coord, sourceAround);
                        info.setResponseAction(responseActionType);
                        festivalModelArrayList.add(info);

                    }
                }
            }

            //Place가 존재 하는 경우..
            if (isFestivalExist == true) {
                chatMessage.setFestivalModelArrayList(festivalModelArrayList);
                chatMessage.setViewPager(true);
            } else {
                chatMessage.setViewPager(false);
                chatMessage.setMessage("요청하신 위치 주변에 행사가 존재 하지 않습니다.");
            }
        }
        chatMessage.setMessageUiType(messageUiType);
        chatMessage.setActionType(responseActionType);
        chatMessage.setMeesageIntentType(responseIntentType);               //intentType 설정

        return responseActionType;
    }

    //응답 Action 및 메시지 Ui 타입을 결정한다.
    public ResponseActionClass getResponseActionType(String responseAction, boolean isActionInCompleted) {
        int responseActionType;
        int messageUiType = 0;

        if ("action-recommendPlaceSearch".equals(responseAction)) {
            responseActionType = ACTION_RECOMMEND_PLACE_SEARCH;
            messageUiType = UI_TEXT_CARD;
        } else if("action-detailRecommendPlaceSearch".equals(responseAction))
        {
            if(isActionInCompleted == true) {
                responseActionType = ACTION_DETAIL_RECOMMEND_PLACE_SEARCH_INCOMPLETE;
                messageUiType = UI_TEXT_ONLY;
            }
            else {
                responseActionType = ACTION_DETAIL_RECOMMEND_PLACE_SEARCH;
                messageUiType = UI_TEXT_CARD;
                //messageUiType = UI_TEXT_BUTTON;
            }
        }

        else if ("action-currentPlaceSearch".equals(responseAction) || "WEB_Action_CurrentPlaceSearch".equals(responseAction)) {
            //Device 동작
            if(isActionInCompleted == true)
                responseActionType = ACTION_CURRENT_PLACE_SEARCH_INCOMPLETE;
            else
                responseActionType = ACTION_CURRENT_PLACE_SEARCH;
            //messageUiType = UI_TEXT_CARD;
            messageUiType = UI_TEXT_BUTTON;
        }
        else if ("action-destPlaceSearch".equals(responseAction))
        {
            responseActionType = ACTION_DEST_PLACE_SEARCH;
            messageUiType = UI_TEXT_CARD;
        }
        else if ("action-destPlaceSearchNoEntity".equals(responseAction)) {
            //Device 동작
            //아직 다음 대화가 이어져야 하는 상황
            if(isActionInCompleted == true)
            {
                responseActionType = ACTION_DEST_PLACE_SEARCH_NO_ENTITY_INCOMPLETE;
                messageUiType = UI_TEXT_BUTTON;
            }else {
                responseActionType = ACTION_DEST_PLACE_SEARCH_NO_ENTITY;
                messageUiType = UI_TEXT_CARD;
            }
        }
        //출도착지 액션
        else if ("action-pathPlaceSearchNoEntity".equals(responseAction)) {
            //Device 동작
            //아직 다음 대화가 이어져야 하는 상황
            if(isActionInCompleted == true)
            {
                responseActionType = ACTION_PATH_SEARCH_NO_ENTITY_INCOMPLETE;
                messageUiType = UI_TEXT_BUTTON;
            }else {
                responseActionType = ACTION_PATH_SEARCH_NO_ENTITY;
                messageUiType = UI_TEXT_CARD;
            }
        }
        else if ("action-inputLatLon".equals(responseAction)) {

            responseActionType = ACTION_INPUT_LATLON;
            messageUiType = UI_TEXT_CARD;

        }
        else if ("action-help".equals(responseAction)) {
            //
            responseActionType = ACTION_HELP;
            messageUiType = UI_TEXT_BUTTON;
        } else if ("input.welcome".equals(responseAction)) {
            responseActionType = ACTION_HELLO;
            messageUiType = UI_TEXT_BUTTON_IMAGE;
        } else if ("input.unknown".equals(responseAction)) {
            responseActionType = ACTION_UNKNOWN;
            messageUiType = UI_TEXT_BUTTON_IMAGE;
        } else if ("action-festivalSearch".equals(responseAction)) {
            if(isActionInCompleted == true) {
                responseActionType = ACTION_FESTIVAL_SEARCH_INCOMPLETE;
                messageUiType = UI_TEXT_ONLY;
            }else
            {
                responseActionType = ACTION_FESTIVAL_SEARCH;
                messageUiType = UI_TEXT_ONLY;
            }
        } else if("action-positiveAction".equals(responseAction)) {
            responseActionType = ACTION_POSITIVE_ANSWER;
            messageUiType = UI_TEXT_CARD;
        }
        //NegativeAnswer
        else if("action-negativeAction".equals(responseAction) || "action-negativeWithoutContextAction".equals(responseAction)) {
            responseActionType = ACTION_NEGATIVE_ANSWER;
            messageUiType = UI_TEXT_BUTTON;
        }
        else
        {
            responseActionType = ACTION_NO_TYPE;
        }

        return new ResponseActionClass(responseActionType, messageUiType);
    }

    /**
     * API.AI 로부터 받은 Intent 를 가져온다.
     * Web 서비스의 결과 이후 생성하게 될 Intent를 가져온다.
     *
     * @param intentName
     * @return
     */
    private int getResponseIntentType(String intentName) {
        System.out.println(">>intentName : " + intentName);
        int responseIntentType = 0;

        if ("AskProfile".equals(intentName)) {
            responseIntentType = INTENT_ASK_PROFILE;

        } else if ("CurrentPlaceSearch".equals(intentName)) {
            responseIntentType = INTENT_CURRENT_PLACE_SEARCH;
        } else if ("DestPlaceSearch".equals(intentName)) {
            responseIntentType = INTENT_DEST_PLACE_SEARCH;
        } else if ("DestPlaceSearchNoEntity".equals(intentName)) {
            responseIntentType = INTENT_DEST_PLACE_SEARCH_NO_ENTITY;
        } else if ("FestivalSearch".equals(intentName)) {
            responseIntentType = INTENT_FESTIVAL_SEARCH;
        } else if ("Help".equals(intentName)) {
            responseIntentType = INTENT_HELP;
        } else if ("NegativeAnswer".equals(intentName)) {

        } else if ("Welcome".equals(intentName)) {
            responseIntentType = INTENT_HELLO;
        } else if ("PathPlaceSearch".equals(intentName)) {
            responseIntentType = INTENT_PATH_PLACE_SEARCH;
        } else if ("PathPlaceSearchNoEntity".equals(intentName)) {
            responseIntentType = INTENT_PATH_PLACE_SEARCH_NO_ENTITY;
        } else if ("PositiveAnswer".equals(intentName)) {
            responseIntentType = INTENT_POSITIVE_ANSWER;
        } else if ("NegativeAnswer".equals(intentName)) {
            responseIntentType = INTENT_NEGATIVE_ANSWER;
        } else if("NegativeAnswerWithoutContext".equals(intentName))
        {
            //NegativeAnswerWithoutContext
            responseIntentType = INTENT_NEGATIVE_ANSWER_WITHOUT_CONTEXT;
        }
        else if ("RecommendPlace".equals(intentName)) {
            responseIntentType = INTENT_RECOMMEND_PLACE_SEARCH;
        }
        else if ("DetailRecommendPlace".equals(intentName)) {
            responseIntentType = INTENT_DETAIL_RECOMMEND_PLACE_SEARCH;
        }
        //Web Intent
        else if ("WEB_CurrentPlaceSearch".equals(intentName)) {
            responseIntentType = INTENT_WEB_CURRENT_PLACE_SEARCH;
        } else if ("Unknown".equals(intentName)) {
            responseIntentType = INTENT_UNKNOWN;
        }


        return responseIntentType;
    }


    private void getContextParam()
    {

    }

    public int parseFromAPIAI(Result result, ChatMessage chatMessage) {

   /*     Log.i(TAG, "Resolved query: " + result.getResolvedQuery());

        Log.i(TAG, "Action: " + result.getAction());*/

        final String speech = result.getFulfillment().getSpeech();
        final Metadata metadata = result.getMetadata();
        if (metadata != null) {
/*            Log.i(TAG, "Intent id: " + metadata.getIntentId());
            Log.i(TAG, "Intent name: " + metadata.getIntentName());*/
        }
        final HashMap<String, JsonElement> params = result.getParameters();
        if (params != null && !params.isEmpty()) {

            for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
  /*              Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));*/
            }
        }

        String intentName = metadata.getIntentName();


        return parseFromAPIAI(result.getFulfillment().getData(), metadata.getIntentName(), result.getAction(), result.isActionIncomplete(), chatMessage, params);
    }


    private class ResponseActionClass
    {
        public int responseActionType;
        public int messaegUiType;

        public ResponseActionClass(int responseActionType, int messaegUiType)
        {
            this.responseActionType = responseActionType;
            this.messaegUiType = messaegUiType;
        }
    }
}

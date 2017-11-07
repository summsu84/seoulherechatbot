package com.chat.seoul.here.module.model;

import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by JJW on 2017-03-31.
 *
 * activity를 파싱한다.
 */

public class ChatMessageParser {

    private String lastResponseMsgId;
    private String mBotName;
    private ChatMessageParserListener listener;

    public ChatMessageParser(String mBotName, Context ctx)
    {
        this.mBotName = mBotName;
        this.lastResponseMsgId = "";
        listener = (ChatMessageParserListener)ctx;
    }

    public ArrayList<ChatMessage> parseChatMessage(String botResponse)
    {
        return generateChatMessage(botResponse);
    }



    private ArrayList<ChatMessage> generateChatMessage(String botResponse) {

        ArrayList<ChatMessage> chatMessageArrayList = new ArrayList<ChatMessage>();

        try {
            JSONObject jsonObject = new JSONObject(botResponse);
            Integer arrayLength = jsonObject.getJSONArray("activities").length();
            //ChatMessage
            for(int i = 0 ; i < arrayLength ; i++) {


                String responseMsg = "";

                String msgFrom = jsonObject.getJSONArray("activities").getJSONObject(i).getJSONObject("from").get("id").toString();
                String curMsgId = jsonObject.getJSONArray("activities").getJSONObject(i).get("id").toString();                          //맨 마지막꺼만 가져오네..

                String type = jsonObject.getJSONArray("activities").getJSONObject(i).get("type").toString();

                if (msgFrom.trim().toLowerCase().equals(mBotName)) {
                    //맨처음 시작..
                    if (lastResponseMsgId == "") {


                        //'text'가 Null인 경우
                        if (jsonObject.getJSONArray("activities").getJSONObject(i).isNull("text")) {
                            //responseMsg = jsonObject.getJSONArray("activities").getJSONObject(i).getJSONArray("attachments").getJSONObject(0).getJSONObject("content").get("text").toString();

                            if (!jsonObject.getJSONArray("activities").getJSONObject(i).isNull("attachments")) {

                            }

                        } else {
                            responseMsg = jsonObject.getJSONArray("activities").getJSONObject(i).get("text").toString();

                            if (!jsonObject.getJSONArray("activities").getJSONObject(i).isNull("attachments")) {

                            }
                        }
                        //AddResponseToChat(responseMsg, attachment);
                        lastResponseMsgId = curMsgId;
                    } else if (!lastResponseMsgId.equals(curMsgId)) {

                        if (jsonObject.getJSONArray("activities").getJSONObject(i).isNull("text")) {

                            //responseMsg = jsonObject.getJSONArray("activities").getJSONObject(i).getJSONArray("attachments").getJSONObject(0).getJSONObject("content").get("text").toString();
                            if (!jsonObject.getJSONArray("activities").getJSONObject(i).isNull("attachments")) {


                            }

                        } else {

                            responseMsg = jsonObject.getJSONArray("activities").getJSONObject(i).get("text").toString();
                            //Choice prompt인 경우, text가 존재하면서 attachments를 가진다.
                            if (!jsonObject.getJSONArray("activities").getJSONObject(i).isNull("attachments")) {


                            }
                        }
                        //AddResponseToChat(responseMsg, attachment);
                        lastResponseMsgId = curMsgId;
                    }

                    //ChatMessage 클래스에 저장

                    //attachment가 여러개인 경우

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return chatMessageArrayList;
    }




}

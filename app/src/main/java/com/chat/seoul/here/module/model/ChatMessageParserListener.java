package com.chat.seoul.here.module.model;

/**
 * Created by JJW on 2017-03-31.
 */

public interface ChatMessageParserListener {

    void onExecuteSpeechData(ChatMessage chatMessage);
    void onExecuteSpeechData(String str);
}

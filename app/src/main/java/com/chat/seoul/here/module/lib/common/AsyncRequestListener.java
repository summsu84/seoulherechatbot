package com.chat.seoul.here.module.lib.common;

/**
 * Created by JJW on 2017-09-12.
 */

import com.chat.seoul.here.module.model.ChatMessage;

/**
 *  AsyncHttp 요청에 대한 핸들러
 */
public interface AsyncRequestListener {
    void onStartRequest();
    void onEndRequest(ChatMessage chatMessage);

    //View Count 증가 인터페이스
    void onPutEndRequest(int action);
    void onPutEndRequest(int action, ChatMessage chatMessage);

}

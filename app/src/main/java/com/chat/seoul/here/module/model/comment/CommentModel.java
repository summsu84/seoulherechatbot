package com.chat.seoul.here.module.model.comment;

/**
 * Created by JJW on 2017-10-20.
 */

public class CommentModel {

    private String COMMENT_ID;
    private String COMMENT_MESSAGE;
    private String COMMENT_USER;
    private String COMMENT_DATE;
    private String COMMNET_PLACE_ID;

    public CommentModel(String COMMENT_ID, String COMMENT_MESSAGE, String COMMENT_USER, String COMMENT_DATE, String COMMNET_PLACE_ID) {
        this.COMMENT_ID = COMMENT_ID;
        this.COMMENT_MESSAGE = COMMENT_MESSAGE;
        this.COMMENT_USER = COMMENT_USER;
        this.COMMENT_DATE = COMMENT_DATE;
        this.COMMNET_PLACE_ID = COMMNET_PLACE_ID;
    }

    public String getCOMMENT_ID() {
        return COMMENT_ID;
    }

    public void setCOMMENT_ID(String COMMENT_ID) {
        this.COMMENT_ID = COMMENT_ID;
    }

    public String getCOMMENT_MESSAGE() {
        return COMMENT_MESSAGE;
    }

    public void setCOMMENT_MESSAGE(String COMMENT_MESSAGE) {
        this.COMMENT_MESSAGE = COMMENT_MESSAGE;
    }

    public String getCOMMENT_USER() {
        return COMMENT_USER;
    }

    public void setCOMMENT_USER(String COMMENT_USER) {
        this.COMMENT_USER = COMMENT_USER;
    }

    public String getCOMMENT_DATE() {
        return COMMENT_DATE;
    }

    public void setCOMMENT_DATE(String COMMENT_DATE) {
        this.COMMENT_DATE = COMMENT_DATE;
    }

    public String getCOMMNET_PLACE_ID() {
        return COMMNET_PLACE_ID;
    }

    public void setCOMMNET_PLACE_ID(String COMMNET_PLACE_ID) {
        this.COMMNET_PLACE_ID = COMMNET_PLACE_ID;
    }

    @Override
    public String toString() {
        return "CommentModel{" +
                "COMMENT_ID='" + COMMENT_ID + '\'' +
                ", COMMENT_MESSAGE='" + COMMENT_MESSAGE + '\'' +
                ", COMMENT_USER='" + COMMENT_USER + '\'' +
                ", COMMENT_DATE='" + COMMENT_DATE + '\'' +
                ", COMMNET_PLACE_ID='" + COMMNET_PLACE_ID + '\'' +
                '}';
    }
}

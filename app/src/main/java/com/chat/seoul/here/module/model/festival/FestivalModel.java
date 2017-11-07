package com.chat.seoul.here.module.model.festival;

import com.chat.seoul.here.module.model.BaseModel;

import java.io.Serializable;

/**
 * Created by JJW on 2016-09-30.
 */
public class FestivalModel extends BaseModel implements Serializable {

    private String FESTIVAL_ID;
    private String FESTIVAL_NAME;
    private String FESTIVAL_CLASS;
    private String FESTIVAL_TARGET;
    private String FESTIVAL_PLACE;
    private String FESTIVAL_START_DATE;
    private String FESTIVAL_END_DATE;
    private String FESTIVAL_PAYMENT;
    private String FESTIVAL_X_COORD;
    private String FESTIVAL_Y_COORD;
    private String FESTIVAL_AREA;
    private String FESTIVAL_LINK;
    private boolean isChecked;
    private int responseAction;
    //20170913 - 해당 PlaceSource정보
    private String sourceX_Coord;
    private String sourceY_Coord;
    private double sourceAround;

    public FestivalModel(String FESTIVAL_ID, String FESTIVAL_NAME, String FESTIVAL_CLASS, String FESTIVAL_TARGET, String FESTIVAL_PLACE, String FESTIVAL_START_DATE, String FESTIVAL_END_DATE, String FESTIVAL_PAYMENT, String FESTIVAL_X_COORD, String FESTIVAL_Y_COORD, String FESTIVAL_AREA, String FESTIVAL_LINK) {
        this.FESTIVAL_ID = FESTIVAL_ID;
        this.FESTIVAL_NAME = FESTIVAL_NAME;
        this.FESTIVAL_CLASS = FESTIVAL_CLASS;
        this.FESTIVAL_TARGET = FESTIVAL_TARGET;
        this.FESTIVAL_PLACE = FESTIVAL_PLACE;
        this.FESTIVAL_START_DATE = FESTIVAL_START_DATE;
        this.FESTIVAL_END_DATE = FESTIVAL_END_DATE;
        this.FESTIVAL_PAYMENT = FESTIVAL_PAYMENT;
        this.FESTIVAL_X_COORD = FESTIVAL_X_COORD;
        this.FESTIVAL_Y_COORD = FESTIVAL_Y_COORD;
        this.FESTIVAL_AREA = FESTIVAL_AREA;
        this.FESTIVAL_LINK = FESTIVAL_LINK;
    }

    public FestivalModel(String FESTIVAL_ID, String FESTIVAL_NAME, String FESTIVAL_CLASS, String FESTIVAL_TARGET, String FESTIVAL_PLACE, String FESTIVAL_START_DATE, String FESTIVAL_END_DATE, String FESTIVAL_PAYMENT, String FESTIVAL_X_COORD, String FESTIVAL_Y_COORD, String FESTIVAL_AREA, String FESTIVAL_LINK, String sourceX_Coord, String sourceY_Coord, double sourceAround) {
        this.FESTIVAL_ID = FESTIVAL_ID;
        this.FESTIVAL_NAME = FESTIVAL_NAME;
        this.FESTIVAL_CLASS = FESTIVAL_CLASS;
        this.FESTIVAL_TARGET = FESTIVAL_TARGET;
        this.FESTIVAL_PLACE = FESTIVAL_PLACE;
        this.FESTIVAL_START_DATE = FESTIVAL_START_DATE;
        this.FESTIVAL_END_DATE = FESTIVAL_END_DATE;
        this.FESTIVAL_PAYMENT = FESTIVAL_PAYMENT;
        this.FESTIVAL_X_COORD = FESTIVAL_X_COORD;
        this.FESTIVAL_Y_COORD = FESTIVAL_Y_COORD;
        this.FESTIVAL_AREA = FESTIVAL_AREA;
        this.FESTIVAL_LINK = FESTIVAL_LINK;
        this.sourceX_Coord = sourceX_Coord;
        this.sourceY_Coord = sourceY_Coord;
        this.sourceAround = sourceAround;
    }

    @Override
    public String toString() {
        return "FestivalModel{" +
                "FESTIVAL_ID='" + FESTIVAL_ID + '\'' +
                ", FESTIVAL_NAME='" + FESTIVAL_NAME + '\'' +
                ", FESTIVAL_CLASS='" + FESTIVAL_CLASS + '\'' +
                ", FESTIVAL_TARGET='" + FESTIVAL_TARGET + '\'' +
                ", FESTIVAL_PLACE='" + FESTIVAL_PLACE + '\'' +
                ", FESTIVAL_START_DATE='" + FESTIVAL_START_DATE + '\'' +
                ", FESTIVAL_END_DATE='" + FESTIVAL_END_DATE + '\'' +
                ", FESTIVAL_PAYMENT='" + FESTIVAL_PAYMENT + '\'' +
                ", FESTIVAL_X_COORD='" + FESTIVAL_X_COORD + '\'' +
                ", FESTIVAL_Y_COORD='" + FESTIVAL_Y_COORD + '\'' +
                ", FESTIVAL_AREA='" + FESTIVAL_AREA + '\'' +
                ", FESTIVAL_LINK='" + FESTIVAL_LINK + '\'' +
                ", sourceX_Coord='" + sourceX_Coord + '\'' +
                ", sourceY_Coord='" + sourceY_Coord + '\'' +
                ", sourceAround=" + sourceAround +
                '}';
    }

    public String getFESTIVAL_ID() {
        return FESTIVAL_ID;
    }

    public void setFESTIVAL_ID(String FESTIVAL_ID) {
        this.FESTIVAL_ID = FESTIVAL_ID;
    }

    public String getFESTIVAL_NAME() {
        return FESTIVAL_NAME;
    }

    public void setFESTIVAL_NAME(String FESTIVAL_NAME) {
        this.FESTIVAL_NAME = FESTIVAL_NAME;
    }

    public String getFESTIVAL_CLASS() {
        return FESTIVAL_CLASS;
    }

    public void setFESTIVAL_CLASS(String FESTIVAL_CLASS) {
        this.FESTIVAL_CLASS = FESTIVAL_CLASS;
    }

    public String getFESTIVAL_TARGET() {
        return FESTIVAL_TARGET;
    }

    public void setFESTIVAL_TARGET(String FESTIVAL_TARGET) {
        this.FESTIVAL_TARGET = FESTIVAL_TARGET;
    }

    public String getFESTIVAL_PLACE() {
        return FESTIVAL_PLACE;
    }

    public void setFESTIVAL_PLACE(String FESTIVAL_PLACE) {
        this.FESTIVAL_PLACE = FESTIVAL_PLACE;
    }

    public String getFESTIVAL_START_DATE() {
        return FESTIVAL_START_DATE;
    }

    public void setFESTIVAL_START_DATE(String FESTIVAL_START_DATE) {
        this.FESTIVAL_START_DATE = FESTIVAL_START_DATE;
    }

    public String getFESTIVAL_END_DATE() {
        return FESTIVAL_END_DATE;
    }

    public void setFESTIVAL_END_DATE(String FESTIVAL_END_DATE) {
        this.FESTIVAL_END_DATE = FESTIVAL_END_DATE;
    }

    public String getFESTIVAL_PAYMENT() {
        return FESTIVAL_PAYMENT;
    }

    public void setFESTIVAL_PAYMENT(String FESTIVAL_PAYMENT) {
        this.FESTIVAL_PAYMENT = FESTIVAL_PAYMENT;
    }

    public String getFESTIVAL_X_COORD() {
        return FESTIVAL_X_COORD;
    }

    public void setFESTIVAL_X_COORD(String FESTIVAL_X_COORD) {
        this.FESTIVAL_X_COORD = FESTIVAL_X_COORD;
    }

    public String getFESTIVAL_Y_COORD() {
        return FESTIVAL_Y_COORD;
    }

    public void setFESTIVAL_Y_COORD(String FESTIVAL_Y_COORD) {
        this.FESTIVAL_Y_COORD = FESTIVAL_Y_COORD;
    }

    public String getFESTIVAL_AREA() {
        return FESTIVAL_AREA;
    }

    public void setFESTIVAL_AREA(String FESTIVAL_AREA) {
        this.FESTIVAL_AREA = FESTIVAL_AREA;
    }

    public String getFESTIVAL_LINK() {
        return FESTIVAL_LINK;
    }

    public void setFESTIVAL_LINK(String FESTIVAL_LINK) {
        this.FESTIVAL_LINK = FESTIVAL_LINK;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getResponseAction() {
        return responseAction;
    }

    public void setResponseAction(int responseAction) {
        this.responseAction = responseAction;
    }

    public String getSourceX_Coord() {
        return sourceX_Coord;
    }

    public void setSourceX_Coord(String sourceX_Coord) {
        this.sourceX_Coord = sourceX_Coord;
    }

    public String getSourceY_Coord() {
        return sourceY_Coord;
    }

    public void setSourceY_Coord(String sourceY_Coord) {
        this.sourceY_Coord = sourceY_Coord;
    }

    public double getSourceAround() {
        return sourceAround;
    }

    public void setSourceAround(double sourceAround) {
        this.sourceAround = sourceAround;
    }
}

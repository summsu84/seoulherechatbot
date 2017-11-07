package com.chat.seoul.here.module.model.place;

import android.os.Parcel;
import android.os.Parcelable;

import com.chat.seoul.here.module.model.BaseModel;

import java.util.ArrayList;

/**
 * Created by JJW on 2016-09-30.
 */
public class PlaceModel extends BaseModel implements Parcelable {

    private String PLACE_ID; //장소 아이디
    private int CODE ; //코드
    private String CODENAME;
    private String PLACE_NAME;  //장소명
    private String PLACE_DESC; //기타사항
    private String SEAT_CNT; //객석수",
    private String PHONE;    //전화번호
    private String OPEN_HOUR;    //관람시간","
    private String CLOSE_DAY;    //휴관일","
    private String OPEN_DAY;    //개관일자","
    private String HOMEPAGE;    //홈페이지","
    private String ADDR;    //주소","
    private String ENTR_FEE;    //관람료(원)","
    private int VIEW_CNT;        //읽은 횟수
    private int LIKE;            //좋아요
    private int COMMENT_CNT;
    //private String FAX;    //팩스번호"
    //좌표 정보
    private PlaceCoordModel PLACE_COORD;
    //Image
    private ArrayList<PlaceImageModel> PLACE_IMAGE_LIST = new ArrayList<PlaceImageModel>();

    private boolean isChecked;      //체크 여부
    private int responseAction;

    //20170913 - 해당 PlaceSource정보
    private String sourceX_Coord;
    private String sourceY_Coord;
    private double sourceAround;

    public PlaceModel()
    {
        PLACE_IMAGE_LIST = new ArrayList<PlaceImageModel>();
    }

    public PlaceModel(String PLACE_NAME, String PLACE_DESC) {
        this.PLACE_NAME = PLACE_NAME;
        this.PLACE_DESC = PLACE_DESC;
        PLACE_IMAGE_LIST = new ArrayList<PlaceImageModel>();
    }

    public PlaceModel(String PLACE_ID, int CODE, String PLACE_NAME, String PLACE_DESC, String SEAT_CNT, String PHONE, String OPEN_HOUR, String CLOSE_DAY, String OPEN_DAY, String HOMEPAGE, String ADDR, String ENTR_FEE, PlaceCoordModel PLACE_COORD, ArrayList<PlaceImageModel> PLACE_IMAGE_LIST, String sourceX_Coord, String sourceY_Coord, double sourceAround) {
        this.PLACE_ID = PLACE_ID;
        this.CODE = CODE;
        this.PLACE_NAME = PLACE_NAME;
        this.PLACE_DESC = PLACE_DESC;
        this.SEAT_CNT = SEAT_CNT;
        this.PHONE = PHONE;
        this.OPEN_HOUR = OPEN_HOUR;
        this.CLOSE_DAY = CLOSE_DAY;
        this.OPEN_DAY = OPEN_DAY;
        this.HOMEPAGE = HOMEPAGE;
        this.ADDR = ADDR;
        this.ENTR_FEE = ENTR_FEE;
        this.PLACE_COORD = PLACE_COORD;
        this.PLACE_IMAGE_LIST = PLACE_IMAGE_LIST;
        this.isChecked = false;
        PLACE_IMAGE_LIST = new ArrayList<PlaceImageModel>();
        this.sourceX_Coord = sourceX_Coord;
        this.sourceY_Coord = sourceY_Coord;
        this.sourceAround = sourceAround;
    }

    public PlaceModel(String PLACE_ID, int CODE, String PLACE_NAME, String PLACE_DESC, String SEAT_CNT, String PHONE, String OPEN_HOUR, String CLOSE_DAY, String OPEN_DAY, String HOMEPAGE, String ADDR, String ENTR_FEE, String sourceX_Coord, String sourceY_Coord, double sourceAround) {
        this.PLACE_ID = PLACE_ID;
        this.CODE = CODE;
        this.PLACE_NAME = PLACE_NAME;
        this.PLACE_DESC = PLACE_DESC;
        this.SEAT_CNT = SEAT_CNT;
        this.PHONE = PHONE;
        this.OPEN_HOUR = OPEN_HOUR;
        this.CLOSE_DAY = CLOSE_DAY;
        this.OPEN_DAY = OPEN_DAY;
        this.HOMEPAGE = HOMEPAGE;
        this.ADDR = ADDR;
        this.ENTR_FEE = ENTR_FEE;
        PLACE_IMAGE_LIST = new ArrayList<PlaceImageModel>();
        this.sourceX_Coord = sourceX_Coord;
        this.sourceY_Coord = sourceY_Coord;
        this.sourceAround = sourceAround;
    }

    //여기 사용
    public PlaceModel(String PLACE_ID, String CODE, String PLACE_NAME, String PLACE_DESC, String SEAT_CNT, String PHONE, String OPEN_HOUR, String CLOSE_DAY, String OPEN_DAY, String HOMEPAGE, String ADDR, String ENTR_FEE, int VIEW_CNT, int LIKE, int COMMENT_CNT, String sourceX_Coord, String sourceY_Coord, double sourceAround) {
        this.PLACE_ID = PLACE_ID;
        this.CODENAME = CODE;
        this.PLACE_NAME = PLACE_NAME;
        this.PLACE_DESC = PLACE_DESC;
        this.SEAT_CNT = SEAT_CNT;
        this.PHONE = PHONE;
        this.OPEN_HOUR = OPEN_HOUR;
        this.CLOSE_DAY = CLOSE_DAY;
        this.OPEN_DAY = OPEN_DAY;
        this.HOMEPAGE = HOMEPAGE;
        this.ADDR = ADDR;
        this.ENTR_FEE = ENTR_FEE;
        this.VIEW_CNT = VIEW_CNT;
        this.LIKE = LIKE;
        this.COMMENT_CNT = COMMENT_CNT;
        PLACE_IMAGE_LIST = new ArrayList<PlaceImageModel>();
        this.sourceX_Coord = sourceX_Coord;
        this.sourceY_Coord = sourceY_Coord;
        this.sourceAround = sourceAround;
    }

    protected PlaceModel(Parcel in) {
        PLACE_ID = in.readString();
        CODE = in.readInt();
        CODENAME = in.readString();
        PLACE_NAME = in.readString();
        PLACE_DESC = in.readString();
        SEAT_CNT = in.readString();
        PHONE = in.readString();
        OPEN_HOUR = in.readString();
        CLOSE_DAY = in.readString();
        OPEN_DAY = in.readString();
        HOMEPAGE = in.readString();
        ADDR = in.readString();
        ENTR_FEE = in.readString();
        VIEW_CNT = in.readInt();
        LIKE = in.readInt();
        COMMENT_CNT = in.readInt();
        PLACE_COORD = in.readParcelable(PlaceCoordModel.class.getClassLoader());
        in.readTypedList(PLACE_IMAGE_LIST, PlaceImageModel.CREATOR);
        isChecked = in.readByte() != 0;
        responseAction = in.readInt();
        sourceX_Coord = in.readString();
        sourceY_Coord = in.readString();
        sourceAround = in.readDouble();
    }



    public static final Creator<PlaceModel> CREATOR = new Creator<PlaceModel>() {
        @Override
        public PlaceModel createFromParcel(Parcel in) {
            return new PlaceModel(in);
        }

        @Override
        public PlaceModel[] newArray(int size) {
            return new PlaceModel[size];
        }
    };

    public String getPLACE_ID() {
        return PLACE_ID;
    }

    public void setPLACE_ID(String PLACE_ID) {
        this.PLACE_ID = PLACE_ID;
    }

    public int getCODE() {
        return CODE;
    }

    public void setCODE(int CODE) {
        this.CODE = CODE;
    }

    public String getCODENAME() {
        return CODENAME;
    }

    public void setCODENAME(String CODENAME) {
        this.CODENAME = CODENAME;
    }

    public String getPLACE_NAME() {
        return PLACE_NAME;
    }

    public void setPLACE_NAME(String PLACE_NAME) {
        this.PLACE_NAME = PLACE_NAME;
    }

    public String getPLACE_DESC() {
        return PLACE_DESC;
    }

    public void setPLACE_DESC(String PLACE_DESC) {
        this.PLACE_DESC = PLACE_DESC;
    }

    public String getSEAT_CNT() {
        return SEAT_CNT;
    }

    public void setSEAT_CNT(String SEAT_CNT) {
        this.SEAT_CNT = SEAT_CNT;
    }

    public String getPHONE() {
        return PHONE;
    }

    public void setPHONE(String PHONE) {
        this.PHONE = PHONE;
    }

    public String getOPEN_HOUR() {
        return OPEN_HOUR;
    }

    public void setOPEN_HOUR(String OPEN_HOUR) {
        this.OPEN_HOUR = OPEN_HOUR;
    }

    public String getCLOSE_DAY() {
        return CLOSE_DAY;
    }

    public void setCLOSE_DAY(String CLOSE_DAY) {
        this.CLOSE_DAY = CLOSE_DAY;
    }

    public String getOPEN_DAY() {
        return OPEN_DAY;
    }

    public void setOPEN_DAY(String OPEN_DAY) {
        this.OPEN_DAY = OPEN_DAY;
    }

    public String getHOMEPAGE() {
        return HOMEPAGE;
    }

    public void setHOMEPAGE(String HOMEPAGE) {
        this.HOMEPAGE = HOMEPAGE;
    }

    public String getADDR() {
        return ADDR;
    }

    public void setADDR(String ADDR) {
        this.ADDR = ADDR;
    }

    public String getENTR_FEE() {
        return ENTR_FEE;
    }

    public void setENTR_FEE(String ENTR_FEE) {
        this.ENTR_FEE = ENTR_FEE;
    }

    public int getVIEW_CNT() {
        return VIEW_CNT;
    }

    public void setVIEW_CNT(int VIEW_CNT) {
        this.VIEW_CNT = VIEW_CNT;
    }

    public int getLIKE() {
        return LIKE;
    }

    public void setLIKE(int LIKE) {
        this.LIKE = LIKE;
    }

    public int getCOMMENT_CNT() {
        return COMMENT_CNT;
    }

    public void setCOMMENT_CNT(int COMMENT_CNT) {
        this.COMMENT_CNT = COMMENT_CNT;
    }

    public PlaceCoordModel getPLACE_COORD() {
        return PLACE_COORD;
    }

    public void setPLACE_COORD(PlaceCoordModel PLACE_COORD) {
        this.PLACE_COORD = PLACE_COORD;
    }

    public ArrayList<PlaceImageModel> getPLACE_IMAGE_LIST() {
        return PLACE_IMAGE_LIST;
    }

    public void setPLACE_IMAGE_LIST(ArrayList<PlaceImageModel> PLACE_IMAGE_LIST) {
        this.PLACE_IMAGE_LIST = PLACE_IMAGE_LIST;
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

    /*    public boolean isShared() {
        return isShared;
    }

    public void setShared(boolean shared) {
        isShared = shared;
    }*/

    @Override
    public String toString() {
        return "PlaceModel{" +
                "PLACE_ID='" + PLACE_ID + '\'' +
                ", CODE=" + CODE +
                ", PLACE_NAME='" + PLACE_NAME + '\'' +
                ", PLACE_DESC='" + PLACE_DESC + '\'' +
                ", SEAT_CNT='" + SEAT_CNT + '\'' +
                ", PHONE='" + PHONE + '\'' +
                ", OPEN_HOUR='" + OPEN_HOUR + '\'' +
                ", CLOSE_DAY='" + CLOSE_DAY + '\'' +
                ", OPEN_DAY='" + OPEN_DAY + '\'' +
                ", HOMEPAGE='" + HOMEPAGE + '\'' +
                ", ADDR='" + ADDR + '\'' +
                ", ENTR_FEE='" + ENTR_FEE + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(PLACE_ID);
        parcel.writeInt(CODE);
        parcel.writeString(CODENAME);
        parcel.writeString(PLACE_NAME);
        parcel.writeString(PLACE_DESC);
        parcel.writeString(SEAT_CNT);
        parcel.writeString(PHONE);
        parcel.writeString(OPEN_HOUR);
        parcel.writeString(CLOSE_DAY);
        parcel.writeString(OPEN_DAY);
        parcel.writeString(HOMEPAGE);
        parcel.writeString(ADDR);
        parcel.writeString(ENTR_FEE);
        parcel.writeInt(VIEW_CNT);
        parcel.writeInt(LIKE);
        parcel.writeInt(COMMENT_CNT);
        //Object
        parcel.writeParcelable(PLACE_COORD, i);
        parcel.writeTypedList(PLACE_IMAGE_LIST);
        parcel.writeByte((byte)(isChecked ? 1 : 0));
        parcel.writeInt(responseAction);
        //신규 추가
        parcel.writeString(sourceX_Coord);
        parcel.writeString(sourceY_Coord);
        parcel.writeDouble(sourceAround);

    }

    private void readFromParcel(Parcel in){
        PLACE_ID = in.readString();
        CODE = in.readInt();
        PLACE_NAME = in.readString();
        PLACE_DESC = in.readString();
        SEAT_CNT = in.readString();
        PHONE = in.readString();
        OPEN_HOUR = in.readString();
        CLOSE_DAY = in.readString();
        OPEN_DAY = in.readString();
        HOMEPAGE = in.readString();
        ADDR = in.readString();
        ENTR_FEE = in.readString();
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

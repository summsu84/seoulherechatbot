package com.chat.seoul.here.module.model.place;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JJW on 2017-08-08.
 */

public class PlaceImageModel implements Parcelable {

    private String MAIN_IMG = null;    //대표이미지","
    //private Bitmap IMG_BITMAP = null;

    public PlaceImageModel(Parcel in) {
        MAIN_IMG = in.readString();
      //  IMG_BITMAP = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<PlaceImageModel> CREATOR = new Creator<PlaceImageModel>() {
        @Override
        public PlaceImageModel createFromParcel(Parcel in) {
            return new PlaceImageModel(in);
        }

        @Override
        public PlaceImageModel[] newArray(int size) {
            return new PlaceImageModel[size];
        }
    };

    public PlaceImageModel() {

    }

    public String getMAIN_IMG() {
        return MAIN_IMG;
    }

    public void setMAIN_IMG(String MAIN_IMG) {
        this.MAIN_IMG = MAIN_IMG;
    }

 /*   public Bitmap getIMG_BITMAP() {
        return IMG_BITMAP;
    }

    public void setIMG_BITMAP(Bitmap IMG_BITMAP) {
        this.IMG_BITMAP = IMG_BITMAP;
    }*/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(MAIN_IMG);
      //  parcel.writeParcelable(IMG_BITMAP, i);
    }
}

package com.chat.seoul.here.module.model.place;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by JJW on 2017-08-08.
 */

public class PlaceCoordModel implements Parcelable {

    private double X_COORD;    //X좌표","
    private double Y_COORD;    //Y좌표","

    public PlaceCoordModel(Parcel in) {
        X_COORD = in.readDouble();
        Y_COORD = in.readDouble();
    }


    public static final Creator<PlaceCoordModel> CREATOR = new Creator<PlaceCoordModel>() {
        @Override
        public PlaceCoordModel createFromParcel(Parcel in) {
            return new PlaceCoordModel(in);
        }

        @Override
        public PlaceCoordModel[] newArray(int size) {
            return new PlaceCoordModel[size];
        }
    };

    public PlaceCoordModel() {

    }

    public double getX_COORD() {
        return X_COORD;
    }

    public void setX_COORD(double x_COORD) {
        X_COORD = x_COORD;
    }

    public double getY_COORD() {
        return Y_COORD;
    }

    public void setY_COORD(double y_COORD) {
        Y_COORD = y_COORD;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(X_COORD);
        parcel.writeDouble(Y_COORD);
    }
}

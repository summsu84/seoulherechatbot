package com.chat.seoul.here.module.lib.common;

import android.location.Location;

import com.chat.seoul.here.module.map.inf.DialogCommonListener;
import com.chat.seoul.here.module.model.place.PlaceModel;

import java.util.ArrayList;

/**
 * Created by JJW on 2016-10-05.
 * 앱에서 사용되는 모든 전역변수를 관리 한다..
 */
public class AppPlaceInfo  {

    public ArrayList<PlaceModel> uniquePlaceModelList;				    // 서울관광 파일로부터 받은 장소정보
      public GpsInfo  uniqueGpsInfo;                              //GPS 정보 클래스
    public Location currentLocationInfo;
    public ImageProcess uniqueImageProcessInfo;


    private int mDialogRetVal;
    //Test
    private DialogCommonListener listener;

    //  public Firebase ref;

    /** onCreate()
     * 액티비티, 리시버, 서비스가 생성되기전 어플리케이션이 시작 중일때
     * Application onCreate() 메서드가 만들어 진다고 나와 있습니다.
     * by. Developer 사이트
     */
    public AppPlaceInfo(){

        //MultiDex.install(this);
        System.out.println("-------------AppPlaceInfo onCreate()-------------");
        //리스트 초기화
        uniquePlaceModelList = new ArrayList<PlaceModel>();

         //GPS Info
        uniqueGpsInfo = new GpsInfo();
        uniqueImageProcessInfo = new ImageProcess();
        currentLocationInfo = null;
    }



}

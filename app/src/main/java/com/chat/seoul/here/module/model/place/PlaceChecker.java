package com.chat.seoul.here.module.model.place;


import com.chat.seoul.here.module.map.direction.Route;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by JJW on 2016-10-01.
 *  사용자가 지정한 출발지 및 목적지 사이의 경로상에 Place 및 Shared Place 를 검색하고, 이를 현시 해준다.
 */
public class PlaceChecker {

    private List<Route> routes;
    private ArrayList<PlaceModel> mAllItems;
    private ArrayList<PlaceModel> mShowItems;        //반경에 들어오는 보여지는 Pace.
    private ArrayList<PlaceModel> mShowItemsTmp;

    private ArrayList<PlaceModel> mSharedAllItems;
    private ArrayList<PlaceModel> mSharedShowItems;        //반경에 들어오는 보여지는 Shared Items
    private ArrayList<PlaceModel> mSharedShowItemsTmp;


    private double mAroundDistance;                     // 경로 반경 거리 (300 m ~ 2000m 까지 제한)

    public PlaceChecker()
    {
        mShowItemsTmp = new ArrayList<PlaceModel>();

        mSharedShowItemsTmp = new ArrayList<PlaceModel>();
        mAroundDistance = 1;
    }
    public PlaceChecker(List<Route> routes, ArrayList<PlaceModel> mAllItems, ArrayList<PlaceModel> mShowItems) {
        this.routes = routes;
        this.mAllItems = mAllItems;
        this.mShowItems = mShowItems;

        //test
       // latlnToUtm(37.566679,126.978430);
        mAroundDistance = 1;
    }

    /**
     * 경로 주변의 장소들을 검색한다 -- 경로 주변인 경우..
     * @return
     */
    public ArrayList<PlaceModel> calculateAroundPlace() {

        LatLng startPoint;

        int nextPointPosition = 0;
        for (Route route : routes) {
           // System.out.println("------Route start location : " + route.startLocation + ", end location : " + route.endLocation + ", arounddistance : " + mAroundDistance);

            List<LatLng> pointList = route.points;
            for(int i = 0 ; i < pointList.size() ; i++)
            {
                startPoint = pointList.get(i);
                calculateAroundPlace(startPoint);
                //다음 시작 지점을 찾는다.
                /*
                1. startPoint에서 다음 지점은 거리가 반경을 고려하여 해당 반경에 들어오게되면, 해당 지점은 패스하고, 그다음 지점으로 검색한다.
                 */
                for(int j = i + 1 ; j < pointList.size() ; j++)
                {
                    if(j >= pointList.size())
                    {
                        break;
                    }

                    LatLng nextPoint = pointList.get(j);
                    double dist = calculateAroundPlace(startPoint.latitude, startPoint.longitude, nextPoint.latitude, nextPoint.longitude);
                    if(dist > mAroundDistance && dist < (mAroundDistance * 2)){
                        i = j;
                        break;
                    }
                }
            }
           // checkCoveredPlace(startLocation);
           // calculateAroundPlace(endtLocation);

        }


        mShowItems = new ArrayList<PlaceModel>(new HashSet<PlaceModel>(mShowItemsTmp));

        return mShowItems;
    }

    /**
     * 경로 주변의 공유 장소를 검색한다.
     * @return
     */
    public ArrayList<PlaceModel> calculateAroundSharedPlace()
    {
        LatLng startPoint;

        int nextPointPosition = 0;
        for (Route route : routes) {
           // System.out.println("------Route start location : " + route.startLocation + ", end location : " + route.endLocation);
            List<LatLng> pointList = route.points;
            for(int i = 0 ; i < pointList.size() ; i++)
            {
                startPoint = pointList.get(i);
                calculateAroundSharedPlace(startPoint);
                //다음 시작 지점을 찾는다.
                /*
                1. startPoint에서 다음 지점은 거리가 반경을 고려하여 해당 반경에 들어오게되면, 해당 지점은 패스하고, 그다음 지점으로 검색한다.
                 */
                for(int j = i + 1 ; j < pointList.size() ; j++)
                {
                    if(j >= pointList.size())
                    {
                        break;
                    }

                    LatLng nextPoint = pointList.get(j);
                    double dist = calculateAroundPlace(startPoint.latitude, startPoint.longitude, nextPoint.latitude, nextPoint.longitude);
                    if(dist > mAroundDistance && dist < (mAroundDistance * 2)){
                        i = j;
                        break;
                    }
                }
            }
        }

        //중복제거 필요..

        mSharedShowItems = new ArrayList<PlaceModel>(new HashSet<PlaceModel>(mSharedShowItemsTmp));

        return mSharedShowItems;
    }


    public void calculateAroundPlace(LatLng cLatLon)
    {
        //기준 좌표
        double cLat = cLatLon.latitude;
        double cLon = cLatLon.longitude;

        for(PlaceModel info : mAllItems)
        {
            double dist = calculateAroundPlace(cLat, cLon, info.getPLACE_COORD().getX_COORD(), info.getPLACE_COORD().getY_COORD());

            //1 km 이내인지 검색한다.. (1km == 1)
            if(dist <= mAroundDistance)
            {
                //반경에 들어온것으로 체크한다.
                mShowItemsTmp.add(info);
                //System.out.println("---------dist : " + dist + ", info : " + info.toString() + ", aroundistance : " + mAroundDistance);
            }
        }
       // System.out.println("----------PlaceChecker::calculateAroundPlace - mShowItemsTmp : " + mShowItemsTmp);
    }

    public void calculateAroundSharedPlace(LatLng cLatLon)
    {
        //기준 좌표
        double cLat = cLatLon.latitude;
        double cLon = cLatLon.longitude;

        for(PlaceModel info : mSharedAllItems)
        {
            double dist = calculateAroundPlace(cLat, cLon,info.getPLACE_COORD().getX_COORD(), info.getPLACE_COORD().getY_COORD());
            //1.5 km 이내인지 검색한다..
           // System.out.println("Info Name : " + info.getFacName() + " X : " + info.getX_Coord() + ", Y : " + info.getY_Coord() + ", dist : " + dist);
            if(dist <= mAroundDistance)
            {
                //반경에 들어온것으로 체크한다.
                mSharedShowItemsTmp.add(info);
            }
        }
    }



    /**
     * 두 위경도 사이에서의 거리를 가져온다..
     * @param cLat 이전 위도
     * @param cLon 이전 경도
     * @param tLat 다음 위도
     * @param tLon 다음 경도
     * @return 두 위경도 지점 사이의 거리를 반환한다.
     */
    public double calculateAroundPlace(double cLat, double cLon, double tLat, double tLon)
    {
        double dist = (6371* Math.acos(Math.cos(Math.toRadians(cLat))* Math.cos(Math.toRadians(tLat))* Math.cos(Math.toRadians(tLon)

                - Math.toRadians(cLon))+ Math.sin(Math.toRadians(cLat)) * Math.sin(Math.toRadians(tLat))));

       // System.out.println("-------------distance : " + dist);      //1km 이면 1을 리턴..
        return dist;
    }

    public void init()
    {
       // mAllItems.clear();
        mShowItems.clear();        //반경에 들어오는 보여지는 Pace.
        mShowItemsTmp.clear();

    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public ArrayList<PlaceModel> getmAllItems() {
        return mAllItems;
    }

    public void setmAllItems(ArrayList<PlaceModel> mAllItems) {
        this.mAllItems = mAllItems;
    }

    public ArrayList<PlaceModel> getmShowItems() {
        return mShowItems;
    }

    public void setmShowItems(ArrayList<PlaceModel> mShowItems) {
        this.mShowItems = mShowItems;
    }

    public ArrayList<PlaceModel> getmSharedAllItems() {
        return mSharedAllItems;
    }

    public void setmSharedAllItems(ArrayList<PlaceModel> mSharedAllItems) {
        this.mSharedAllItems = mSharedAllItems;
    }

    public ArrayList<PlaceModel> getmSharedShowItems() {
        return mSharedShowItems;
    }

    public void setmSharedShowItems(ArrayList<PlaceModel> mSharedShowItems) {
        this.mSharedShowItems = mSharedShowItems;
    }

    public double getmAroundDistance() {
        return mAroundDistance;
    }

    public void setmAroundDistance(double mAroundDistance) {
        System.out.println("---------setAroundDistance : " + mAroundDistance);
        this.mAroundDistance = mAroundDistance;
    }

    //위경도를 UTM으로 변경한다.
    public LatLng midPoint(LatLng latLng1, LatLng latLng2)
    {
        return midPoint(latLng1.latitude, latLng1.longitude, latLng2.latitude, latLng2.longitude);
    }

    public LatLng midPoint(double lat1,double lon1,double lat2,double lon2){

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        //print out in degrees
        //System.out.println("result mid : " + Math.toDegrees(lat3) + " " + Math.toDegrees(lon3));
        return new LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3));
    }
}

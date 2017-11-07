/**
 *  전체 보기 정보
 */
package com.chat.seoul.here;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.seoul.here.module.adapter.CustomListViewAdapter;
import com.chat.seoul.here.module.adapter.CustomListViewBaseAdapter;
import com.chat.seoul.here.module.lib.app.AIApplication;
import com.chat.seoul.here.module.lib.common.AsyncHttpRequest;
import com.chat.seoul.here.module.lib.common.AsyncRequestListener;
import com.chat.seoul.here.module.map.direction.DirectionFinder;
import com.chat.seoul.here.module.map.direction.DirectionFinderListener;
import com.chat.seoul.here.module.map.direction.Distance;
import com.chat.seoul.here.module.map.direction.Route;
import com.chat.seoul.here.module.map.inf.DialogCommonListener;
import com.chat.seoul.here.module.map.inf.PlaceFinderListener;
import com.chat.seoul.here.module.model.BaseModel;
import com.chat.seoul.here.module.model.ChatMessage;
import com.chat.seoul.here.module.model.festival.FestivalModel;
import com.chat.seoul.here.module.model.place.PlaceChecker;
import com.chat.seoul.here.module.model.place.PlaceCoordModel;
import com.chat.seoul.here.module.model.place.PlaceModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.chat.seoul.here.module.lib.conf.Common.ACTION_CURRENT_PLACE_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_DEST_PLACE_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_DEST_PLACE_SEARCH_NO_ENTITY;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_PATH_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_PATH_SEARCH_NO_ENTITY;
import static com.chat.seoul.here.module.lib.conf.Common.CHAT_MESSAGE_FESTIVAL;
import static com.chat.seoul.here.module.lib.conf.Common.CHAT_MESSAGE_PLACE;
import static com.chat.seoul.here.module.lib.conf.Common.URI_REQUEST_PLACE_DEST;

/**
 *  장소 종류에 따라 다르게 동작하도록 변경함..
 */
public class MapSearchDetailActivity extends BaseActivity implements OnMapReadyCallback, DirectionFinderListener, PlaceFinderListener, ViewPager.OnPageChangeListener, View.OnClickListener, DialogCommonListener, AsyncRequestListener {

    //어떤 정보를 클릭했는지 확인한다.
    public static final int SEND_PLAN_REGISTER_REQUEST = 1;
    public static final int SEND_PLAN_SHOW_REQUEST = 2;
    private static final String TAG = "MapSearchDetailActivity";

    final Context context = this;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private GoogleMap mMap;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog1;
    //Place and SharedPlace Marker Setting
    private List<Marker> mPlaceMarkers = new ArrayList<>();
    private List<Marker> mSharedPlaceMarkers = new ArrayList<>();

    /* Src and Dest */
    private String start;
    private String dest;
    private String mStartMarkerName;
    private String mDestMarkerName;
    private PlaceChecker mPlaceChecker;

    //basic information
    private ArrayList<PlaceModel> mShowItems;            //전체 가져온 아이템

    //baseModel
    private ArrayList<BaseModel> mBaseShowItems;


    private TextView mCount;
    private Spinner mSpinner;

    private double sAroundDistance = 0.3;

    //onCreate Check..
    private boolean mIsCreated;
    List<Route> mRoutes;

    private ListView listView;
    private CustomListViewAdapter adapter;

    //20170818 - JJW
    private int mActionType;
    private String sourceX_Coord;
    private String sourceY_Coord;

    private int PLACE_TYPE;
    private boolean isSpinnerInitial = true;
    private double defaultLat = 37.5545043;
    private double defaultLon = 126.8573291;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mRoutes = null;
        mIsCreated = true;
        //보여줄 아이템을 생성한다.
        mShowItems = new ArrayList<PlaceModel>();
        mBaseShowItems = new ArrayList<BaseModel>();

        Bundle initBurndle = getIntent().getExtras();
        //아래 부분 체크 필요 getExtras
        if(initBurndle != null)
        {
            //명소 검색인지, 행사 검색인지 판단하자. 값이 없으면 명소 검색
            int tmpType = initBurndle.getInt("placeType", 0);
            mActionType = initBurndle.getInt("responseActionType", 0);
            this.PLACE_TYPE = tmpType;
            if(tmpType == CHAT_MESSAGE_PLACE)
            {
                //ACTION을 먼저 체크 하자
                if(mActionType == ACTION_PATH_SEARCH_NO_ENTITY || mActionType == ACTION_PATH_SEARCH) {
                    //PathSearch 인 경우..
                    this.start = initBurndle.getString("origin", "NONE");
                    this.dest = initBurndle.getString("destination", "NONE");

                    //사용자 정보에 추가하기..
                    this.mStartMarkerName = initBurndle.getString("originName", "NONE");
                    this.mDestMarkerName = initBurndle.getString("destinationName", "NONE");

                    sourceX_Coord = initBurndle.getString("sourceX_Coord", "NONE");
                    sourceY_Coord = initBurndle.getString("sourceY_Coord", "NONE");
                    sAroundDistance = initBurndle.getDouble("sourceAround", 1.0);
                }else {
                    ArrayList<PlaceModel> tmp = initBurndle.getParcelableArrayList("placeItems");
                    mShowItems.addAll(tmp);
                    sourceX_Coord = initBurndle.getString("sourceX_Coord", "NONE");
                    sourceY_Coord = initBurndle.getString("sourceY_Coord", "NONE");
                    sAroundDistance = initBurndle.getDouble("sourceAround", 1.0);
                }
            }else{
                ArrayList<FestivalModel> tmpFestivalModelList = (ArrayList<FestivalModel>) initBurndle.getSerializable("festivalItems");
                mBaseShowItems.addAll(tmpFestivalModelList);
                sourceX_Coord = initBurndle.getString("sourceX_Coord", "NONE");
                sourceY_Coord = initBurndle.getString("sourceY_Coord", "NONE");
                sAroundDistance = initBurndle.getDouble("sourceAround", 1.0);
                //Festival인 경우, 시간, 경로 조절 제거 한다.
                //((TextView) findViewById(R.id.txtSearchRstDistance)).setVisibility(View.GONE);
                //((TextView) findViewById(R.id.txtSearchRstDuration)).setVisibility(View.GONE);

            }
        }
        Log.i(TAG, "sourceX_Coord : " + sourceX_Coord + ", sourceY_Coord : " + sourceY_Coord + ", isIniSpinner : " + isSpinnerInitial);

        //장소 검색 객체 생성
        mPlaceChecker = new PlaceChecker();
        mPlaceChecker.setmAllItems(((AIApplication)getApplication()).getAppPlaceInfo().uniquePlaceModelList );
        init();
        initListView();

    }

    //리스트 뷰 초기화화
    public void initListView()
    {
        //리스트뷰 설정
        listView = (ListView) findViewById(R.id.listPlaceList);
        listView.addFooterView(((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_footer, null));

        //명소 전체 검색 인 경우..
        if(PLACE_TYPE == CHAT_MESSAGE_PLACE) {
            if(mActionType != ACTION_PATH_SEARCH || mActionType != ACTION_PATH_SEARCH_NO_ENTITY)
            {
                adapter = new CustomListViewAdapter(this, 0, mShowItems);
                listView.setAdapter(adapter);
            }
        }
        //행사 전체 검색 인 경우.
        else
        {
            //베이스 아답타 사용
            CustomListViewBaseAdapter baseAdapter = new CustomListViewBaseAdapter(this, 0, mBaseShowItems, PLACE_TYPE);
            listView.setAdapter(baseAdapter);
        }

        TextView txtLoadmore = (TextView)findViewById(R.id.listview_footer);
        txtLoadmore.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                adapter.loadMoreListView();
            }
        });
    }

    //지도 및 스핀 초기화
    public void init()
    {
        // Initializing
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapShareReg);
        mapFragment.getMapAsync(this);

        mCount = (TextView) this.findViewById(R.id.txtSearchRstCount);


        if ("NONE".equals(sourceX_Coord) || "NONE".equals(sourceY_Coord)) {
            if(mActionType == ACTION_PATH_SEARCH || mActionType == ACTION_PATH_SEARCH_NO_ENTITY) {
                initSpinner();
            }else {
                mSpinner = (Spinner) findViewById(R.id.spinnerSearchRstDistance);
                TextView txtSearchRoundLabel = (TextView) findViewById(R.id.txtSearchRoundLabel);
                txtSearchRoundLabel.setVisibility(View.GONE);
                mSpinner.setVisibility(View.GONE);
            }
        } else {
            initSpinner();
        }

    }

    //스피너 설정 (반경 설정 도구)
    public void initSpinner()
    {
        //Spinner
        mSpinner = (Spinner)findViewById(R.id.spinnerSearchRstDistance);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.mapdistance, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        //스피너 설정하기..
        mSpinner.setAdapter(adapter);

        //우선 하드코딩
        int spinIdx = 0;
        if(sAroundDistance == 0.3)
        {
            spinIdx = 0;
        }else if(sAroundDistance == 0.5)
        {
            spinIdx = 1;
        }else if(sAroundDistance == 1)
        {
            spinIdx = 2;
        }else if(sAroundDistance == 1.5)
        {
            spinIdx = 3;
        }else{
            spinIdx = 4;
        }
        mSpinner.setSelection(spinIdx);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            // 스피너 즉 경로 거리를 변경 했을 때, 호출된다.
            // 1. 맵은 그대로 둔다.
            // 2.리스트 뷰만 지우고, 다시 그린다.
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String strDist = mSpinner.getItemAtPosition(position).toString();
                strDist = strDist.replace("m", "");
                double distValue  = (Double.parseDouble(strDist)) / 1000;

//                System.out.println("--------onItemSelected... str : " + (Double.parseDouble(strDist)) + ", double value : " + distValue);

                sAroundDistance = distValue;
                //mRoute가 null 인경우는 처음 액티비티가 호출된 경우이다.
                //따라서, mRoute가 null이 아닌경우, 즉 주변 경로가 검색되었던 적이 있는 경우, 리스트 뷰를 업데이트 한다.
                if(isSpinnerInitial)
                {
                    isSpinnerInitial = false;
                }else {

                    //반경 설정을 재조정한다.
                    // 경로 주변 검색인 경우
                    if (mActionType == ACTION_PATH_SEARCH || mActionType == ACTION_PATH_SEARCH_NO_ENTITY) {
                        if (mRoutes != null) {
                            clearListShowItems();
                            processListViews(mRoutes);
                        }
                    }
                    //경로주변인 아닌 경우..
                    else {
                        clearListShowItems();
                        processListView();
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //no jobs
            }
        });
    }

    // 경로 주변 명소 검색인 경우 - 출발지와 목적지 정보를 전송한다.
    private void sendRequest() {
        String origin = this.start;
        String destination = this.dest;
       // System.out.println("----------------SendRequest-------------origin : " + origin + ", dest : " + destination);
        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart() {
        Log.i(TAG, "OnDreictionFinderStart...");
        String title = getResources().getString(R.string.search_rst_progress_title);
        String content = getResources().getString(R.string.search_rst_progress_content_map);
        progressDialog1 = ProgressDialog.show(this, title,
                content, true);

        try {
            if (originMarkers != null) {
                for (Marker marker : originMarkers) {
                    marker.remove();
                }
            }

            if (destinationMarkers != null) {
                for (Marker marker : destinationMarkers) {
                    marker.remove();
                }
            }

            if (polylinePaths != null) {
                for (Polyline polyline : polylinePaths) {
                    polyline.remove();
                }
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 구글로부터 받아온 루트 정보를 처리한다..
     * @param routes
     */
    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        Log.i(TAG, "onDirectionFinderSuccess...");
        //progressDialog.dismiss();
        if(progressDialog1.isShowing())
        {
            progressDialog1.dismiss();
        }
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        //구글로부터 경로를 못가지고 온경우..
        if (routes.size() == 0)
        {
            String content = getResources().getString(R.string.search_rst_direction_not_found);

            Toast.makeText(this, content,
                    Toast.LENGTH_SHORT).show();

            return;
        }

        for (Route route : routes) {
            //Check ZoomValue according to distance
            Distance distance = route.distance;
          //  System.out.println("Distance : " + distance.value);     //m 단위
            int zoomLevel = calculateZoomLevel(distance.value);


            //출발지와 목적지의 중간지점을 계산한다..
            LatLng mid = mPlaceChecker.midPoint(route.startLocation, route.endLocation);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mid, zoomLevel));
            ((TextView) findViewById(R.id.txtSearchRstDistance)).setText(route.distance.text);
            String duration = route.duration.text;
            duration = duration.replace("hour", "시간");
            duration = duration.replace("mins", "분");
            ((TextView) findViewById(R.id.txtSearchRstDuration)).setText(duration);

            //((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            // ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title(mStartMarkerName)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title(mDestMarkerName)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(6);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 18));
        mRoutes = routes;
        processListViews(routes);
    }


    // 거리에 따른 줌 레벨 계산
    public int calculateZoomLevel(double distance)
    {
        //1km 범위 ..
        int zoomLevel;
        switch ((int)(distance/1000)) {
            //1km 미만
            case 0 :
                zoomLevel = 15;
                break;
            //1km ~ 2Km
            case 1 :
            case 2 :
            case 3:
                zoomLevel = 14;
                break;
            case 4 :
            case 5:
            case 6:
                zoomLevel = 13;
                break;
            case 7 :
            case 8 :
            case 9:
            case 10:
                zoomLevel = 12;
                break;
            default:
                zoomLevel = 11;
                break;
        }

        return zoomLevel;
    }


    /**
     * 주변 경로 명소 검색인 경우의 리스트 뷰 업데이트 로직
     * @param routes
     */
    public void processListViews(List<Route> routes)
    {
        //20160922 - JJW 해당 경로 지점을 체크한다.
        //Distance설정
        mPlaceChecker.setmAroundDistance(sAroundDistance);
        mPlaceChecker.setRoutes(routes);

        //경로 주변 검색 인 경우..
        if(mActionType == ACTION_PATH_SEARCH || mActionType == ACTION_PATH_SEARCH_NO_ENTITY) {
            //경로 주변 검색인 경우, 서버와 통신 할경우 계산량 때문에, 클라이언트 자체에서 계산 한다.
            calculateAroundPlace();
        }

    }


    /**
     *  주변 경로명소가 아닌 경우, 반경을 설정할때 마다 서버로 부터 데이터를 새롭게 가져온다.
     *  --> 기존 클라이언트에서 반경 계산을 서버를  통해서 가져오도록 변경
     *  기준 지점을 중심으로 반경을 다시 계산하여 보여준다.
     */
    private void processListView() {
        AsyncHttpRequest httpRequest = new AsyncHttpRequest(this, "POST");
        //Dest로 한다..
        Log.i(TAG, "SourceX_Coord : " + sourceX_Coord + ", SourceY_Coord : " + sourceY_Coord);
        httpRequest.execute(sourceX_Coord, sourceY_Coord, URI_REQUEST_PLACE_DEST, Double.toString(sAroundDistance));
    }


    /**
     *  주변에 보여질 장소를 계산한다.
     *
     */
    public void calculateAroundPlace()
    {
        //여기서 UI업데이트 한다..
        try {
            //추후 스레드로 빼던가..
            //주변의 장소들을 검색하여 가져온다.. 동기화가 필요없지?? 동기화가 필요한건.. 실제로 보여질 아이템들..
            mShowItems = mPlaceChecker.calculateAroundPlace();
            adapter = new CustomListViewAdapter(this, 0, mShowItems);
            listView.setAdapter(adapter);
            //공유 장소업데이트
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     *  화면에 보여지는 리스트들을 모두 제거한다.
     */
    public void clearListShowItems()
    {
        //PlaceChecker 를초기화한다..
        if(mActionType == ACTION_PATH_SEARCH_NO_ENTITY || mActionType == ACTION_PATH_SEARCH)
            mPlaceChecker.init();           //아마 mShowItems들도 다 클리어될듯..

        mShowItems.clear();
    }


    /**
     *  마커 드로잉
     */
    public void drawPlaceMarker(ArrayList<PlaceModel> listShowPlace)
    {
        //마커 생성하기
        //1. 우선 모든 마커를 지운다.
        clearPlaceMarkers();
        String strCount = null;
        if(listShowPlace != null) {
            for (int i = 0; i < listShowPlace.size(); i++) {
                PlaceModel info = listShowPlace.get(i);

                PlaceCoordModel coordModel = info.getPLACE_COORD();

                LatLng latlng = new LatLng(coordModel.getX_COORD(), coordModel.getY_COORD());
                //coordModel.getX_COORD(),coordModel.getY_COORD()
                mPlaceMarkers.add(mMap.addMarker(new MarkerOptions()
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .title(info.getPLACE_NAME())
                        .position(latlng)));

            }

            if(PLACE_TYPE == CHAT_MESSAGE_PLACE)
                strCount = Integer.toString(listShowPlace.size()) + "/" + Integer.toString(mShowItems.size());
            else
                strCount = Integer.toString(listShowPlace.size()) + "/" + Integer.toString(mBaseShowItems.size());

        }

        if(strCount == null)
        {
            mCount.setText("");
        }else
        {
            mCount.setText(strCount);
        }
    }

    /**
     *  Generic 방법을 사용한 경우, 마커 드로잉 부분 (일반 클래스 및 제네릭 이용한 방법으로 구현)
     * @param listShowPlace
     * @param placeType
     */
    public void drawBaseModelMarker(ArrayList<BaseModel> listShowPlace, int placeType)
    {
        //마커 생성하기
        //1. 우선 모든 마커를 지운다.
        clearPlaceMarkers();
        String strCount = null;
        if(listShowPlace != null) {
            for (int i = 0; i < listShowPlace.size(); i++) {

                if(placeType == CHAT_MESSAGE_PLACE) {
                    PlaceModel info = (PlaceModel) listShowPlace.get(i);

                    PlaceCoordModel coordModel = info.getPLACE_COORD();

                    LatLng latlng = new LatLng(coordModel.getX_COORD(), coordModel.getY_COORD());
                    //coordModel.getX_COORD(),coordModel.getY_COORD()
                    mPlaceMarkers.add(mMap.addMarker(new MarkerOptions()
                            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .title(info.getPLACE_NAME())
                            .position(latlng)));
                }else
                {
                    FestivalModel info = (FestivalModel) listShowPlace.get(i);

                    LatLng latlng = new LatLng(Double.parseDouble(info.getFESTIVAL_X_COORD()), Double.parseDouble(info.getFESTIVAL_Y_COORD()));
                    //coordModel.getX_COORD(),coordModel.getY_COORD()
                    mPlaceMarkers.add(mMap.addMarker(new MarkerOptions()
                            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .title(info.getFESTIVAL_NAME())
                            .position(latlng)));
                }

            }

            strCount = Integer.toString(listShowPlace.size()) + "/" + Integer.toString(mShowItems.size());

        }

        if(strCount == null)
        {
            mCount.setText("");
        }else
        {
            mCount.setText(strCount);
        }
    }





    public void drawMarker(int position)
    {
        //탭 1인경우..
        ArrayList<PlaceModel> drawList = null;
        try {
            if (position == 0) {

            } else {

            }
        }catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        drawPlaceMarker(drawList);
    }


    /**
     * 탭의 위치에따라 마커를 제거한다.
     * 탭이 기본 장소이면, 공유장소 마커를제거한다. 그반대로 동작.
     *
     */
    public void clearPlaceMarkers()
    {
        try {
            if (mSharedPlaceMarkers != null) {
                for (Marker marker : mSharedPlaceMarkers) {
                    marker.remove();
                }
            }

            if (mPlaceMarkers != null) {
                for (Marker marker : mPlaceMarkers) {
                    marker.remove();
                }
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * onCreate 이후 호출되며, 구글로 부터 맵을 수신 한 이후 호출된다.
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        //구글 맵 설정..
        mMap = googleMap;
        try {
            Log.i(TAG, "[onMapReady] sourceX_Coord : " + sourceX_Coord + ", sourceY_Coord : " + sourceY_Coord + ", double Value of : " + Double.valueOf(sourceX_Coord) + ", double Value of Y : " + Double.valueOf(sourceY_Coord));
            //System.out.println(i);
        } catch (NumberFormatException nfe) {
            System.err.println(nfe);
        }
        double cx = 0;
        double cy = 0;
        if("NONE".equals(sourceX_Coord) || "NONE".equals(sourceY_Coord))
        {
            cx = defaultLat;
            cy = defaultLon;
        }else
        {
            cx = Double.parseDouble(sourceX_Coord);
            cy = Double.parseDouble(sourceY_Coord);
        }

        Log.i(TAG, "[onMapReady] double cx : " + cx + ", double cy : " + cy);
        LatLng hcmus = new LatLng(cx, cy);
        int zoomLevel = 10;
        if(mActionType == ACTION_CURRENT_PLACE_SEARCH || mActionType == ACTION_DEST_PLACE_SEARCH || mActionType == ACTION_DEST_PLACE_SEARCH_NO_ENTITY){
            zoomLevel = 13;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, zoomLevel));
        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .title("중심위치")
                .position(hcmus)));

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //현재 위치 기능 활성화
        mMap.setMyLocationEnabled(true);

        //경로 주변 명소인 경우..
        if(mActionType == ACTION_PATH_SEARCH || mActionType == ACTION_PATH_SEARCH_NO_ENTITY)
        {
            sendRequest();
        }
        //만약 행사 검색인 경우, 지도가 만들어진 이 후 마커를 드로우 한다.
        if(PLACE_TYPE == CHAT_MESSAGE_FESTIVAL)
        {
            drawBaseModelMarker(mBaseShowItems, PLACE_TYPE);
        }

    }

    /**
     * 액션 메뉴 생성
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_rst1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *  화면에 보여질 리스트검색 시작 시작 시 프로그레스바 활성화
     */
    @Override
    public void onPlaceFinderStart() {
        String title = getResources().getString(R.string.search_rst_progress_title);
        String content = getResources().getString(R.string.search_rst_progress_content_place);
        if(progressDialog != null)
        {
            if(progressDialog.isShowing() == true) {
                //no jobs..
                progressDialog.dismiss();
            }else
            {
                progressDialog = ProgressDialog.show(this, title,
                        content, true);
            }
        }else {
            progressDialog = ProgressDialog.show(this, title,
                    content, true);
        }
    }

    /**
     *  위치 찾기 완료 이 후 프로그레스 바 종료 및 마커 드로잉
     */
    @Override
    public void onPlaceFinderSuccess(ArrayList<PlaceModel> items) {

        if(progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
        //drawsucess 발생시킨다..
        drawPlaceMarker(items);

    }

    /**
     * 위치 찾기 완료 이 후 프로그레스 바 종료 및 마커 드로잉
     * @param items
     * @param placeType
     */
    @Override
    public void onPlaceFinderSuccess(ArrayList<BaseModel> items, int placeType) {
        if(progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
        //drawsucess 발생시킨다..

        drawBaseModelMarker(items, placeType);
    }

    /**
     * 장소 검색 실패.
     * 1. 장소 검색 실패는 요청한 페이지 즉, 현재 보여지는 리스트 뷰가 계산을 통해 경로 주변에 나타나는 장소의 개수보다 많을 때,즉 페이지가 없을 때 발생시킨다.
     */
    @Override
    public void onPlaceFinderFailed()
    {
        String content = getResources().getString(R.string.search_rst_list_not_found);
        if(progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
        Toast.makeText(this, content,
                Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * 탭 선택시 호출
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        //화면에 보여질 아이템
        drawMarker(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onDialogOk(final int requestCode) {

        int tabPosition = mViewPager.getCurrentItem();
       // System.out.println("----------------Dialog Ok Clicked..-------------Tab Position : " + tabPosition);

        if(requestCode == SEND_PLAN_REGISTER_REQUEST)
        {

            String content = getResources().getString(R.string.search_rst_plan_register_success);
            Toast.makeText(this, content,
                    Toast.LENGTH_SHORT).show();

        }else if(requestCode == SEND_PLAN_SHOW_REQUEST)
        {
            //액티비티 이동..
        }

    }

    @Override
    public void onDialogCancel() {
    }


    @Override
    public void onClick(View view) {

    }

    /**
     * AsyncReqeust Start..
     */
    @Override
    public void onStartRequest() {

        String title = getResources().getString(R.string.search_rst_progress_title);
        String content = getResources().getString(R.string.search_rst_progress_content_map);
        progressDialog = ProgressDialog.show(this, title,
                content, true);

    }

    @Override
    public void onEndRequest(ChatMessage chatMessage) {

        if(progressDialog.isShowing())
        {
            progressDialog.dismiss();
        }
        //null exception
        try {
            ArrayList<PlaceModel> tmp = chatMessage.getPlaceModelArrayList();
            //데이터가 없는 경우..
            if(tmp == null){

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set title
                alertDialogBuilder.setTitle("알림");

                // set dialog message
                alertDialogBuilder
                        .setMessage("요청하신 반경에 장소가 존재 하지 않습니다.")
                        .setCancelable(false)
                        .setPositiveButton("확인",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();

            }else {

                Log.i(TAG, "HttpOnEndRequest..chatMesagePlaceModes : " + tmp.size());
                mShowItems.addAll(tmp);
                adapter.setTotalItems(mShowItems);
                //adapter.notifyDataSetChanged();         //데이터 변화 알림..
                //drawPlaceMarker(mShowItems);
                //여기에 PlaceCheckr를 통해서 주변 반경을 계산하자..
/*
                if(mShowItems.size() > 2)
                {

                }
*/

            }
        }catch(NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onPutEndRequest(int action) {
        //no jobs
    }

    @Override
    public void onPutEndRequest(int action, ChatMessage chatMessage) {
        //no jobs
    }
}



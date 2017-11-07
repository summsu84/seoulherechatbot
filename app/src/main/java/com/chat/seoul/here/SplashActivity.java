package com.chat.seoul.here;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.chat.seoul.here.module.lib.app.AIApplication;
import com.chat.seoul.here.module.lib.common.AsyncHttpRequest;
import com.chat.seoul.here.module.lib.common.AsyncRequestListener;
import com.chat.seoul.here.module.map.inf.GPSFinderListener;
import com.chat.seoul.here.module.model.ChatMessage;
import com.chat.seoul.here.module.model.festival.FestivalModel;
import com.chat.seoul.here.module.model.place.PlaceModel;

import java.util.ArrayList;

import static com.chat.seoul.here.module.lib.conf.Common.GPS_RESULT_FAILED;
import static com.chat.seoul.here.module.lib.conf.Common.GPS_RESULT_SUCESS;

// Splash Activity
public class SplashActivity extends Activity implements GPSFinderListener, AsyncRequestListener{

    private static final int REQUEST_LOCATION_SETTING = 0;
    private static final int PERMISSIONS_REQUEST_ACCESS_LOCATION = 1000;
    private static int requestCnt = 0;
    private int gpsResult = 0;
    private static boolean isRequestRefresh = false;
    private static boolean isRequestFestivalRecommend = false;
    private static boolean isRequestPlaceAll = false;
    private static boolean isRequestTrain = false;
    private final int SPLASH_DISPLAY_LENGTH = 1500;
    private ProgressDialog progressDialog;
    private ArrayList<PlaceModel> currentPlaceList;
    private ArrayList<PlaceModel> recommendPlaceList;
    private ArrayList<FestivalModel> recommendFestivalList;
    private ArrayList<PlaceModel> allPlaceList;

    private int loadingMessage;
    private Context self;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        self = this;
        // SPLASH_DISPLAY_LENGTH 뒤에 메인 액티비티를 실행시키고 종료한다.
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 메인 액티비티를 실행하고 로딩화면을 죽인다.
                Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);*/

        initAlarm();
        //GPS 체크 한다..
    }

    public void initAlarm()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("알림");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("앱을 구동하기 위해 GPS와 네트워크를 활성화 해주세요. 그리고 6.0 이상에서는 위치 권한을 설정해주세요. 설정 후에 앱이 실행되지 않으면 다시 한번 실행해주세요.");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                checkPermission();
            }
        });
        alertDialog.show();
    }

    public void initCheckService()
    {
        if(checkGPSService())
        {
            if(checkNetwork())
                initSplash();
        }
    }

    //GPS 사용 여부 체크
    public boolean checkGPSService() {
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean retVal = false;
        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS OFF 일때 Dialog 표시
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            alertDialog.setTitle("GPS 사용유무셋팅");
            alertDialog.setMessage("GPS 셋팅이 되지 않았을수도 있습니다. 설정창으로 가시겠습니까?");
            alertDialog.setCancelable(false);

            // OK 를 누르게 되면 설정창으로 이동합니다.
            alertDialog.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            //startActivity(intent);
                            startActivityForResult(intent, REQUEST_LOCATION_SETTING);
                        }
                    });
            // Cancle 하면 종료 합니다.
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(self);

                            alertDialog.setTitle("주의");
                            alertDialog.setMessage("서울?여긴? 챗봇 어플은 GPS가 설정되어야 합니다. 다시 한번 확인 해주세요");

                            // OK 를 누르게 되면 설정창으로 이동합니다.
                            alertDialog.setPositiveButton("확인",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });

                            alertDialog.show();
                        }
                    });
            //AlertDialog alert = alertDialog.create();
            alertDialog.show();
            retVal = false;
        } else {
            retVal = true;
        }
        return retVal;
    }

    //네트워크 연결 여부 체크
    public boolean checkNetwork()
    {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        boolean retVal = false;
        if(cm.getActiveNetworkInfo()!=null){
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            switch(activeNetwork.getType()){
                case ConnectivityManager.TYPE_WIMAX:
                    //Log.d("Test","Wibro 네트워크연결");
                    chkGpsService();

                    break;
                case ConnectivityManager.TYPE_WIFI:
                    //Log.d("Test","WiFi 네트워크연결");
                    chkGpsService();

                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    //Log.d("Test","3G 네트워크연결");
                    chkGpsService();

                    break;
            }
            retVal = true;
        }else{
            Log.d("SplashActivity","네트워크연결 안됨");
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("네트워크 에러");
            alertDialog.setCancelable(false);
            alertDialog.setMessage("Wifi, 3G, Wibro 연결이 되지 않았습니다. 네트워크 연결을 확인하시고 다시 시작해주세요.");
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            });
            alertDialog.show();

            retVal = false;
        }
        return retVal;
    }

    //GPS 설정 체크
    private boolean chkGpsService() {

        String gps = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        Log.d(gps, "aaaa");

        if (!(gps.matches(".*gps.*") && gps.matches(".*network.*"))) {

            // GPS OFF 일때 Dialog 표시
            AlertDialog.Builder gsDialog = new AlertDialog.Builder(this);
            gsDialog.setTitle("위치 서비스 설정");
            gsDialog.setMessage("무선 네트워크 사용, GPS 위성 사용을 모두 체크하셔야 정확한 위치 서비스가 가능합니다.\n위치 서비스 기능을 설정하시겠습니까?설정 후 앱이 시작되지 않으면 재시작 해주세요");
            gsDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // GPS설정 화면으로 이동
                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(intent);
                }
            })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).create().show();
            return false;

        } else {
            return true;
        }
    }

    public void checkPermission()
    {
  /*      if (ContextCompat.checkSelfPermission(this, Manifest.permission.) == PackageManager.PERMISSION_GRANTED){
            //Manifest.permission.READ_CALENDAR이 접근 승낙 상태 일때
        } else{
            //Manifest.permission.READ_CALENDAR이 접근 거절 상태 일때
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CALENDAR)){
                //사용자가 다시 보지 않기에 체크를 하지 않고, 권한 설정을 거절한 이력이 있는 경우
            } else{
                //사용자가 다시 보지 않기에 체크하고, 권한 설정을 거절한 이력이 있는 경우
            }

            //사용자에게 접근권한 설정을 요구하는 다이얼로그를 띄운다.
            //만약 사용자가 다시 보지 않기에 체크를 했을 경우엔 권한 설정 다이얼로그가 뜨지 않고,
            //곧바로 OnRequestPermissionResult가 실행된다.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CALENDAR},0);

        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            /* 사용자 단말기의 권한 중 "전화걸기" 권한이 허용되어 있는지 체크한다.
            *  int를 쓴 이유? 안드로이드는 C기반이기 때문에, Boolean 이 잘 안쓰인다.
            */
            int permissionResult = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
            /* ACCESS_FINE_LOCATION 의 권한이 없을 때 */
            // 패키지는 안드로이드 어플리케이션의 아이디다.( 어플리케이션 구분자 )
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
            /* 사용자가ACCESS_FINE_LOCATION 권한을 한번이라도 거부한 적이 있는 지 조사한다.
            * 거부한 이력이 한번이라도 있다면, true를 리턴한다.
            */
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                    dialog.setTitle("권한이 필요합니다.")
                            .setMessage("이 기능을 사용하기 위해서는 단말기의 \"위치 서비스\" 권한이 필요합니다. 계속하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_LOCATION);


                                    }

                                }
                            })
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(SplashActivity.this, "기능을 취소했습니다.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .create()
                            .show();
                }

                //최초로 권한을 요청할 때
                else {
                    // ACCESS_FINE_LOCATION 권한을 Android OS 에 요청한다.
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_LOCATION);
                }

            }
                    /* ACCESS_FINE_LOCATION의 권한이 있을 때 */
            else {
                //성공
                initCheckService();
            }

        }
                /* 사용자의 OS 버전이 마시멜로우 이하일 떄 */
        else {

            //성공
            initCheckService();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    initCheckService();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("알림");
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage("위치 권한이 허용되지 않았습니다. 앱 동작이 제대로 되지 않을 수 있습니다. 감사합니다.");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            checkPermission();
                        }
                    });
                    alertDialog.show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void initSplash()
    {
        //GPS 사용하기 추가 하기
        ((AIApplication)getApplication()).getAppPlaceInfo().uniqueGpsInfo.setContext(this);
        ((AIApplication)getApplication()).getAppPlaceInfo().uniqueGpsInfo.getGPSEnabled();
    }

    @Override
    public void onGPSFinderSuccess() {

        //현재 위치 주변
        String currentLatitude = "37.566303";
        String currentLongitude = "126.977917";
        try {
            Location currentLocation = ((AIApplication) getApplication()).getAppPlaceInfo().uniqueGpsInfo.getLocation();
            currentLatitude = Double.toString(currentLocation.getLatitude());
            currentLongitude = Double.toString(currentLocation.getLongitude());
        }catch (NullPointerException e)
        {
            e.printStackTrace();
        }

        loadingMessage = R.string.splash_load_progress_content;
        String url = "https://seoulherechat.herokuapp.com/request/place/current";
        gpsResult = GPS_RESULT_SUCESS;
        AsyncHttpRequest postRequest = new AsyncHttpRequest(this, "POST");
        postRequest.execute(currentLatitude, currentLongitude, url, "1.0");

    }

    @Override
    public void onGPSFinderFailed() {
        gpsResult = GPS_RESULT_FAILED;
        //((AIApplication)getApplication()).getAppPlaceInfo().uniqueGpsInfo.showSettingsAlert();

        requestPlaceRefresh();
    }

    public void requestPlaceRefresh()
    {
        String url = "https://seoulherechat.herokuapp.com/request/place/refresh";
        loadingMessage = R.string.splash_load_progress_recommend_content;
        isRequestRefresh = true;
        AsyncHttpRequest postRequest = new AsyncHttpRequest(this, "GET");
        postRequest.execute(url);
    }

    public void requestRecommendFestival()
    {
        String url = "https://seoulherechat.herokuapp.com/request/festival/recommend";
        loadingMessage = R.string.splash_load_progress_festival_content;
        isRequestFestivalRecommend = true;
        AsyncHttpRequest postRequest = new AsyncHttpRequest(this, "GET");
        postRequest.execute(url);
    }

    public void requestPlaceAll()
    {
        String url = "https://seoulherechat.herokuapp.com/request/place/all";
        loadingMessage = R.string.splash_load_progress_all_content;
        isRequestPlaceAll = true;
        AsyncHttpRequest postRequest = new AsyncHttpRequest(this, "GET");
        postRequest.execute(url);
    }

    public void requestPredictionTrain()
    {
        String url = "https://seoulherepredition.herokuapp.com/train";
        loadingMessage = R.string.splash_load_progress_prediction_content;
        isRequestTrain = true;
        AsyncHttpRequest postRequest = new AsyncHttpRequest(this, "GET", true);     //train..
        postRequest.execute(url);
    }


    @Override
    public void onStartRequest() {
        String title = getResources().getString(R.string.splash_load_progress_title);
        String content = getResources().getString(loadingMessage);

        if(progressDialog == null)
        {
            progressDialog = ProgressDialog.show(this, title,
                    content, true);
        }else
        {
            if(progressDialog.isShowing() == true)
            {
                //no jobs..
                progressDialog.setMessage(content);
            }else {
                progressDialog = ProgressDialog.show(this, title,
                        content, true);
            }
        }

    }

    @Override
    public void onEndRequest(ChatMessage chatMessage) {

        //GPS가 성공인 경우..
        if(gpsResult == GPS_RESULT_SUCESS)
        {

            //추천 정보요청이 되지 않으면, 추천 정보 리스트를 요청한다.
            if(isRequestRefresh == false) {
                currentPlaceList = chatMessage.getPlaceModelArrayList();
                requestPlaceRefresh();
            }
            //추천 정보 요청이 완료 되었으면, 다음 요청으로 넘어간다.
            else
            {
                //행사 정보 요청이 되지 않으면, 행사요청을 한다.
                if(isRequestFestivalRecommend == false)
                {
                    recommendPlaceList = chatMessage.getPlaceModelArrayList();
                    requestRecommendFestival();
                }
                //행사 정보 요청이 완료 되었으면, 다음 요청을 한다.
                else
                {
                    //경로 주변검색을 위한 전체 명소를 가져오지 않았으면, 가져온다.
                    if(isRequestPlaceAll == false)
                    {
                        recommendFestivalList = chatMessage.getFestivalModelArrayList();
                        requestPlaceAll();

                    }
                    //그렇지 않으면 다음 요청을 한다.
                    else
                    {
                        //예측 서버 훈련 요청이 되지 않았으면, 훈련 요청을 한다.
                        if(isRequestTrain == false)
                        {
                            allPlaceList = chatMessage.getPlaceModelArrayList();
                            //ApplicationItem과 연동
                            ((AIApplication)getApplication()).getAppPlaceInfo().uniquePlaceModelList = allPlaceList;
                            requestPredictionTrain();
                        }else {

                            //마지막으로 모든 요청이 완료 되었으면, 메인 화면으로 인동한다.
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            startSiginActivity();
                        }
                    }
                }
            }
        }else
        {
            //GPS 실패였던 경우, 추천 정보를 가져온다..
            if(isRequestRefresh == true) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                recommendPlaceList = chatMessage.getPlaceModelArrayList();
                startSiginActivity();
            }
        }

    }

    @Override
    public void onPutEndRequest(int action) {

    }

    @Override
    public void onPutEndRequest(int action, ChatMessage chatMessage) {

    }


    public void startSiginActivity()
    {
        Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
        intent.putParcelableArrayListExtra("recommendPlaceList", recommendPlaceList);
        intent.putParcelableArrayListExtra("currentPlaceList", currentPlaceList);
        intent.putExtra("festivalList", recommendFestivalList);                     //Serialize Object ArrayList Putting

        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }

    //장소 검색 이 후 결과 화면으로 리턴한다..
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        System.out.println(">>>>>>>>>>>>>Result RequestCode ... : " + requestCode);
        if (requestCode == REQUEST_LOCATION_SETTING) {
            //다른 액티비티로 부터 받음 코드..
            if (resultCode == RESULT_OK) {      //-1

                System.out.println(">>>>>>>>>>>>>Result OK...");
                initCheckService();
            } else {
                System.out.println(">>>>>>>>>>>>>Result Else...");
                initCheckService();
            }
        }
    }
}

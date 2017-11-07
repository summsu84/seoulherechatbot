package com.chat.seoul.here;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chat.seoul.here.module.adapter.CustomViewPagerAdapter;
import com.chat.seoul.here.module.adapter.CustomViewPagerListener;
import com.chat.seoul.here.module.adapter.MainRecycleViewAdapter;
import com.chat.seoul.here.module.lib.common.AsyncHttpRequest;
import com.chat.seoul.here.module.lib.common.AsyncRequestListener;
import com.chat.seoul.here.module.lib.conf.NetworkUtil;
import com.chat.seoul.here.module.model.ChatMessage;
import com.chat.seoul.here.module.model.festival.FestivalModel;
import com.chat.seoul.here.module.model.place.PlaceModel;

import java.util.ArrayList;

import static com.chat.seoul.here.module.lib.conf.Common.HTTP_ACTION_PLACE_DET;
import static com.chat.seoul.here.module.lib.conf.NetworkUtil.TYPE_NOT_CONNECTED;


public class SignInActivity extends BaseActivity implements View.OnClickListener, CustomViewPagerListener, AsyncRequestListener {

    private static final String TAG = "SignInActivity";
    public static final int REQUEST_CHAT_ACTIVITY = 0;
    public static final int RESULT_FAIL = 2;
/*    private static final int RESULT_OK = 1;
    private static final int RESULT_FAIL = 0;*/


    private Button mSignInButton;

    //메인 이미지 가져오기
    private ViewPager mViewPager;
    private CustomViewPagerAdapter mCustomViewPagerAdapter;

    //주변 위치 가져오기
    private ViewPager mViewPagerCurrentPlace;
    private CustomViewPagerAdapter mCurrentPlaceCustomViewPagerAdapter;

    private Context context;

    //스위프 레이아웃
    //SwipeRefreshLayout mSwipeRefreshLayout;       //스위프 제거하기
    private ArrayList<PlaceModel> mCurrentPlaceList;
    private ArrayList<PlaceModel> mRecommendPlaceList;
    private ArrayList<FestivalModel> mFestivalList;

    //Festival
    private MainRecycleViewAdapter mFestivalAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mSignInButton = (Button) findViewById(R.id.button_sign_in);
        mSignInButton.setOnClickListener(this);

        Bundle initBurndle = getIntent().getExtras();
        //아래 부분 체크 필요 getExtras
        if(initBurndle != null) {
            mCurrentPlaceList = initBurndle.getParcelableArrayList("currentPlaceList");
            mRecommendPlaceList = initBurndle.getParcelableArrayList("recommendPlaceList");
            mFestivalList = (ArrayList<FestivalModel>) initBurndle.getSerializable("festivalList");
        }
        initViewPager();
     }

    public void initViewPager()
    {
        //뷰페이저 2개 및 리사이클 뷰를 초기화 한다.
        mViewPager = (ViewPager)this.findViewById(R.id.viewpagerMain);       //ViewPager 로딩
        mViewPagerCurrentPlace = (ViewPager)this.findViewById(R.id.viewpagerCurrentPlace);
        mRecyclerView = (RecyclerView) this.findViewById(R.id.recyclerviewMain);

        if(mCurrentPlaceList == null)
        {
            mCurrentPlaceCustomViewPagerAdapter = new CustomViewPagerAdapter(this, true);
        }else {
            mCurrentPlaceCustomViewPagerAdapter = new CustomViewPagerAdapter(this, mCurrentPlaceList, true);
        }
        if(mRecommendPlaceList == null)
        {
            mCustomViewPagerAdapter = new CustomViewPagerAdapter(this, true);
        }else {
            mCustomViewPagerAdapter = new CustomViewPagerAdapter(this, mRecommendPlaceList, true);
        }

        if(mFestivalList != null)
        {
            Log.i(TAG, "FestivalList : " + mFestivalList.size());
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            //mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(layoutManager);
            mFestivalAdapter = new MainRecycleViewAdapter(this, mFestivalList, 0);
            mRecyclerView.setAdapter(mFestivalAdapter);
        }else
        {
            Log.i(TAG, "FestivalList doesnt exist");
        }

        mViewPager.setAdapter(mCustomViewPagerAdapter);
        mViewPagerCurrentPlace.setAdapter(mCurrentPlaceCustomViewPagerAdapter);
    }

    private void signIn() {
        Log.d(TAG, "signIn");

        //네트워크 연결 확인 - 20170531 - JJW
        if(NetworkUtil.getConnectivityStatus(this) == TYPE_NOT_CONNECTED)
        {
            NetworkUtil.showSettingsAlert(this);
            return;
        }else {

            resultSignIn();
        }
    }


    private void resultSignIn(){


        System.out.println(">>>>>ResultSiginIn....");

        Intent intent2 = new Intent(getApplicationContext(), ChatActivity.class);
        Bundle extras = new Bundle();
        intent2.putExtras(extras);

        //Request Code
        startActivityForResult(intent2, REQUEST_CHAT_ACTIVITY);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHAT_ACTIVITY) {
            //다른 액티비티로 부터 받음 코드..
            if (resultCode == RESULT_OK) {

            } else if (resultCode == RESULT_FAIL) {
                Toast.makeText(SignInActivity.this, "봇과 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
            } else
            {
                // no jobs
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_sign_in) {
            //clickedChatType = 0;
            signIn();
        }
    }

    @Override
    public void onCustomViewPagerListenerStart() {

    }

    @Override
    public void onCustomViewPagerListenerEnd() {

    }

    public void clickDetailPage(String placeId)
    {
        String url = "https://seoulherechat.herokuapp.com/request/place/view/" + placeId;
        AsyncHttpRequest putRequest = new AsyncHttpRequest(this, "PUT", HTTP_ACTION_PLACE_DET);
        putRequest.execute(url);
    }


    @Override
    public void onStartRequest() {
        //no jobs
    }

    @Override
    public void onEndRequest(ChatMessage chatMessage) {
        //no jobs
    }

    @Override
    public void onPutEndRequest(int action) {
        //no jobs
    }

    @Override
    public void onPutEndRequest(int action, ChatMessage chatMessage) {
        //조회수를 증가 시킨 이 후 PlaceDetActivity로 이동한다.
        if(action == HTTP_ACTION_PLACE_DET)
        {
            PlaceModel clickedPlace = chatMessage.getPlaceModelArrayList().get(0);
            Intent intent = new Intent(this, PlaceDetActivity.class);
            intent.putExtra("clickedPlace", clickedPlace);
//        intent.putExtra("clickedPosition", pagerPosition);
            startActivity(intent);
        }
    }
}

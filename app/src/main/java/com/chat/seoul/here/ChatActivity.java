package com.chat.seoul.here;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chat.seoul.here.module.adapter.ChatAdapter;
import com.chat.seoul.here.module.adapter.ChatAdapterButtonListener;
import com.chat.seoul.here.module.lib.app.AIApplication;
import com.chat.seoul.here.module.lib.common.AsyncHttpRequest;
import com.chat.seoul.here.module.lib.common.AsyncRequestListener;
import com.chat.seoul.here.module.lib.common.PlaceParser;
import com.chat.seoul.here.module.lib.common.PlacePostAction;
import com.chat.seoul.here.module.lib.common.PlacePostActionListener;
import com.chat.seoul.here.module.lib.conf.LanguageConfig;
import com.chat.seoul.here.module.map.inf.GPSFinderListener;
import com.chat.seoul.here.module.model.ChatMessage;
import com.chat.seoul.here.module.model.festival.FestivalModel;
import com.chat.seoul.here.module.model.place.PlaceModel;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ai.api.AIServiceException;
import ai.api.RequestExtras;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.GsonFactory;
import ai.api.model.AIContext;
import ai.api.model.AIError;
import ai.api.model.AIEvent;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Metadata;
import ai.api.model.Result;
import ai.api.model.Status;

import static com.chat.seoul.here.R.layout.activity_chat;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_DEST_PLACE_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_DEST_PLACE_SEARCH_NO_ENTITY_INCOMPLETE;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_DETAIL_RECOMMEND_PLACE_SEARCH_INCOMPLETE;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_PATH_SEARCH_NO_ENTITY;
import static com.chat.seoul.here.module.lib.conf.Common.ACTION_PATH_SEARCH_NO_ENTITY_INCOMPLETE;
import static com.chat.seoul.here.module.lib.conf.Common.CHAT_MESSAGE_FESTIVAL;
import static com.chat.seoul.here.module.lib.conf.Common.CHAT_MESSAGE_PLACE;
import static com.chat.seoul.here.module.lib.conf.Common.HTTP_ACTION_PLACE_DET;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_CURRENT_PLACE_SEARCH;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_END;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_HELLO_PLACE_SUBMENU;
import static com.chat.seoul.here.module.lib.conf.Common.INTENT_UNKNOWN;
import static com.chat.seoul.here.module.lib.conf.Common.MSG_BUTTON_MENU;
import static com.chat.seoul.here.module.lib.conf.Common.MSG_RESPONSE_LOCATION_NO_SEOUL;
import static com.chat.seoul.here.module.lib.conf.Common.MSG_RESPONSE_LOCATION_NO_USE;
import static com.chat.seoul.here.module.lib.conf.Common.MSG_RESPONSE_PLACE_SUBMENU;
import static com.chat.seoul.here.module.lib.conf.Common.UI_TEXT_BUTTON;

/**
 * A Chat Screen Activity
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener, ChatAdapterButtonListener, GPSFinderListener, AsyncRequestListener, PlacePostActionListener {
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;   //검색 요청 코드
    private static final int REQUEST_PLACE_PATH_RESULT = 2;
    public static final String TAG = ChatActivity.class.getName();

    private EditText messageET;
    private ListView messagesContainer;
    private ImageButton sendBtn;

    private Gson gson = GsonFactory.getGson();
    private AIDataService aiDataService;


    private Button voiceBtn;
    private Button muteBtn;


    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    private Context chatCtx;

    private ProgressDialog progressDialog;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private String m_userId = "";
    private int mResponseActionType;
    boolean mStartedActivityFromFragment;

    //PostAction 처리
    private PlacePostAction placePostAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        chatCtx = this;
        setContentView(activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initControls();
        //LanguageConfig selectedLanguage = new LanguageConfig("ko", "95e5e537386b4cc08179a5720a4e9154");
        LanguageConfig selectedLanguage = new LanguageConfig("en", "c538e89975d34248bd4948e9e2fb1ad7");
        initService(selectedLanguage);

        //PostAction
        placePostAction = new PlacePostAction(this, this);
        sayHelloToClient();
    }


    private void initService(final LanguageConfig selectedLanguage) {
        final AIConfiguration.SupportedLanguages lang = AIConfiguration.SupportedLanguages.fromLanguageTag(selectedLanguage.getLanguageCode());
        final AIConfiguration config = new AIConfiguration(selectedLanguage.getAccessToken(),
                lang,
                AIConfiguration.RecognitionEngine.System);


        aiDataService = new AIDataService(this, config);
    }

    public void displayMessageString(String str)
    {
        //테스트
        ChatMessage message = new ChatMessage();
        message.setMessage(str);
        adapter.add(message);
        adapter.notifyDataSetChanged();
    }

    private void initControls() {


        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (ImageButton) findViewById(R.id.chatSendButton);
        voiceBtn = (Button) findViewById(R.id.chatTest);
        muteBtn = (Button) findViewById(R.id.chatVoice);


  /*      TextView meLabel = (TextView) findViewById(R.id.meLbl);
        if(m_userId.length() != 0) {
            meLabel.setText(m_userId);
        }*/
        //TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        //companionLabel.setText("Chat Bot");// Hard Coded



        //setClickListener
        sendBtn.setOnClickListener(this);

    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    public void displayMessage(ArrayList<ChatMessage> messageArrayList) {


    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void sayHelloToClient() {

        chatHistory = new ArrayList<ChatMessage>();

        ChatMessage msg = new ChatMessage();
        msg.setId(1);
        msg.setMe(true);
        msg.setMessage("하이");
        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        //msg.setViewPager(true);
        //viewpager test
        /*ArrayList<PlaceModel> placeModels = new ArrayList<PlaceModel>();
        for(int i = 0 ; i < 3 ; i++)
        {
            String text = "Test" + Integer.toString(i);
            PlaceModel message = new PlaceModel(text, "TEST");
            placeModels.add(message);
        }

        msg.setPlaceModelArrayList(placeModels);*/

        //chatHistory.add(msg);     //맨처음 인사말은 보여주지 않는다.

        adapter = new ChatAdapter(ChatActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        for (int i = 0; i < chatHistory.size(); i++) {
            ChatMessage message = chatHistory.get(i);
            displayMessage(message);
        }
        new SendMessageAsyncTask().execute("하이", "", "");
    }


    /**
     *  메시지를 전송한다.
     */
    private void sendRequest() {

        final String queryString = String.valueOf(messageET.getText());
        final String eventString = "";
        final String contextString = "";
        if (TextUtils.isEmpty(queryString) && TextUtils.isEmpty(eventString)) {
            onError(new AIError(getString(R.string.non_empty_query)));
            return;
        }

        String messageText = messageET.getText().toString();
        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(122);//dummy
        chatMessage.setMessage(messageText);
        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));;
        chatMessage.setMe(true);

        messageET.setText("");
        displayMessage(chatMessage);

        //나중에 이거 공통으로 묶자
        //int lastResponseActionType = chatMessage.getActionType();
        Log.i(TAG, "MResponseActinoType : " + mResponseActionType);
        if(mResponseActionType == ACTION_DEST_PLACE_SEARCH_NO_ENTITY_INCOMPLETE)
        {
            Log.i(TAG, "Action Dest Place No Entity send request..");
            //사용자는 특정 지점을 입력한다.
            //이것을 주소로 변환한다.
            String address = queryString;
            Location location = ((AIApplication)getApplication()).getAppPlaceInfo().uniqueGpsInfo.findGeoPoint(this, address);
            if(location == null || location.getLongitude() == 0 || location.getLatitude() == 0)
            {
                //위에 해당하는 경우, 값이 없는 경우..
                String content = getResources().getString(R.string.share_reg_address_not_found);
                /*Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
                return;*/
                displayMessage(generateChatMessage(content, false));
            }else {
                Log.i(TAG, "get Location : " + location);

                //서울인지 체크하자..----

                //현위치 제거하기..
                String x_Coord = Double.toString(location.getLatitude());
                String y_Coord = Double.toString(location.getLongitude());

                String tmpQueryString = "C:" + x_Coord + "_" + y_Coord;
                Log.i(TAG, "sendMessage : " + tmpQueryString);
                new SendMessageAsyncTask().execute(tmpQueryString, eventString, contextString);
            }

        }else if(mResponseActionType == ACTION_DETAIL_RECOMMEND_PLACE_SEARCH_INCOMPLETE)
        {
            String tmpValue = "";
            if("남".equals(queryString) || "남자".equals(queryString) || "네".equals(queryString))
            {
                tmpValue = Integer.toString(0);
            }else if("여".equals(queryString) ||"여자".equals(queryString) || "아니오".equals(queryString))
            {
                tmpValue = Integer.toString(1);
            }else {
                tmpValue = queryString;
            }

            new SendMessageAsyncTask().execute(tmpValue, eventString, contextString);
        }
        else
        {
            new SendMessageAsyncTask().execute(queryString, eventString, contextString);
        }


    }


    private void onResult(final AIResponse response) {


        //parsing..
        final Context self = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onResult");

                //resultTextView.setText(gson.toJson(response));

                Log.i(TAG, "Received success response");

                // this is example how to get different parts of result object
                final Status status = response.getStatus();
                Log.i(TAG, "Status code: " + status.getCode());
                Log.i(TAG, "Status type: " + status.getErrorType());

                final Result result = response.getResult();
                Log.i(TAG, "Resolved query: " + result.getResolvedQuery());

                Log.i(TAG, "Action: " + result.getAction());

                final String speech = result.getFulfillment().getSpeech();
                Log.i(TAG, "Speech: " + speech);
                //TTS.speak(speech);

                final Metadata metadata = result.getMetadata();
                if (metadata != null) {
                    Log.i(TAG, "Intent id: " + metadata.getIntentId());
                    Log.i(TAG, "Intent name: " + metadata.getIntentName());
                }

                final HashMap<String, JsonElement> params = result.getParameters();
                if (params != null && !params.isEmpty()) {
                    Log.i(TAG, "Parameters: ");
                    for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                        Log.i(TAG, String.format("%s: %s", entry.getKey(), entry.getValue().toString()));
                    }
                }

                mResponseActionType = 0;
                //Chat Message 생성
                if("206".equals(status.getCode()))
                {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setId(122);//dummy
                    chatMessage.setMessage("서버의 응답 시간이 초과되었습니다. 다시 질문 해주세요..");
                    chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                    chatMessage.setMe(false);
                    chatMessage.setViewPager(false);
                    displayMessage(chatMessage);
                    //placePostAction.generatePostAction(mResponseActionType);
                }else {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setId(122);//dummy
                    chatMessage.setMessage(speech);
                    chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                    chatMessage.setMe(false);


                    //mResponseActionType = PlaceParser.getInstance().parseFromAPIAI(result.getFulfillment().getData(), metadata.getIntentName(), result.getAction(), result.isActionIncomplete(), chatMessage);
                    mResponseActionType = PlaceParser.getInstance().parseFromAPIAI(result, chatMessage);


                    //context를 체크 하자.
                    if(metadata.getIntentName().equals("PositiveAnswer"))
                    {
                        String contextPlaceParam;
                    }

                    chatMessage.setActionType(mResponseActionType);         //메시지의 ActionType을 지정한다.
                    Log.i(TAG, "SpeechMessage : " + chatMessage.getMessage() + ", ActionType : " + mResponseActionType);

                    if(chatMessage.getMessage().length() > 0)
                        displayMessage(chatMessage);

                    //주요 ACTION들은 앞에서 처리 하고, 나머지 POST ACTION은 POST ACTION 클래스를 이용하자..
                    if(mResponseActionType == ACTION_PATH_SEARCH_NO_ENTITY)
                    {
                        String startLocation = "";
                        String destinationLocation = "";
                        if (params != null && !params.isEmpty()) {
                            startLocation = params.get("StartLocation").toString();
                            destinationLocation = params.get("DestLocation").toString();

                            if(startLocation.length() > 0 && destinationLocation.length() > 0)
                            {
                                requestPathPlaceSearch(mResponseActionType, startLocation, destinationLocation, chatMessage.getMeesageIntentType());
                            }
                        }else
                        {
                            //출도착지 관련 정보 없음을 제공..
                        }
                    }
                    //목적지 싱글톤인 경우, 우선 디바이스에서 주소 변환을 하자..
                    else if(mResponseActionType == ACTION_DEST_PLACE_SEARCH)
                    {
                        String destinationLocation = null;
                        if (params != null && !params.isEmpty()) {
                            destinationLocation = params.get("DestLocation").toString();

                            if(destinationLocation != null)
                            {
                                //Location 정보를 가져오자..
                                Location location = ((AIApplication)getApplication()).getAppPlaceInfo().uniqueGpsInfo.findGeoPoint(chatCtx, destinationLocation);
                                if(location == null || location.getLongitude() == 0 || location.getLatitude() == 0)
                                {
                                    //위에 해당하는 경우, 값이 없는 경우..
                                    String content = getResources().getString(R.string.share_reg_address_not_found);
                                    Toast.makeText(chatCtx, content, Toast.LENGTH_SHORT).show();

                                    new SendMessageAsyncTask().execute("아니요", "", "");
                                }else {
                                    Log.i(TAG, "get Location : " + location);
                                    //현위치 제거하기..
                                    String x_Coord = Double.toString(location.getLatitude());
                                    String y_Coord = Double.toString(location.getLongitude());
                                    //String queryString = "C:" + Double.parseDouble(String.valueOf(placeLatLng.latitude)) + "_" + Double.parseDouble(String.valueOf(placeLatLng.longitude));
                                    //String tmpQueryString = "C:" + x_Coord + "_" + y_Coord;
                                    String tmpQueryString = x_Coord + " " + y_Coord;
                                    Log.i(TAG, "sendMessage : " + tmpQueryString);
                                    new SendMessageAsyncTask().execute(tmpQueryString, "", "");
                                }
                            }
                        }else
                        {
                            //출도착지 관련 정보 없음을 제공..
                            new SendMessageAsyncTask().execute("아니요", "", "");
                        }
                    }
                    else {
                        placePostAction.generatePostAction(mResponseActionType, chatMessage.getMeesageIntentType());
                    }
                }
            }

        });
    }

    private void onError(final AIError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //resultTextView.setText(error.toString());
                System.out.println(error.toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chatSendButton:
                sendRequest();
                break;
        }
    }


    /**
     *  대화 창에서 버튼 클릭 시 처리 함수
     * @param buttonMessage             버튼에 현시된 메시지
     * @param meesageIntentType        해당 메시지의 IntentType (버튼이 현시되었던 소스)
     */
    @Override
    public void onChatAdapterButtonClicked(String buttonMessage, int meesageIntentType) {
        //이부분은 추후 하나의 클래스로 통합예정

        switch (meesageIntentType)
        {
            //의도가 현재 위치 찾기 인 경우
            case INTENT_CURRENT_PLACE_SEARCH :

                //현재 위치 사용하기 클릭 시
                if("사용하기".equals(buttonMessage))
                {
                    ((AIApplication)getApplication()).getAppPlaceInfo().uniqueGpsInfo.setContext(chatCtx);
                    ((AIApplication)getApplication()).getAppPlaceInfo().uniqueGpsInfo.getGPSEnabled();
                    displayMessage(generateChatMessage(buttonMessage, true));
                }else
                {
                    //메뉴로 이동한다..
                    displayMessage(generateChatMessage(MSG_RESPONSE_LOCATION_NO_USE, false));
                    new SendMessageAsyncTask().execute(MSG_BUTTON_MENU[0], "", "");
                }
                break;
            case INTENT_UNKNOWN :
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://seoulherechat.herokuapp.com/request/reg/intent"));
                startActivity(intent);
                displayMessage(generateChatMessage(buttonMessage, true));
                break;

            // 한번의 대화 트랜잭션이 끝난 경우..
            case INTENT_END :
                //new SendMessageAsyncTask().execute(MSG_BUTTON_MENU[0], "", "");
                new SendMessageAsyncTask().execute(buttonMessage, "", "");
                displayMessage(generateChatMessage(buttonMessage, true));
                break;
            default:
                new SendMessageAsyncTask().execute(buttonMessage, "", "");
                //메시지 버튼은 조건에 따라서 조절 한다..
                displayMessage(generateChatMessage(buttonMessage, true));
            break;
        }



    }

    /**
     *  메시지를 전송한다.
     * @param msg
     */
    @Override
    public void onChatAdapterSendMessage(String msg) {
        displayMessage(generateChatMessage(msg, true));
        new SendMessageAsyncTask().execute(msg, "", "");
    }

    @Override
    public void onChatAdapterFindPlace() {
        openAutocompleteActivity();
    }

    /**
     *  명소 검색의 서브 타이틀을 제공한다..
     */
    @Override
    public void onChatAdapterResponsePlaceSubButton() {

        ChatMessage chatMessage = generateChatMessage(MSG_RESPONSE_PLACE_SUBMENU, false);
        chatMessage.setMessageUiType(UI_TEXT_BUTTON);
        chatMessage.setMeesageIntentType(INTENT_HELLO_PLACE_SUBMENU);
        displayMessage(chatMessage);
    }

    public ChatMessage generateChatMessage(String message, boolean isMe)
    {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(123);//dummy
        chatMessage.setMessage(message);
        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));;
        chatMessage.setMe(isMe);

        return chatMessage;
    }

    @Override
    public void onChatAdapterFindOriginDestPathPlace() {

    }

    @Override
    public void onChatAdapterUIButtonClicked(String msg) {

        String displayMsg = "";
        if("행사찾기".equals(msg))
        {
            displayMsg = "네";
        }else if("아니요".equals(msg))
        {
            displayMsg = "아니요";
        }
        displayMessage(generateChatMessage(displayMsg, true));
        new SendMessageAsyncTask().execute(displayMsg, "", "");

    }

    /**
     * 명소 전체 보기 클릭 시
     * @param viewPagerPlaceItems
     */
    @Override
    public void onChatAdapterViewPagerAdapterAllListPlaceButtonClicked(ArrayList<PlaceModel> viewPagerPlaceItems) {
        Intent intent = new Intent(this, MapSearchDetailActivity.class);
        try {
            intent.putParcelableArrayListExtra("placeItems", viewPagerPlaceItems);
            intent.putExtra("sourceX_Coord", viewPagerPlaceItems.get(0).getSourceX_Coord());
            intent.putExtra("sourceY_Coord", viewPagerPlaceItems.get(0).getSourceY_Coord());
            intent.putExtra("sourceAround", viewPagerPlaceItems.get(0).getSourceAround());
            intent.putExtra("placeType", CHAT_MESSAGE_PLACE);
            intent.putExtra("responseActionType", viewPagerPlaceItems.get(0).getResponseAction());
        }catch (NullPointerException e)
        {
            e.printStackTrace();
        }

        startActivity(intent);
    }

    //명소 자세히 보기 클릭시
    @Override
    public void onChatAdapterViewPagerAdapterPlaceDetailButtonClicked(PlaceModel viewPagerPlaceItem) {

        //조회수를 증가 시킨다.
        PlaceModel clickedPlace = viewPagerPlaceItem;
        String url = "https://seoulherechat.herokuapp.com/request/place/view/" + viewPagerPlaceItem.getPLACE_ID();
        AsyncHttpRequest putRequest = new AsyncHttpRequest(this, "PUT", HTTP_ACTION_PLACE_DET);
        putRequest.execute(url);

    }

    //행사 정보 자세히 보기 클릭시
    @Override
    public void onChatAdapterViewPagerAdapterFestivalDetailButtonClicked(FestivalModel festivalModel) {
        String url = festivalModel.getFESTIVAL_LINK();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(festivalModel.getFESTIVAL_LINK()));
        startActivity(intent);
    }

    //행사 정보 전체 보기 클릭 시
    @Override
    public void onChatAdapterViewPagerAdapterAllListFestivalButtonClicked(ArrayList<FestivalModel> viewPagerFestivalItems) {


/*        intent.putExtra("sourceX_Coord", viewPagerPlaceItems.get(0).getSourceX_Coord());
        intent.putExtra("sourceY_Coord", viewPagerPlaceItems.get(0).getSourceY_Coord());
        intent.putExtra("sourceAround", viewPagerPlaceItems.get(0).getSourceAround());*/

        Intent intent = new Intent(this, MapSearchDetailActivity.class);
        try {

            intent.putExtra("festivalItems", viewPagerFestivalItems);
            intent.putExtra("placeType", CHAT_MESSAGE_FESTIVAL);
            intent.putExtra("sourceX_Coord", viewPagerFestivalItems.get(0).getSourceX_Coord());
            intent.putExtra("sourceY_Coord", viewPagerFestivalItems.get(0).getSourceY_Coord());
        }catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        startActivity(intent);
    }

    //나중에 리스너로 처
    private void openAutocompleteActivity() {
        //장소명을 가져온다..
        Intent intent = new Intent(this, MainSrcActivity.class);
        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
    }

    //장소 검색 이 후 결과 화면으로 리턴한다..
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.

        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            //다른 액티비티로 부터 받음 코드..
            if (resultCode == RESULT_OK) {      //-1
                // Get the user's selected place from the Intent.
                String placeName = data.getStringExtra("placeName");
                String placeAddress = data.getStringExtra("placeAddress");
                LatLng placeLatLng = (LatLng)(data.getParcelableExtra("placeLatLng"));

                System.out.println("----------placeLatLng : " + placeLatLng);
                try{
                    if (!placeAddress.contains("서울")) {
                        //서울 특별시가 아니면 메시지를 출력한다.
                        /*String content = getResources().getString(R.string.main_not_seoul);
                        Toast.makeText(this, content,
                                Toast.LENGTH_SHORT).show();*/
                        ChatMessage chatMessage = generateChatMessage(MSG_RESPONSE_LOCATION_NO_SEOUL, false);
                        chatMessage.setActionType(ACTION_DEST_PLACE_SEARCH_NO_ENTITY_INCOMPLETE);
                        chatMessage.setMessageUiType(UI_TEXT_BUTTON);
                        displayMessage(chatMessage);



                    } else {
                        ChatMessage chatMessage = new ChatMessage();
                        chatMessage.setId(123);//dummy
                        //chatMessage.setMessage(messageText);
                        chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));;
                        chatMessage.setMe(true);
                        chatMessage.setMessage(placeName);

                        displayMessage(chatMessage);
                        String queryString = "C:" + Double.parseDouble(String.valueOf(placeLatLng.latitude)) + "_" + Double.parseDouble(String.valueOf(placeLatLng.longitude));
                        if(mResponseActionType == ACTION_PATH_SEARCH_NO_ENTITY_INCOMPLETE)
                        {
                            new SendMessageAsyncTask().execute(placeName, "", "");
                        }else {
                            new SendMessageAsyncTask().execute(queryString, "", "");
                        }

                    }
                }catch(NullPointerException e)
                {
                    e.printStackTrace();
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                // no jobs
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }else if(requestCode == REQUEST_PLACE_PATH_RESULT)
        {
            //  no jobs
            /*if(resultCode != 0) {
                new SendMessageAsyncTask().execute("하이", "", "");
            }*/
            new SendMessageAsyncTask().execute("하이", "", "");
        }/*else if(requestCode == REQUEST_MAIN_PLACE)
        {
            //메인화면에서 이달의 추천장소 정보 요청후, 목적지 등록이 클릭된 경우..
            // no jobs
            if(resultCode != 0) {
                String placeName = data.getStringExtra("placeName");
                String placeAddr = data.getStringExtra("placeAddress");
                LatLng placeLatLng = (LatLng)(data.getParcelableExtra("placeLatLng"));
                mInputPlaceDestination.setText(placeAddr);

                mSearchPlaceInfoDestination.placeAddress = placeAddr;
                mSearchPlaceInfoDestination.placeName = placeName;
                mSearchPlaceInfoDestination.placeLatLng = placeLatLng;
            }
        }*/
    }

    @Override
    public void onGPSFinderSuccess() {
        System.out.println("-----GPSFinder Success!-----");
        LatLng mOriginLatLng;
        try{
            //위치 정보 얻기
            Location currentLocation = ((AIApplication)getApplication()).getAppPlaceInfo().uniqueGpsInfo.getLocation();
            String currentLatitude = Double.toString(currentLocation.getLatitude());
            String currentLongitude = Double.toString(currentLocation.getLongitude());
            String url = "https://seoulherechat.herokuapp.com/request/place/current";

            //AsyncHttpRequest postRequest = new AsyncHttpRequest(this, "POST");
            //postRequest.execute(currentLatitude, currentLongitude, url, "1.0");
            Log.e(TAG, "Current Address : " + currentLatitude + ", " + currentLongitude);

            String queryString = currentLatitude + " " + currentLongitude;

            new SendMessageAsyncTask().execute(queryString, "", "");


        }catch(NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onGPSFinderFailed() {
        System.out.println("-----GPSFinder Failed!------");
    }

    public String getGeoLocation(double lat, double lng){
        String str = null;
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);

        List<Address> address;
        try {
            if (geocoder != null) {
                address = geocoder.getFromLocation(lat, lng, 1);
                if (address != null && address.size() > 0) {
                    str = address.get(0).getAddressLine(0).toString();
                }
            }
        } catch (IOException e) {
            Log.e("Main3Activity", "주소를 찾지 못하였습니다.");
            Toast.makeText(ChatActivity.this, "주소를 찾지 못하였습니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return str;

    }

    @Override
    public void onStartRequest() {

    }

    /**
     *  해당 메서드는 웹 서비스를 통해서 생성된 메시지를 처리한다.
     * @param chatMessage
     */
    @Override
    public void onEndRequest(ChatMessage chatMessage) {

        Log.i(TAG, "OnEndHTTP Request.. chatMessage : " + chatMessage + ", chatMessageActionType : " + chatMessage.getActionType());
        displayMessage(chatMessage);
        //추가 작업
        int responseActionType = chatMessage.getActionType();
        placePostAction.generatePostAction(responseActionType, chatMessage.getMeesageIntentType());

    }

    @Override
    public void onPutEndRequest(int action) {

        //조회수를 증가 시킨 이 후 PlaceDetActivity로 이동한다.




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

    /**
     *
     * @param message
     */
    @Override
    public void onDisplayMessage(ChatMessage message) {


    }

    @Override
    public void onSendMessage(String method, String lat, String lon, String url, String version) {

        AsyncHttpRequest postRequest = new AsyncHttpRequest(this, method);
        postRequest.execute(lat, lon, url, version);
        Log.e(TAG, "Current Address : " + lat + ", " + lon);
    }

    @Override
    public void onDisplayMessage(String s, boolean b, int uiType, int intentType, int actionType) {

        ChatMessage message = generateChatMessage(s, b);
        message.setMessageUiType(uiType);
        message.setMeesageIntentType(intentType);
        message.setActionType(actionType);

        displayMessage(message);
        //END INTENT 인 경우, 키보드 해제 하자
        if(intentType == INTENT_END)
            hideKeyboardwithoutPopulate(this);
    }

    public static void hideKeyboardwithoutPopulate(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onRequestPathPlaceSearch() {

        //경로 주변 검색 정보를 현시한다.

    }

    public void requestPathPlaceSearch(int responseActionType, String startLocation, String destinationLocation, int intentType)
    {
        Intent intent2 = new Intent(getApplicationContext(), MapSearchDetailActivity.class);

        Bundle extras = new Bundle();
        //먼저 위경도가있는지 확인한다.
        extras.putInt("responseActionType", responseActionType);
        extras.putString("origin", startLocation);
        extras.putString("originName", startLocation);

        extras.putString("destination", destinationLocation);
        extras.putString("destinationName", destinationLocation);

       /* if(mSearchPlaceInfoOrigin.placeLatLng == null) {
            extras.putString("origin", mInputPlaceOrigin.getText().toString());
            extras.putString("originName", mInputPlaceOrigin.getText().toString());
        }else
        {
            extras.putString("origin", mSearchPlaceInfoOrigin.getPlaceLatLng());
            extras.putString("originName", mInputPlaceOrigin.getText().toString());
        }

        if(mSearchPlaceInfoDestination.placeLatLng == null) {
            extras.putString("destination", mInputPlaceDestination.getText().toString());
            extras.putString("destinationName", mInputPlaceDestination.getText().toString());
        }else{
            extras.putString("destination", mSearchPlaceInfoDestination.getPlaceLatLng());
            extras.putString("destinationName", mInputPlaceDestination.getText().toString());
        }*/

        // 위에서 만든 Bundle을 인텐트에 넣는다.
        intent2.putExtras(extras);

        //startActivity(intent2);
        startActivityForResult(intent2, REQUEST_PLACE_PATH_RESULT);
    }


    //API.AI로 메시지 전송
    private class SendMessageAsyncTask extends AsyncTask<String, Void, AIResponse>
    {

        private AIError aiError;

        @Override
        protected AIResponse doInBackground(final String... params) {
            final AIRequest request = new AIRequest();
            String query = params[0];
            String event = params[1];

            Log.i(TAG, "SendMessage : " + query);

            if (!TextUtils.isEmpty(query))
                request.setQuery(query);
            if (!TextUtils.isEmpty(event))
                request.setEvent(new AIEvent(event));
            final String contextString = params[2];
            RequestExtras requestExtras = null;
            if (!TextUtils.isEmpty(contextString)) {
                final List<AIContext> contexts = Collections.singletonList(new AIContext(contextString));
                requestExtras = new RequestExtras(contexts, null);
            }

            try {
                return aiDataService.request(request, requestExtras);
            } catch (final AIServiceException e) {
                aiError = new AIError(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final AIResponse response) {
            if (response != null) {
                onResult(response);
            } else {
                onError(aiError);
            }
        }
    };
/*
//Prediction 서버로 전송
*//**
 *  댓글을 작성하는 비동기 클래스
 *//*
    private class PredictionRequestClass extends AsyncTask<String, String, String > {

        ProgressDialog wDialog;
        String UrlText = "https://seoulherepredition.herokuapp.com/predict";

        protected void onPreExecute()
        {
            super.onPreExecute();
            wDialog = new ProgressDialog(ChatActivity.this);
            wDialog.setMessage("예측모델링과 연결중입니다.");
            wDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            System.out.println("----Param : " + params[0] + ", --Parms1 : " + params[1] + ", params2 : " + params[2]);

            if(params[0] == null){
                return null;
            }
            String age = params[0];
            String sex = params[1];
            String comp = params[2];
            String baby = params[3];
            String visitCnt = params[4];

            String responseValue = null;
            URL url = null;


            JSONObject jsonObject = new JSONObject();
            JSONObject requestData = new JSONObject();
            try {
                jsonObject.put("Age", age);
                jsonObject.put("Sex", sex);
                jsonObject.put("Companion", comp);
                jsonObject.put("Baby", baby);
                jsonObject.put("VisitSeoul", visitCnt);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject);

            String postData = jsonArray.toString();

            try {
                url = new URL(UrlText);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Content-Length", "" + postData.getBytes().length);
                OutputStream out = urlConnection.getOutputStream();
                out.write(postData.getBytes());
                int responseCode = urlConnection.getResponseCode(); //can call this instead of con.connect()
                if (responseCode >= 400 && responseCode <= 499) {
                    throw new Exception("Bad authentication status: " + responseCode); //provide a more meaningful exception message
                } else {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    responseValue = readStream(in);
                    Log.w("responseSendMsgToBot ", responseValue);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return responseValue;


        }

        protected void onPostExecute(String responseValue) {

            //parsing 한다..
            wDialog.dismiss();
            System.out.println(">>>>onPoastExcution...reponseValue : " + responseValue);

            String url = "https://seoulherechat.herokuapp.com/request/place/view/" + viewPagerPlaceItem.getPLACE_ID();
            AsyncHttpRequest putRequest = new AsyncHttpRequest(this, "PUT", HTTP_ACTION_PLACE_DET);
            putRequest.execute(url);

            //new PlaceDetActivity.LoadCommentClass().execute(placeModel.getPLACE_ID());

        }
    }


    private String readStream(InputStream in) {

        char[] buf = new char[2048];
        Reader r = null;
        try {
            r = new InputStreamReader(in, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuilder s = new StringBuilder();
        while (true) {
            int n = 0;
            try {
                n = r.read(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (n < 0)
                break;
            s.append(buf, 0, n);
        }

        Log.w("streamValue",s.toString());
        return s.toString();
    }*/

}


/***
 *  상세 보기 정보
 */
package com.chat.seoul.here;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.seoul.here.module.adapter.CommentRecycleViewAdapter;
import com.chat.seoul.here.module.adapter.CustomViewPagerAdapter;
import com.chat.seoul.here.module.adapter.CustomViewPagerListener;
import com.chat.seoul.here.module.lib.app.AIApplication;
import com.chat.seoul.here.module.model.comment.CommentModel;
import com.chat.seoul.here.module.model.place.PlaceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class PlaceDetActivity extends BaseActivity implements CustomViewPagerListener {

    //layout 관련
    private ViewPager viewPager;
    private CustomViewPagerAdapter customViewPagerAdapter;

    //TextView
    private TextView mPlaceName;
    private TextView mPlaceDesc;

    //Comment Write
    private EditText mEditAuthor;
    private EditText mEditComment;
    private Button mBtnCommentSend;

    //RankDialog
    private Dialog rankDialog;
    private RatingBar ratingBar;

    private Bitmap mBitmap;
    private float useRankValue;
    //ClickedPlace
    private PlaceModel placeModel;
    private int mPlaceType;
    private boolean mIsMain;
    private ProgressDialog pDialog;

    //댓글 관련
    private RecyclerView mCommentListView;

    //private ListView mCommentListView;
    private CommentRecycleViewAdapter mCommentListViewAdapter;
    private Context self;

    private final String COMMENT_ENDPOINT = "https://seoulherechat.herokuapp.com/request/place/comments";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_det);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();
        int position = 0;
        if(bundle != null)
        {
            placeModel = bundle.getParcelable("clickedPlace");
            mIsMain = bundle.getBoolean("isMain", false);
        }

        if(mIsMain == false)
        {
            Button btnRegi = (Button) this.findViewById(R.id.btnSearchDetRegister);
            btnRegi.setVisibility(View.GONE);
        }else
        {
            /*Button btnRegi = (Button) this.findViewById(R.id.btnSearchDetRegister);
            btnRegi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("placeName", placeModel.getPLACE_NAME());
                    intent.putExtra("placeAddress", placeModel.getADDR());
                    LatLng placeLatLng = new LatLng(placeModel.getPLACE_COORD().getX_COORD(), placeModel.getPLACE_COORD().getY_COORD());
                    intent.putExtra("placeLatLng", placeLatLng);
                    setResult(1, intent);
                    finish();
                }
            });*/

        }

        this.self = this;
        //뷰 페이저 아래 레이아웃..
        mPlaceName = (TextView)this.findViewById(R.id.txtSearchDetPlaceName);
        mPlaceName.setText(placeModel.getPLACE_NAME());
        useRankValue = 1;
        initLayout();
        viewPager = (ViewPager)this.findViewById(R.id.viewpager);       //ViewPager 로딩
        customViewPagerAdapter = new CustomViewPagerAdapter(this, placeModel, false);
        viewPager.setAdapter(customViewPagerAdapter);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    public void initLayout()
    {
        //Comment 숨김처리
        mCommentListView = (RecyclerView) findViewById(R.id.listviewComment);
        mEditComment = (EditText) findViewById(R.id.editComment);
        mEditAuthor =  (EditText) findViewById(R.id.editCommentAuthor);
        mBtnCommentSend  = (Button) findViewById(R.id.btnCommentSubmit);
        // mCommentListView.setVisibility(View.GONE);

        //래크 평가하기 버튼클릭시
        Button btnRank = (Button)this.findViewById(R.id.btnSearchDetRate);
        btnRank.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                rankDialog = new Dialog(PlaceDetActivity.this, R.style.FullHeightDialog);
                rankDialog.setContentView(R.layout.rank_dialog);
                rankDialog.setCancelable(true);
                ratingBar = (RatingBar)rankDialog.findViewById(R.id.dialog_ratingbar);
                ratingBar.setRating(useRankValue);

                TextView text = (TextView)rankDialog.findViewById(R.id.rank_dialog_text1);
                text.setText("평가하기");

                Button updateButton = (Button)rankDialog.findViewById(R.id.rank_dialog_button);
                updateButton.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        rankDialog.cancel();
                        ((AIApplication)getApplication()).createOkDialog(PlaceDetActivity.this, getResources().getString(R.string.common_information),
                                getResources().getString(R.string.share_det_restriction_vote));
                        //여기 알람 메시지 호출..
                    }
                });

                rankDialog.show();
            }
        });


        //공유 버튼클릭시
        Button btnShare = (Button) this.findViewById(R.id.btnSearchDetShare);
        btnShare.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String share = "명소명:" + placeModel.getPLACE_NAME() + "\n" + "주소:" + placeModel.getADDR();
                if(false)
                {
                    if("null".equals(placeModel.getHOMEPAGE())){
                        share = share + "\n" + "홈페이지:정보없음";
                    }else {
                        share = share + "\n" + "홈페이지:" + placeModel.getHOMEPAGE();
                    }
                }
                Intent msg = new Intent(Intent.ACTION_SEND);
                msg.addCategory(Intent.CATEGORY_DEFAULT);
                msg.putExtra(Intent.EXTRA_SUBJECT, "주제");
                msg.putExtra(Intent.EXTRA_TEXT, share);
                msg.putExtra(Intent.EXTRA_TITLE, "제목");
                msg.setType("text/plain");

                startActivity(Intent.createChooser(msg, "공유"));
            }
        });

        Button btnMap = (Button) this.findViewById(R.id.btnSearchDetMap);
        btnMap.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //구글 지도 띄우기..
                Intent intent = new Intent(PlaceDetActivity.this, MapsActivity.class);
                intent.putExtra("clickedPlace", placeModel);
                // 액티비티를 생성한다.
                PlaceDetActivity.this.startActivity(intent);
            }
        });

        TextView txtReply =  (TextView)findViewById(R.id.txtSearchDetReply);
        txtReply.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                //댓글 데이터 가져오기..
                new LoadCommentClass().execute(placeModel.getPLACE_ID());

            }
        });

        //댓글을 전송한다..
        mBtnCommentSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //edit 박스를 사이즈를 체크 한다.
                int msgCount = mEditComment.getText().toString().length();
                if(msgCount == 0)
                {
                    Toast.makeText(self, "메시지를 입력 해주 세요..", Toast.LENGTH_SHORT).show();
                }else
                {
                    //Put 수행
                    String author = (mEditAuthor.getText().toString().length() > 0) ? mEditAuthor.getText().toString() : "테스터";
                    String message = mEditComment.getText().toString();
                    new WriteCommentClass().execute(placeModel.getPLACE_ID(), message, author);
                }

            }
        });
    }

    public void setDetailContents()
    {
//        if(mPlaceType == BASIC_PLACE) {
            //Code Name
        try {


            //View Count

            TextView viewCount = (TextView) this.findViewById(R.id.txtViewPlaceDetViewCnt);
            viewCount.setText(Integer.toString(placeModel.getVIEW_CNT()));

            //comment Count
            TextView commentCount = (TextView) this.findViewById(R.id.txtViewPlaceDetComment);
            commentCount.setText(Integer.toString(placeModel.getCOMMENT_CNT()));

            //Code Class
            TextView codeName = (TextView) this.findViewById(R.id.txtSearchDetCodeNameContent);
            codeName.setText((placeModel.getCODENAME() == null || placeModel.getCODENAME().equals("null")) ? "정보 없음 " : (placeModel.getCODENAME()));


            //Etc Desc
            TextView etcDesc = (TextView) this.findViewById(R.id.txtSearchDetEtc_DescContent);
            etcDesc.setText((placeModel.getPLACE_DESC() == null || placeModel.getPLACE_DESC().equals("null")) ? "정보 없음 " : (placeModel.getPLACE_DESC()));
            //Seat

            TextView seat = (TextView) this.findViewById(R.id.txtSearchDetSeat_CntContent);
            seat.setText((placeModel.getSEAT_CNT() == null || placeModel.getSEAT_CNT().equals("null")) ? "정보 없음 " : (placeModel.getSEAT_CNT()));
            //PhoneNumber
            TextView phone = (TextView) this.findViewById(R.id.txtSearchDetEtc_DescPhneContent);
            phone.setText((placeModel.getPHONE() == null || placeModel.getPHONE().equals("null")) ? "정보 없음 " : (placeModel.getPHONE()));

            //openHour
            TextView openHour = (TextView) this.findViewById(R.id.txtSearchDetOpenhourContent);
            openHour.setText((placeModel.getOPEN_HOUR() == null || placeModel.getOPEN_HOUR().equals("null")) ? "정보 없음 " : (placeModel.getOPEN_HOUR()));

            //Free
            TextView free = (TextView) this.findViewById(R.id.txtSearchDetEntrfreeContent);
            free.setText((placeModel.getENTR_FEE() == null || placeModel.getENTR_FEE().equals("null")) ? "정보 없음 " : (placeModel.getENTR_FEE()));

            //Entry Fee
            TextView entryFee = (TextView) this.findViewById(R.id.txtSearchDetEntrfreeContent);
            entryFee.setText((placeModel.getENTR_FEE() == null || placeModel.getENTR_FEE().equals("null")) ? "정보 없음 " : (placeModel.getENTR_FEE()));

            //CloseDay
            TextView closeDay = (TextView) this.findViewById(R.id.txtSearchDetClosedayContent);
            closeDay.setText((placeModel.getCLOSE_DAY() == null|| placeModel.getCLOSE_DAY().equals("null")) ? "정보 없음 " : (placeModel.getCLOSE_DAY()));

            //OpenDay
            TextView openDay = (TextView) this.findViewById(R.id.txtSearchDetOpen_DayContent);
            openDay.setText((placeModel.getOPEN_DAY() == null || placeModel.getOPEN_DAY().equals("null")) ? "정보 없음 " : (placeModel.getOPEN_DAY()));


            TextView addr = (TextView) this.findViewById(R.id.txtSearchDetAddrContent);
            addr.setText((placeModel.getADDR() == null || placeModel.getADDR().equals("null")) ? "정보 없음 " : (placeModel.getADDR()));

            //CloseDay
            TextView hp = (TextView) this.findViewById(R.id.txtSearchDetHomepageContent);
            if(placeModel.getHOMEPAGE() == null || placeModel.getHOMEPAGE().equals("null"))
            {
                hp.setText("정보 없음");
            }else
            {
                hp.setText(placeModel.getHOMEPAGE());
                Linkify.addLinks(hp, Linkify.WEB_URLS);
            }
               // hp.setText((placeModel.getHOMEPAGE().isEmpty() || placeModel.getHOMEPAGE().equals("null")) ? "정보 없음 " : Html.fromHtml("<b>"+placeModel.getHOMEPAGE()+"</b>"));
        }catch(NullPointerException e)
        {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void onCustomViewPagerListenerStart() {
        pDialog = new ProgressDialog(PlaceDetActivity.this);
        pDialog.setMessage("잠시만 기다려 주세요..");
        pDialog.show();
    }

    @Override
    public void onCustomViewPagerListenerEnd() {

        pDialog.dismiss();
        setDetailContents();
    }

    //이미지 불러오기.. 아래 방식은 사용하지 않는다.. 20170905
    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        ProgressDialog pDialog;

        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(PlaceDetActivity.this);
            pDialog.setMessage("잠시만 기다려 주세요..");
            pDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            System.out.println("----Param : " + params[0]);
            Bitmap bitmap = null;
            try{

                if("null".equals(params[0]) || params[0] ==null)
                {
                    bitmap = BitmapFactory.decodeResource(PlaceDetActivity.this.getResources(), R.drawable.img_no_photo);
                }else {
                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(params[0]).getContent());
                }

            }catch(Exception e)
            {
                e.printStackTrace();
            }

            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if (image != null) {
                customViewPagerAdapter.setBitmaps(image);
                pDialog.dismiss();

            } else {
                pDialog.dismiss();
                Toast.makeText(PlaceDetActivity.this, "이미지가 존재하지 않습니다.",
                        Toast.LENGTH_SHORT).show();

            }
            PlaceDetActivity.this.setDetailContents();
        }
    }


    //댓글 불러오기 AsyncInnerClass 방식
    private class LoadCommentClass extends AsyncTask<String, String, String > {

        ProgressDialog pDialog;
        //String UrlText = "https://seoulherechat.herokuapp.com/request/place/comments/";
        String UrlText = COMMENT_ENDPOINT;

        protected void onPreExecute()
        {
            super.onPreExecute();
            pDialog = new ProgressDialog(PlaceDetActivity.this);
            pDialog.setMessage("잠시만 기다려 주세요..");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            System.out.println("----Param : " + params[0]);

            if(params[0] == null){
                return null;
            }
            String placeId = params[0];
            String responseValue = null;

            //GET
            URL url = null;
            try {
                //url = new URL(UrlText + placeId);
                url = new URL(UrlText + "/" + placeId);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
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

            pDialog.dismiss();
            System.out.println(">>>>onPoastExcution...reponseValue : " + responseValue);
            if(responseValue != null) {
                ArrayList<CommentModel> commentModels = parseCommentLists(responseValue);
                //adapter.. 생성

                if(commentModels.size() > 0) {
                    System.out.println(">>>commonModel is higher than zero..");
                    //mFestivalAdapter = new MainRecycleViewAdapter(this, mFestivalList, 0);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    //mRecyclerView.setHasFixedSize(true);
                    mCommentListView.setLayoutManager(layoutManager);
                    mCommentListViewAdapter = new CommentRecycleViewAdapter(self, commentModels, 0);
                    mCommentListView.setAdapter(mCommentListViewAdapter);
                }else
                {
                    System.out.println(">>>commonModel is lower than zero..");
                    //댓글이 없는 경우..
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    //mRecyclerView.setHasFixedSize(true);
                    mCommentListView.setLayoutManager(layoutManager);
                    mCommentListViewAdapter = new CommentRecycleViewAdapter(self, commentModels, -1);
                    mCommentListView.setAdapter(mCommentListViewAdapter);
                }
            }else
            {
                //null 인경우..
            }

        }
    }

    /**
     *  댓글을 작성하는 비동기 클래스
     */
    private class WriteCommentClass extends AsyncTask<String, String, String > {

        ProgressDialog wDialog;
        //String UrlText = "https://seoulherechat.herokuapp.com/request/place/comments";
        String UrlText = COMMENT_ENDPOINT;

        protected void onPreExecute()
        {
            super.onPreExecute();
            wDialog = new ProgressDialog(PlaceDetActivity.this);
            wDialog.setMessage("잠시만 기다려 주세요..");
            wDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            System.out.println("----Param : " + params[0] + ", --Parms1 : " + params[1] + ", params2 : " + params[2]);

            if(params[0] == null){
                return null;
            }
            String placeId = params[0];
            String author = params[1];
            String message = params[2];

            String responseValue = null;
            URL url = null;

            JSONObject jsonObject = new JSONObject();
            JSONObject requestData = new JSONObject();
            try {
                jsonObject.put("placeId", placeId);
                jsonObject.put("message", message);
                jsonObject.put("author", author);
                Date now = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

                //jsonObject.put("date", DateFormat.getDateTimeInstance().format(new Date()));
                jsonObject.put("date", format.format(now));
                System.out.println(">>Placedet date : " + format.format(now));
                requestData.put("comment", jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String postData = requestData.toString();

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
            mEditAuthor.setText("");
            mEditComment.setText("");
            new LoadCommentClass().execute(placeModel.getPLACE_ID());

        }
    }


    // 해당부분을 공통 부분에 넣어야 함.
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
    }



    //댓글 파싱 부분..
    public ArrayList<CommentModel> parseCommentLists(String responseValue)
    {
        ArrayList<CommentModel> commentModelArrayList = new ArrayList<CommentModel>();

        JSONObject jsonObject = null;



        try {
            jsonObject = new JSONObject(responseValue);
            JSONObject data = jsonObject.getJSONObject("data");


            JSONArray commentList = data.getJSONArray("commentList");


            if (commentList.length() > 0) {

                for (int i = 0; i < commentList.length(); i++) {
                    JSONObject commentItem = commentList.getJSONObject(i);

                    String commentId = null;
                    String message = null;
                    String date = null;
                    String user = null;
                    String placeId = null;

                    if (commentItem.has("COMMENT_ID")) {
                        commentId = commentItem.get("COMMENT_ID").toString();
                    }

                    if (commentItem.has("COMMENT_MESSAGE")) {
                        message = commentItem.get("COMMENT_MESSAGE").toString();
                    }


                    if (commentItem.has("COMMENT_DATE")) {
                        date = commentItem.get("COMMENT_DATE").toString();
                    }
                    if (commentItem.has("COMMENT_USER")) {
                        user = commentItem.get("COMMENT_USER").toString();
                    }
                    if (commentItem.has("FK_PLACE_ID")) {
                        placeId = commentItem.get("FK_PLACE_ID").toString();
                    }

                    CommentModel comment = new CommentModel(commentId, message, user, date, placeId);
                    commentModelArrayList.add(comment);

                }
            } else {
                // no jobs
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }


        return commentModelArrayList;
    }
}

package com.chat.seoul.here.module.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.chat.seoul.here.PlaceDetActivity;
import com.chat.seoul.here.R;
import com.chat.seoul.here.module.lib.common.AsyncHttpRequest;
import com.chat.seoul.here.module.lib.common.AsyncRequestListener;
import com.chat.seoul.here.module.map.inf.PlaceFinderListener;
import com.chat.seoul.here.module.model.ChatMessage;
import com.chat.seoul.here.module.model.place.PlaceModel;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static com.chat.seoul.here.module.lib.conf.Common.HTTP_ACTION_PLACE_DET;
import static com.chat.seoul.here.module.lib.conf.Common.LIST_VIEW_COUNT;
import static com.chat.seoul.here.module.lib.conf.Common.PAGE_FOUND;
import static com.chat.seoul.here.module.lib.conf.Common.PAGE_FULLY_NOT_FOUND;
import static com.chat.seoul.here.module.lib.conf.Common.PAGE_NOT_FOUND;

/**
 * 장소 전체 보기의 리스트뷰에 대한 Adapter
 * Created by JJW on 2017-08-09.
 */

public class CustomListViewAdapter extends ArrayAdapter<PlaceModel> implements AsyncRequestListener{

    final private int SHOW_MAX_NUM = 5;
    private HashMap<String, Bitmap> mBitmapList;
    private ArrayList<PlaceModel> mShowItems;                       // 리스트에 보여질 리스트 정보
    private ArrayList<PlaceModel> mTotalmShowItems;                // 전체 Place리스트 정보
    private Context mContext;
    private ArrayList<PlaceModel> mListShowmShowItemsTmp;
    private PlaceFinderListener listener;
    private ArrayAdapter self;

    private ArrayList<PlaceModel> tmpPlaceModelList = new ArrayList<PlaceModel>();


    public CustomListViewAdapter(Context context, int textViewResourceId, ArrayList<PlaceModel> objects) {
        super(context, textViewResourceId, objects);
        this.mContext = context;
        this.mBitmapList = new HashMap<>();
        this.mTotalmShowItems = objects;
        this.mListShowmShowItemsTmp = new ArrayList<PlaceModel>();
        this.mShowItems = new ArrayList<PlaceModel>();
        this.listener = (PlaceFinderListener) context;
        this.self = this;

        System.out.println(">>>CustomListViewAdapter totalshowitems Count : " + this.mTotalmShowItems.size());
        new loadMoreListView().execute();
    }


    @Override
    public int getCount() {

        int count = 0;
        try {
            count = mShowItems.size();
        } catch (IllegalStateException e) {
            e.printStackTrace();

            return 0;
        }
        return count;
    }

    //우선 하드코딩으로 하기..
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.custom_lit_item, null);

            //리스트뷰 클릭 시 이벤트 발생..
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (Integer) v.getTag();
                    PlaceModel clickedPlace = mShowItems.get(position);
                    //조회수를 증가 시킨다.
                    String url = "https://seoulherechat.herokuapp.com/request/place/view/" + clickedPlace.getPLACE_ID();
                    AsyncHttpRequest putRequest = new AsyncHttpRequest(self, "PUT", HTTP_ACTION_PLACE_DET);
                    putRequest.execute(url);

                }
            });
        }
        v.setTag(position);

        ImageView imageView = (ImageView) v.findViewById(R.id.imageView);

        //리스트뷰의 아이템에 이미지를 변경한다.
        PlaceModel info = mShowItems.get(position);
        TextView textCnt = (TextView) v.findViewById(R.id.listCnt);

        textCnt.setText(Integer.toString(position));

        if (mBitmapList == null) {
            //비트맵 리스트에 아무것도 없는경우..처리 하지 않는다..
        } else {
            //해쉬맵에서 해당 시설이름에 해당되는 이미지를 가져온다..
            imageView.setImageBitmap(mBitmapList.get(mShowItems.get(position).getPLACE_ID()));
        }

        try {
            TextView textView = (TextView) v.findViewById(R.id.textView);
            textView.setText(info.getCODENAME());
            TextView txtCodeName = (TextView) v.findViewById(R.id.textView2);
            txtCodeName.setText(info.getPLACE_NAME());
            CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkTest);
            checkBox.setChecked(info.isChecked());
            checkBox.setTag(position);
            checkBox.setOnClickListener(buttonClickListener);
            checkBox.setVisibility(View.GONE);
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        return v;
    }

    //CheckBox클릭시 이벤트 설정
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            switch (v.getId()) {

                // 이미지 클릭
                case R.id.checkTest:
                    CheckBox tmp = (CheckBox) v.findViewById(R.id.checkTest);
                    System.out.println("------------Checkbox clicekd..status : " + tmp.isChecked() + ", position : " + position);
                    mShowItems.get(position).setChecked(tmp.isChecked());
                    break;
            }
        }
    };

    public ArrayList<PlaceModel> getAllmShowItems() {
        return this.mShowItems;
    }

    //리스트 뷰를 리프레쉬 한다.
    public void refreshAdater() {

        notifyDataSetChanged();
    }

    //아이템을 받아서 해당 아이템으로 리프레시 한다.
    public void refreshAdater(ArrayList<PlaceModel> showItems) {

        System.out.println("-------------refreshAdapter totalItems size : " + showItems.size());
        this.mShowItems.clear();
        this.mShowItems.addAll(showItems);
        notifyDataSetChanged();
    }

    //아이템을 클리어 한뒤 리프레쉬 한다..
    public void refreshClearAdapter() {
        this.mShowItems.clear();
        refreshAdater();
    }

    public void setBitmapList(HashMap<String, Bitmap> mBitmapList) {
        this.mBitmapList = mBitmapList;
        refreshAdater();
    }

    public void setTotalItems(ArrayList<PlaceModel> totalItems) {
        this.mTotalmShowItems = totalItems;
        this.mShowItems.clear();
        this.mBitmapList.clear();
        //데이터 리프레쉬
        refreshAdater();
        new loadMoreListView().execute();
    }

    public void loadMoreListView() {
        new loadMoreListView().execute();
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

    }

    @Override
    public void onPutEndRequest(int action, ChatMessage chatMessage) {
        //조회수를 증가 시킨 이 후 PlaceDetActivity로 이동한다.
        if(action == HTTP_ACTION_PLACE_DET)
        {
            PlaceModel clickedPlace = chatMessage.getPlaceModelArrayList().get(0);
            Intent intent = new Intent(mContext, PlaceDetActivity.class);
            intent.putExtra("clickedPlace", clickedPlace);
//        intent.putExtra("clickedPosition", pagerPosition);
            mContext.startActivity(intent);
        }
    }


    //더 보기 버튼 클릭 시 동작
    private class loadMoreListView extends AsyncTask<Void, Void, Integer> {



        @Override
        protected void onPreExecute() {
            tmpPlaceModelList.clear();
            listener.onPlaceFinderStart();
        }

        @Override
        protected Integer doInBackground(Void... unused) {

            //1. 먼저 전체 아이템 리스트를 개수를 체크한다.
            int mTotalShowItemsSize = mTotalmShowItems.size();
            //1.1 만약 전체 아이템 리스트 개수가 0 개이면 리턴 한다.
            if (mTotalShowItemsSize == 0) {
                System.out.println(">>mTotalShowItem is no found..");
                return PAGE_FULLY_NOT_FOUND;
            }
            //2. 현재 보여지고 있는 아이템 개수를 체크한다.
            //2.1 현재 보여지고 있는 아이템 개수가 0 인 경우 -- 최초 로딩 되는 경우
            // - 이때 전체 아이템 개수에서 SHOW_LIST_CNT 개를 보여준다.
            int currentListViewCount = mShowItems.size();       //현재 보여지게 될 아이템의 개수
            if (currentListViewCount == 0) {
                int loadingCnt = 0;
                if (mTotalShowItemsSize < SHOW_MAX_NUM) {
                    loadingCnt = mTotalShowItemsSize;
                } else {
                    loadingCnt = SHOW_MAX_NUM;
                }

                System.out.println(">>loading Count : " + loadingCnt);

                for (int i = 0; i < loadingCnt; i++) {
                    Bitmap bitmap = null;
                    //mShowItems.add(mTotalmShowItems.get(i));   20171029
                    tmpPlaceModelList.add(mTotalmShowItems.get(i));

                    try {

                        //System.out.println("---------main img : " + mShowItem.get(i).getMAIN_IMG());
                        if(mTotalmShowItems.get(i).getPLACE_IMAGE_LIST() != null)
                        {
                            if(mTotalmShowItems.get(i).getPLACE_IMAGE_LIST().size() == 0)
                            {
                                bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_no_photo);
                            }else {
                                if ("null".equals(mTotalmShowItems.get(i).getPLACE_IMAGE_LIST().get(0).getMAIN_IMG())) {
                                    bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_no_photo);
                                } else {
                                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(mTotalmShowItems.get(i).getPLACE_IMAGE_LIST().get(0).getMAIN_IMG()).getContent());
                                }
                                if (bitmap == null) {
                                    bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_no_photo);
                                }
                            }
                        }else
                        {
                            //null 인경우
                            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_no_photo);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return PAGE_FULLY_NOT_FOUND;
                    }
                    System.out.println(">>before total count : " + loadingCnt);
                    mBitmapList.put(mTotalmShowItems.get(i).getPLACE_ID(), bitmap);
                }
            }
            //2.2 만약 현재 보여진 아이템  개수가 있으면, 전체 아이템 개수에서 현재 보여지는 아이템 개수 이후의 아이템을 등록 시킨다.
            else {
                System.out.println(">>it is not first loading");

                //2.2.1. 현재 뷰에보여지는 갯수가 전체 주변경로의 개수와 같으면 더이상 페이지가 존재 하지않는다.
                //2.2.1 현재 리스트에 보여지는 개수가 전체 개수보다 많으면 리턴 한다.
                if (currentListViewCount > mTotalShowItemsSize) {
                    return PAGE_FULLY_NOT_FOUND;          //페이지가 없는 경우.
                }
                //2.2.2 현재 리스트에 보여지는 개수가 전체 개수와 같으면 리턴 한다.
                else if (currentListViewCount == mTotalShowItemsSize) {
                    return PAGE_FULLY_NOT_FOUND;
                }
                //2.2.3 그렇지 않으면, 현재 보여지는 아이템 이 후로 SHOW_LIST_CNT 만큼 더 증가 시킨다.
                else {
                    tmpPlaceModelList.addAll(mShowItems);
                    for (int i = currentListViewCount; i < currentListViewCount + LIST_VIEW_COUNT; i++) {
                        Bitmap bitmap = null;
                        //2.2.3.1 아이템을 추가 하다가, 전체 아이템 개수보다 많은 경우 리턴 한다.
                        if (i > mTotalShowItemsSize) {

                            return PAGE_NOT_FOUND;       //로딩 하다가 페이지가 없는 경우..
                        }
                        //2.2.3.2 아이템을 추가 하다가, 전체 아이템 개수와 같은 경우 리턴 한다.
                        else if (i == mTotalShowItemsSize) {
                            return PAGE_NOT_FOUND;
                        }
                        //2.2.3.3 그렇지 않은 경우, 현재 보여지는 아이템에서 LIST_SHOW_CNT 개수 만큼 현재 보여지기 위한 리스트에 추가 한다.
                        else {
                            //mShowItems.add(mTotalmShowItems.get(i));      //20171029
                            tmpPlaceModelList.add(mTotalmShowItems.get(i));
                            try {

                                if(mTotalmShowItems.get(i).getPLACE_IMAGE_LIST().size() > 0) {

                                    if ("null".equals(mTotalmShowItems.get(i).getPLACE_IMAGE_LIST().get(0).getMAIN_IMG())) {
                                        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_no_photo);
                                    } else {
                                        bitmap = BitmapFactory.decodeStream((InputStream) new URL(mTotalmShowItems.get(i).getPLACE_IMAGE_LIST().get(0).getMAIN_IMG()).getContent());
                                    }
                                    if (bitmap == null) {
                                        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_no_photo);
                                    }
                                }else
                                {
                                    bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_no_photo);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                return PAGE_FULLY_NOT_FOUND;
                            }
                            mBitmapList.put(mTotalmShowItems.get(i).getPLACE_ID(), bitmap);
                        }
                    }
                }

            }

            return PAGE_FOUND;
        }

        //더보기가 끝난 뒤 UI 작업
        @Override
        protected void onPostExecute(Integer value) {
            // closing progress dialog
            if(value == PAGE_FULLY_NOT_FOUND) {
                setBitmapList(mBitmapList);
                refreshAdater(tmpPlaceModelList);
                //listener.onPlaceFinderSuccess(mShowItems);
                listener.onPlaceFinderSuccess(tmpPlaceModelList);
            }else
            {
                setBitmapList(mBitmapList);
                refreshAdater(tmpPlaceModelList);
                //listener.onPlaceFinderSuccess(mShowItems);
                listener.onPlaceFinderSuccess(tmpPlaceModelList);
            }

        }
    }
}

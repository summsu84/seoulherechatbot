package com.chat.seoul.here.module.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.chat.seoul.here.module.map.inf.PlaceFinderListener;
import com.chat.seoul.here.module.model.BaseModel;
import com.chat.seoul.here.module.model.festival.FestivalModel;
import com.chat.seoul.here.module.model.place.PlaceModel;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static com.chat.seoul.here.module.lib.conf.Common.CHAT_MESSAGE_PLACE;
import static com.chat.seoul.here.module.lib.conf.Common.LIST_VIEW_COUNT;
import static com.chat.seoul.here.module.lib.conf.Common.PAGE_FOUND;
import static com.chat.seoul.here.module.lib.conf.Common.PAGE_FULLY_NOT_FOUND;
import static com.chat.seoul.here.module.lib.conf.Common.PAGE_NOT_FOUND;

/**
 * 장소 전체 보기의 리스트뷰에 대한 Base Adapter (FestivalModel 및 PlaceModel 허용) 사용 버전
 * Created by JJW on 2017-08-09.
 */

public class CustomListViewBaseAdapter extends ArrayAdapter<BaseModel>{

    private HashMap<String, Bitmap> mBitmapList;
    private ArrayList<BaseModel> mShowItems;                       // 리스트에 보여질 리스트 정보
    private ArrayList<BaseModel> mTotalmShowItems;                // 전체 Place리스트 정보
    private Context mContext;
    private ArrayList<BaseModel> mListShowmShowItemsTmp;
    private PlaceFinderListener listener;
    private int PLACE_TYPE;

    public CustomListViewBaseAdapter(Context context, int textViewResourceId, ArrayList<BaseModel> objects, int placeType) {
        super(context, textViewResourceId, objects);
        this.mContext = context;
        this.mBitmapList = new HashMap<>();
        this.mTotalmShowItems = objects;
        this.mListShowmShowItemsTmp = new ArrayList<BaseModel>();
        this.mShowItems = objects;
        this.listener = (PlaceFinderListener) context;
        this.PLACE_TYPE = placeType;
        System.out.println(">>>CustomListViewBaseAdapter mShowItems Count : " + this.mShowItems.size());
    }
    @Override
    public int getCount() {

         //System.out.println("------------ customListViewAdapter.. getCount - mShowItems count : " + mShowItems.size() + ", mListShowTotalItem count : " + mTotalmShowItems.size());
        int count = 0;
        try {
            count = mShowItems.size();
        }catch(IllegalStateException e)
        {
            e.printStackTrace();

            return 0;
        }
        return count;
    }

    //우선 하드코딩으로 하기..
    public View getView(int position, View convertView, ViewGroup parent) {

        System.out.println(">>>>[CustomViewBaseAdapter] getview... position : " + position + ", mshowitems : " + mShowItems);
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.custom_lit_item, null);

            //리스트뷰 클릭 시 이벤트 발생..
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //perform action
                    int position = (Integer) v.getTag();
                    if(PLACE_TYPE == CHAT_MESSAGE_PLACE) {
                        Intent intent = new Intent(mContext, PlaceDetActivity.class);
                        //intent.putExtra("clickedPlace", clickedPlace);
                        // 액티비티를 생성한다.
                        mContext.startActivity(intent);
                    }else
                    {
                        FestivalModel item = (FestivalModel)mShowItems.get(position);

                        String url = item.getFESTIVAL_LINK();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        mContext.startActivity(intent);
                    }

                }
            });
        }
        v.setTag(position);

        ImageView imageView = (ImageView)v.findViewById(R.id.imageView);

        //리스트뷰의 아이템에 이미지를 변경한다.
        TextView textView = (TextView)v.findViewById(R.id.textView);
        TextView txtCodeName = (TextView)v.findViewById(R.id.textView2);
        CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkTest);
        checkBox.setChecked(false);
        checkBox.setTag(position);
        checkBox.setOnClickListener(buttonClickListener);
        checkBox.setVisibility(View.GONE);
        TextView textCnt = (TextView)v.findViewById(R.id.listCnt);
        textCnt.setText(Integer.toString(position));

        if(PLACE_TYPE == CHAT_MESSAGE_PLACE)
        {
            PlaceModel info = (PlaceModel)mShowItems.get(position);
            if(mBitmapList == null)
            {
                //비트맵 리스트에 아무것도 없는경우..처리 하지 않는다..
            }else {
                //해쉬맵에서 해당 시설이름에 해당되는 이미지를 가져온다..
                imageView.setImageBitmap(mBitmapList.get(info.getPLACE_ID()));
            }
            textView.setText(info.getPLACE_NAME());
            txtCodeName.setText(info.getPLACE_NAME());
        }else
        {
            //FestivalModel로 casting 한다.
            FestivalModel info = (FestivalModel)mShowItems.get(position);

            //이미지 넣기기
            final String festivalClass = info.getFESTIVAL_CLASS();

            //이미지 체크ㅡ
            if("행사/대회".equals(festivalClass))
            {
                imageView.setImageResource(R.drawable.ic_festival_2);
            }else if("전시/관람".equals(festivalClass))
            {
                imageView.setImageResource(R.drawable.ic_festival_1);
            }else
            {
                imageView.setImageResource(R.drawable.ic_festival_3);
            }


            textView.setText(info.getFESTIVAL_NAME().replaceAll("\"", ""));
            txtCodeName.setMaxLines(1);
            String startDate = info.getFESTIVAL_START_DATE().replaceAll("\"", "").substring(0, 10);
            String endDate = info.getFESTIVAL_END_DATE().replaceAll("\"", "").substring(0, 10);
            String date = startDate + " ~ " + endDate;
            txtCodeName.setText(date);

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
                    CheckBox tmp = (CheckBox)v.findViewById(R.id.checkTest);
                    System.out.println("------------Checkbox clicekd..status : " + tmp.isChecked() + ", position : " +position);
                    //mShowItems.get(position).setChecked(tmp.isChecked());
                    break;
            }
        }
    };

    public ArrayList<BaseModel> getAllmShowItems()
    {
        return this.mShowItems;
    }

    //리스트 뷰를 리프레쉬 한다.
    public void refreshAdater() {

        notifyDataSetChanged();
    }

    //아이템을 받아서 해당 아이템으로 리프레시 한다.
    public void refreshAdater(ArrayList<BaseModel> showItems) {

        System.out.println("-------------refreshAdapter totalItems size : " + showItems.size());
        mShowItems = showItems;
        refreshAdater();
    }

    //아이템을 클리어 한뒤 리프레쉬 한다..
    public void refreshClearAdapter()
    {
        this.mShowItems.clear();
        refreshAdater();
    }

    public void setBitmapList(HashMap<String, Bitmap> mBitmapList) {
        this.mBitmapList = mBitmapList;
        refreshAdater();
    }

    public void setTotalItems(ArrayList<BaseModel> totalItems) {
        this.mTotalmShowItems = totalItems;
        this.mShowItems.clear();
        this.mBitmapList.clear();
        new loadMoreListView().execute();
    }

    public void loadMoreListView() {
        new loadMoreListView().execute();
    }


    //더 보기 버튼 클릭 시 동작
    private class loadMoreListView extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {

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
                if (mTotalShowItemsSize < 5) {
                    loadingCnt = mTotalShowItemsSize;
                } else {
                    loadingCnt = 5;
                }

                for (int i = 0; i < loadingCnt; i++) {
                    Bitmap bitmap = null;
                    mShowItems.add(mTotalmShowItems.get(i));
                    if(PLACE_TYPE == CHAT_MESSAGE_PLACE) {
                        PlaceModel placeModel = (PlaceModel) mTotalmShowItems.get(i);
                        try {

                            //System.out.println("---------main img : " + mShowItem.get(i).getMAIN_IMG());
                            if ("null".equals(placeModel.getPLACE_IMAGE_LIST().get(0).getMAIN_IMG())) {
                                bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_no_photo);
                            } else {
                                bitmap = BitmapFactory.decodeStream((InputStream) new URL(placeModel.getPLACE_IMAGE_LIST().get(0).getMAIN_IMG()).getContent());
                            }
                            if (bitmap == null) {
                                bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_no_photo);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            return PAGE_FULLY_NOT_FOUND;
                        }
                        mBitmapList.put(placeModel.getPLACE_ID(), bitmap);
                    }
                    //FESTIVAL
                    else
                    {
                        FestivalModel festivalModel = (FestivalModel) mTotalmShowItems.get(i);
                        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_no_photo);
                        mBitmapList.put(festivalModel.getFESTIVAL_ID(), bitmap);
                    }
                }
            }
            //2.2 만약 현재 보여진 아이템  개수가 있으면, 전체 아이템 개수에서 현재 보여지는 아이템 개수 이후의 아이템을 등록 시킨다.
            else {
                //2.2.1. 현재 뷰에보여지는 갯수가 전체 주변경로의 개수와 같으면 더이상 페이지가 존재 하지않는다.
                //2.2.1 현재 리스트에 보여지는 개수가 전체 개수보다 많으면 리턴 한다.
                if (currentListViewCount > mTotalShowItemsSize) {
                    return PAGE_FULLY_NOT_FOUND;          //페이지가 없는 경우.
                }
                //2.2.2 현재 리스트에 보여지는 개수가 전체 개수와 같으면 리턴 한다.
                else if (currentListViewCount == mTotalmShowItems.size()) {
                    return PAGE_FULLY_NOT_FOUND;
                }
                //2.2.3 그렇지 않으면, 현재 보여지는 아이템 이 후로 SHOW_LIST_CNT 만큼 더 증가 시킨다.
                else {
                    for (int i = currentListViewCount; i < currentListViewCount + LIST_VIEW_COUNT; i++) {
                        Bitmap bitmap = null;
                        //2.2.3.1 아이템을 추가 하다가, 전체 아이템 개수보다 많은 경우 리턴 한다.
                        if (i > mTotalShowItemsSize) {

                            return PAGE_NOT_FOUND;       //로딩 하다가 페이지가 없는 경우..
                        }
                        //2.2.3.2 아이템을 추가 하다가, 전체 아이템 개수와 같은 경우 리턴 한다.
                        else if (i == mTotalmShowItems.size()) {
                            return PAGE_NOT_FOUND;
                        }
                        //2.2.3.3 그렇지 않은 경우, 현재 보여지는 아이템에서 LIST_SHOW_CNT 개수 만큼 현재 보여지기 위한 리스트에 추가 한다.
                        else {
                            mShowItems.add(mTotalmShowItems.get(i));

                            if(PLACE_TYPE == CHAT_MESSAGE_PLACE) {
                                PlaceModel placeModel = (PlaceModel) mTotalmShowItems.get(i);
                                try {

                                    //System.out.println("---------main img : " + mShowItem.get(i).getMAIN_IMG());
                                    if ("null".equals(placeModel.getPLACE_IMAGE_LIST().get(0).getMAIN_IMG())) {
                                        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_no_photo);
                                    } else {
                                        bitmap = BitmapFactory.decodeStream((InputStream) new URL(placeModel.getPLACE_IMAGE_LIST().get(0).getMAIN_IMG()).getContent());
                                    }
                                    if (bitmap == null) {
                                        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_no_photo);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return PAGE_FULLY_NOT_FOUND;
                                }
                                mBitmapList.put(placeModel.getPLACE_ID(), bitmap);
                            }
                            //FESTIVAL
                            else
                            {
                                FestivalModel festivalModel = (FestivalModel) mTotalmShowItems.get(i);
                                bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_no_photo);
                                mBitmapList.put(festivalModel.getFESTIVAL_ID(), bitmap);
                            }

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
                //...check..

                setBitmapList(mBitmapList);
                refreshAdater(mShowItems);

            }else
            {

                //...check..
                setBitmapList(mBitmapList);
                refreshAdater(mShowItems);
                listener.onPlaceFinderSuccess(mShowItems, PLACE_TYPE);
            }

        }
    }
}

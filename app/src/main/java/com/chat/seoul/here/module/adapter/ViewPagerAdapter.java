package com.chat.seoul.here.module.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chat.seoul.here.R;
import com.chat.seoul.here.module.lib.image.AsyncImageLoader;
import com.chat.seoul.here.module.model.festival.FestivalModel;
import com.chat.seoul.here.module.model.place.PlaceModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static com.chat.seoul.here.module.lib.conf.Common.CHAT_MESSAGE_PLACE;

/**
 * Created by JJW on 2017-08-03.
 * 채팅 시 장소 검색 결과 뷰 페이저 관련 Adapter (해당 버전은 두개의 Model을 사용을 한다)
 * 명소 장보 : viewPagerPlaceItems
 * 행사 정보 : viewPagerFestivalItems
 */

public class ViewPagerAdapter extends PagerAdapter {


    //SHOW_MAX_NUM
    final private int SHOW_MAX_NUM = 5;
    //추후 제네릭으로 변경
    private ArrayList<PlaceModel> viewPagerPlaceItems;
    private ArrayList<FestivalModel> viewPagerFestivalItems;
    private Context ctx;
    private HashMap<String, Bitmap> mBitmapList;
    private ViewPagerAdapterButtonListener listener;
    private int PLACE_TYPE;

    private int currentPosition;
    private double sourceLat;
    private double sourceLon;
    private double sourceAround;

    @SuppressLint("NewApi")
    @Override
    public void finishUpdate(ViewGroup container) {
        // TODO Auto-generated method stub
        super.finishUpdate(container);

    }

    public ViewPagerAdapter() {

        super();

    }

    public ViewPagerAdapter(ArrayList viewPagerPlaceItems, Context ctx, ChatAdapter adapter, int paramPlaceType) {

        super();
        //현재 요청하는 메시지의 타입을 결정한다.
        //명소 정보
        if(paramPlaceType == CHAT_MESSAGE_PLACE) {
            this.viewPagerPlaceItems = viewPagerPlaceItems;
        }
        //행사 정보
        else
        {
            this.viewPagerFestivalItems = viewPagerPlaceItems;
        }
        this.ctx = ctx;
        this.listener = (ViewPagerAdapterButtonListener) adapter;
        this.mBitmapList = new HashMap<String, Bitmap>();      //배열 객체 생성.
        this.currentPosition = 0;
        System.out.println(">>ViewPagerAdapter Constructor... ");
        PLACE_TYPE = paramPlaceType;
    }

    // 뷰페이저에는 SHOW_MAX_NUM 개의 자료를 보여 준다.
    @Override
    public int getCount() {

        if(PLACE_TYPE == CHAT_MESSAGE_PLACE) {

            if(viewPagerPlaceItems.size() < SHOW_MAX_NUM + 1)
            {
                return viewPagerPlaceItems.size();
            }else
            {
                return SHOW_MAX_NUM;
            }


        }
        else {
            if(viewPagerFestivalItems.size() < SHOW_MAX_NUM + 1)
            {
                return viewPagerFestivalItems.size();
            }else
            {
                return SHOW_MAX_NUM;
            }
        }

    }

    @Override
    public boolean isViewFromObject(View collection, Object object) {

        return collection == ((View) object);
    }

    @Override
    public Object instantiateItem(View collection, int position) {

        // Inflating layout
        LayoutInflater inflater = (LayoutInflater) collection.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.view_pager_item, null);

        final ViewPager viewPager = (ViewPager)collection;
        final TextView txtPlaceName = (TextView) view.findViewById(R.id.txtPlaceName);
        TextView txtPlaceDesc = (TextView) view.findViewById(R.id.txtPlaceDesc);
        final TextView txtPlaceClass = (TextView) view.findViewById(R.id.txtPlaceClass);
        final TextView txtViewPagerCount = (TextView) view.findViewById(R.id.txtItemCount);

/*        Typeface fontBoldFace = Typeface.createFromAsset(ctx.getAssets(), "fonts/nanumgothicBold.ttf");
        Typeface fontFace = Typeface.createFromAsset(ctx.getAssets(), "fonts/nanumgothic.ttf");
        //for font setting
        txtPlaceName.setTypeface(fontBoldFace);
        txtPlaceDesc.setTypeface(fontFace);
        txtPlaceClass.setTypeface(fontFace);*/

        final Button btnPlaceDetail = (Button) view.findViewById(R.id.btnPlaceDetail);
        final Button btnAllPlaceList = (Button)view.findViewById(R.id.btnAllPlaceList);
        ImageView imgPlaceImage = (ImageView) view.findViewById(R.id.imgViewIcon);
        final int pagerPosition = position;

        //NavButton
        final ImageView btnRightNav = (ImageView) view.findViewById(R.id.right_nav);
        final ImageView btnLeftNav = (ImageView) view.findViewById(R.id.left_nav);

        //view and comment cnt;
        final ImageView imgViewCnt = (ImageView) view.findViewById(R.id.imgViewPlaceViewCnt);
        final ImageView imgViewComment = (ImageView) view.findViewById(R.id.imgViewPlaceCommentCnt);
        final TextView viwCnt = (TextView) view.findViewById(R.id.txtViewPlaceViewCnt) ;
        final TextView commentCnt = (TextView) view.findViewById(R.id.txtViewPlaceCommentCnt);

        btnRightNav.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                int tab = viewPager.getCurrentItem();
                System.out.println(">>Right >tab : " + tab);
                tab++;
                viewPager.setCurrentItem(tab);

            }
        });

        btnLeftNav.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int tab = viewPager.getCurrentItem();
                System.out.println(">>>Left tab : " + tab);
                if (tab > 0) {
                    tab--;
                    viewPager.setCurrentItem(tab);
                } else if (tab == 0) {
                    viewPager.setCurrentItem(tab);
                }

            }
        });

        //명소 정보인 경우..
        if(PLACE_TYPE == CHAT_MESSAGE_PLACE) {

            btnPlaceDetail.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    listener.onViewPagerAdapterPlaceDetailButtonClicked(viewPagerPlaceItems.get(pagerPosition));
                }
            });


            btnAllPlaceList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listener.onViewPagerAdapterAllListPlaceButtonClicked(viewPagerPlaceItems);
                }
            });


            try {

                txtPlaceName.setText((viewPagerPlaceItems.get(position).getPLACE_NAME() == null || viewPagerPlaceItems.get(position).getPLACE_NAME().equals("null")) ? "정보 없음 " : viewPagerPlaceItems.get(position).getPLACE_NAME());
                txtPlaceDesc.setText((viewPagerPlaceItems.get(position).getPLACE_DESC() == null || viewPagerPlaceItems.get(position).getPLACE_DESC().equals("null")) ? "정보 없음 " : viewPagerPlaceItems.get(position).getPLACE_DESC());
                txtPlaceClass.setText((viewPagerPlaceItems.get(position).getCODENAME() == null || viewPagerPlaceItems.get(position).getCODENAME().equals("null")) ? "관광명소 " : viewPagerPlaceItems.get(position).getCODENAME());          //나중에 정보 변경
                //전체 카운트 개수 구하기
                String strItemPosition = Integer.toString(position + 1) + " / " + ((viewPagerPlaceItems.size() > SHOW_MAX_NUM) ? Integer.toString(SHOW_MAX_NUM) : Integer.toString(viewPagerPlaceItems.size()));
                txtViewPagerCount.setText(strItemPosition);
                viwCnt.setText((viewPagerPlaceItems.get(position).getVIEW_CNT() == 0) ? "0" : Integer.toString(viewPagerPlaceItems.get(position).getVIEW_CNT()));
                commentCnt.setText((viewPagerPlaceItems.get(position).getCOMMENT_CNT() == 0) ? "0" : Integer.toString(viewPagerPlaceItems.get(position).getCOMMENT_CNT()));
                //이미지 썸네일 가져오기
                System.out.println(">>>>>>>>>>ViePagerAdapter bitmaplist... : " + mBitmapList);
                String imgUrl = null;
                if (viewPagerPlaceItems.get(position).getPLACE_IMAGE_LIST() == null || viewPagerPlaceItems.get(position).getPLACE_IMAGE_LIST().size() == 0) {
                    imgUrl = null;
                } else {
                    Bitmap bmap = imgPlaceImage.getDrawingCache();
                    System.out.println(">>>>>>>>imgPlaceBitmap : " + bmap + ", imgViewAddress : " + imgPlaceImage);
                    if (bmap == null) {
                        imgUrl = viewPagerPlaceItems.get(position).getPLACE_IMAGE_LIST().get(0).getMAIN_IMG();
                        AsyncImageLoader task = new AsyncImageLoader(imgPlaceImage, imgUrl, mBitmapList, viewPagerPlaceItems.get(position).getPLACE_ID());
                        task.execute();
                    } else {

                    }
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        //Festival Model 인 경우
        else
        {
            //label을 변경한다.
            final TextView txtPlaceNameLabel = (TextView) view.findViewById(R.id.txtLabelPlaceName);
            final TextView txtPlaceDescLabel = (TextView) view.findViewById(R.id.txtLabelPlaceDesc);
            final TextView txtPlaceClassLabel = (TextView) view.findViewById(R.id.txtLabelPlaceClass);

            imgViewCnt.setVisibility(View.GONE);
            imgViewComment.setVisibility(View.GONE);
            viwCnt.setVisibility(View.GONE);
            commentCnt.setVisibility(View.GONE);

            txtPlaceNameLabel.setText("종류");
            txtPlaceDescLabel.setText("이름");
            txtPlaceClassLabel.setText("기간");

            txtPlaceName.setText((viewPagerFestivalItems.get(position).getFESTIVAL_CLASS() == null || viewPagerFestivalItems.get(position).getFESTIVAL_CLASS().equals("null")) ? "정보 없음 " : viewPagerFestivalItems.get(position).getFESTIVAL_CLASS());
            txtPlaceDesc.setText((viewPagerFestivalItems.get(position).getFESTIVAL_NAME() == null || viewPagerFestivalItems.get(position).getFESTIVAL_NAME().equals("null")) ? "정보 없음 " : viewPagerFestivalItems.get(position).getFESTIVAL_NAME());

            String startDate = viewPagerFestivalItems.get(position).getFESTIVAL_START_DATE().substring(0, 10);
            String endDate = viewPagerFestivalItems.get(position).getFESTIVAL_END_DATE().substring(0, 10);
            String date = startDate + " ~ " + endDate;
            txtPlaceClass.setText(date);

            //이미지 체크ㅡ
            if("행사/대회".equals(viewPagerFestivalItems.get(position).getFESTIVAL_CLASS()))
            {
                imgPlaceImage.setImageResource(R.drawable.ic_festival_2);
            }else if("전시/관람".equals(viewPagerFestivalItems.get(position).getFESTIVAL_CLASS()))
            {
                imgPlaceImage.setImageResource(R.drawable.ic_festival_1);
            }else
            {
                imgPlaceImage.setImageResource(R.drawable.ic_festival_3);
            }

            //전체 카운트 개수 구하기
            String strItemPosition = Integer.toString(position + 1) + " / " + ((viewPagerFestivalItems.size() > SHOW_MAX_NUM) ? Integer.toString(SHOW_MAX_NUM) : Integer.toString(viewPagerFestivalItems.size()));
            txtViewPagerCount.setText(strItemPosition);

            btnPlaceDetail.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    listener.onViewPagerAdapterFestivalDetailButtonClicked(viewPagerFestivalItems.get(pagerPosition));
                }
            });


            btnAllPlaceList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listener.onViewPagerAdapterAllListFestivalButtonClicked(viewPagerFestivalItems);

                }
            });
        }

        ((ViewPager) collection).addView(view, 0);
        return view;

    }


    @Override
    public void destroyItem(View container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    public void refreshAdapter()
    {
        notifyDataSetChanged();
    }



    //이미지를 불러와서 저장한다..
    private class loadImageTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... image) {

            /*PlaceImageModel tmp = image[0];*/
            Bitmap bitmap = null;

            for(int i = 0 ; i < viewPagerPlaceItems.size(); i++) {
                try {
                    //System.out.println("---------main img : " + mShowItem.get(i).getMAIN_IMG());
                    if(viewPagerPlaceItems.get(i).getPLACE_IMAGE_LIST() != null && viewPagerPlaceItems.get(i).getPLACE_IMAGE_LIST().size() > 0) {

                        if ("null".equals(viewPagerPlaceItems.get(i).getPLACE_IMAGE_LIST().get(0).getMAIN_IMG())) {
                            bitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_no_photo);
                        } else {
                            bitmap = BitmapFactory.decodeStream((InputStream) new URL(viewPagerPlaceItems.get(i).getPLACE_IMAGE_LIST().get(0).getMAIN_IMG()).getContent());
                        }

                        if (bitmap == null) {
                            bitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_no_photo);
                        }
                    }

                    //tmp.setIMG_BITMAP(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //여기에, FAC_NAME기반의 해쉬맵을 사용한다..
                mBitmapList.put(viewPagerPlaceItems.get(i).getPLACE_ID(), bitmap);
            }
            return null;
        }
        //더보기가 끝난 뒤 UI 작업
        @Override
        protected void onPostExecute(Integer value) {
            refreshAdapter();
        }


    }
}

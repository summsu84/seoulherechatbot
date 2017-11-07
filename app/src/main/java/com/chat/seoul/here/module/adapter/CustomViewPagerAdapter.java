package com.chat.seoul.here.module.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chat.seoul.here.R;
import com.chat.seoul.here.SignInActivity;
import com.chat.seoul.here.module.lib.image.AsyncImageLoader;
import com.chat.seoul.here.module.model.place.PlaceModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by JJW on 2016-10-02.
 * PlaceDetail ViewPager를 위해서 사용됨.
 */
public class CustomViewPagerAdapter extends PagerAdapter  {

    private ArrayList<Bitmap> bitmaps;
    private int[] image_resource = {R.drawable.ic_chatbot, R.drawable.ic_chatbot};
    private Context ctx;
    private LayoutInflater layoutInflater;
    private boolean mIsMain;
    private boolean mIsShared;
    private ArrayList<PlaceModel> mShowPlaceList;
    private HashMap<String, Bitmap> mBitmapList;

    private CustomViewPagerListener listener;

    public CustomViewPagerAdapter(Context ctx, boolean isMain)
    {
        this.listener = (CustomViewPagerListener)ctx;
        this.mIsMain = isMain;
        this.mIsShared = false;
        this.ctx = ctx;
        this.bitmaps = new ArrayList<Bitmap>();
        this.mBitmapList = new HashMap<String, Bitmap>();      //배열 객체 생성.
        System.out.println(">>ViewPagerAdapter Constructor... ");
        this.mShowPlaceList = new ArrayList<PlaceModel>();
    }

    public CustomViewPagerAdapter(Context ctx, ArrayList<PlaceModel> placeList, boolean isMain)
    {
        this.listener = (CustomViewPagerListener)ctx;
        this.mIsMain = isMain;
        this.mIsShared = false;
        this.ctx = ctx;
        this.bitmaps = new ArrayList<Bitmap>();
        this.mShowPlaceList = placeList;
        this.mBitmapList = new HashMap<String, Bitmap>();      //배열 객체 생성.
        System.out.println(">>ViewPagerAdapter Constructor... PlaceList : " + placeList.toString());
    }

    public CustomViewPagerAdapter(Context ctx, PlaceModel placeModel, boolean isMain) {
        this.listener = (CustomViewPagerListener)ctx;
        this.mIsMain = isMain;
        this.mIsShared = false;
        this.ctx = ctx;
        this.bitmaps = new ArrayList<Bitmap>();
        this.mShowPlaceList = new ArrayList<PlaceModel>();
        this.mShowPlaceList.add(placeModel);
        this.mBitmapList = new HashMap<String, Bitmap>();      //배열 객체 생성.
        System.out.println(">>ViewPagerAdapter Constructor... PlaceItem : " + placeModel.toString());
    }

    public void setBitmaps(Bitmap bitmap)
    {

        bitmaps.add(bitmap);
        notifyDataSetChanged();
    }

    public void setPlaceList(ArrayList<PlaceModel> placeList)
    {
        mBitmapList.clear();
        mShowPlaceList = placeList;
        new loadImageTask().execute();

        //notifyDataSetChanged();
    }


    public void refreshAdapter()
    {
        //비트맵 정보를모두 제거한다.
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {

//        return image_resource.length;
        //return bitmaps.size();
        return mShowPlaceList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        System.out.println(">>>>>CustomViewPagerAdapter...is called..");
        layoutInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.search_det_viewpager, container, false);

        ImageView imageView = (ImageView) item_view.findViewById(R.id.imagePager);
        TextView textView = (TextView) item_view.findViewById(R.id.imageCount);
        textView.setVisibility(View.GONE);

        //imageView.setImageResource(image_resource[position]);
        final PlaceModel placeModel = mShowPlaceList.get(position);

        Bitmap bmap = imageView.getDrawingCache();
        if(bmap == null) {

            AsyncImageLoader task = null;
            String imgUrl = null;
            if(placeModel.getPLACE_IMAGE_LIST() == null || placeModel.getPLACE_IMAGE_LIST().size() == 0)
            {
                imgUrl = null;
            }else
            {
                imgUrl = placeModel.getPLACE_IMAGE_LIST().get(0).getMAIN_IMG();
            }

            if(mIsMain == true) {
                task = new AsyncImageLoader(imageView, imgUrl);
                task.execute();
            }else
            {
                if(imgUrl != null) {
                    task = new AsyncImageLoader(imageView, imgUrl, listener);
                    task.execute();
                }else
                {
                    //이미지가 없는 경우.
                }

            }

        }

        //imageView.setImageBitmap(bitmaps.get(position));
        String tmp = "";

        //메인인경우..
        if(mIsMain == true)
        {
            TextView txtdesc = (TextView) item_view.findViewById(R.id.txtViewPagerImgDesc);
            txtdesc.setText(placeModel.getPLACE_NAME());
            /*if(position == 0) {
                txtdesc.setText(ctx.getResources().getString(R.string.main1_img_desc));
            }
            else if(position == 1) {
                txtdesc.setText(ctx.getResources().getString(R.string.main2_img_desc));
            }
            else {
                txtdesc.setText(ctx.getResources().getString(R.string.main3_img_desc));
            }*/

            item_view.setTag(position);
            item_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //페이저 클릭시 Det메뉴로 이동..단, 메인에서 호출되는지 확인..
                    PlaceModel clickedPlace = placeModel;
                    ((SignInActivity)ctx).clickDetailPage(clickedPlace.getPLACE_ID());
                    //조회수를 증가 시킨다.
/*


*/

                }
            });
        }else
        {
            TextView txtdesc = (TextView) item_view.findViewById(R.id.txtViewPagerImgDesc);
            txtdesc.setGravity(Gravity.CENTER_HORIZONTAL);
            txtdesc.setText(tmp);
        }





        container.addView(item_view);

        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }


    //이미지를 불러와서 저장한다..
    private class loadImageTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... image) {

            /*PlaceImageModel tmp = image[0];*/
            Bitmap bitmap = null;

            for(int i = 0 ; i < mShowPlaceList.size(); i++) {
                try {
                    //System.out.println("---------main img : " + mShowItem.get(i).getMAIN_IMG());
                    if(mShowPlaceList.get(i).getPLACE_IMAGE_LIST() != null && mShowPlaceList.get(i).getPLACE_IMAGE_LIST().size() > 0) {

                        if ("null".equals(mShowPlaceList.get(i).getPLACE_IMAGE_LIST().get(0).getMAIN_IMG())) {
                            bitmap = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_no_photo);
                        } else {
                            bitmap = BitmapFactory.decodeStream((InputStream) new URL(mShowPlaceList.get(i).getPLACE_IMAGE_LIST().get(0).getMAIN_IMG()).getContent());
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
                mBitmapList.put(mShowPlaceList.get(i).getPLACE_ID(), bitmap);
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

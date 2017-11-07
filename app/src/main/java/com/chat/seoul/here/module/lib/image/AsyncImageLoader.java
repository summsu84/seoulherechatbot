package com.chat.seoul.here.module.lib.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.chat.seoul.here.module.adapter.CustomViewPagerListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by JJW on 2017-09-04.
 */

public class AsyncImageLoader extends AsyncTask<Void,Void,Bitmap> {
    public static final String TAG = "AsyncImageLoader";
    public String url;
    public ImageView img ;
    public CustomViewPagerListener customViewPagerListener = null;
    public HashMap<String, Bitmap> mBitmapList;
    public String mPlaceID = null;


    public AsyncImageLoader(ImageView img,String url){
        this.url = url;
        this.img = img;
        Log.i(TAG, "AsyncImageLoader value : " + url + ", imgObject : " + img);
    }

    public AsyncImageLoader(ImageView img,String url, CustomViewPagerListener listener){
        this.url = url;
        this.img = img;
        this.customViewPagerListener = listener;
        Log.i(TAG, "AsyncImageLoader value : " + url + ", imgObject : " + img + ", listener : " + listener);
    }

    /**
     *  이미지 로더 생성자
     * @param imgPlaceImage
     * @param imgUrl
     * @param bitmapList
     */
    public AsyncImageLoader(ImageView imgPlaceImage, String imgUrl, HashMap<String, Bitmap> bitmapList, String placeID) {
        this.url = imgUrl;
        this.img = imgPlaceImage;
        this.mBitmapList = bitmapList;
        this.mPlaceID = placeID;
        Log.i(TAG, "AsyncImageLoader value : " + url + ", imgObject : " + img + ", bitmapList : " + bitmapList + ", placeID : " + placeID);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        if(customViewPagerListener != null)
        {
            customViewPagerListener.onCustomViewPagerListenerStart();
        }

    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap bitmap = null;
        try {

            URL imageUrl = new URL(url);

            HttpURLConnection connection;
            if (url.startsWith("https://")) {
                connection = (HttpsURLConnection) imageUrl.openConnection();
            } else {
                connection = (HttpURLConnection) imageUrl.openConnection();
            }
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setInstanceFollowRedirects(true);

            InputStream is = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);


        }catch(Exception e){

        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        //super.onPostExecute(bitmap);
        if(img!=null){
            Log.i(TAG, "image is success bitmap : " + bitmap + ", imgViewAddress : " + img);
            img.setImageBitmap(bitmap);
        }else
        {
            Log.i(TAG, "image is failed..");
        }
        if(customViewPagerListener != null)
        {
            customViewPagerListener.onCustomViewPagerListenerEnd();
        }
    }
}
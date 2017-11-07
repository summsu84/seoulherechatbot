package com.chat.seoul.here.module.lib.common;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.chat.seoul.here.module.model.ChatMessage;

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

/**
 * Created by JJW on 2017-09-04.
 * HTTP Request 클래스 (Place 파싱용)
 *
 */

public class AsyncHttpRequest extends AsyncTask<String, Void, String> {
    public static final String TAG = "AsyncHttpRequest";

    public String url = null;
    public String method = null;
    public int action = -1;
    public AsyncRequestListener listener = null;
    public boolean isTrain = false;

/*    public AsyncHttpRequest(Context ctx, String url, String method){
        this.url = url;
        this.listener = (AsyncRequestListener)ctx;
        this.method = method;
        Log.i(TAG, "AsyncImageLoader value : " + url + ", listener : " + listener + ", method : " + method);
    }*/

    //요청하고, action 동작을 취한다.
    public AsyncHttpRequest(Context ctx, String method, int action){
        this.method = method;
        this.listener = (AsyncRequestListener)ctx;
        this.action = action;
        Log.i(TAG, "AsyncImageLoader value : " + this.method + ", listener : " + listener + ", action : " + this.action);
    }

    //ArrayAdapter용
    public AsyncHttpRequest(ArrayAdapter ctx, String method, int action){
        this.method = method;
        this.listener = (AsyncRequestListener)ctx;
        this.action = action;
        Log.i(TAG, "AsyncImageLoader value : " + this.method + ", listener : " + listener + ", action : " + this.action);
    }

    public AsyncHttpRequest(Context ctx, String method){

        this.listener = (AsyncRequestListener)ctx;
        this.method = method;
        Log.i(TAG, "AsyncImageLoader listener : " + listener + ", method : " + method);
    }

    public AsyncHttpRequest(Context ctx, String method, boolean isTrain){

        this.listener = (AsyncRequestListener)ctx;
        this.method = method;
        this.isTrain = isTrain;
        Log.i(TAG, "AsyncImageLoader listener : " + listener + ", method : " + method);
    }




    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        listener.onStartRequest();
    }

    @Override
    protected String doInBackground(final String... params) {
        String x_coord = "0";
        String y_coord = "0";
        Double roundDistance = 1.0;
        String UrlText;
        String responseValue = "";
        if("POST".equals(method)) {
            x_coord = params[0];
            y_coord = params[1];
            if (url != null) {
                UrlText = url;
            } else {
                UrlText = params[2];
            }
            roundDistance = Double.parseDouble(params[3]);

            URL url = null;

            JSONObject jsonObject = new JSONObject();
            JSONObject requestData = new JSONObject();
            try {
                JSONObject place = new JSONObject();
                place.put("x_coord", x_coord);
                place.put("y_coord", y_coord);
                JSONObject requesttype = new JSONObject();
                requesttype.put("requestType", "request-currentPlace");
                jsonObject.put("place", place);
                jsonObject.put("round", roundDistance);                 //반경 기본 셋팅 1km
                jsonObject.put("requestType", requesttype);
                requestData.put("requestData", jsonObject);

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

            Log.i(TAG, "AsyncHttpRequest Post... x_coord : " + x_coord + ", y_coord : " + y_coord + ", URI : " + UrlText + ", sAround : " + roundDistance);

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
        }else if ("GET".equals(method))
        {
            //GET
            UrlText = params[0];
            URL url = null;

            try {
                url = new URL(UrlText);
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
        }else if ("PUT".equals(method))
        {
            //PUT
            UrlText = params[0];
            String jsonData = null;
            if(params.length > 1) {
                jsonData = params[1];
            }
            URL url = null;

            JSONObject requestData = null;

            //Data가 있는 경우..
            if(jsonData != null) {
                try {
                    requestData = new JSONObject(jsonData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String postData = requestData.toString();
            }

            try {
                url = new URL(UrlText);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("PUT");
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
        }
        return responseValue;
    }

    @Override
    protected void onPostExecute(final String response) {

        if (response != null && response.length() > 0) {
            System.out.println(">>>Response : " + response);

            if("PUT".equals(method))
            {
                listener.onPutEndRequest(action, PlaceParser.getInstance().parseFromWebAPI(response));
            }else {
                if(isTrain)
                    listener.onEndRequest(new ChatMessage());
                else
                    listener.onEndRequest(PlaceParser.getInstance().parseFromWebAPI(response));
            }

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
    }
};


package com.chat.seoul.here.module.lib.app;

/***********************************************************************************************************************
 * API.AI Android SDK -  API.AI libraries usage example
 * =================================================
 * <p/>
 * Copyright (C) 2015 by Speaktoit, Inc. (https://www.speaktoit.com)
 * https://www.api.ai
 * <p/>
 * **********************************************************************************************************************
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ***********************************************************************************************************************/

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.chat.seoul.here.module.lib.common.AppPlaceInfo;
import com.chat.seoul.here.module.lib.conf.SettingsManager;
import com.chat.seoul.here.module.map.inf.DialogCommonListener;

import ai.api.util.BluetoothController;

public class AIApplication extends Application {

    private static final String TAG = AIApplication.class.getSimpleName();

    private int activitiesCount;
    private BluetoothControllerImpl bluetoothController;
    private SettingsManager settingsManager;

    //PlaceModel
    private AppPlaceInfo appPlaceInfo;
    //Test
    private DialogCommonListener listener;

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothController = new BluetoothControllerImpl(this);
        settingsManager = new SettingsManager(this);
        appPlaceInfo = new AppPlaceInfo();
    }

    public BluetoothController getBluetoothController() {
        return bluetoothController;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public void onActivityResume() {
        if (activitiesCount++ == 0) { // on become foreground
            if (settingsManager.isUseBluetooth()) {
                bluetoothController.start();
            }
        }
    }

    public void onActivityPaused() {
        if (--activitiesCount == 0) { // on become background
            bluetoothController.stop();
        }
    }

    private boolean isInForeground() {
        return activitiesCount > 0;
    }

    private class BluetoothControllerImpl extends BluetoothController {

        public BluetoothControllerImpl(Context context) {
            super(context);
        }

        @Override
        public void onHeadsetDisconnected() {
            Log.d(TAG, "Bluetooth headset disconnected");
        }

        @Override
        public void onHeadsetConnected() {
            Log.d(TAG, "Bluetooth headset connected");

            if (isInForeground() && settingsManager.isUseBluetooth()
                    && !bluetoothController.isOnHeadsetSco()) {
                bluetoothController.start();
            }
        }

        @Override
        public void onScoAudioDisconnected() {
            Log.d(TAG, "Bluetooth sco audio finished");
            bluetoothController.stop();

            if (isInForeground() && settingsManager.isUseBluetooth()) {
                bluetoothController.start();
            }
        }

        @Override
        public void onScoAudioConnected() {
            Log.d(TAG, "Bluetooth sco audio started");
        }

    }

    public AppPlaceInfo getAppPlaceInfo()
    {
        return this.appPlaceInfo;
    }


    public void createOkCancelDialog(Context context, String title, String contetn, int requestCode)
    {
        listener = (DialogCommonListener) context;          //되나??
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);


        final int _requestCode = requestCode;

// AlertDialog 셋팅
        alertDialogBuilder
                .setMessage(contetn)
                .setCancelable(false)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //다음 액티비티로 이동
                                listener.onDialogOk(_requestCode);

                            }
                        })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //취소 버튼.
                                dialog.cancel();
                                listener.onDialogCancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void createOkDialog(Context context, String title, String content)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(content)
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialog, int id) {
                        // 다이얼로그를 취소한다
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();

        // 다이얼로그 보여주기
        alertDialog.show();
    }

}

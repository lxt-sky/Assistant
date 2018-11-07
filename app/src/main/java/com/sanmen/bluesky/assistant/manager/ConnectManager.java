package com.sanmen.bluesky.assistant.manager;

import com.sanmen.bluesky.assistant.base.BluetoothClient;
import com.sanmen.bluesky.assistant.MyApplication;

import java.util.UUID;

/**
 * @author lxt_bluesky
 * @date 2018/11/7
 * @description
 */
public class ConnectManager {


    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static BluetoothClient mClient;

    public static BluetoothClient getClient(){
        if (mClient==null){
            synchronized (ConnectManager.class){
                if (mClient==null){
                    mClient = new BluetoothClient(MyApplication.getInstance());
                }
            }
        }
        return mClient;
    }
}

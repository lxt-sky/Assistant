package com.sanmen.bluesky.assistant.manager;

import com.inuker.bluetooth.library.BluetoothClient;
import com.sanmen.bluesky.assistant.MyApplication;

/**
 * @author lxt_bluesky
 * @date 2018/11/5
 * @description
 */
public class ClientManager {

    private static BluetoothClient mClient;

    public static BluetoothClient getClient(){
        if (mClient==null){
            synchronized (ClientManager.class){
                if (mClient==null){
                    mClient = new BluetoothClient(MyApplication.getInstance());
                }
            }
        }
        return mClient;
    }
}

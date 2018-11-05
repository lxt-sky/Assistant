package com.sanmen.bluesky.assistant;

import android.app.Application;

import com.inuker.bluetooth.library.BluetoothContext;
import com.sanmen.bluesky.assistant.manager.PaperManager;

/**
 * @author lxt_bluesky
 * @date 2018/11/5
 * @description
 */
public class MyApplication extends Application {
    private static MyApplication instance;

    public static Application getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        BluetoothContext.set(this);
        PaperManager.getPaperManager().init(this);
    }
}

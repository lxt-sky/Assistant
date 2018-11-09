package com.sanmen.bluesky.assistant;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothContext;
import com.sanmen.bluesky.assistant.manager.PaperManager;
import com.sanmen.bluesky.assistant.ui.activities.MainActivity;
import com.sanmen.bluesky.assistant.utils.PermissionUtil;

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
        PaperManager manager=PaperManager.getPaperManager();
        manager.init(this);
        if (!manager.isNeedGuide()){
            checkMyPermission();
        }
    }

    private void checkMyPermission() {
        String permissionText="";

        if (!PermissionUtil.hasPermission(this, Manifest.permission.BLUETOOTH)) {
            permissionText+=" 蓝牙";
        }
        if (!PermissionUtil.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            permissionText+=" 位置";
        }
        if (!PermissionUtil.hasPermission(this, Manifest.permission.CALL_PHONE)){
            permissionText+=" 通话";
        }
        if (!PermissionUtil.hasPermission(this, Manifest.permission.SEND_SMS)){
            permissionText+=" 短信";
        }
        if (permissionText!=""){
            Toast.makeText(this,"缺少"+permissionText+",请确认",Toast.LENGTH_LONG).show();
        }
    }
}

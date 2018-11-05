package com.sanmen.bluesky.assistant.utils;

import android.content.Context;
import android.content.Intent;

import com.sanmen.bluesky.assistant.ui.activities.BluetoothApplyActivity;
import com.sanmen.bluesky.assistant.ui.activities.BluetoothConnectActivity;
import com.sanmen.bluesky.assistant.ui.activities.ConnectEmptyActivity;
import com.sanmen.bluesky.assistant.ui.activities.MainActivity;
import com.sanmen.bluesky.assistant.ui.activities.SettingActivity;

/**
 * @author lxt_bluesky
 * @date 2018/10/30
 * @description
 */
public class SwitchUtil {

    public static void switchToMainActivity(Context activity,String mac){
        Intent intent = new Intent(activity,MainActivity.class);
        intent.putExtra("MAC_ADDRESS",mac);
        activity.startActivity(intent);
    }

    public static void switchToSettingActivity(Context context){
        Intent intent = new Intent(context,SettingActivity.class);
        context.startActivity(intent);
    }

    public static void switchToBluetoothActivity(Context context){
        Intent intent = new Intent(context,BluetoothApplyActivity.class);
        context.startActivity(intent);
    }

    public static void switchToConnectEmptyActivity(Context context){
        Intent intent = new Intent(context,ConnectEmptyActivity.class);
        context.startActivity(intent);
    }

    public static void switchToBluetoothConnectActivity(Context context){
        Intent intent = new Intent(context,BluetoothConnectActivity.class);
        context.startActivity(intent);
    }
}

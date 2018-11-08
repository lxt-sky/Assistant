package com.sanmen.bluesky.assistant.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import com.sanmen.bluesky.assistant.ui.activities.BluetoothApplyActivity;
import com.sanmen.bluesky.assistant.ui.activities.BluetoothConnectActivity;
import com.sanmen.bluesky.assistant.ui.activities.ConnectEmptyActivity;
import com.sanmen.bluesky.assistant.ui.activities.DeviceActivity;
import com.sanmen.bluesky.assistant.ui.activities.DeviceDetailActivity;
import com.sanmen.bluesky.assistant.ui.activities.DeviceListActivity;
import com.sanmen.bluesky.assistant.ui.activities.MainActivity;
import com.sanmen.bluesky.assistant.ui.activities.SettingActivity;

/**
 * @author lxt_bluesky
 * @date 2018/10/30
 * @description
 */
public class SwitchUtil {

    public static void switchToMainActivity(Context activity, String mac) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra("MAC_ADDRESS", mac);
        activity.startActivity(intent);
    }

    public static void switchToDeviceActivity(Context activity, String mac) {
        Intent intent = new Intent(activity, DeviceActivity.class);
        intent.putExtra("MAC_ADDRESS", mac);
        activity.startActivity(intent);
    }

    public static void switchToDeviceDetailActivity(Context activity, String mac) {
        Intent intent = new Intent(activity, DeviceDetailActivity.class);
        intent.putExtra("MAC_ADDRESS", mac);
        activity.startActivity(intent);
    }

    public static void switchToSettingActivity(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    public static void switchToBluetoothActivity(Context context) {
        Intent intent = new Intent(context, BluetoothApplyActivity.class);
        context.startActivity(intent);
    }

    public static void switchToConnectEmptyActivity(Context context) {
        Intent intent = new Intent(context, ConnectEmptyActivity.class);
        context.startActivity(intent);
    }

    public static void switchToBluetoothConnectActivity(Context context) {
        Intent intent = new Intent(context, BluetoothConnectActivity.class);
        context.startActivity(intent);
    }

    public static void switchToDeviceListActivity(Context context){
        Intent intent = new Intent(context, DeviceListActivity.class);
        context.startActivity(intent);
    }

    public static boolean switchToCallPhone(Context context, String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return false;
        }
        context.startActivity(intent);
        return true;
    }
}

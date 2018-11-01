package com.sanmen.bluesky.assistant.utils;

import android.content.Context;
import android.content.Intent;

import com.sanmen.bluesky.assistant.ui.activities.MainActivity;
import com.sanmen.bluesky.assistant.ui.activities.SettingActivity;

/**
 * @author lxt_bluesky
 * @date 2018/10/30
 * @description
 */
public class SwitchUtil {

    public static void switchToMainActivity(Context activity){
        Intent intent = new Intent(activity,MainActivity.class);
        activity.startActivity(intent);
    }

    public static void switchToSettingActivity(Context context){
        Intent intent = new Intent(context,SettingActivity.class);
        context.startActivity(intent);
    }
}

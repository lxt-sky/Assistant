package com.sanmen.bluesky.assistant.utils;

import android.content.Context;
import android.content.Intent;

import com.sanmen.bluesky.assistant.ui.activities.MainActivity;

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
}

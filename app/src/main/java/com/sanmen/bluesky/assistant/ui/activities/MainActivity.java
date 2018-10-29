package com.sanmen.bluesky.assistant.ui.activities;

import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.os.Bundle;

import com.sanmen.bluesky.assistant.R;
import com.sanmen.bluesky.assistant.base.BaseActivity;

/**
 * @author lxt_bluesky
 * @date 2018/10/29
 * @description
 */
public class MainActivity extends BaseActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        setContentView(R.layout.activity_main2);

    }
}

package com.sanmen.bluesky.assistant.ui.activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.sanmen.bluesky.assistant.R;
import com.sanmen.bluesky.assistant.base.BaseActivity;
import com.sanmen.bluesky.assistant.utils.SwitchUtil;

/**
 * @author lxt_bluesky
 * @date 2018/11/5
 * @description
 */
public class ConnectEmptyActivity extends BaseActivity implements View.OnClickListener {

    Button btnPair;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        setContentView(R.layout.activity_main);
        initTitleBar();

        initLayout();

    }

    private void initLayout() {

        btnPair = findViewById(R.id.btnPair);
        btnPair.setOnClickListener(this);
    }

    @Override
    public void onRightClick(View view) {
        super.onRightClick(view);
        SwitchUtil.switchToSettingActivity(this);
    }

    @Override
    public void onClick(View v) {

        SwitchUtil.switchToBluetoothConnectActivity(this);
        finish();
    }
}

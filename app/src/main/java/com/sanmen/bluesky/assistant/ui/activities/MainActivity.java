package com.sanmen.bluesky.assistant.ui.activities;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.sanmen.bluesky.assistant.R;
import com.sanmen.bluesky.assistant.base.BaseActivity;
import com.sanmen.bluesky.assistant.manager.ClientManager;
import com.sanmen.bluesky.assistant.utils.SwitchUtil;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;

/**
 * @author lxt_bluesky
 * @date 2018/10/29
 * @description
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private String MAC;
    BluetoothDevice mDevice;

    TextView tvDeviceName;
    TextView tvDeviceAddress;
    TextView tvDeviceState;
    TextView tvReConnect;
    private boolean mConnected;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        setContentView(R.layout.activity_main2);
        initTitleBar();
        initLayout();
        obtainParams();
        initData();


    }

    private void initLayout() {
        tvDeviceState = findViewById(R.id.tvDeviceState);
        tvDeviceAddress = findViewById(R.id.tvDeviceAddress);
        tvDeviceName = findViewById(R.id.tvDeviceName);
        tvReConnect = findViewById(R.id.tvReConnect);
        tvReConnect.setOnClickListener(this);
    }

    private void obtainParams() {
        Intent intent = getIntent();
        if (intent!=null){
            MAC = intent.getStringExtra("MAC_ADDRESS");
        }
    }

    private void initData() {
        if (MAC==null){
            return;
        }
        mDevice = BluetoothUtils.getRemoteDevice(MAC);
        if (mDevice!=null){
            String name = mDevice.getName();
            String address = mDevice.getAddress();
            tvDeviceName.setText(name!=null?name:"无");
            tvDeviceAddress.setText(address);
        }

        showProgressDialog("加载中");
        ClientManager.getClient().registerConnectStatusListener(mDevice.getAddress(), mConnectStatusListener);
        connectDeviceIfNeeded();
    }

    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {

            mConnected = (status == STATUS_CONNECTED);
            tvDeviceState.setText(mConnected?"已连接":"连接失败");
//            connectDeviceIfNeeded();
        }
    };

    private void connectDeviceIfNeeded() {
        if (!mConnected) {
            connectDevice();
        }
    }

    private void connectDevice() {
        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(20000)
                .setServiceDiscoverRetry(3)
                .setServiceDiscoverTimeout(10000)
                .build();
        ClientManager.getClient().connect(mDevice.getAddress(),options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                //如果连接不成功则重试
                if (code==REQUEST_SUCCESS){
//                    oldPosition=position;
                    //                    setGattProfile(data);
                    tvReConnect.setVisibility(View.GONE);
                }else {
                    //                    connectDeviceIfNeeded();
                    tvReConnect.setVisibility(View.VISIBLE);
                }
                dismissProgressDialog();
            }
        });
    }


    @Override
    public void onRightClick(View view) {
        super.onRightClick(view);
        SwitchUtil.switchToSettingActivity(this);
    }

    @Override
    public void onClick(View v) {
        //重连
        connectDeviceIfNeeded();
    }
}

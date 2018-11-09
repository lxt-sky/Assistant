package com.sanmen.bluesky.assistant.ui.activities;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.sanmen.bluesky.assistant.R;
import com.sanmen.bluesky.assistant.base.BaseActivity;
import com.sanmen.bluesky.assistant.entity.LocationDataBean;
import com.sanmen.bluesky.assistant.manager.PaperManager;
import com.sanmen.bluesky.assistant.ui.adapter.HistoryAdapter;

import com.sanmen.bluesky.assistant.service.BluetoothLeService;

import java.util.List;
import java.util.UUID;

/**
 * @author lxt_bluesky
 * @date 2018/11/7
 * @description
 */
public class DeviceActivity extends BaseActivity implements View.OnClickListener {

    private String MAC;
    BluetoothDevice mDevice;

    TextView tvDeviceName;
    TextView tvDeviceAddress;
    TextView tvDeviceState;
    TextView tvReConnect;
    TextView tvData;
    TextView tvHistoryClear;
    RecyclerView rvHistoryList;
    private boolean mConnected;

    HistoryAdapter historyAdapter;

    List<LocationDataBean> dataBeanList;

    private BluetoothLeService mBluetoothLeService;


    private final static UUID MY_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    private BluetoothGattCharacteristic readCharacteristic;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_device);
        initTitleBar();
        obtainParams();
        initLayout();
        initData();
    }

    private void initLayout() {
        tvDeviceState = findViewById(R.id.tvDeviceState);
        tvDeviceAddress = findViewById(R.id.tvDeviceAddress);
        tvDeviceName = findViewById(R.id.tvDeviceName);
        tvReConnect = findViewById(R.id.tvReConnect);
        tvData = findViewById(R.id.tvData);
        rvHistoryList = findViewById(R.id.rvHistoryList);
        tvHistoryClear = findViewById(R.id.tvHistoryClear);
        tvReConnect.setOnClickListener(this);
        tvHistoryClear.setOnClickListener(this);

        historyAdapter = new HistoryAdapter(dataBeanList);
        rvHistoryList.setLayoutManager(new LinearLayoutManager(this));
        rvHistoryList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvHistoryList.setAdapter(historyAdapter);

    }

    private void obtainParams() {
        Intent intent = getIntent();
        if (intent != null) {
            MAC = intent.getStringExtra("MAC_ADDRESS");
        }

        dataBeanList = PaperManager.getPaperManager().getHistoryList();

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

        //绑定服务
        Intent intent = new Intent(DeviceActivity.this,BluetoothLeService.class);
        bindService(intent,mServiceConnection,Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onClick(View v) {

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.MyBinder) service).getService();
            if (!mBluetoothLeService.initialize()){
                //连接失败
            }
            mBluetoothLeService.connect(MAC);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)){
                //连接成功
                mConnected = true;

                updateConnectionState(R.string.connected);
//                invalidateOptionsMenu();

            }else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)){
                //连接失败
//                mConnected = false;
                updateConnectionState(R.string.disconnected);
//                invalidateOptionsMenu();
//                clearUI();

            }else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)){

                Toast.makeText(DeviceActivity.this,"发现服务",Toast.LENGTH_LONG).show();
                //发现有可支持的服务和characteristic,读数据服务.
                readCharacteristic = findCharacteristic();
                if (readCharacteristic!=null){
                    mBluetoothLeService.readCharacteristic(readCharacteristic);
                }
//                BluetoothGattService readGattService =mBluetoothLeService.getSupportedGattService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));
//                BluetoothGattService readGattService =mBluetoothLeService.getSupportedGattService(MY_UUID);
//                readCharacteristic = readGattService.getCharacteristic(UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb"));

            }else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)){
                //将读到的数据进行显示
//                String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
//                System.out.println("data----" + data);
//                displayData(data);
            }
        }
    };

    /**
     * 发现可用特征值,用于后续数据通信
     * @return
     */
    private BluetoothGattCharacteristic findCharacteristic() {
        List<BluetoothGattService> gattServiceList= mBluetoothLeService.getSupportedGattServices();
        for (BluetoothGattService gattService:gattServiceList){
            List<BluetoothGattCharacteristic> characteristicList = gattService.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic:characteristicList){
                if (MY_UUID.toString().equalsIgnoreCase(characteristic.getUuid().toString())){
                    return characteristic;
                }
            }
        }

        return null;
    }

    /**
     * 更新界面UI
     * @param connected
     */
    private void updateConnectionState(int connected) {
        tvDeviceState.setText(mConnected?"连接成功":"连接失败");
    }

    /**
     * 注册广播接收事件
     * @return
     */
    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        return intentFilter;
    }


    @Override
    protected void onResume() {
        super.onResume();
        //注册广播
        registerReceiver(mGattUpdateReceiver,makeGattUpdateIntentFilter());
        if (mBluetoothLeService!=null&&!MAC.isEmpty()){
            mBluetoothLeService.connect(MAC);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //注销广播
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBluetoothLeService!=null){
            unbindService(mServiceConnection);
            mBluetoothLeService=null;
        }
    }
}

package com.sanmen.bluesky.assistant.ui.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sanmen.bluesky.assistant.R;
import com.sanmen.bluesky.assistant.base.BaseActivity;
import com.sanmen.bluesky.assistant.entity.BluetoothDeviceBean;
import com.sanmen.bluesky.assistant.manager.ClientManager;
import com.sanmen.bluesky.assistant.ui.adapter.BluetoothDeviceAdapter;
import com.sanmen.bluesky.assistant.utils.SwitchUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lxt_bluesky
 * @date 2018/11/8
 * @description
 */
public class DeviceListActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    Switch switchBluetooth;

    RecyclerView rvDeviceList;

    Button btnSave;

    BluetoothAdapter bluetoothAdapter;

    BluetoothDeviceAdapter deviceAdapter;

    List<BluetoothDevice> deviceList = new ArrayList<>();

    List<String> addressList =  new ArrayList<>();

    private boolean isBluetoothOpen=false;

    private static final int REQUEST_CODE=2;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        setContentView(R.layout.activity_bluetooth_apply);
        initTitleBar();

        initLayout();
        initData();
    }


    private void initData() {
        isBluetoothOpen = bluetoothAdapter.isEnabled();
        switchBluetooth.setChecked(isBluetoothOpen);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
    }

    private void initLayout() {
        switchBluetooth = findViewById(R.id.switchBluetooth);
        rvDeviceList = findViewById(R.id.rvDeviceList);
        btnSave = findViewById(R.id.btnSave);

        deviceAdapter = new BluetoothDeviceAdapter(new ArrayList<BluetoothDeviceBean>());
        rvDeviceList.setLayoutManager(new LinearLayoutManager(this));
        rvDeviceList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvDeviceList.setAdapter(deviceAdapter);

        deviceAdapter.setOnItemChildClickListener(this);
        btnSave.setOnClickListener(this);
        switchBluetooth.setOnCheckedChangeListener(this);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        //到设备详情

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        if (deviceList.size()!=0){
            BluetoothDevice device = deviceList.get(position);
            SwitchUtil.switchToDeviceDetailActivity(this,device.getAddress());
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked){
            toOpenBluetooth();
        }else {
            toCloseBluetooth();
        }
    }

    private void toCloseBluetooth() {
        deviceList.clear();
        addressList.clear();
        bluetoothAdapter.cancelDiscovery();
        deviceAdapter.setNewData(new ArrayList<BluetoothDeviceBean>());
        deviceAdapter.notifyDataSetChanged();
    }

    private void toOpenBluetooth() {
        if (bluetoothAdapter!=null){
            if (!bluetoothAdapter.isEnabled()){
                Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(bluetoothIntent,REQUEST_CODE);

            }else {
                //去搜索蓝牙设备列表
                getBluetoothList();
            }


            //同时扫描且能被发现
            if(bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                i.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
                startActivity(i);
            }
        }else {
            Toast.makeText(this,"当前设备不支持蓝牙!",Toast.LENGTH_SHORT).show();
        }
    }

    private void getBluetoothList() {
        if (bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==REQUEST_CODE){
            if (resultCode==Activity.RESULT_OK){
                Toast.makeText(this, "蓝牙已经开启", Toast.LENGTH_SHORT).show();
                this.isBluetoothOpen = true;
                //提出设备信息
                getBluetoothList();
            }else if (resultCode==Activity.RESULT_CANCELED){
                Toast.makeText(this, "没有蓝牙权限,请前往打开蓝牙", Toast.LENGTH_SHORT).show();
                this.isBluetoothOpen = false;
            }
        }
        switchBluetooth.setChecked(isBluetoothOpen);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (addressList.indexOf(device.getAddress())==-1){
                    BluetoothDeviceBean bean = new BluetoothDeviceBean();
                    bean.setDevice(device);
                    bean.setState(false);

                    deviceAdapter.addData(bean);
                    deviceList.add(device);
                    addressList.add(device.getAddress());
                    deviceAdapter.notifyDataSetChanged();
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {


            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

            }else {

            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);
    }
}

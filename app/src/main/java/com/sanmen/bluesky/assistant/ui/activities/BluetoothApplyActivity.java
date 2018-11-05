package com.sanmen.bluesky.assistant.ui.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sanmen.bluesky.assistant.R;
import com.sanmen.bluesky.assistant.base.BaseActivity;
import com.sanmen.bluesky.assistant.entity.BluetoothDeviceBean;
import com.sanmen.bluesky.assistant.ui.adapter.BluetoothDeviceAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lxt_bluesky
 * @date 2018/11/1
 * @description
 */
public class BluetoothApplyActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, BaseQuickAdapter.OnItemChildClickListener {

    Switch switchBluetooth;

    RecyclerView rvDeviceList;

    Button btnSave;

    BluetoothAdapter bluetoothAdapter;

    BluetoothGatt mBluetoothGatt;

    BluetoothDeviceAdapter deviceAdapter;

    private static final int REQUEST_CODE=2;

    private static final int STATE_DISCONNECTED = 0;

    private static final int STATE_CONNECTING = 1;

    private static final int STATE_CONNECTED = 2;

    List<BluetoothDeviceBean> deviceBeanList = new ArrayList<>();

    List<BluetoothDevice> deviceList = new ArrayList<>();

    List<String> addressList =  new ArrayList<>();

    private boolean isBluetoothOpen=false;

    private int mConnectionState = STATE_DISCONNECTED;

    private boolean mScanning =false;

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

    /**
     * 打开蓝牙
     */
    private void toOpenBluetooth() {
        if (bluetoothAdapter!=null){
            if (!bluetoothAdapter.isEnabled()){
                Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(bluetoothIntent,REQUEST_CODE);

            }else {
                //去搜索蓝牙设备列表
                getBluetoothList();
            }
        }else {
            Toast.makeText(this,"当前设备不支持蓝牙!",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 关闭蓝牙
     */
    private void toCloseBluetooth() {
        isBluetoothOpen=false;
        //清空设备列表
        deviceBeanList.clear();
        addressList.clear();
        //停止扫描设备
        scanLeService(false);
        //关闭蓝牙服务
        bluetoothAdapter.disable();
        deviceAdapter.setNewData(new ArrayList<BluetoothDeviceBean>());
        deviceAdapter.notifyDataSetChanged();
    }

    /**
     * 获取蓝牙设备列表
     */
    private void getBluetoothList() {
        deviceBeanList.clear();
        addressList.clear();

        scanLeService(true);
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

    /**
     * 扫描设备
     * @param enable
     */
    private void scanLeService(boolean enable){
        if (enable){
            Handler mHandler = new Handler();
            //8秒后停止扫描
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning=false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                }
            },8000);

            mScanning=true;
            bluetoothAdapter.startLeScan(leScanCallback);
        }else {
            mScanning=false;
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            //更新UI
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (addressList.indexOf(device.getAddress())==-1){
//                        BluetoothDeviceBean bean = new BluetoothDeviceBean();
//                        bean.setDeviceName(device.getName());
//                        bean.setMacAddress(device.getAddress());
//
//                        if (device.getBondState()==BluetoothDevice.BOND_BONDED){
//                            Log.e(".Device",device.getName()+device.getAddress());
//                            bean.setState(true);
//                        }else{
//                            //未配对
//                            bean.setState(false);
//
//                        }
//                        deviceAdapter.addData(device);
                        deviceAdapter.notifyDataSetChanged();
                        //将搜索到的设备添加至列表
                        addressList.add(device.getAddress());
//                        deviceBeanList.add(bean);

                        deviceList.add(device);
                    }
                }
            });

        }
    };

    /**
     * 设备列表点击事件处理
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        BluetoothDevice device = deviceList.get(position);
        //连接设备
        mBluetoothGatt = device.connectGatt(this,false,gattCallback);

        bluetoothAdapter.stopLeScan(leScanCallback);
    }

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        //连接状态改变回调
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState==BluetoothProfile.STATE_CONNECTED){
                //蓝牙设备已连接,连接成功后启动服务发现
                Log.e(".ApplyActivity", "启动服务发现:" + mBluetoothGatt.discoverServices());
            }else if (newState==BluetoothProfile.STATE_DISCONNECTED){
                //蓝牙设备未连接
                mBluetoothGatt.connect();

            }
        }
        //发现服务回调
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status==BluetoothGatt.GATT_SUCCESS){
                Log.e(".ApplyActivity", "成功发现服务");
            }else{
                Log.e(".ApplyActivity", "服务发现失败，错误码为:" + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }
    };

}

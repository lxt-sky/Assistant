package com.sanmen.bluesky.assistant.ui.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sanmen.bluesky.assistant.R;
import com.sanmen.bluesky.assistant.base.BaseFragment;
import com.sanmen.bluesky.assistant.entity.BluetoothDeviceBean;
import com.sanmen.bluesky.assistant.ui.adapter.BluetoothDeviceAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lxt_bluesky
 * @date 2018/10/29
 * @description
 */
public class BluetoothSearchFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener,BluetoothDeviceAdapter.OnItemClickListener {

    View rootView;
    Switch switchBluetooth;
    RecyclerView rvBluetoothList;

    private BluetoothAdapter bluetoothAdapter;
    /**
     * 设备列表适配器
     */
    private BluetoothDeviceAdapter deviceAdapter;

    private Activity activity;

    List<BluetoothDeviceBean> deviceBeanList = new ArrayList<>();

    List<String> addressList =  new ArrayList<>();

    private boolean isBluetoothOpen=false;

    private static final int REQUEST_CODE=1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView==null){
            rootView=inflater.inflate(R.layout.fragment_guide_item2,container,false);
        }
        activity = getActivity();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initLayout();
//        toSearchBluetooth();
        initData();
    }

    /**
     * 页面初始化
     */
    private void initLayout() {
        switchBluetooth = rootView.findViewById(R.id.switchBluetooth);
        rvBluetoothList = rootView.findViewById(R.id.rvBluetoothList);
        //初始化设备列表
        deviceAdapter = new BluetoothDeviceAdapter(new ArrayList<BluetoothDeviceBean>());
        rvBluetoothList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvBluetoothList.setAdapter(deviceAdapter);
        deviceAdapter.setOnItemClickListener(this);

        //获取蓝牙适配器实例
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //注册蓝牙搜索和接收广播
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        activity.getApplicationContext().registerReceiver(mReceiver,filter);
//        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        activity.getApplicationContext().registerReceiver(mReceiver,filter1);

        switchBluetooth.setOnCheckedChangeListener(this);
    }

    /**
     * 数据初始化
     */
    private void initData() {
        if (bluetoothAdapter.isEnabled()){
            switchBluetooth.setChecked(true);
        }

    }

    /**
     * 打开并搜索蓝牙
     */
    private void toSearchBluetooth() {
        //询问打开蓝牙
        if (bluetoothAdapter!=null){
            if (!bluetoothAdapter.isEnabled()){
                Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(bluetoothIntent,REQUEST_CODE);

            }else {
                //去搜索蓝牙设备列表
                getBluetoothList();
            }
        }
    }

    /**
     * 获取蓝牙设备列表
     */
    private void getBluetoothList() {
        //清空列表
        deviceBeanList.clear();
        addressList.clear();
//        showProgressDialog("加载中");
        //开始搜索蓝牙设备
//        BluetoothLeScanner scanner = bluetoothAdapter.getBluetoothLeScanner();
//        scanner.startScan(scanCallback);
        //第二种扫描方法
        bluetoothAdapter.startLeScan(leScanCallback);

//        bluetoothAdapter.startDiscovery();
    }

    /**
     * 关闭蓝牙
     */
    private void toCloseBluetooth() {
        //清空设备列表
        deviceBeanList.clear();
        addressList.clear();

        rvBluetoothList.removeAllViews();
        bluetoothAdapter.stopLeScan(leScanCallback);
        deviceAdapter.setNewData(new ArrayList<BluetoothDeviceBean>());
//        BluetoothLeScanner scanner = bluetoothAdapter.getBluetoothLeScanner();
//        scanner.stopScan(scanCallback);
//        bluetoothAdapter.cancelDiscovery();
    }

    /**
     * switch开关事件处理
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        isBluetoothOpen = isChecked;
        if (isBluetoothOpen){
            toSearchBluetooth();
        }else {
            toCloseBluetooth();
        }
    }

    /**
     * 申请打开蓝牙请求回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE==requestCode){
            if (resultCode==Activity.RESULT_OK){
                Toast.makeText(getContext(), "蓝牙已经开启", Toast.LENGTH_SHORT).show();
                this.isBluetoothOpen = true;
                //提出设备信息
                String address = bluetoothAdapter.getAddress();
                String name = bluetoothAdapter.getName();

                getBluetoothList();

            }else if (resultCode==Activity.RESULT_CANCELED){
                Toast.makeText(getContext(), "没有蓝牙权限", Toast.LENGTH_SHORT).show();
                this.isBluetoothOpen = false;
            }
        }

        switchBluetooth.setChecked(isBluetoothOpen);
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (addressList.indexOf(device.getAddress())==-1){
                //已经配对
                BluetoothDeviceBean bean = new BluetoothDeviceBean();
                bean.setDeviceName(device.getName());
                bean.setMacAddress(device.getAddress());

                if (device.getBondState()==BluetoothDevice.BOND_BONDED){
                    Log.e(".Device",device.getName()+device.getAddress());
                    bean.setState(true);
                }else{
                    //未配对
                    bean.setState(false);

                }
                deviceAdapter.addData(bean);
                deviceAdapter.notifyDataSetChanged();

                addressList.add(device.getAddress());
                deviceBeanList.add(bean);
            }
        }
    };

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();

            if (addressList.indexOf(device.getAddress())==-1){
                //已经配对
                BluetoothDeviceBean bean = new BluetoothDeviceBean();
                bean.setDeviceName(device.getName());
                bean.setMacAddress(device.getAddress());

                if (device.getBondState()==BluetoothDevice.BOND_BONDED){
                    Log.e(".Device",device.getName()+device.getAddress());
                    bean.setState(true);
                }else{
                    //未配对
                    bean.setState(false);

                }
                deviceAdapter.addData(bean);
                deviceAdapter.notifyDataSetChanged();

                addressList.add(device.getAddress());
                deviceBeanList.add(bean);
            }

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);

        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Toast.makeText(getContext(),"蓝牙搜索失败",Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 定义广播接收实例
     */
//    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//
//            if (action.equals(BluetoothDevice.ACTION_FOUND)){
//
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                if (addressList.indexOf(device.getAddress())==-1){
//                    //已经配对
//                    BluetoothDeviceBean bean = new BluetoothDeviceBean();
//                    bean.setDeviceName(device.getName());
//                    bean.setMacAddress(device.getAddress());
//
//                    if (device.getBondState()==BluetoothDevice.BOND_BONDED){
//                        Log.e(".Device",device.getName()+device.getAddress());
//                        bean.setState(true);
//                    }else{
//                        //未配对
//                        bean.setState(false);
//
//                    }
//                    addressList.add(device.getAddress());
//                    deviceBeanList.add(bean);
//                }
//
//            }else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
//                Toast.makeText(getContext(),"搜索完成",Toast.LENGTH_SHORT).show();
//                //展示列表
//                if (deviceBeanList.size()!=0){
//                    deviceAdapter.setNewData(deviceBeanList);
//                }
//                dismissProgressDialog();
//            }
//        }
//    };

    @Override
    public void onDestroy() {
        super.onDestroy();
//        activity.getApplicationContext().unregisterReceiver(mReceiver);
        Log.e("destory","解除注册");
    }

    /**
     * 设备列表Item点击事件处理
     * @param adapter
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }
}

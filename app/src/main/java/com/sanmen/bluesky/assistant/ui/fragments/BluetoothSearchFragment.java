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
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.sanmen.bluesky.assistant.R;
import com.sanmen.bluesky.assistant.base.BaseFragment;
import com.sanmen.bluesky.assistant.entity.BluetoothDeviceBean;
import com.sanmen.bluesky.assistant.manager.ClientManager;
import com.sanmen.bluesky.assistant.ui.adapter.BluetoothDeviceAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;

/**
 * @author lxt_bluesky
 * @date 2018/10/29
 * @description
 */
public class BluetoothSearchFragment extends BaseFragment implements CompoundButton.OnCheckedChangeListener,BluetoothDeviceAdapter.OnItemChildClickListener {

    View rootView;
    Switch switchBluetooth;
    RecyclerView rvBluetoothList;

    private BluetoothAdapter bluetoothAdapter;
    /**
     * 设备列表适配器
     */
    private BluetoothDeviceAdapter deviceAdapter;

    private Activity activity;

    private BluetoothClient mClient;

    List<BluetoothDeviceBean> deviceBeanList = new ArrayList<>();

    List<String> addressList =  new ArrayList<>();

    List<BluetoothDevice> deviceList = new ArrayList<>();

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

//        mClient = ClientManager.getClient();
        initLayout();
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
        deviceAdapter.setOnItemChildClickListener(this);

        mClient= new BluetoothClient(getActivity());
        mClient.registerBluetoothStateListener(mBluetoothStateListener);

        switchBluetooth.setOnCheckedChangeListener(this);
    }

    /**
     * 数据初始化
     */
    private void initData() {

        if (mClient.isBluetoothOpened()){
            switchBluetooth.setChecked(true);
        }
    }

    /**
     * 打开并搜索蓝牙
     */
    private void toSearchBluetooth() {
        if (!mClient.isBluetoothOpened()){
            mClient.openBluetooth();
        }else {
            getBluetoothList();
        }
    }

    /**
     * 获取蓝牙设备列表
     */
    private void getBluetoothList() {
        //清空列表
        deviceList.clear();
        addressList.clear();
//        showProgressDialog("加载中");

        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(3000,3)
                .searchBluetoothClassicDevice(5000)
                .searchBluetoothLeDevice(2000)
                .build();

        mClient.search(request,searchResponse);
    }

    /**
     * 关闭蓝牙
     */
    private void toCloseBluetooth() {
        //清空设备列表
        deviceList.clear();
        addressList.clear();
        //停止扫描
        mClient.stopSearch();
        mClient.closeBluetooth();

        deviceAdapter.setNewData(new ArrayList<BluetoothDeviceBean>());
        deviceAdapter.notifyDataSetChanged();
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

    private SearchResponse searchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            deviceList.clear();
            addressList.clear();
        }

        @Override
        public void onDeviceFounded(SearchResult device) {
            BluetoothDevice bluetoothDevice = device.device;

            if (addressList.indexOf(device.getAddress())==-1){

//                deviceAdapter.addData();
                deviceAdapter.notifyDataSetChanged();

                addressList.add(device.getAddress());
                deviceList.add(bluetoothDevice);
            }
        }

        @Override
        public void onSearchStopped() {

        }

        @Override
        public void onSearchCanceled() {

        }
    };

    private final BluetoothStateListener mBluetoothStateListener = new BluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {

            if (openOrClosed){
                Toast.makeText(getContext(), "蓝牙已经开启", Toast.LENGTH_SHORT).show();
                isBluetoothOpen = true;

                getBluetoothList();
            }else {
                Toast.makeText(getContext(), "蓝牙已经关闭", Toast.LENGTH_SHORT).show();
                isBluetoothOpen = false;

            }

            switchBluetooth.setChecked(isBluetoothOpen);
        }

    };


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
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (deviceList!=null){
            BluetoothDevice device = deviceList.get(position);
            connectService(device);
        }
    }

    private void connectService(BluetoothDevice device) {
//        if (device.getBondState()==Constants.BOND_NONE){
//            try {
//                Method createMethod = BluetoothDevice.class.getMethod("createBond");
//                createMethod.invoke(device);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(30000)
                .setServiceDiscoverRetry(3)
                .setServiceDiscoverTimeout(20000)
                .build();
        mClient.connect(device.getAddress(),options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                //如果连接不成功则重试
                if (code==REQUEST_SUCCESS){
                    setGattProfile(data);
                }else {
                    connectDeviceIfNeeded();
                }
            }
        });
    }

    private void connectDeviceIfNeeded() {

    }

    private void setGattProfile(BleGattProfile data) {
        Log.e("获取属性",data.toString());
    }

}

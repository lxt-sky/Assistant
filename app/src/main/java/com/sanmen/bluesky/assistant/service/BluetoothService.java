package com.sanmen.bluesky.assistant.service;

import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lxt_bluesky
 * @date 2018/11/2
 * @description
 */
public class BluetoothService extends Service {

    IBinder iBinder = new MyBinder();

    BluetoothClient mClient;

    List<String> addressList =  new ArrayList<>();

    List<BluetoothDevice> deviceList = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mClient = new BluetoothClient(this);

        openBluetooth();
    }

    private void getBluetoothList() {
//        //清空列表
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

    /**
     * 获取当前蓝牙客户端
     * @return
     */
    public BluetoothClient getBluetooClient(){
        if (mClient==null){
            mClient = new BluetoothClient(this);
        }
        return mClient;
    }

    public void openBluetooth(){
        if (!mClient.isBluetoothOpened()){
            mClient.openBluetooth();
        }else {
            getBluetoothList();
        }
    }

    public void closeBluetooth(){
        deviceList.clear();
        addressList.clear();
        //停止扫描
        mClient.stopSearch();
        mClient.closeBluetooth();
    }

    /**
     * 解除绑定服务
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    private class MyBinder extends Binder {
        /**
         * 获取当前服务
         * @return
         */
        public BluetoothService getService(){
            return BluetoothService.this;
        }

    }
}

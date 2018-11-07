package com.sanmen.bluesky.assistant.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author lxt_bluesky
 * @date 2018/11/2
 * @description
 */
public class BluetoothLeService extends Service {
    //蓝牙设备地址
    private String mDeviceAddress;

    private BluetoothGatt mBluetoothGatt;

    private BluetoothAdapter mBluetoothAdapter;

    //当前连接状态
    private int mConnectionState = STATE_DISCONNECTED;
    //设备连接失败
    private static final int STATE_DISCONNECTED = 0;
    //设备正在连接
    private static final int STATE_CONNECTING = 1;
    //设备连接成功
    private static final int STATE_CONNECTED = 2;

    private final IBinder mBinder = new MyBinder();

    public static final String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED  = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public static final String ACTION_DATA_AVAILABLE   = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE ";
    public static final String EXTRA_DATA    = "com.example.bluetooth.le.EXTRA_DATA  ";
    private final static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /**
     * 通过BLE API的不同类型的回调方法
     */

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        //连接状态改变回调
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            String intentAction;
            if (newState==BluetoothProfile.STATE_CONNECTED){

                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                //蓝牙设备已连接,连接成功后启动服务发现
                Log.e(".DeviceActivity", "启动服务发现:" + mBluetoothGatt.discoverServices());

            }else if (newState==BluetoothProfile.STATE_DISCONNECTED){
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;

                Log.e(".DeviceActivity", "连接失败");
                broadcastUpdate(intentAction);
            }
        }
        //发现服务回调
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status==BluetoothGatt.GATT_SUCCESS){
                Log.e(".DeviceActivity", "成功发现服务");
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                //                BluetoothGattService service = gatt.getService(UUID.fromString(MY_UUID));
                //                List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            }else{
                Log.e(".DeviceActivity", "服务发现失败，错误码为:" + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status==BluetoothGatt.GATT_SUCCESS){
                Log.e(".DeviceActivity", "读取成功:" + characteristic.getValue());
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
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


    private void broadcastUpdate(String intentAction, BluetoothGattCharacteristic characteristic) {
        Intent intent = new Intent(intentAction);
        //进行数据的处理,窗口通信服务的UUID
        if(MY_UUID.equals(characteristic.getUuid())){
        }

    }

    private void broadcastUpdate(String intentAction) {

        Intent intent = new Intent(intentAction);
        sendBroadcast(intent);
    }

    public boolean initialize(){

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter==null){
            Log.e(".BluetoothLeService", "无法活的蓝牙适配器");
            return false;
        }
        return true;
    }

    /**
     * 蓝牙连接
     * @param address
     * @return
     */
    public boolean connect(String address){
        if (mBluetoothAdapter==null||address==null){
            Log.w(".BluetoothLeService", "蓝牙适配器未实例化 或 设备地址无效");
            return false;
        }
        if (mDeviceAddress!=null&&address.equals(mDeviceAddress)&&mBluetoothGatt!=null){
            if (mBluetoothGatt.connect()){
                mConnectionState = STATE_CONNECTING;
                return true;
            }
            return false;
        }

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device==null){
            Log.w(".BluetoothLeService", address+":未发现该设备.无法进行连接.");
            return false;
        }
        //建立连接
        mBluetoothGatt = device.connectGatt(this,false,gattCallback);
        mDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;

        return true;
    }

    /**
     * 断开连接
     */
    public void disconnect(){
        if (mBluetoothGatt ==null){
            Log.w(".BluetoothLeService", "蓝牙适配器未实例化");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * 关闭蓝牙通信
     */
    public void close(){
        if (mBluetoothGatt==null){
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void readCharacteristic(BluetoothGattCharacteristic readCharacteristic){
        if (mBluetoothGatt==null||mBluetoothAdapter==null){
            Log.w(".BluetoothLeService", "蓝牙适配器未实例化");
            return;
        }
        mBluetoothGatt.readCharacteristic(readCharacteristic);
    }

    /**
     * 获取指定通信服务
     * @param serviceUUID
     * @return
     */
    public BluetoothGattService getSupportedGattService(UUID serviceUUID){
        if (mBluetoothGatt==null){
            return null;
        }
        return mBluetoothGatt.getService(serviceUUID);
    }

    /**
     * 获取设备的所有服务
     * @return
     */
    public List<BluetoothGattService> getSupportedGattServices(){
        if (mBluetoothGatt==null){
            return null;
        }
        return mBluetoothGatt.getServices();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public class MyBinder extends Binder{
        public BluetoothLeService getService(){
            return BluetoothLeService.this;
        }
    }

}

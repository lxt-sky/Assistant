package com.sanmen.bluesky.assistant.ui.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.sanmen.bluesky.assistant.R;
import com.sanmen.bluesky.assistant.base.BaseActivity;
import com.sanmen.bluesky.assistant.entity.BluetoothDeviceBean;
import com.sanmen.bluesky.assistant.manager.ClientManager;
import com.sanmen.bluesky.assistant.ui.adapter.BluetoothDeviceAdapter;
import com.sanmen.bluesky.assistant.utils.SwitchUtil;

import java.util.ArrayList;
import java.util.List;

import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;

/**
 * @author lxt_bluesky
 * @date 2018/11/5
 * @description
 */
public class BluetoothConnectActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener, CompoundButton.OnCheckedChangeListener {

    Switch switchBluetooth;
    RecyclerView rvDeviceList;
    private BluetoothDeviceAdapter deviceAdapter;
    private boolean isBluetoothOpen;
    private boolean mConnected;

    List<String> addressList =  new ArrayList<>();

    List<BluetoothDeviceBean> deviceList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_bluetooth_apply);

        initTitleBar();
        initLayout();
        initData();
    }

    private void initData() {
        if (ClientManager.getClient().isBluetoothOpened()){
            switchBluetooth.setChecked(true);
        }
    }

    private void initLayout() {
        switchBluetooth = findViewById(R.id.switchBluetooth);
        rvDeviceList = findViewById(R.id.rvDeviceList);
        //初始化设备列表
        deviceAdapter = new BluetoothDeviceAdapter(new ArrayList<BluetoothDeviceBean>());
        rvDeviceList.setLayoutManager(new LinearLayoutManager(this));
        rvDeviceList.setAdapter(deviceAdapter);
        deviceAdapter.setOnItemChildClickListener(this);

        ClientManager.getClient().registerBluetoothStateListener(mBluetoothStateListener);
        switchBluetooth.setOnCheckedChangeListener(this);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (deviceList!=null){
            BluetoothDeviceBean device = deviceList.get(position);
            SwitchUtil.switchToMainActivity(this,device.getDevice().getAddress());
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        isBluetoothOpen = isChecked;
        if (isBluetoothOpen){
            toSearchBluetooth();
        }else {
            toCloseBluetooth();
        }

    }

    private void toSearchBluetooth() {
        BluetoothClient clientManager =ClientManager.getClient();
        if (!clientManager.isBluetoothOpened()){
            clientManager.openBluetooth();
        }else {
            getBluetoothList();
        }
    }

    private void toCloseBluetooth() {
        //清空设备列表
        deviceList.clear();
        addressList.clear();
        //停止扫描
        ClientManager.getClient().stopSearch();
        ClientManager.getClient().closeBluetooth();

        deviceAdapter.setNewData(new ArrayList<BluetoothDeviceBean>());
        deviceAdapter.notifyDataSetChanged();
    }

    private final BluetoothStateListener mBluetoothStateListener = new BluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {

            if (openOrClosed){
                Toast.makeText(BluetoothConnectActivity.this, "蓝牙已经开启", Toast.LENGTH_SHORT).show();
                isBluetoothOpen = true;

                getBluetoothList();
            }else {
                Toast.makeText(BluetoothConnectActivity.this, "蓝牙已经关闭", Toast.LENGTH_SHORT).show();
                isBluetoothOpen = false;

            }

            switchBluetooth.setChecked(isBluetoothOpen);
        }

    };

    private void getBluetoothList() {
        deviceList.clear();
        addressList.clear();

        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(3000,3)
                .searchBluetoothClassicDevice(5000)
                .searchBluetoothLeDevice(2000)
                .build();

        ClientManager.getClient().search(request,searchResponse);
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

            BluetoothDeviceBean bean = new BluetoothDeviceBean();
            bean.setDevice(bluetoothDevice);
            bean.setState(false);

            if (addressList.indexOf(device.getAddress())==-1){

                deviceAdapter.addData(bean);
                deviceAdapter.notifyDataSetChanged();

                addressList.add(device.getAddress());
                deviceList.add(bean);
            }
        }

        @Override
        public void onSearchStopped() {

        }

        @Override
        public void onSearchCanceled() {

        }
    };
}

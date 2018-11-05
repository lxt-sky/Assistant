package com.sanmen.bluesky.assistant.ui.fragments;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.sanmen.bluesky.assistant.R;
import com.sanmen.bluesky.assistant.base.BaseFragment;
import com.sanmen.bluesky.assistant.entity.BluetoothDeviceBean;
import com.sanmen.bluesky.assistant.manager.ClientManager;
import com.sanmen.bluesky.assistant.ui.adapter.BluetoothDeviceAdapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static com.inuker.bluetooth.library.Constants.STATUS_DISCONNECTED;

/**
 * @author lxt_bluesky
 * @date 2018/11/5
 * @description
 */
public class BluetoothSettingFragment extends BaseFragment implements BaseQuickAdapter.OnItemChildClickListener, CompoundButton.OnCheckedChangeListener {
    View rootView;
    Switch switchBluetooth;
    RecyclerView rvBluetoothList;
    private BluetoothDeviceAdapter deviceAdapter;
    private boolean isBluetoothOpen;

    private int position;
    private int oldPosition=-1;

    private boolean mConnected;

    private BluetoothDevice mDevice;

    List<String> addressList =  new ArrayList<>();

    List<BluetoothDeviceBean> deviceList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView==null){
            rootView=inflater.inflate(R.layout.fragment_guide_item2,container,false);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLayout();
        initData();
    }

    private void initData() {
        if (ClientManager.getClient().isBluetoothOpened()){
            switchBluetooth.setChecked(true);
        }
    }

    private void initLayout() {
        switchBluetooth = rootView.findViewById(R.id.switchBluetooth);
        rvBluetoothList = rootView.findViewById(R.id.rvBluetoothList);
        //初始化设备列表
        deviceAdapter = new BluetoothDeviceAdapter(new ArrayList<BluetoothDeviceBean>());
        rvBluetoothList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvBluetoothList.setAdapter(deviceAdapter);
        deviceAdapter.setOnItemChildClickListener(this);

        ClientManager.getClient().registerBluetoothStateListener(mBluetoothStateListener);
        switchBluetooth.setOnCheckedChangeListener(this);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if (deviceList!=null){
            BluetoothDeviceBean device = deviceList.get(position);
            this.position = position;
            ClientManager.getClient().registerConnectStatusListener(device.getDevice().getAddress(), mBleConnectStatusListener);
            mConnected=false;
            connectDeviceIfNeeded();
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

    private void connectService(BluetoothDeviceBean bean) {

        BleConnectOptions options = new BleConnectOptions.Builder()
                .setConnectRetry(3)
                .setConnectTimeout(20000)
                .setServiceDiscoverRetry(3)
                .setServiceDiscoverTimeout(10000)
                .build();
        ClientManager.getClient().connect(bean.getDevice().getAddress(),options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                //如果连接不成功则重试
                if (code==REQUEST_SUCCESS){
                    oldPosition=position;
//                    setGattProfile(data);
                }else {
//                    connectDeviceIfNeeded();
                }
            }
        });


    }

    private final BleConnectStatusListener mBleConnectStatusListener = new BleConnectStatusListener() {

        @Override
        public void onConnectStatusChanged(String mac, int status) {
            if (deviceList==null){
                return;
            }

            mConnected=(status==STATUS_CONNECTED);
            if (position!=oldPosition&&oldPosition!=-1&&mConnected){
                deviceList.get(oldPosition).setState(!mConnected);
            }
            deviceList.get(position).setState(mConnected);
            deviceAdapter.notifyDataSetChanged();
            connectDeviceIfNeeded();
        }
    };

    private void connectDeviceIfNeeded() {
        if (deviceList==null){
            return;
        }
        if (!mConnected){
            connectService(deviceList.get(position));
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
                Toast.makeText(getActivity(), "蓝牙已经开启", Toast.LENGTH_SHORT).show();
                isBluetoothOpen = true;

                getBluetoothList();
            }else {
                Toast.makeText(getActivity(), "蓝牙已经关闭", Toast.LENGTH_SHORT).show();
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

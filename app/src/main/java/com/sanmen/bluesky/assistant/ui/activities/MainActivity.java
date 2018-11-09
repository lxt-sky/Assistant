package com.sanmen.bluesky.assistant.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.model.BleGattService;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.sanmen.bluesky.assistant.R;
import com.sanmen.bluesky.assistant.base.BaseActivity;
import com.sanmen.bluesky.assistant.entity.DetailItem;
import com.sanmen.bluesky.assistant.entity.LocationDataBean;
import com.sanmen.bluesky.assistant.manager.ClientManager;
import com.sanmen.bluesky.assistant.manager.PaperManager;
import com.sanmen.bluesky.assistant.ui.adapter.HistoryAdapter;
import com.sanmen.bluesky.assistant.utils.PermissionUtil;
import com.sanmen.bluesky.assistant.utils.SwitchUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    TextView tvData;
    TextView tvHistoryClear;
    RecyclerView rvHistoryList;
    private boolean mConnected;

    HistoryAdapter historyAdapter;

    List<LocationDataBean> dataBeanList;

    /**
     * UUID
     */
    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    private UUID Uuid_Character;

    private UUID Uuid_Service;
    /**
     * 位置信息
     */
    LocationManager locationManager;
    private Location myLocation;

    private long starTime = 0;

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

//        BleGattService service = mDevice.createRfcommSocketToServiceRecord(MY_UUID.toString());
    }

    private void obtainParams() {
        Intent intent = getIntent();
        if (intent != null) {
            MAC = intent.getStringExtra("MAC_ADDRESS");
        }

        dataBeanList = PaperManager.getPaperManager().getHistoryList();

        //获取位置数据
        if (!isLocationEnabled()) {
            Toast.makeText(MainActivity.this, "位置服务未开启!", Toast.LENGTH_SHORT).show();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (myLocation==null){
                myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }

    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            return true;
        }
        return false;
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


        ClientManager.getClient().registerConnectStatusListener(mDevice.getAddress(), mConnectStatusListener);
        connectDeviceIfNeeded();

//        serviceConnect();

    }

    private void serviceConnect() {
        try {
            BluetoothSocket socket = mDevice.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
            connected(socket, true);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void connected(BluetoothSocket socket, boolean needConnect) {
//        ConnectedThread()
    }

    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {

            mConnected = (status == STATUS_CONNECTED);
            tvDeviceState.setText(mConnected?"已连接":"连接失败");
            connectDeviceIfNeeded();

        }
    };

    private void connectDeviceIfNeeded() {
        showProgressDialog("加载中");
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
//                dismissProgressDialog();
                //如果连接不成功则重试
                if (code==REQUEST_SUCCESS){
                    tvReConnect.setVisibility(View.GONE);

                    setGattProfile(data);
                    readData();
                }else {
                    //connectDeviceIfNeeded();
                    tvReConnect.setVisibility(View.VISIBLE);
                }
                Toast.makeText(MainActivity.this,"Code:"+code,Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void readData() {
        if (Uuid_Service!=null&&Uuid_Character!=null&&MAC!=null){
            ClientManager.getClient().read(MAC,Uuid_Service,Uuid_Character,mReadRsp);
        }

    }
    private void setGattProfile(BleGattProfile data) {
        List<DetailItem> items = new ArrayList<DetailItem>();


        List<BleGattService> services = data.getServices();

        for (BleGattService service : services) {
            items.add(new DetailItem(DetailItem.TYPE_SERVICE, service.getUUID(), null));
            List<BleGattCharacter> characters = service.getCharacters();
            for (BleGattCharacter character : characters) {
                items.add(new DetailItem(DetailItem.TYPE_CHARACTER, character.getUuid(), service.getUUID()));
                //打印每个特征的读写属性
                Log.e(".MainActivity",character.toString());
                if(BluetoothGattCharacteristic.PROPERTY_READ==character.getProperty()){
                    Uuid_Character=character.getUuid();
                    Uuid_Service=service.getUUID();
                    return;
                }

            }
        }

    }

    private final BleReadResponse mReadRsp = new BleReadResponse() {
        @Override
        public void onResponse(int code, byte[] data) {
            if (code == REQUEST_SUCCESS) {
                tvData.setText(String.format("read: %s", ByteUtils.byteToString(data)));
                Toast.makeText(MainActivity.this,"接收指令成功",Toast.LENGTH_SHORT).show();
                //解析指令,完成后续任务.
                toParseInstruction(ByteUtils.byteToString(data));
            } else {
                Toast.makeText(MainActivity.this,"接收指令失败",Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 解析指令
     * @param data
     */
    private void toParseInstruction(String data) {
        getLocationData();
        //校验指令是否匹配
        if (data!=null){
            toAlarm();

        }
    }

    private void getLocationData(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, mLocationListener);

    }

    private void toAlarm() {
        String phone = PaperManager.getPaperManager().getAlarmPhone();
        int alarmType = PaperManager.getPaperManager().getAlarmType();
        switch (alarmType){
            case 0:
                toCallPhone(phone);
                addLocationData();
                break;
            case 1:
                toSendMessage(phone);
                break;
            case 2:
                toSendMessage(phone);
                toCallPhone(phone);
                break;
            default:
                break;
        }
    }

    /**
     * 发送短信
     * @param phone
     */
    private void toSendMessage(String phone) {

        if (phone==""){
            Toast.makeText(MainActivity.this,"报警号码为空,请前往App设置页填写!",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!PermissionUtil.hasPermission(this, Manifest.permission.SEND_SMS)) {
            Toast.makeText(MainActivity.this,"缺少短信权限,请前往系统设置页获取!",Toast.LENGTH_SHORT).show();
        }else {
            //将位置信息通过短信发送给指定号码
            if (myLocation!=null){
                String SENT_SMS_ACTION = "SENT_SMS_ACTION";
                Intent sendIntent = new Intent(SENT_SMS_ACTION);
                PendingIntent sentPI = PendingIntent.getBroadcast(this,0,sendIntent,0);

                SmsManager.getDefault().sendTextMessage(phone,null,
                        "经度:"+myLocation.getLongitude()+" 纬度:"+myLocation.getLatitude(),sentPI,null);
                registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        switch (getResultCode()){
                            case Activity.RESULT_OK:
                                addLocationData();

                                break;
                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                break;
                            default:
                                break;
                        }
                    }
                },new IntentFilter(SENT_SMS_ACTION));
                Toast.makeText(MainActivity.this,"短信已发送!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 拨打电话
     * @param phone
     */
    private void toCallPhone(String phone) {
        if (phone!=""){
            boolean flag = SwitchUtil.switchToCallPhone(this,phone);
            if (!flag){
                Toast.makeText(MainActivity.this,"缺少通话权限,请前往系统设置页获取!",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(MainActivity.this,"拨打报警电话成功!",Toast.LENGTH_SHORT).show();

            }
        }else {
            Toast.makeText(MainActivity.this,"报警号码为空,请前往App设置页填写!",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRightClick(View view) {
        super.onRightClick(view);
        SwitchUtil.switchToSettingActivity(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvReConnect:
                //重连
                connectDeviceIfNeeded();
                break;
            case R.id.tvHistoryClear:
                clearHistory();
                break;
            default:
                break;
        }

    }

    private void clearHistory() {
        dataBeanList.clear();
        historyAdapter.notifyDataSetChanged();
        PaperManager.getPaperManager().setHistoryList(new ArrayList<LocationDataBean>());
    }

    @Override
    protected void onDestroy() {
        ClientManager.getClient().disconnect(mDevice.getAddress());
        ClientManager.getClient().unregisterConnectStatusListener(mDevice.getAddress(), mConnectStatusListener);
        super.onDestroy();
    }

    private LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            if (location!=null){
                myLocation=location;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    private void addLocationData() {
        LocationDataBean bean = new LocationDataBean();
        long time = System.currentTimeMillis();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = format.format(time);

        bean.setAlarmType(PaperManager.getPaperManager().getAlarmType());
        bean.setLatitude(myLocation.getLatitude());
        bean.setLongitude(myLocation.getLongitude());
        bean.setAlarmTime(timeStr);
        //添加数据
        dataBeanList.add(bean);
//        historyAdapter.addData(bean);
        historyAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (dataBeanList.size()!=0){
            PaperManager.getPaperManager().setHistoryList(dataBeanList);
        }

    }
}

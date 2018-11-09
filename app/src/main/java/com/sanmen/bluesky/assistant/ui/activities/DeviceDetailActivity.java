package com.sanmen.bluesky.assistant.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.sanmen.bluesky.assistant.R;
import com.sanmen.bluesky.assistant.base.BaseActivity;
import com.sanmen.bluesky.assistant.entity.LocationDataBean;
import com.sanmen.bluesky.assistant.manager.ConnectManager;
import com.sanmen.bluesky.assistant.manager.PaperManager;
import com.sanmen.bluesky.assistant.ui.adapter.HistoryAdapter;
import com.sanmen.bluesky.assistant.utils.PermissionUtil;
import com.sanmen.bluesky.assistant.utils.SwitchUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lxt_bluesky
 * @date 2018/11/8
 * @description
 */
public class DeviceDetailActivity extends BaseActivity implements View.OnClickListener {

    private static String MAC = "";

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
    List<LocationDataBean> dataBeanList=new ArrayList<>();
    private ConnectManager mConnectionManager;
    private final static int MSG_SENT_DATA = 0;
    private final static int MSG_RECEIVE_DATA = 1;
    private final static int MSG_UPDATE_UI = 2;

    /**
     * 位置信息
     */
    LocationManager locationManager;
    private Location myLocation;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case MSG_RECEIVE_DATA:
                    byte [] data = (byte []) msg.obj;
                    String command = new String(data);
                    toParseCommand(command);
                    break;
                case MSG_UPDATE_UI:
                    dismissProgressDialog();
                    if(mConnectionManager == null) {
                        return;
                    }
                    int state = (int)msg.obj;
                    String text = "";
                    if (state==0){
                        text="连接失败";
                        tvReConnect.setVisibility(View.VISIBLE);
                    }else
                    if (state==1){
                        text="连接中";
                        tvReConnect.setVisibility(View.GONE);
                    }else
                    if (state==2){
                        text="连接成功";
                        tvReConnect.setVisibility(View.GONE);
                    }
//                    Toast.makeText(DeviceDetailActivity.this,"状态是:"+text,Toast.LENGTH_LONG).show();
                    tvDeviceState.setText(text+"");
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_device);
        initTitleBar();
        obtainParams();
        initLayout();
        initData();
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

        mConnectionManager = new ConnectManager(this,mConnectionListener);
        connectDeviceIfNeeded();
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

        //获取位置数据
        if (!isLocationEnabled()) {
            Toast.makeText(DeviceDetailActivity.this, "位置服务未开启!", Toast.LENGTH_SHORT).show();
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

    private void connectDeviceIfNeeded() {
        showProgressDialog("加载中");
        mConnectionManager.connect(MAC);

    }

    /**
     * 解析指令
     * @param command
     */
    private void toParseCommand(String command) {
        getLocationData();
        Toast.makeText(DeviceDetailActivity.this,"正在接收指令,指令为:"+command,Toast.LENGTH_SHORT).show();
        if (command.equalsIgnoreCase("W")){
            //W为报警指令
            toAlarm();

        }
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
     * 拨打电话
     * @param phone
     */
    private void toCallPhone(String phone) {
        if (phone!=""){
            boolean flag = SwitchUtil.switchToCallPhone(this,phone);
            if (!flag){
                Toast.makeText(DeviceDetailActivity.this,"缺少通话权限,请前往系统设置页获取!",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(DeviceDetailActivity.this,"拨打报警电话成功!",Toast.LENGTH_SHORT).show();

            }
        }else {
            Toast.makeText(DeviceDetailActivity.this,"报警号码为空,请前往App设置页填写!",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 发送短信
     * @param phone
     */
    private void toSendMessage(String phone) {

        if (phone==""){
            Toast.makeText(DeviceDetailActivity.this,"报警号码为空,请前往App设置页填写!",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!PermissionUtil.hasPermission(this, Manifest.permission.SEND_SMS)) {
            Toast.makeText(DeviceDetailActivity.this,"缺少短信权限,请前往系统设置页获取!",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DeviceDetailActivity.this,"短信已发送!",Toast.LENGTH_SHORT).show();
            }
        }
    }

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
        historyAdapter.notifyDataSetChanged();

    }

    /**
     * 检测是否定位
     * @return
     */
    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            return true;
        }
        return false;
    }

    /**
     * 点击事件处理
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvReConnect:
                //重连
                if (mConnectionManager!=null){
                    mConnectionManager.disconnect();
                    connectDeviceIfNeeded();
                }

                break;
            case R.id.tvHistoryClear:
                clearHistory();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRightClick(View view) {
        super.onRightClick(view);
        SwitchUtil.switchToSettingActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (dataBeanList.size()!=0){
            PaperManager.getPaperManager().setHistoryList(dataBeanList);
        }
    }

    @Override
    protected void onDestroy() {

        if(mConnectionManager != null) {
            mConnectionManager.disconnect();
        }
        super.onDestroy();
    }

    private void clearHistory() {
        dataBeanList.clear();
        historyAdapter.notifyDataSetChanged();
        PaperManager.getPaperManager().setHistoryList(new ArrayList<LocationDataBean>());
    }

    /**
     * 获取位置信息
     */
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


    private ConnectManager.ConnectionListener mConnectionListener = new ConnectManager.ConnectionListener() {
        @Override
        public void onConnectStateChange(int oldState, int State) {

            mHandler.obtainMessage(MSG_UPDATE_UI,State).sendToTarget();
        }

        @Override
        public void onReadData(byte[] data) {

            mHandler.obtainMessage(MSG_RECEIVE_DATA,  data).sendToTarget();
        }
    };

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

}

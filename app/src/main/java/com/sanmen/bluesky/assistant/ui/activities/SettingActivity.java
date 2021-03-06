package com.sanmen.bluesky.assistant.ui.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sanmen.bluesky.assistant.R;
import com.sanmen.bluesky.assistant.base.BaseActivity;
import com.sanmen.bluesky.assistant.manager.PaperManager;
import com.sanmen.bluesky.assistant.utils.SwitchUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author lxt_bluesky
 * @date 2018/10/31
 * @description
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    BottomSheetDialog sheetDialog;
    @BindView(R.id.tvDeviceName)
    TextView tvDeviceName;
    @BindView(R.id.tvPhoneValue)
    TextView tvPhoneValue;
    @BindView(R.id.tvAlarmValue)
    TextView tvAlarmValue;
    @BindView(R.id.tvGpsValue)
    TextView tvGpsValue;
    @BindView(R.id.llApplyLayout)
    LinearLayout llApplyLayout;
    @BindView(R.id.llPhoneLayout)
    LinearLayout llPhoneLayout;
    @BindView(R.id.llAlarmLayout)
    LinearLayout llAlarmLayout;
    @BindView(R.id.llLocateLayout)
    LinearLayout llLocateLayout;

    String[] alarmArray = new String[]{"电话","短信","电话+短信"};
    String[] gpsArray = new String[]{"打开","关闭"};
    /**
     * 为0,显示报警方式弹窗
     * 为1,显示GPS定位弹窗
     */
    int showType = 0;

    private int alarmType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);
        initTitleBar();
        ButterKnife.bind(this);
        obtainParams();

    }

    private void obtainParams() {
        PaperManager manager = PaperManager.getPaperManager();
        String phone = manager.getAlarmPhone();
        int type = manager.getAlarmType();
        tvPhoneValue.setText(phone);
        tvAlarmValue.setText(alarmArray[type]);
    }

    @OnClick({R.id.llApplyLayout,R.id.llPhoneLayout,R.id.llAlarmLayout,R.id.llLocateLayout})
    public void onItemClick(View view){
        switch (view.getId()){
            case R.id.llApplyLayout:
                //前往蓝牙配对页
                SwitchUtil.switchToBluetoothActivity(this);
                break;
            case R.id.llPhoneLayout:
                //打开电话设置弹窗
                toShowPhoneDialog("电话号码",tvPhoneValue.getText().toString());
                break;
            case R.id.llAlarmLayout:
                toShowSelectDialog(alarmArray);
                break;
            case R.id.llLocateLayout:
                checkLocate();

                break;
            default:
                break;
        }
    }

    /**
     * 检查是否获得定位权限，定位服务是否打开
     */
    private void checkLocate() {
        int permissionState;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionState=checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionState==PackageManager.PERMISSION_GRANTED){

            }else if (permissionState==PackageManager.PERMISSION_DENIED){

            }
        }
        toShowSelectDialog(gpsArray);

    }

    private void toShowSelectDialog(String[] itemArray) {
        int itemCount = itemArray.length;

        View alarmDialogView = getLayoutInflater().inflate(R.layout.dialog_select_image,null);
        TextView tvItem1 = alarmDialogView.findViewById(R.id.tvItem1);
        TextView tvItem2 = alarmDialogView.findViewById(R.id.tvItem2);
        TextView tvItem3 = alarmDialogView.findViewById(R.id.tvItem3);
        TextView tvCancel = alarmDialogView.findViewById(R.id.tvCancel);

        tvItem1.setOnClickListener(this);
        tvItem2.setOnClickListener(this);
        tvItem3.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        tvItem1.setText(itemArray[0]);
        tvItem2.setText(itemArray[1]);
        if (itemCount==3){
            showType=0;
            tvItem3.setText(itemArray[2]);
        }else {
            showType=1;
            tvItem3.setVisibility(View.GONE);
        }

        if (sheetDialog == null) {
            //设置弹框主题样式
            sheetDialog = new BottomSheetDialog(this, R.style.SheetDialog);
            sheetDialog.setContentView(alarmDialogView);
        }
        //设置布局背景色透明
        sheetDialog.getWindow().findViewById(R.id.llRootLayout).setBackgroundResource(android.R.color.transparent);
        //显示弹框
        sheetDialog.show();
        sheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                sheetDialog = null;
            }
        });

    }

    private void toShowPhoneDialog(String itemName, String itemValue) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_complete_value,null);
        TextView tvCompleteItemName = dialogView.findViewById(R.id.tvCompleteItemName);
        final TextView etCompleteValue = dialogView.findViewById(R.id.etCompleteValue);
        TextView tvCompleteCancel = dialogView.findViewById(R.id.tvCompleteCancel);
        TextView tvCompleteSave = dialogView.findViewById(R.id.tvCompleteSave);
        //设置Dialog标题
        tvCompleteItemName.setText(itemName);
        //设置Item现有值
        etCompleteValue.setText(itemValue);
        //注册点击监听
        tvCompleteCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetDialog.dismiss();
            }
        });
        tvCompleteSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetDialog.dismiss();
                tvPhoneValue.setText(etCompleteValue.getText().toString());
            }
        });

        if (sheetDialog == null) {
            //设置弹框主题样式
            sheetDialog = new BottomSheetDialog(this, R.style.SheetDialog);
            sheetDialog.setContentView(dialogView);
        }
        //显示弹框
        sheetDialog.show();
        //弹窗dismiss监听
        sheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                sheetDialog = null;
            }
        });
        //自动弹出软键盘
        Window window = sheetDialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        TextView tvItemView = (TextView) v;
        switch (v.getId()){
            case R.id.tvItem1:
            case R.id.tvItem2:
            case R.id.tvItem3:

                if (showType==0){
                    tvAlarmValue.setText(tvItemView.getText());
                }else {
                    tvGpsValue.setText(tvItemView.getText());
                }
                sheetDialog.dismiss();
                break;
            case R.id.tvCancel:
                sheetDialog.dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //保存数据，报警方式和报警电话
        PaperManager manager = PaperManager.getPaperManager();
        manager.setAlarmPhone(tvPhoneValue.getText().toString());
        String alarmValue = tvAlarmValue.getText().toString();
        for (int i=0;i<alarmArray.length;i++){
            if (alarmValue.equals(alarmArray[i])){
                alarmType=i;
                break;
            }
        }

        manager.setAlarmType(alarmType);
    }
}

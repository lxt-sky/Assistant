package com.sanmen.bluesky.assistant.ui.fragments;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sanmen.bluesky.assistant.PermissionHelper;
import com.sanmen.bluesky.assistant.R;
import com.sanmen.bluesky.assistant.base.PermissionInterface;

/**
 * @author lxt_bluesky
 * @date 2018/10/29
 * @description
 */
public class PermissionApplyFragment extends Fragment implements View.OnClickListener,PermissionInterface {

    String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS
    };

    String[] permissionNameArray = new String[]{
            "定位权限","拨打电话权限","发送短信权限"
    };

    View rootView;

    Button btnApply;
    LinearLayout linearLayout;

    PermissionHelper helper;

    private int requestCode = 10000;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView==null){
            rootView = inflater.inflate(R.layout.fragment_guide_item1,null,false);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initLayout();

    }

    private void initLayout() {
        btnApply = rootView.findViewById(R.id.btnApply);
        linearLayout = rootView.findViewById(R.id.llPermissionLayout);

        btnApply.setOnClickListener(this);
        helper = new PermissionHelper(this,this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnApply:
                helper.requestPermissions();
                //设置按钮不可点击
                btnApply.setEnabled(false);
                break;
            default:
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (helper.requestPermissionsResult(requestCode,permissions,grantResults)){

            showPermissionText(grantResults);
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showPermissionText(int[] grantResults) {
        String text;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,50);
        layoutParams.setMargins(20,20,20,20);
        layoutParams.gravity=Gravity.CENTER;

        for (int i=1;i<grantResults.length;i++){

            TextView textView = new TextView(getContext());
            if (grantResults[i]==0){
                text=permissionNameArray[i-1]+" 已被允许";
            }else {
                text=permissionNameArray[i-1]+" 已被拒绝";
            }
            textView.setText(text);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextColor(getResources().getColor(R.color.black_text,null));
            }

            linearLayout.addView(textView,layoutParams);
        }
    }

    @Override
    public int getPermissionRequestCode() {
        return requestCode;
    }

    @Override
    public String[] getPermissions() {
        return permissions;
    }

    @Override
    public void requestPermissionSuccess() {
        //用户所需权限已经全部允许
    }

    @Override
    public void requestPermissionFail() {
        //用户所需权限被拒绝
        Toast.makeText(getContext(),"您已拒绝相应的权限,为了程序的正常运行,请前往设置页设置相应权限",Toast.LENGTH_SHORT).show();

    }
}

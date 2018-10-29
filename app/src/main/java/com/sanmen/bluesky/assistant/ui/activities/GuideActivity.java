package com.sanmen.bluesky.assistant.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sanmen.bluesky.assistant.R;
import com.sanmen.bluesky.assistant.base.BaseActivity;
import com.sanmen.bluesky.assistant.ui.fragments.BluetoothSearchFragment;
import com.sanmen.bluesky.assistant.ui.fragments.PermissionApplyFragment;
import com.sanmen.bluesky.assistant.ui.fragments.PhoneSettingFragment;

/**
 * @author lxt_bluesky
 * @date 2018/10/29
 * @description
 */
public class GuideActivity extends BaseActivity implements View.OnClickListener {

    ImageView ivGuideCancel;
    ViewPager viewPager;
    LinearLayout llIndicatorLayout;
    Button btnNextStep;
    MyAdapter adapter;

    private String[] pager = {"权限申请","蓝牙配对","号码设置"};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guide);

        initLayout();

    }

    /**
     * 界面初始化
     */
    private void initLayout() {

        ivGuideCancel= findViewById(R.id.ivGuideCancel);
        viewPager = findViewById(R.id.viewPager);
        llIndicatorLayout = findViewById(R.id.llIndicatorLayout);
        btnNextStep = findViewById(R.id.btnNextStep);

        ivGuideCancel.setOnClickListener(this);
        btnNextStep.setOnClickListener(this);

        adapter = new MyAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        for (int i=0;i<pager.length;i++){
            View view = new View(this);
            view.setBackgroundResource(R.drawable.selector_dot_indicator);
            view.setEnabled(false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(20,20);
            if (i!=0){
                layoutParams.leftMargin=10;
            }
            llIndicatorLayout.addView(view,layoutParams);
        }

    }

    /**
     * 点击事件处理
     * @param v
     */
    @Override
    public void onClick(View v) {

    }

    private class MyAdapter extends FragmentPagerAdapter{


        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new PermissionApplyFragment();
                case 1:
                    return new BluetoothSearchFragment();
                case 2:
                    return new PhoneSettingFragment();
                default:
                    break;
            }
            return null;
        }

        @Override
        public int getCount() {
            return pager.length;
        }
    }
}

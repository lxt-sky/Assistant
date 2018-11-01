package com.sanmen.bluesky.assistant.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sanmen.bluesky.assistant.R;
import com.sanmen.bluesky.assistant.widgets.TitleBar;

/**
 * @author lxt_bluesky
 * @date 2018/10/29
 * @description
 */
public class BaseActivity extends AppCompatActivity implements TitleBar.TitleOnClickListener {

    private TitleBar titleBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);


    }

    public void initTitleBar(){
        titleBar = findViewById(R.id.titleBar);
        if (titleBar!=null){
            titleBar.setOnTitleClickListener(this);
        }
    }

    @Override
    public void onLeftClick(View view) {
        finish();
    }

    @Override
    public void onRightClick(View view) {

    }

    /**
     * 设置标题
     * @param title
     */
    public void setTitle(String title){
        titleBar.setTitleText(title);
    }

    /**
     * 通过ID设置标题
     * @param reid
     */
    @SuppressLint("ResourceType")
    public void setTitleById(@IdRes int reid){
        titleBar.setTitleText(getResources().getString(reid));
    }

    /**
     * 设置右边按钮文字
     * @param rightText
     */
    public void setRightText(String rightText){
        titleBar.setRightText(rightText);
    }
}

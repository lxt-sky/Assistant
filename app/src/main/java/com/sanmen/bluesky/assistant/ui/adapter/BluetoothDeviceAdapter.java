package com.sanmen.bluesky.assistant.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sanmen.bluesky.assistant.R;
import com.sanmen.bluesky.assistant.entity.BluetoothDeviceBean;

import java.util.List;

/**
 * @author lxt_bluesky
 * @date 2018/10/31
 * @description
 */
public class BluetoothDeviceAdapter extends BaseQuickAdapter<BluetoothDeviceBean,BaseViewHolder> {

    public BluetoothDeviceAdapter(@Nullable List<BluetoothDeviceBean> data) {
        super(R.layout.item_bluetooth_device,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BluetoothDeviceBean item) {
        helper.setText(R.id.tvDeviceName,item.getDeviceName()!=null?item.getDeviceName():item.getMacAddress());
        helper.addOnClickListener(R.id.llDeviceLayout);
    }

}

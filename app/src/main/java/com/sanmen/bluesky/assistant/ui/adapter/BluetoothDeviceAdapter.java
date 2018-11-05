package com.sanmen.bluesky.assistant.ui.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.inuker.bluetooth.library.Constants;
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
        BluetoothDevice device = item.getDevice();
        helper.setText(R.id.tvDeviceName,device.getName()!=null?device.getName():device.getAddress());
        helper.setVisible(R.id.tvConnectState,item.isState());
        helper.addOnClickListener(R.id.llDeviceLayout);
    }

}

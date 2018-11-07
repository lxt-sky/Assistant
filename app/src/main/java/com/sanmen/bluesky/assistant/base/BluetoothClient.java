package com.sanmen.bluesky.assistant.base;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.inuker.bluetooth.library.BluetoothClientImpl;

/**
 * @author lxt_bluesky
 * @date 2018/11/7
 * @description
 */
public class BluetoothClient {

    private final BluetoothAdapter mBluetoothAdapter;

    public BluetoothClient(Context context) {
        if (context == null) {
            throw new NullPointerException("Context null");
        }
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


}

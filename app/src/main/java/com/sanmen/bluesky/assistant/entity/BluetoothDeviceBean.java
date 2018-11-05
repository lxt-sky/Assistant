package com.sanmen.bluesky.assistant.entity;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author lxt_bluesky
 * @date 2018/10/31
 * @description
 */
public class BluetoothDeviceBean implements Parcelable {
    private BluetoothDevice device;
    private boolean state;

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.device, flags);
        dest.writeByte(this.state ? (byte) 1 : (byte) 0);
    }

    public BluetoothDeviceBean() {
    }

    protected BluetoothDeviceBean(Parcel in) {
        this.device = in.readParcelable(BluetoothDevice.class.getClassLoader());
        this.state = in.readByte() != 0;
    }

    public static final Parcelable.Creator<BluetoothDeviceBean> CREATOR = new Parcelable.Creator<BluetoothDeviceBean>() {
        @Override
        public BluetoothDeviceBean createFromParcel(Parcel source) {
            return new BluetoothDeviceBean(source);
        }

        @Override
        public BluetoothDeviceBean[] newArray(int size) {
            return new BluetoothDeviceBean[size];
        }
    };
}

package com.sanmen.bluesky.assistant.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author lxt_bluesky
 * @date 2018/10/31
 * @description
 */
public class BluetoothDeviceBean implements Parcelable {
    private String deviceName;
    private String macAddress;
    private boolean state;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
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
        dest.writeString(this.deviceName);
        dest.writeString(this.macAddress);
        dest.writeByte(this.state ? (byte) 1 : (byte) 0);
    }

    public BluetoothDeviceBean() {
    }

    protected BluetoothDeviceBean(Parcel in) {
        this.deviceName = in.readString();
        this.macAddress = in.readString();
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

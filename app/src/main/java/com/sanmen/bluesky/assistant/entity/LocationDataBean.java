package com.sanmen.bluesky.assistant.entity;

/**
 * @author lxt_bluesky
 * @date 2018/11/6
 * @description
 */
public class LocationDataBean {

    private int alarmType;
    private double longitude;
    private double latitude;
    private String alarmTime;

    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }
}

package com.ascend.wangfeng.locationbyhand.data.saveData;

/**
 * Created by zsw on 2018/5/9.
 */

public class LocationData {

    private String TAG =getClass().getCanonicalName();

    private double latitude;              //纬度
    private double longitude;             //经度


    private long time;                    //时间

    public LocationData(double latidute,double longitude,long time){
        this.latitude = latidute;
        this.longitude = longitude;
        setTime(time);

    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return time+","+latitude+","+longitude;

    }

}

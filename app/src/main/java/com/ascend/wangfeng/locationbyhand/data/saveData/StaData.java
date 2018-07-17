package com.ascend.wangfeng.locationbyhand.data.saveData;

/**
 * Created by zsw on 2018/5/8.

 */

//终端上传的信息
public class StaData {

    private String TAG = getClass().getCanonicalName();

    private String staMACStr;       //终端MAC

    private long staMAC;            //长整型MAC
    private int signal;          //信号强度
    private int scanNum;            //扫描次数
    private long fTime;           //第一次采集到的时间
    private long lTime;           //最后一次采集到的时间

    private double latitude;              //纬度
    private double longitude;             //经度

    private String apName;
    private String apMac;


    public String getStaMACStr() {
        return staMACStr;
    }

    public void setStaMACStr(String staMACStr) {
        this.staMACStr = staMACStr;
        String mac = staMACStr.replaceAll(":", "").trim(); // colon
        setStaMAC(Long.valueOf(mac, 16));
    }

    public long getStaMAC() {
        return staMAC;
    }

    private void setStaMAC(long staMAC) {

        this.staMAC = staMAC;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public int getScanNum() {
        return scanNum;
    }

    public void setScanNum(int scanNum) {
        this.scanNum = scanNum;
    }

    public long getfTime() {
        return fTime;
    }


    public void setfTime(long fTime) {
        this.fTime = fTime;
    }

    public long getlTime() {
        return lTime;
    }

    public void setlTime(long lTime) {
        this.lTime = lTime;
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

    public String getApName() {
        return apName;
    }

    public void setApName(String apName) {
        this.apName = apName;
    }

    public String getApMac() {
        return apMac;
    }

    public void setApMac(String apMac) {
        this.apMac = apMac;
    }


    @Override
    public String toString() {
//        return "StaData{" +
//                "staMAC=" + staMAC +
//                ", signal=" + signal +
//                ", scanNum=" + scanNum +
//                ", fTime=" + fTime +
//                ", lTime=" + lTime +
//                ", latitude=" + latitude +
//                ", longitude=" + longitude +
//                '}';
        return staMAC+","+signal+","+scanNum+","+fTime+","+lTime+","+latitude+","+longitude;
    }
}

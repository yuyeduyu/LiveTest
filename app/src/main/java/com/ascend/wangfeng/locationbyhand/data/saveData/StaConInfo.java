package com.ascend.wangfeng.locationbyhand.data.saveData;

/**
 * Created by zsw on 2018/5/8.
 */

//终端的连接信息
public class StaConInfo {

    private String TAG = getClass().getCanonicalName();

    private String staMACStr;
    private String apMACStr;

    private Long staMAC;
    private Long apMac;

    private String apName;                  //ap名称
    private int startIp;                    //源ip地址          = null
    private int endIp;                      //目的Ip地址        = null
    private int startPort;                  //源端口号          = null
    private int endPort;                    //目的端口号        = null
    private long fTime;                   //第一次采集到的时间
    private long lTime;                   //最后一次采集到的时间

    private double latitude;                  //纬度
    private double longitude;                 //经度

    public String getStaMACStr() {
        return staMACStr;
    }

    public void setStaMACStr(String staMACStr) {
        this.staMACStr = staMACStr;
        String mac = staMACStr.replaceAll(":", "").trim(); // colon
        setStaMAC(Long.valueOf(mac, 16));
    }

    public String getApMACStr() {
        return apMACStr;
    }

    public void setApMACStr(String apMACStr) {
        this.apMACStr = apMACStr;

        String mac = apMACStr.replaceAll(":", "").trim(); // colon

        setApMac(Long.valueOf(mac, 16));
    }

    public Long getStaMAC() {
        return staMAC;
    }

    private void setStaMAC(Long staMAC) {

        this.staMAC = staMAC;
    }

    public Long getApMac() {
        return apMac;
    }

    public void setApMac(Long apMac) {
        this.apMac = apMac;
    }

    public String getApName() {
        return apName;
    }

    public void setApName(String apName) {
        this.apName = apName;
    }

    public int getStartIp() {
        return startIp;
    }

    public void setStartIp(int startIp) {
        this.startIp = startIp;
    }

    public int getEndIp() {
        return endIp;
    }

    public void setEndIp(int endIp) {
        this.endIp = endIp;
    }

    public int getStartPort() {
        return startPort;
    }

    public void setStartPort(int startPort) {
        this.startPort = startPort;
    }

    public int getEndPort() {
        return endPort;
    }

    public void setEndPort(int endPort) {
        this.endPort = endPort;
    }

    public long getfTime() {
        return fTime;
    }

    /**
     * 以秒做单位
     * @param fTime
     */
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

    @Override
    public String toString() {
        return staMAC+","+apMac+","+apName+","+startIp+","+endIp+","+startPort+","+endPort+","+fTime+","+lTime+","+latitude+","+longitude;
//        return "StaConInfo{" +
//                "staMAC=" + staMAC +
//                ", apMac=" + apMac +
//                ", apName='" + apName + '\'' +
//                ", startIp=" + startIp +
//                ", endIp=" + endIp +
//                ", startPort=" + startPort +
//                ", endPort=" + endPort +
//                ", fTime='" + fTime + '\'' +
//                ", lTime='" + lTime + '\'' +
//                ", latitude=" + latitude +
//                ", longitude=" + longitude +
//                '}';
    }
}

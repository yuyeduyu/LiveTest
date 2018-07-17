package com.ascend.wangfeng.locationbyhand.data.saveData;


/**
 * Created by zsw on 2018/5/8.
 */

//需要上传的ap信息
public class ApData {

    private String TAG = getClass().getCanonicalName();

    private String apMACStr;            //APMAC

    private long apMAC;
    private String apName;              //AP名称
    private int scanNum;                //扫描次数
    private int apChannel;              //ap信道
    private int signal;                 //信道强度
    private long fTime;               //第一次采集到的时间
    private long lTime;               //最后一次采集到的时间
    private int encrypt;             //加密方式

    private double latitude;              //纬度
    private double longitude;             //经度

    public ApData(){

    }

    public String getApMACStr() {
        return apMACStr;
    }

    public void setApMACStr(String apMACStr) {
        this.apMACStr = apMACStr;
        String mac = "";
        mac = apMACStr.replaceAll(":", "").trim(); // colon
        setApMAC(Long.valueOf(mac, 16));
    }

    public long getApMAC() {
        return apMAC;
    }

    public void setApMAC(long apMAC) {
        this.apMAC = apMAC;
    }

    public String getApName() {
        return apName;
    }

    public void setApName(String apName) {
        this.apName = apName;
    }

    public int getScanNum() {
        return scanNum;
    }

    public void setScanNum(int scanNum) {
        this.scanNum = scanNum;
    }

    public int getApChannel() {
        return apChannel;
    }

    public void setApChannel(int apChannel) {
        this.apChannel = apChannel;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public long getfTime() {
        return fTime;
    }

    /**
     * 以秒做danwe
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

    public int getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(int encrypt) {
        this.encrypt = encrypt;
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
//        return "ApData{" +
//                "apMAC=" + apMAC +
//                ", apName='" + apName + '\'' +
//                ", scanNum=" + scanNum +
//                ", apChannel=" + apChannel +
//                ", signal=" + signal +
//                ", fTime=" + fTime +
//                ", lTime=" + lTime +
//                ", encrypt=" + encrypt +
//                ", latitude=" + latitude +
//                ", longitude=" + longitude +
//                '}';
        return apMAC+","+apName+","+apChannel+","+signal+","+scanNum+","+fTime+","+lTime+","+encrypt+","+latitude+","+longitude;

    }
}

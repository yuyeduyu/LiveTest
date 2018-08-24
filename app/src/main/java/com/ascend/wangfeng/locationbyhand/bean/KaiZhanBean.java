package com.ascend.wangfeng.locationbyhand.bean;

import java.io.Serializable;

/**
 * 作者：lish on 2018-08-10.
 * 描述：
 */

public class KaiZhanBean implements Serializable {
    //    MAC地址
//            设备编号
//    手机IMEI
//            姓名
//    身份证/警号
//            手机号
//    所属警种
//    地市
//           区县
//    派出所
//            经度
//    纬度
    private String mac;
    private String imei;
    private String devCode;
    private String name;
    private String card;
    private String phone;
    private String minjing;
    private String area;
    private String city;
    private String paichusuo;
    private double latitude;
    private double longitude;

    public String getDevCode() {
        return devCode;
    }

    public void setDevCode(String devCode) {
        this.devCode = devCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMinjing() {
        return minjing;
    }

    public void setMinjing(String minjing) {
        this.minjing = minjing;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPaichusuo() {
        return paichusuo;
    }

    public void setPaichusuo(String paichusuo) {
        this.paichusuo = paichusuo;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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
}

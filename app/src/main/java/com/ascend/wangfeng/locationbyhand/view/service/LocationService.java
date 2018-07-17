package com.ascend.wangfeng.locationbyhand.view.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.ascend.wangfeng.locationbyhand.data.saveData.LocationData;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by zsw on 2018/5/10.
 */

public class LocationService extends Service {

    private String TAG = getClass().getCanonicalName();
//定位服务客户
    private LocationClient mLocationClient = null;
    private BDLocationListener mBDLocationListener;
    private double mLatitude;             //纬度
    private double mLongitude;            //经度
    private double lastmLatitude;             //最后一次纬度
    private double lastmLongitude;            //最后一次经度
    private long gpsTime;                 //经纬度时间

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getLocation();
    }

    //    private LocationData locationData;
    /**
     * 获取准确的经纬度信息
     */
    public void getLocation(){
        mBDLocationListener = new MyBDLocationListener();
        mLocationClient = new LocationClient(getBaseContext());
        //注册监听
        mLocationClient.registerLocationListener(mBDLocationListener);
        //声明定位参数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");               //gcj02（国测局坐标）和bd09ll（百度经纬度坐标）
        option.setScanSpan(2000);                   //定位请求间隔
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setWifiCacheTimeOut(5*60*1000);
        option.setEnableSimulateGps(false);
        option.setIsNeedAddress(true);
        option.setIsNeedAltitude(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }



    // 定位请求回调接口，得到经纬度，地址信息
    public class MyBDLocationListener implements BDLocationListener {
        //定位请求回调函数，在这里得到定位信息
        @Override
        public void onReceiveLocation(BDLocation location) {
//            Log.e("LocationService", "onReceiveLocation: 返回码："+location.getLocType()+
//                    "     "+location.getLatitude()+","+location.getLongitude());
            if (location!=null){
                mLatitude = location.getLatitude();          //获取百度地图纬度
                mLongitude = location.getLongitude();        //获取百度地图经度
//                Log.e("mLatitude:","mLatitude:"+mLatitude+"     mLongitude:"+mLongitude);
                //坐标偏差纠正
                double[] Gcj02 = Bd09ToGcj02(mLatitude,mLongitude);        //百度经纬度坐标改成火星坐标系
                double[] WGS84 = GcjToWGS84(Gcj02[0],Gcj02[1]);           //火星坐标系改成谷歌地图坐标系
                //谷歌地图下的经纬度
//                mLatitude = (double)Math.round(WGS84[0]*100000)/100000;
                mLatitude = new BigDecimal(WGS84[0]).setScale(5, RoundingMode.UP).doubleValue();
//                mLongitude = (double)Math.round(WGS84[1]*100000)/100000;
                mLongitude = new BigDecimal(WGS84[1]).setScale(5, RoundingMode.UP).doubleValue();
                if (mLatitude<0){
                    mLatitude = lastmLatitude;
                }
                if (mLongitude < 0){
                    mLongitude = lastmLongitude;
                }
                lastmLatitude = mLatitude;

                lastmLongitude = mLongitude;
                gpsTime = System.currentTimeMillis()/1000;
                EventBus.getDefault().post(new LocationData(mLatitude,mLongitude,gpsTime));
//                Log.e("mLatitude", mLatitude+"    "+mLongitude);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }


    private double PI = 3.1415926535897932384626;
    private double A = 6378245.0;
    private double EE = 0.00669342162296594323;

    //: Change BD-09 to Gcj-02          //百度转火星坐标系
    private double[] Bd09ToGcj02(double mLatitude,double mLongitude){
        double x = mLongitude - 0.0065;
        double y = mLatitude - 0.006;
        double z = Math.sqrt(x*x+y*y)-0.00002* Math.sin(y*PI);
        double theta = Math.atan2(y,x) - 0.000003* Math.cos(x*PI);
        double latitude = z* Math.sin(theta);
        double longitude = z* Math.cos(theta);
        double[] location = {latitude,longitude};
        return location;

    }

    //: Change Gcj-02 to WGS-84             //火星转谷歌
    private double[] GcjToWGS84(double mLatitude,double mLongitude){
        double[] wgs84_pos = tran(mLatitude,mLongitude);
        double gcj02_lon = mLongitude * 2 - wgs84_pos[1];           //纬度
        double gcj02_lat = mLatitude * 2 - wgs84_pos[0];           //经度

        double[] location = {gcj02_lat,gcj02_lon};
        return location;
    }



    private double[] tran(double lat ,double lon){
        double dLat = transformaLat(lon-105.0,lat-35.0);
        double dLon = transformaLon(lon-105.0,lat-35.0);
        double radLat = lat/180.0*PI;
        double magic = Math.sin(radLat);
        magic = 1-EE*magic*magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0)/((A * (1 - EE)) / (magic * sqrtMagic) * PI);
        dLon = (dLon * 180.0)/(A / sqrtMagic * Math.cos(radLat) * PI);
        double mgLat = lat+dLat;
        double mgLon = lon+dLon;
        double[] location = {mgLat,mgLon};
        return location;
    }


    private double transformaLat(double x,double y){
        double ret = -100.0+2.0*x+3.0*y;
        ret += 0.2 * y * y + 0.1 * x * y;
        ret += 0.2* Math.sqrt(Math.abs(x));
        ret += (20.0* Math.sin(6.0 * x * PI)+20.0* Math.sin(2.0 * x *PI))*2.0/3.0;
        ret += (20.0* Math.sin(y * PI)+40.0* Math.sin(y/3.0*PI))*2.0/3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private double transformaLon(double x,double y){
        double ret = 300.0 + x +2.0 * y;
        ret += 0.1 * x * x + 0.1 * x * y;
        ret += 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }


}

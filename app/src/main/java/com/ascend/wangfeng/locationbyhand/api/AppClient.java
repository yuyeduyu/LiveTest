package com.ascend.wangfeng.locationbyhand.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils;
import com.ascend.wangfeng.locationbyhand.view.activity.SetftpActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by fengye on 2016/10/20.
 * email 1040441325@qq.com
 * Retrofit 客户端
 */
public class AppClient {
    public static WiFiApi wiFiApi;

    public static WiFiApi getStaticWiFiApi() {
        String url = "http://" + (String) SharedPreferencesUtils
                .getParam(MyApplication.mContext, "url_equipment", "192.168.111.11") + "/";
        if (wiFiApi == null) {
            synchronized (AppClient.class) { //synchronized 线程锁，锁的是对象
                if (wiFiApi == null) {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(2, TimeUnit.SECONDS)
                            .readTimeout(2000, TimeUnit.SECONDS)
                            .writeTimeout(2000, TimeUnit.SECONDS)
                            .addNetworkInterceptor(new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Request request = chain.request()
                                            .newBuilder()
                                            .addHeader("Connection", "close")
                                            .build();
                                    return chain.proceed(request);
                                }
                            }).build();

                    wiFiApi = new Retrofit.Builder()
                            .client(client)
                            .baseUrl(url)
//                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .build()
                            .create(WiFiApi.class);
                }
            }
        }
        return wiFiApi;
    }

    public static WiFiApi getWiFiApi() {
        String url = "http://" + (String) SharedPreferencesUtils
                .getParam(MyApplication.mContext, "url_equipment", "192.168.111.11") + "/";
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("Connection", "close")
                                .build();
                        return chain.proceed(request);
                    }
                }).build();

        WiFiApi wiFiApi = new Retrofit.Builder()
                .client(client)
                .baseUrl(url)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WiFiApi.class);
        return wiFiApi;
    }

    public static AppVersionApi appVesionApi;

    public static AppVersionApi getAppVersionApi() {
        if (appVesionApi == null) {
            synchronized (AppClient.class) { //synchronized 线程锁，锁的是对象
                if (appVesionApi == null) {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(2000, TimeUnit.SECONDS)
                            .writeTimeout(2000, TimeUnit.SECONDS)
                            .addNetworkInterceptor(new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Request request = chain.request()
                                            .newBuilder()
                                            .addHeader("Connection", "close")
                                            .build();
                                    return chain.proceed(request);
                                }
                            }).build();
                    appVesionApi = new Retrofit.Builder()
                            .baseUrl("http://123.57.175.155:9120/")
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(client)
                            .build()
                            .create(AppVersionApi.class);
                }
            }
        }
        return appVesionApi;
    }

    //获取布控目标 api
    public static TargetDataApi targetApi;

    public static TargetDataApi getTargetApi() {
        if (targetApi == null) {
            synchronized (AppClient.class) { //synchronized 线程锁，锁的是对象
                if (targetApi == null) {
                    SharedPreferences preferences = MyApplication.getInstances()
                            .getSharedPreferences("ftpData", Context.MODE_PRIVATE);
//                    String url = "http://" +preferences.getString("url", "123.57.175.155")
//                            +":"+preferences.getInt("port", 9120)+"/";
                    String url = "http://192.168.168.56:9086/";
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(2000, TimeUnit.SECONDS)
                            .writeTimeout(2000, TimeUnit.SECONDS)
                            .addNetworkInterceptor(new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Request request = chain.request()
                                            .newBuilder()
                                            .addHeader("Connection", "close")
                                            .build();
                                    return chain.proceed(request);
                                }
                            }).build();
                    targetApi = new Retrofit.Builder()
                            .baseUrl(url)
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(client)
                            .build()
                            .create(TargetDataApi.class);
                }
            }
        }
        return targetApi;
    }

    public static void reSetTargetApi() {
        targetApi = null;
    }

    public static OkHttpClient getOkhttpClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(2000, TimeUnit.SECONDS)
                .writeTimeout(2000, TimeUnit.SECONDS)
                .build();
        return client;
    }
}

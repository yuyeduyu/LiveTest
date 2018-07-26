package com.ascend.wangfeng.locationbyhand.api;

import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by fengye on 2016/10/20.
 * email 1040441325@qq.com
 * Retrofit 客户端
 */
public class AppClient {
    public static  WiFiApi wiFiApi;
    public static WiFiApi getStaticWiFiApi(){
        String url="http://"+ (String) SharedPreferencesUtils
                .getParam(MyApplication.mContext,"url_equipment","192.168.111.11")+"/";
        if (wiFiApi==null){
           synchronized (AppClient.class){if (wiFiApi == null){
                OkHttpClient client=new OkHttpClient.Builder()
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
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                        .create(WiFiApi.class);
            }
        }}
        return wiFiApi;
    }
    public static WiFiApi getWiFiApi(){
        String url="http://"+ (String) SharedPreferencesUtils
                .getParam(MyApplication.mContext,"url_equipment","192.168.111.11")+"/";
        OkHttpClient client=new OkHttpClient.Builder()
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

    public static AppVersionApi getAppVersionApi() {
        OkHttpClient client=new OkHttpClient.Builder()
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
        AppVersionApi appVesionApi = new Retrofit.Builder()
                .baseUrl("http://123.57.175.155:9120/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(AppVersionApi.class);
        return appVesionApi;
    }

}

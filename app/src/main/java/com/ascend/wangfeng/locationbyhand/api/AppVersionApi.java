package com.ascend.wangfeng.locationbyhand.api;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by fengye on 2017/4/7.
 * email 1040441325@qq.com
 */
public interface AppVersionApi {
    @FormUrlEncoded
    @POST("web-com.feng.ssm/appVersion")
    Observable<Integer> getLatestVersion(@Field("id")Integer id);

    @FormUrlEncoded
    @POST("web-com.feng.ssm/apk")
    Observable<String> getApkUrl(@Field("version")Integer id);

    @GET("apps/{url}")
    Observable<ResponseBody> UpdateApk(@Path("url") String url);

}

package com.ascend.wangfeng.locationbyhand.api;

import com.ascend.wangfeng.locationbyhand.bean.dbBean.NoteDo;
import com.ascend.wangfeng.locationbyhand.resultBack.TargetFromNetRuslt;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 作者：lish on 2018-08-17.
 * 描述：获取网络布控目标 (废弃)
 */

public interface TargetDataApi {
    @GET("app/monitor/getRuleCorrelationGroup.do")
    Call<String> getTargtFromNet();
}

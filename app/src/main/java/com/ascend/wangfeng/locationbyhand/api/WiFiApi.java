package com.ascend.wangfeng.locationbyhand.api;

import com.ascend.wangfeng.locationbyhand.bean.AlarmMacListDo;
import com.ascend.wangfeng.locationbyhand.bean.ApAssociatedDo;
import com.ascend.wangfeng.locationbyhand.bean.ApMessageDo;
import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.StaAssociatedDo;
import com.ascend.wangfeng.locationbyhand.bean.StaMessageDo;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by fengye on 2016/10/8.
 * email 1040441325@qq.com
 */
public interface WiFiApi {
    /**
     * @return Ap列表
     */
    @POST("get_ap_message")
    Observable<ApMessageDo> getApMessage();

    /**
     * @return Sta列表
     */
    @POST("get_sta_message")
    Observable<StaMessageDo> getStaMessage();

    /**
     * @return 备注列表，包括配置信息
     */
    @POST("get_alarmMacList")
    Observable<AlarmMacListDo> getAlarmMacList();

    /**
     * @param mac  添加布控的mac
     * @param note 添加的备注
     * @return 备注列表，包括配置信息
     */
    @FormUrlEncoded
    @POST("addMac")
    Observable<AlarmMacListDo> addMac(@Field("mac") String mac, @Field("note") String note);

    /**
     * @param mac 要删除的布控mac
     * @return 备注列表，包括配置信息
     */
    @FormUrlEncoded
    @POST("del_mac")
    Observable<AlarmMacListDo> delMac(@Field("mac") String mac);

    /**
     * @param macBefore 需要更改的布控mac
     * @param macAfter  更改后的布控空mac
     * @param note      更改后的备注
     * @return 备注列表，包括配置信息
     */
    @FormUrlEncoded
    @POST("update_mac")
    Observable<AlarmMacListDo> updateMac(@Field("macOld") String macBefore,
                                         @Field("macNew") String macAfter,
                                         @Field("note") String note);

    /**
     * @param channel 要锁定的信道 a表示自动;
     * @return true
     */
    @FormUrlEncoded
    @POST("set_channel")
    Observable<Boolean> setChannel(@Field("channel") String channel);

    /**
     * @param time 同步系统时间;
     * @return true
     */
    @FormUrlEncoded
    @POST("set_time")
    Observable<String> setTime(@Field("time") long time);

   /**
     *
     * @param count 模式id  0或1 0表示移动模式 1表示静止模式
     * @return true
     */
    @FormUrlEncoded
    @POST("/set_count")
    Observable<Boolean> setCount(@Field("count") String count);

    /**
     * @param type 0 ：表示非自动锁定模式 1 ：表示自动锁定模式
     * @return
     */
    @FormUrlEncoded
    @POST("set_track_channel")
    Observable<Boolean> setPatternOfChannelLock(@Field("track_num") Integer type);

    /**
     * @param mac ap的mac
     * @return 单个ap的信息
     */
    @FormUrlEncoded
    @POST("LineChartAp")
    Observable<ApVo> getAp(@Field("mac") String mac);

    /**
     * @param mac 终端的mac
     * @return 单个终端的信息
     */
    @FormUrlEncoded
    @POST("LineChartSta")
    Observable<StaVo> getSta(@Field("mac") String mac);

    /**
     *
     * @param mac
     * @return 连接关系，(当前ap下的所有终端)
     */
    @FormUrlEncoded
    @POST("get_ap_associated")
    Observable<ApAssociatedDo> getApAssociated(@Field("mac") String mac);

    /**
     *
     * @param mac
     * @return 连接关系(当前终端连接的ap,及同ap下的所有终端)
     */
    @FormUrlEncoded
    @POST("get_sta_associated")
    Observable<StaAssociatedDo> getStaAssociated(@Field("mac")String mac);
    /**
     *
     * @param mac
     * @return 获取mac oui -->设备厂商
     */
    @FormUrlEncoded
    @POST("get_sta_associated")
    Observable<String> getOui(@Field("mac")String mac);

}

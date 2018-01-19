package com.ascend.wangfeng.locationbyhand.contract;

/**
 * Created by fengye on 2017/2/7.
 * email 1040441325@qq.com
 */
public class SetContract {
public interface View{
    void showMessage(String message);
}

public interface Presenter{

    void setRing(boolean ring);//追踪
    void setChannel(int channel);
    //void setWorkStyle(int workStyle);
    void setChannelLock(int channelLock);
    void setUrl(String url);
    void setTime(String time);
    void receiveResult(String message);
}

public interface Model{
    void setRing(boolean ring);//追踪
    void setChannel(int channel);
    //void setWorkStyle(int workStyle);
    void setChannelLock(int channelLock);
    void setUrl(String url);
    void setTime(long time);

}


}
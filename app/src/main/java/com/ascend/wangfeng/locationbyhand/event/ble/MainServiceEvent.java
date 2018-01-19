package com.ascend.wangfeng.locationbyhand.event.ble;

/**
 * Created by fengye on 2017/8/11.
 * email 1040441325@qq.com
 * 向MainService发送信息
 */

public class MainServiceEvent {
    public static final int LOCK=1;//锁定信道并发送当前的需要的mac,类型
    public static final int UNLOCK=2;
    public static final int GETNUMBER=3;//获取编号
    public static final int CLEAE_DATA =4;//切换2.4g,5g时清空数据
    private int command;
    private int channel;
    private String mac;
    private int type;

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public MainServiceEvent(int command) {
        this.command = command;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

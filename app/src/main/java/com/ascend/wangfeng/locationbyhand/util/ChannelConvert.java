package com.ascend.wangfeng.locationbyhand.util;

/**
 * Created by fengye on 2017/8/29.
 * email 1040441325@qq.com
 */

public class ChannelConvert {
    /**
     * @param channel 5G信道
     * @return 转化为1-13
     */
    public static int convertChannel(int channel) {
        switch (channel) {
            case 36:
                return 1;
            case 40:
                return 2;
            case 44:
                return 3;
            case 48:
                return 4;
            case 52:
                return 5;
            case 56:
                return 6;
            case 60:
                return 7;
            case 64:
                return 8;
            case 149:
                return 9;
            case 153:
                return 10;
            case 157:
                return 11;
            case 161:
                return 12;
            case 165:
                return 13;
            default:
                return 0;
        }
    }
}

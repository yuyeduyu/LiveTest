package com.ascend.wangfeng.locationbyhand.util;

import org.junit.Test;

/**
 * Created by fengye on 2017/8/29.
 * email 1040441325@qq.com
 */
public class ChannelConvertTest {
    @Test
    public void convertChannel() throws Exception {
        for (int i = 1; i <=13; i++) {
            int channel = i*4+32;
            int convert =ChannelConvert.convertChannel(channel);
            System.out.println(convert+"---"+channel);
        }
    }

}
package com.ascend.wangfeng.locationbyhand.util;

import android.widget.ImageView;

import com.ascend.wangfeng.locationbyhand.R;

/**
 * Created by fengye on 2017/3/24.
 * email 1040441325@qq.com
 */

public class PowerImageSet {
    public static void setImage(ImageView mElectric,int power){
        if (power<20){
            mElectric.setImageResource(R.drawable.power_0);
        }else if (power<40){
            mElectric.setImageResource(R.drawable.power_1);
        }else if (power<60){
            mElectric.setImageResource(R.drawable.power_2);
        }else if (power<80){
            mElectric.setImageResource(R.drawable.power_3);
        }else {
            mElectric.setImageResource(R.drawable.power_4);
        }
    }
}

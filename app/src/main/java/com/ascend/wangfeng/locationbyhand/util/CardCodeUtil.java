package com.ascend.wangfeng.locationbyhand.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 作者：lish on 2018-08-23.
 * 描述：
 */

public class CardCodeUtil {
    public static boolean cardCodeVerifySimple(String cardcode) {
        //第一代身份证正则表达式(15位)
        String isIDCard1 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
        //第二代身份证正则表达式(18位)
        String isIDCard2 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])((\\d{4})|\\d{3}[A-Z])$";

        //验证身份证
        if (cardcode.matches(isIDCard1) || cardcode.matches(isIDCard2)) {
            return true;
        }
        return false;
    }

    public static boolean isMobileNO(String mobiles) {

//        Pattern p = Pattern.compile("^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$");
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");

        Matcher m = p.matcher(mobiles);

        return m.matches();

    }

}

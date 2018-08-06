package com.ascend.wangfeng.locationbyhand.util;

import java.util.regex.Pattern;

/**
 * 作者：lishanhui on 2018-07-04.
 * 描述：
 */

public class MacUtils {
    /**
     * 校验mac正确性
     *
     * @author lishanhui
     * created at 2018-07-04 9:48
     */
    public static boolean CheckMac(String mac) {
//        mac = "00-E0-20-1C-7C-0C";

        //正则校验MAC合法性
        if (mac.indexOf("-") > -1) {
            String patternMac = "^[A-Fa-f0-9]{2}(-[A-Fa-f0-9]{2}){5}$";
            if (!Pattern.compile(patternMac).matcher(mac).find()) {
                return false;
            }
        } else if (mac.indexOf(":") > -1) {
            String patternMac = "^[A-Fa-f0-9]{2}(:[A-Fa-f0-9]{2}){5}$";
            if (!Pattern.compile(patternMac).matcher(mac).find()) {
                return false;
            }
        } else {
            String patternMac = "^[A-Fa-f0-9]{2}([A-Fa-f0-9]{2}){5}$";
            if (!Pattern.compile(patternMac).matcher(mac).find()) {
                return false;
            }
        }

        return true;
    }

    /**
     * 将mac格式标准化00-00-00-00-00-00
     *
     * @author lishanhui
     * created at 2018-07-04 10:23
     */
    public static String formatMac(String mac) {
        if (mac.contains(":")) {
            mac = mac.replace(":", "-");
        }
        if (mac.length() == 12) {
            String regex = "(.{2})";
            mac = mac.replaceAll(regex, "$1-");
            mac = mac.substring(0, mac.length() - 1);
        }
        return mac;
    }

    /**
     * 根据mac长度，将mac格式标准化00-00-00-00-00-00
     * 为根据mac模糊查询服务
     *
     * @author lishanhui
     * created at 2018-07-04 10:23
     */
    public static String formatMacForLike(String mac) {
        if (mac.contains(":")) {
            mac = mac.replace(":", "");
        }
        if (mac.contains("-")) {
            mac = mac.replace("-", "");
        }

        String regex = "(.{2})";
        mac = mac.replaceAll(regex, "$1-");
        if (mac.endsWith("-"))
            mac = mac.substring(0, mac.length() - 1);

        return mac;
    }
}

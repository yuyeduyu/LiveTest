package com.ascend.wangfeng.locationbyhand.data;

/**
 * Created by zsw on 2018/6/26.
 * 描述 :
 */

public class FileUp {

//    public static final int FILE_UP = 1;
//
//    public static void upload(final String ftpFileName, final Handler handler, final Context context) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                String localFilePath = Environment.getExternalStorageDirectory()
//                        .getPath() + "/Ascend/RMS/data/";
//                String result = getFileUpTool(context).ftpUpload(localFilePath, ftpFileName);
//                Message message = new Message();
//                if (result.equals("1")) {
//                    message.arg1 = 1;
//                } else {
//                    message.arg1 = 0;
//                    SharedPreferences preferences = context.getSharedPreferences("wwj", Context.MODE_PRIVATE);
//                    if (preferences.getString("url", "") == null ||
//                            preferences.getString("user", "") == null ||
//                            preferences.getString("password", "") == null ||
//                            preferences.getString("path", "") == null) {
//                        message.arg2 = 1;
//                    } else {
//                        message.arg2 = 2;
//                    }
//                }
//                message.what = FILE_UP;
//                handler.sendMessage(message);
//
//            }
//        }).start();
//    }
//
//    private static FileUpTool getFileUpTool(Context context) {
////        context.getSharePreferences（）；    第一个参数是xml文件名， 第二个是存储的格式
//        SharedPreferences preferences = context.getSharedPreferences("wwj", Context.MODE_PRIVATE);
//        FileUpTool upTool = new FileUpTool();
//        upTool.setUrl(preferences.getString("url", ""));
//        upTool.setPort(preferences.getInt("port", 0));
//        upTool.setUser(preferences.getString("user", ""));
//        upTool.setPassword(preferences.getString("password", ""));
//        upTool.setPath(preferences.getString("path", ""));
//        return upTool;
//    }

}

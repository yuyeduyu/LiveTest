package com.ascend.wangfeng.locationbyhand.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;



/**
 * Created by fengye on 2017/3/22.
 * email 1040441325@qq.com
 */

public class OuiDatabase {
    public static SQLiteDatabase database;
    public static final int MATCH_LENGTH = 3;
    private static File file;


    /**
     * 匹配oui
     *
     * @param mac
     * @return
     */
    public static String ouiMatch(String mac) {
        mac = formatMac(mac);
        openDatabase();
        String result = null;
        String sql = "select value from oui where key=?";
        if (database==null)return "have not oui database";
        Cursor cursor = database.rawQuery(sql, new String[]{mac});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndex("value"));
        }
        return result;
    }

    private static SQLiteDatabase openDatabase() {
        if (database!=null)return database;
        checkDatabase();
        try{
            database = SQLiteDatabase.openOrCreateDatabase(file, null);
        }catch (Exception e){

        }

        return database;
    }

    /**
     * 检查是否存在oui数据库，没有则创建;
     * @return
     */
    private static boolean checkDatabase()  {
        String sdPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                + "/AscendLog/LocationShow/";
        String databaseFilename = "oui.db";
        File dir = new File(sdPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        file = new File(dir, databaseFilename);
        if (file.exists()){
            return true;
        }
        try {
           FileOutputStream fos = new FileOutputStream(file,true);
            InputStream is = MyApplication.mContext.getResources().openRawResource(R.raw.oui);
            byte[] buffer = new byte[8192];
            int count =0;
            while ((count = is.read(buffer))>0){
                fos.write(buffer,0,count);
            }
            is.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {


        }
        return false;
    }

    /**
     *  截取mac前三位，并将':'转'-'
     * @param mac
     * @return
     */
    public static String formatMac(String mac) {
        String[] strings = mac.split(":");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < MATCH_LENGTH; i++) {
            result.append(strings[i]);
            if (i<MATCH_LENGTH-1){
            result.append("-");}

        }
        Log.e("test", "formatMac: " + result);
        return String.valueOf(result);
    }
    public static void destroy(){
        if (database!=null){
            database.close();
        }
    }

}

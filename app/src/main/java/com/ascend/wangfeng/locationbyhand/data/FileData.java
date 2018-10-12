package com.ascend.wangfeng.locationbyhand.data;

import android.content.Context;
import android.util.Log;

import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.data.saveData.ApData;
import com.ascend.wangfeng.locationbyhand.data.saveData.LocationData;
import com.ascend.wangfeng.locationbyhand.data.saveData.StaConInfo;
import com.ascend.wangfeng.locationbyhand.data.saveData.StaData;
import com.ascend.wangfeng.locationbyhand.util.LogUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * Created by zsw on 2018/5/8.
 * 将数据写入本地文件中
 */
//往文件中写入数据
public class FileData {

    private String TAG = getClass().getCanonicalName();

    String strFilePath;
    RandomAccessFile raf;
    String strContent;
    File file;

    String filePath = "";
    String fileName = "";
    private String version;
    private Context mContext;

    public FileData() {
    }

    //写入开站数据
    public FileData(Context mContext, String filePath, String fileName, StringBuffer cont) {
        this.mContext = mContext;

        this.filePath = filePath;
        this.fileName = fileName;
        makeFilePath(filePath, fileName, false);                //生成文件夹之后，再生成文件，不然会出错
        writeTxtToFile(cont.toString());       // 将字符串写入到文本文件中
    }

    //写入ap数据
    public File FileData(Context mContext, String filePath, String fileName, List<ApData> aplist, String version) {
        this.mContext = mContext;
        this.filePath = filePath;
        this.fileName = fileName;
        this.version = version;
        makeFilePath(filePath, fileName, true);                //生成文件夹之后，再生成文件，不然会出错
        writeTxtToFile(aplist);       // 将字符串写入到文本文件中
        return file;
    }

    //写入终端数据
    public File FileData(Context mContext, String filePath, String fileName, List<StaData> stalist, String version, int a) {
        this.mContext = mContext;

        this.filePath = filePath;
        this.fileName = fileName;
        this.version = version;
        makeFilePath(filePath, fileName, true);                //生成文件夹之后，再生成文件，不然会出错
        writeTxtToFile(stalist, a);       // 将字符串写入到文本文件中
        return file;
    }

    //写入连接信息
    public File FileData(Context mContext, String filePath, String fileName, List<StaConInfo> sclist, String version, String b) {
        this.mContext = mContext;

        this.filePath = filePath;
        this.fileName = fileName;
        this.version = version;
        makeFilePath(filePath, fileName, true);                //生成文件夹之后，再生成文件，不然会出错
        writeTxtToFile(sclist, b);       // 将字符串写入到文本文件中
        return file;
    }

    //写入经纬度坐标信息
    public File FileData(Context mContext, String filePath, String fileName, List<LocationData> gpslist, String version, String b, int a) {
        this.mContext = mContext;
        this.filePath = filePath;
        this.fileName = fileName;
        this.version = version;
        makeFilePath(filePath, fileName, true);                //生成文件夹之后，再生成文件，不然会出错
        writeTxtToFile(gpslist, b, a);
        return file;
    }


    // 生成文件并写入开头首行内容
    private File makeFilePath(String filePath, String fileName, boolean haveTitle) {
        makeRootDirectory(filePath);                    //生产文件夹
        try {
            file = new File(filePath + fileName);
            LogUtils.e("creactfile",filePath+fileName);
            if (file.exists()) {
                file.delete();
            } else {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            raf = new RandomAccessFile(file, "rwd");           //对文件内容进行操作

            if (haveTitle) {
                strContent = "asd_iwm_02," + MyApplication.mDevicdMac.replaceAll(":", "") + "," + version + "," + MyApplication.mDevicdID + "," + "211.211.211.211," + "\n";
//            Log.e(TAG,"头行信息:"+strContent+"    手机MAC地址："+getLocalMacAddressFromIp());
                raf.seek(file.length());
                raf.write(strContent.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 字符串写入到文本文件中

    private void writeTxtToFile(String cont) {
        try {
            raf.seek(file.length());
            raf.write(cont.getBytes());
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 将ap字符串写入到文本文件中

    private void writeTxtToFile(List<ApData> aplist) {
        try {
            for (int i = 0; i < aplist.size(); i++) {
                String str = aplist.get(i).toString() + "\n";
                raf.seek(file.length());
                raf.write(str.getBytes());
            }

            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 将ap字符串写入到文本文件中
    private void writeTxtToFile(List<StaData> stalist, int a) {
        try {
            for (int i = 0; i < stalist.size(); i++) {
                String str = stalist.get(i).toString() + "\n";
                raf.seek(file.length());
                raf.write(str.getBytes());
            }
//            if (stalist.size() == 0){
//                raf.seek(file.length());
//                raf.write(("暂无数据"+"\n").getBytes());
//            }

            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 将ap字符串写入到文本文件中
    private void writeTxtToFile(List<StaConInfo> sclist, String b) {
        try {
            for (int i = 0; i < sclist.size(); i++) {
                String str = sclist.get(i).toString() + "\n";
                raf.seek(file.length());
                raf.write(str.getBytes());
            }
//            if (sclist.size() == 0){
//                raf.seek(file.length());
//                raf.write(("暂无数据"+"\n").getBytes());
//            }
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 将ap字符串写入到文本文件中
    private void writeTxtToFile(List<LocationData> gpslist, String b, int a) {
        try {
            for (int i = 0; i < gpslist.size(); i++) {
                String str = gpslist.get(i).toString() + "\n";
                raf.seek(file.length());
                raf.write(str.getBytes());

            }
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 生成文件夹
    private static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    private String getMobileMAC() {
        String mobileMac = MacUtils.getMobileMAC(mContext);
        String mobileMAC = "";
        String[] macs = mobileMac.split(":");
        for (int i = 0; i < macs.length; i++) {
            mobileMAC = mobileMAC + macs[i];
        }
        return mobileMAC;
    }
}


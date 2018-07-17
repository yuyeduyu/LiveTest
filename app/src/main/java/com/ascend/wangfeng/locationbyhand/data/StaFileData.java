package com.ascend.wangfeng.locationbyhand.data;//package com.ascend.wangfeng.locationbyhand.data;
//
//import android.content.Context;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.util.Log;
//
//import com.ascend.wangfeng.locationbyhand.MyApplication;
//import com.ascend.wangfeng.locationbyhand.data.saveData.StaData;
//
//import java.io.File;
//import java.io.RandomAccessFile;
//import java.net.InetAddress;
//import java.net.NetworkInterface;
//import java.net.SocketException;
//import java.util.Enumeration;
//import java.util.List;
//
///**
// * Created by Administrator on 2018/5/9.
// */
//
//public class StaFileData {
//
//    private String TAG = getClass().getCanonicalName();
//
//
//    private String filePath;
//    private String fileName;
//    private String version;
//    private String string;
//
//    private List<StaData> stalist;
//
//
//    public StaFileData(String filePath, String fileName, List<StaData> stalist, String version){
//        this.filePath = filePath;
//        this.fileName = fileName;
//        this.version = version;
//        this.stalist = stalist;
//        makeFilePath(filePath,fileName);                //生成文件夹之后，再生成文件，不然会出错
//        writeTxtToFile(stalist,filePath,fileName);       // 将字符串写入到文本文件中
//    }
//
//
//
//
//    // 将字符串写入到文本文件中
//    private void writeTxtToFile(List<StaData> stalist, String filePath, String fileName) {
//
//        String strFilePath = filePath+fileName;
//        // 每次写入时，都换行写    asd_iwm_03+设备编号+版本号
//        String strContent = "asd_iwm_03,"+getLocalMacAddressFromIp()+","+version+"\r\n";
//        try {
//            File file = new File(strFilePath);
//            if (!file.exists()) {
//                Log.d("TestFile", "Create the file:" + strFilePath);
//                file.getParentFile().mkdirs();
//                file.createNewFile();
//            }
//            RandomAccessFile raf = new RandomAccessFile(file, "rwd");           //对文件内容进行操作
//            raf.seek(file.length());
//            raf.write(strContent.getBytes());
//            for (int i = 0;i<stalist.size();i++){
//                raf.seek(file.length());
//                raf.write((stalist.get(i).toString()+"\r\n").getBytes());
//            }
//            if (stalist.size() == 0){
//                raf.seek(file.length());
//                raf.write(("暂无终端"+"\r\n").getBytes());
//            }
//            raf.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("TestFile", "Error on write File:" + e);
//        }
//    }
//
//
//    // 生成文件
//    private File makeFilePath(String filePath, String fileName) {
//        File file = null;
//        makeRootDirectory(filePath);                    //生产文件夹
//        try {
//            file = new File(filePath + fileName);
//            if (file.exists()){
//                file.delete();
//                Log.e(TAG,"删除已经存在的文件");
//            }
//            file.createNewFile();
////            Log.e(TAG,"成功创建新的文件");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return file;
//    }
//
//    // 生成文件夹
//    private static void makeRootDirectory(String filePath) {
//        File file = null;
//        try {
//            file = new File(filePath);
//            if (!file.exists()) {
//                file.mkdir();
//            }
//        } catch (Exception e) {
//            Log.i("error:", e+"");
//        }
//    }
//
//
//    /**
//     * 根据IP地址获取MAC地址
//     *
//     * @return
//     */
//    private static String getLocalMacAddressFromIp() {
//        String strMacAddr = null;
//        try {
//            //获得IpD地址
//            InetAddress ip = getLocalInetAddress();
//            byte[] b = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
//            StringBuffer buffer = new StringBuffer();
//            for (int i = 0; i < b.length; i++) {
//                if (i != 0) {
//                    buffer.append(':');
//                }
//                String str = Integer.toHexString(b[i] & 0xFF);
//                buffer.append(str.length() == 1 ? 0 + str : str);
//            }
//            strMacAddr = buffer.toString().toUpperCase();
//        } catch (Exception e) {
//
//        }
//        return strMacAddr;
//    }
//
//    /**
//     * 获取移动设备本地IP
//     *
//     * @return
//     */
//    private static InetAddress getLocalInetAddress() {
//        InetAddress ip = null;
//        try {
//            //列举
//            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
//            while (en_netInterface.hasMoreElements()) {//是否还有元素
//                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//得到下一个元素
//                Enumeration<InetAddress> en_ip = ni.getInetAddresses();//得到一个ip地址的列举
//                while (en_ip.hasMoreElements()) {
//                    ip = en_ip.nextElement();
//                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
//                        break;
//                    else
//                        ip = null;
//                }
//                if (ip != null) {
//                    break;
//                }
//            }
//        } catch (SocketException e) {
//
//            e.printStackTrace();
//        }
//        return ip;
//    }
//
//}

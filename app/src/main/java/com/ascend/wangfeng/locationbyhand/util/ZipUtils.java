package com.ascend.wangfeng.locationbyhand.util;

import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 作者：lish on 2018-10-10 15:47
 * 描述：
 */
public class ZipUtils {
    private static final String TAG = "ZipUtils";

    public ZipUtils() {

    }

    public void compressFile(File[] fs, String path) {
        //创建文件夹
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new CheckedOutputStream(new FileOutputStream(path), new CRC32()));
            for (File file : fs) {
                if (file == null || !file.exists()) {
                    continue;
                }
                zip(zipOutputStream, "res", file);
                //压缩后删除源文件
                file.delete();
            }
            zipOutputStream.flush();
            zipOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void zip(ZipOutputStream zipOutputStream, String name, File fileSrc) throws IOException {

        if (fileSrc.isDirectory()) {
            System.out.println("需要压缩的地址是目录");
            File[] files = fileSrc.listFiles();

            name = name + "/";
            zipOutputStream.putNextEntry(new ZipEntry(name));  // 建一个文件夹
            System.out.println("目录名: " + name);

            for (File f : files) {
                zip(zipOutputStream, name + f.getName(), f);
                System.out.println("目录: " + name + f.getName());
            }

        } else {
            System.out.println("需要压缩的地址是文件");
            zipOutputStream.putNextEntry(new ZipEntry(fileSrc.getName()));
            System.out.println("文件名: " + name);
            FileInputStream input = new FileInputStream(fileSrc);
            System.out.println("文件路径: " + fileSrc);
            byte[] buf = new byte[1024];
            int len = -1;

            while ((len = input.read(buf)) != -1) {
                zipOutputStream.write(buf, 0, len);
            }

            zipOutputStream.flush();
            input.close();
        }
    }

    /**
     * 解压缩
     * 将zipFile文件解压到folderPath目录下.
     *
     * @param zipFile    zip文件
     * @param folderPath 解压到的地址
     * @throws IOException
     */
    public void upZipFile(File zipFile, String folderPath) throws IOException {
        ZipFile zfile = new ZipFile(zipFile);
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                Log.d(TAG, "ze.getName() = " + ze.getName());
                String dirstr = folderPath + ze.getName();
                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                Log.d(TAG, "str = " + dirstr);
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            Log.d(TAG, "ze.getName() = " + ze.getName());
            OutputStream os = new BufferedOutputStream(new FileOutputStream(folderPath));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    substr = new String(substr.getBytes("8859_1"), "GB2312");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ret = new File(ret, substr);

            }
            Log.d(TAG, "1ret = " + ret);
            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            try {
                substr = new String(substr.getBytes("8859_1"), "GB2312");
                Log.d(TAG, "substr = " + substr);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ret = new File(ret, substr);
            Log.d(TAG, "2ret = " + ret);
            return ret;
        }
        return ret;
    }
}

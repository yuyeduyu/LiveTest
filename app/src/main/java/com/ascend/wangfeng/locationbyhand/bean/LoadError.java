package com.ascend.wangfeng.locationbyhand.bean;

import com.ascend.wangfeng.locationbyhand.data.FileData;

import java.io.Serializable;

/**
 * Created by Administrator on 2018\9\11 0011.
 * 上传数据失败 记录bean
 */

public class LoadError implements Serializable {
    private String filePath;
    private String fileName;

    public LoadError(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LoadError){
            LoadError error = (LoadError) obj;
            return error.getFileName().equals(this.fileName)
                    &&this.filePath.equals(error.getFilePath());
        }
        return super.equals(obj);
    }
}

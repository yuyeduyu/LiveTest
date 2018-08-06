package com.ascend.wangfeng.locationbyhand.event;

/**
 * 作者：lish on 2018-08-06.
 * 描述：
 */

public class FTPEvent {
    private boolean content;

    public FTPEvent(boolean content) {
        this.content = content;
    }

    public boolean isContent() {
        return content;
    }

    public void setContent(boolean content) {
        this.content = content;
    }
}

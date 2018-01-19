package com.ascend.wangfeng.locationbyhand.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by fengye on 2017/8/21.
 * email 1040441325@qq.com
 */
@Entity
public class EventBean {
    @Id(autoincrement = true)
    private Long id;
    private String title;
    private long start;
    private long end;
    private boolean run;

    public boolean getRun() {
        return this.run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public long getEnd() {
        return this.end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getStart() {
        return this.start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 1130884914)
    public EventBean(Long id, String title, long start, long end, boolean run) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
        this.run = run;
    }

    @Generated(hash = 1783294599)
    public EventBean() {
    }

    public EventBean(String title) {
        this.title = title;
    }

    public boolean isFinish() {
        if (end > 0) return true;
        return false;
    }

    public boolean isRun() {
        return getRun();
    }

    public void start() {
        this.start = System.currentTimeMillis();
        this.run = true;
    }

    public void stop() {
        this.end = System.currentTimeMillis();
        this.run = false;
    }
}

package com.ascend.wangfeng.locationbyhand.data.saveData;

import java.util.List;

/**
 * Created by zsw on 2018/5/11.
 */

public class UpLoadData {

    private String TAG = getClass().getCanonicalName();

    private List<ApData> aplist;
    private List<StaData> stalist;
    private List<StaConInfo> sClist;
    private List<LocationData> gpslist;

    public UpLoadData(List<ApData> aplist, List<StaData> stalist, List<StaConInfo> sClist, List<LocationData> gpslist){
        this.aplist = aplist;
        this.stalist = stalist;
        this.sClist = sClist;
        this.gpslist = gpslist;
    }

    public List<ApData> getAplist() {
        return aplist;
    }

    public void setAplist(List<ApData> aplist) {
        this.aplist = aplist;
    }

    public List<StaData> getStalist() {
        return stalist;
    }

    public void setStalist(List<StaData> stalist) {
        this.stalist = stalist;
    }

    public List<StaConInfo> getsClist() {
        return sClist;
    }

    public void setsClist(List<StaConInfo> sClist) {
        this.sClist = sClist;
    }

    public List<LocationData> getGpslist() {
        return gpslist;
    }

    public void setGpslist(List<LocationData> gpslist) {
        this.gpslist = gpslist;
    }
}

package com.ascend.wangfeng.locationbyhand.util.citychoice;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.data.FTPClientData;
import com.ascend.wangfeng.locationbyhand.data.FileData;
import com.ascend.wangfeng.locationbyhand.event.FTPEvent;
import com.ascend.wangfeng.locationbyhand.util.LogUtils;
import com.ascend.wangfeng.locationbyhand.util.citychoice.wheelview.OnWheelChangedListener;
import com.ascend.wangfeng.locationbyhand.util.citychoice.wheelview.OnWheelScrollListener;
import com.ascend.wangfeng.locationbyhand.util.citychoice.wheelview.WheelView;
import com.ascend.wangfeng.locationbyhand.util.citychoice.wheelview.adapter.AbstractWheelTextAdapter1;
import com.ascend.wangfeng.locationbyhand.view.activity.KaiZhanActivity;

import org.apache.commons.net.ftp.FTPClient;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 个人信息设置中城市选择窗口
 */

public class ChangeAddressPopwindow extends PopupWindow implements View.OnClickListener {

    private WheelView wvProvince;
    private WheelView wvCitys;
    private WheelView wvArea;
    private View lyChangeAddress;
    private View lyChangeAddressChild;
    private TextView btnSure;
    private TextView btnCancel;

    private Context context;
    private JSONObject mJsonObj;
    /**
     * 所有省
     */
    private String[] mProvinceDatas;
    /**
     * 所有省代码
     */
    private Map<String, String> mProvinceCodes = new HashMap<>();
    /**
     * 所有市代码
     */
    private Map<String, String> mCitiysCodes = new HashMap<>();
    /**
     * 所有县，区代码
     */
    private Map<String, String> mAreasCodes = new HashMap<>();
    /**
     * key - 省 value - 市s
     */
    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    /**
     * key - 市 values - 区s
     */
    private Map<String, String[]> mAreaDatasMap = new HashMap<String, String[]>();


    private ArrayList<String> arrProvinces = new ArrayList<String>();
    private ArrayList<String> arrCitys = new ArrayList<String>();
    private ArrayList<String> arrAreas = new ArrayList<String>();
    private AddressTextAdapter provinceAdapter;
    private AddressTextAdapter cityAdapter;
    private AddressTextAdapter areaAdapter;

    private String strProvince = "浙江";
    private String strCity = "杭州";
    private String strArea = "西湖区";
    private OnAddressCListener onAddressCListener;

    private int maxsize = 14;
    private int minsize = 12;
    private String filePath;
    private String style;
    public ChangeAddressPopwindow(final Context context,String filePath,String style,int mHeight) {
        super(context);
        this.context = context;
        this.filePath = filePath;
        this.style = style;

        View view = View.inflate(context, R.layout.popupwindow_basic_information_city, null);

        wvProvince = (WheelView) view.findViewById(R.id.wv_address_province);
        wvCitys = (WheelView) view.findViewById(R.id.wv_address_city);
        wvArea = (WheelView) view.findViewById(R.id.wv_address_area);
        lyChangeAddress = view.findViewById(R.id.ly_myinfo_changeaddress);
        lyChangeAddressChild = view.findViewById(R.id.ly_myinfo_changeaddress_child);
        btnSure = (TextView) view.findViewById(R.id.btn_myinfo_sure);
        btnCancel = (TextView) view.findViewById(R.id.btn_myinfo_cancel);


        //设置SelectPicPopupWindow的View
        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(mHeight);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        //this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        lyChangeAddressChild.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        initJsonData();
        initDatas();


        initProvinces();
        provinceAdapter = new AddressTextAdapter(context, arrProvinces, getProvinceItem(strProvince), maxsize, minsize);
        wvProvince.setVisibleItems(5);
        wvProvince.setViewAdapter(provinceAdapter);
        wvProvince.setCurrentItem(getProvinceItem(strProvince));

        initCitys(mCitisDatasMap.get(strProvince));
        cityAdapter = new AddressTextAdapter(context, arrCitys, getCityItem(strCity), maxsize, minsize);
        wvCitys.setVisibleItems(5);
        wvCitys.setViewAdapter(cityAdapter);
        wvCitys.setCurrentItem(getCityItem(strCity));

        initAreas(mAreaDatasMap.get(strCity));
        areaAdapter = new AddressTextAdapter(context, arrAreas, getAreaItem(strArea), maxsize, minsize);
        wvArea.setVisibleItems(5);
        wvArea.setViewAdapter(areaAdapter);
        wvArea.setCurrentItem(getAreaItem(strArea));

        wvProvince.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) provinceAdapter.getItemText(wheel.getCurrentItem());
                strProvince = currentText;
                setTextviewSize(currentText, provinceAdapter);

                String[] citys = mCitisDatasMap.get(currentText);
                initCitys(citys);
                cityAdapter = new AddressTextAdapter(context, arrCitys, 0, maxsize, minsize);
                wvCitys.setVisibleItems(5);
                wvCitys.setViewAdapter(cityAdapter);
                wvCitys.setCurrentItem(0);
                setTextviewSize("0", cityAdapter);

                //根据市，地区联动
                String[] areas = mAreaDatasMap.get(citys[0]);
                initAreas(areas);
                areaAdapter = new AddressTextAdapter(context, arrAreas, 0, maxsize, minsize);
                wvArea.setVisibleItems(5);
                wvArea.setViewAdapter(areaAdapter);
                wvArea.setCurrentItem(0);
                setTextviewSize("0", areaAdapter);
            }
        });

        wvProvince.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) provinceAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, provinceAdapter);
            }
        });

        wvCitys.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) cityAdapter.getItemText(wheel.getCurrentItem());
                strCity = currentText;
                setTextviewSize(currentText, cityAdapter);

                //根据市，地区联动
                String[] areas = mAreaDatasMap.get(currentText);
                initAreas(areas);
                areaAdapter = new AddressTextAdapter(context, arrAreas, 0, maxsize, minsize);
                wvArea.setVisibleItems(5);
                wvArea.setViewAdapter(areaAdapter);
                wvArea.setCurrentItem(0);
                setTextviewSize("0", areaAdapter);


            }
        });

        wvCitys.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) cityAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, cityAdapter);
            }
        });

        wvArea.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                // TODO Auto-generated method stub
                String currentText = (String) areaAdapter.getItemText(wheel.getCurrentItem());
                strArea = currentText;
                setTextviewSize(currentText, cityAdapter);
            }
        });

        wvArea.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                // TODO Auto-generated method stub
                String currentText = (String) areaAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, areaAdapter);
            }
        });
        //所有派出所开站信息测试，后台测试使用
//        uploadKaiZhan();

    }


    public class AddressTextAdapter extends AbstractWheelTextAdapter1 {
        ArrayList<String> list;

        protected AddressTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize, int minsize) {
            super(context, R.layout.item_birth_year, NO_RESOURCE, currentItem, maxsize, minsize);
            this.list = list;
            setItemTextResource(R.id.tempValue);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index) + "";
        }
    }

    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText, AddressTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(14);
            } else {
                textvew.setTextSize(12);
            }
        }
    }

    public void setAddresskListener(OnAddressCListener onAddressCListener) {
        this.onAddressCListener = onAddressCListener;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == btnSure) {
            if (onAddressCListener != null) {
                onAddressCListener.onClick(strProvince, strCity, strArea
                        , getProvinceCode(strProvince),getCityCode(strCity),getAreaCode(strArea));
            }
        } else if (v == btnCancel) {

        } else if (v == lyChangeAddressChild) {
            return;
        } else {
//			dismiss();
        }
        dismiss();
    }

    /**
     * 回调接口
     *
     * @author Administrator
     */
    public interface OnAddressCListener {
        public void onClick(String province, String city, String area
                , String provinceCode,String cityCode,String areaCode);
    }

    /**
     * 从文件中读取地址数据
     */
    private void initJsonData() {
        try {
            StringBuffer sb = new StringBuffer();
            InputStream is = context.getClass().getClassLoader().getResourceAsStream("assets/" + filePath);
            int len = -1;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) != -1) {
                sb.append(new String(buf, 0, len, style));
            }
            is.close();
            mJsonObj = new JSONObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析整个Json对象，完成后释放Json对象的内存
     */
    private void initDatas() {
        try {
            JSONArray jsonArray = mJsonObj.getJSONArray("citylist");
            mProvinceDatas = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonP = jsonArray.getJSONObject(i);// 每个省的json对象
                String province = jsonP.getString("p");// 省名字
                String mProvinceCode = "";
                if (jsonP.has("code")) {
                    mProvinceCode = jsonP.getString("code");// 省名字
                }
                mProvinceDatas[i] = province;
                mProvinceCodes.put(province, mProvinceCode);

                JSONArray jsonCs = null;
                try {
                    /**
                     * Throws JSONException if the mapping doesn't exist or is
                     * not a JSONArray.
                     */
                    jsonCs = jsonP.getJSONArray("c");
                } catch (Exception e1) {
                    continue;
                }
                String[] mCitiesDatas = new String[jsonCs.length()];
                for (int j = 0; j < jsonCs.length(); j++) {
                    JSONObject jsonCity = jsonCs.getJSONObject(j);
                    String city = jsonCity.getString("n");// 市名字
                    String mCityCode = "";
                    if (jsonCity.has("code")) {
                        mCityCode = jsonP.getString("code");// 省名字
                    }
                    mCitiesDatas[j] = city;
                    mCitiysCodes.put(city, mCityCode);
                    JSONArray jsonAreas = null;
                    try {
                        /**
                         * Throws JSONException if the mapping doesn't exist or
                         * is not a JSONArray.
                         */
                        jsonAreas = jsonCity.getJSONArray("a");
                    } catch (Exception e) {
                        continue;
                    }

                    String[] mAreasDatas = new String[jsonAreas.length()];// 当前市的所有区
                    for (int k = 0; k < jsonAreas.length(); k++) {
                        String area = jsonAreas.getJSONObject(k).getString("s");// 区域的名称
                        String mAreaCode = "";
                        if (jsonAreas.getJSONObject(k).has("code")) {
                            mAreaCode = jsonAreas.getJSONObject(k).getString("code");// 省名字
                        }
                        mAreasCodes.put(area, mAreaCode);
                        mAreasDatas[k] = area;
                    }
                    mAreaDatasMap.put(city, mAreasDatas);
                }

                mCitisDatasMap.put(province, mCitiesDatas);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mJsonObj = null;
    }

    /**
     * 获取省会编码
     *
     * @param province
     * @return
     */
    public String getProvinceCode(String province) {

        return mProvinceCodes.get(province);
    }

  /**
     * 获取市编码
     *
     * @param city
     * @return
     */
    public String getCityCode(String city) {

        return mCitiysCodes.get(city);
    }

  /**
     * 获取县，区编码
     *
     * @param area
     * @return
     */
    public String getAreaCode(String area) {

        return mAreasCodes.get(area);
    }

    /**
     * 初始化省会
     */
    public void initProvinces() {
        int length = mProvinceDatas.length;
        for (int i = 0; i < length; i++) {
            arrProvinces.add(mProvinceDatas[i]);
        }
    }

    /**
     * 根据省会，生成该省会的所有城市
     *
     * @param citys
     */
    public void initCitys(String[] citys) {
        if (citys != null) {
            arrCitys.clear();
            int length = citys.length;
            for (int i = 0; i < length; i++) {
                arrCitys.add(citys[i]);
            }
        } else {
            String[] city = mCitisDatasMap.get("广东");
            arrCitys.clear();
            int length = city.length;
            for (int i = 0; i < length; i++) {
                arrCitys.add(city[i]);
            }
        }
        if (arrCitys != null && arrCitys.size() > 0
                && !arrCitys.contains(strCity)) {
            strCity = arrCitys.get(0);
        }
    }

    /**
     * 根据城市，生成该城市的所有地区
     *
     * @param areas
     */
    public void initAreas(String[] areas) {
        if (areas != null) {
            arrAreas.clear();
            int length = areas.length;
            for (int i = 0; i < length; i++) {
                arrAreas.add(areas[i]);
            }
        } else {
            String[] area = mAreaDatasMap.get("深圳");
            arrAreas.clear();
            int length = area.length;
            for (int i = 0; i < length; i++) {
                arrAreas.add(area[i]);
            }
        }
        if (arrAreas != null && arrAreas.size() > 0
                && !arrAreas.contains(strArea)) {
            strArea = arrAreas.get(0);
        }
    }

    /**
     * 初始化地点
     *
     * @param province
     * @param city
     */
    public void setAddress(String province, String city, String area) {
        if (province != null && province.length() > 0) {
            this.strProvince = province;
        }
        if (city != null && city.length() > 0) {
            this.strCity = city;
        }

        if (area != null && area.length() > 0) {
            this.strArea = area;
        }
    }

    /**
     * 返回省会索引，没有就返回默认“广东”
     *
     * @param province
     * @return
     */
    public int getProvinceItem(String province) {
        int size = arrProvinces.size();
        int provinceIndex = 0;
        boolean noprovince = true;
        for (int i = 0; i < size; i++) {
            if (province.equals(arrProvinces.get(i))) {
                noprovince = false;
                return provinceIndex;
            } else {
                provinceIndex++;
            }
        }
        if (noprovince) {
            strProvince = arrProvinces.get(0);
            return 0;
        }
        return provinceIndex;
    }

    /**
     * 得到城市索引，没有返回默认“深圳”
     *
     * @param city
     * @return
     */
    public int getCityItem(String city) {
        int size = arrCitys.size();
        int cityIndex = 0;
        boolean nocity = true;
        for (int i = 0; i < size; i++) {
            System.out.println(arrCitys.get(i));
            if (city.equals(arrCitys.get(i))) {
                nocity = false;
                return cityIndex;
            } else {
                cityIndex++;
            }
        }
        if (nocity) {
            strCity = arrCitys.get(0);
            return 0;
        }
        return cityIndex;
    }

    /**
     * 得到地区索引，没有返回默认“福田区”
     *
     * @param area
     * @return
     */
    public int getAreaItem(String area) {
        int size = arrAreas.size();
        int areaIndex = 0;
        boolean noarea = true;
        for (int i = 0; i < size; i++) {
            System.out.println(arrAreas.get(i));
            if (area.equals(arrAreas.get(i))) {
                noarea = false;
                return areaIndex;
            } else {
                areaIndex++;
            }
        }
        if (noarea) {
            strArea = arrAreas.get(0);
            return 0;
        }
        return areaIndex;
    }
    /**
     *所有派出所开站信息测试,供后台测试使用
     * @Author lish
     * @Date 2018-09-13 9:20
     */
    private void uploadKaiZhan(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int num = 0;
                for (int i=0;i<mProvinceDatas.length;i++){
                    for (int j=0;j<mCitisDatasMap.get(mProvinceDatas[i]).length;j++){
                            for (int k=0;k<mAreaDatasMap.get(mCitisDatasMap.get(mProvinceDatas[i])[j]).length;k++){
                                LogUtils.e("area",mProvinceDatas[i]+"--"+mCitisDatasMap.get(mProvinceDatas[i])[j]+"--"
                                +mAreaDatasMap.get(mCitisDatasMap.get(mProvinceDatas[i])[j])[k]+"/n");
                                uoload(mProvinceDatas[i],mCitisDatasMap.get(mProvinceDatas[i])[j]
                                        ,mAreaDatasMap.get(mCitisDatasMap.get(mProvinceDatas[i])[j])[k]
                                        ,i,j,k,num);
                                num++;
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                    }
                }
            }

        }).start();
    }

    private void uoload(String mProvinceData, String s, String s1, int i, int j, int k,int id) {
                long time = (System.currentTimeMillis() / 1000);
                String filePath = "/mnt/sdcard/";
                String fileName = MyApplication.mDevicdID + "[211.211.211.211]_" + time;

                FTPClientData ftpClientData = new FTPClientData(context);

                FTPClient ftpClient = ftpClientData.ftpConnect();

                try {
                    ftpClient.makeDirectory(MyApplication.UpLoadFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //上传开站数据
                StringBuffer content = new StringBuffer();
                content.append("MAC"+i+""+j+""+k);
                content.append(",设备编号" + id);
                content.append(",IMEI" + id);
                content.append(",姓名" + id);
                content.append(",身份证" +"88888888" );
                content.append(",手机号" + "13333333333");
                content.append(",民警" + id);
                content.append("," + mProvinceData);
                content.append("," + s);
                content.append("," + s1);

                content.append(",120.354656");
                content.append(",30.231456");
                FileData fileData = new FileData(context, filePath, fileName + ".carsite", content);
                final boolean sub = ftpClientData.ftpUpload(ftpClient, filePath, fileName + ".carsite",false,true);

                Log.e("load","文件:"+id+"--->"+sub);
    }
}
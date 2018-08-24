package com.ascend.wangfeng.locationbyhand.view.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.anye.greendao.gen.NoteDoDao;
import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.TabMainAdapter;
import com.ascend.wangfeng.locationbyhand.api.AppClient;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.NoteDoDeal;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.NoteDo;
import com.ascend.wangfeng.locationbyhand.dialog.LoadingDialog;
import com.ascend.wangfeng.locationbyhand.event.FastScan;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.ble.MessageEvent;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils;
import com.ascend.wangfeng.locationbyhand.view.fragment.TargetFragment;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 目标Ap设置界面
 */
public class TargetActivity extends AppCompatActivity {

    public static final String TAG = "Targetactivity";
    @BindView(R.id.btn_fast_scan)
    FloatingActionButton btnFastScan;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_my_basic)
    TabLayout tabMyBasic;
    @BindView(R.id.vp_my_basic)
    ViewPager vpMyBasic;
    private Subscription mFastScanRxBus;
    private TabMainAdapter adapter;
    public static int LOCALTARGET = 0; //本地布控目标
    public static int NETTARGET = 1;//网络布控

    private MenuItem refreshItem;
    public LoadingDialog loadingDialog; //上传dialog
    private List<NoteDo> targets = new ArrayList<>();//网络布控目标
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        ButterKnife.bind(this);
        initTitle();
        initViewPager();
        //cplus
        if (MyApplication.AppVersion == Config.C_PLUS) {
            initView();
        }
        loadingDialog = new LoadingDialog(this);
    }

    private void initTitle() {
        toolbar.setTitle("布控目标");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initViewPager() {
        String[] titles = new String[]{"本地布控", "网络布控"};

        Fragment[] fragments = new Fragment[]{
                new TargetFragment().newInstance(LOCALTARGET), new TargetFragment().newInstance(NETTARGET)
        };
        adapter = new TabMainAdapter(getSupportFragmentManager(), titles, fragments, null);
        vpMyBasic.setAdapter(adapter);
        tabMyBasic.setupWithViewPager(vpMyBasic);

        tabMyBasic.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0)
                    refreshItem.setVisible(false);
                else if (tab.getPosition() == 1)
                    refreshItem.setVisible(true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //同步网络布控目标
        showCompareDialog();

        return super.onOptionsItemSelected(item);
    }

    private void showCompareDialog() {
        final AlertDialog.Builder
                normalDialog =
                new AlertDialog.Builder(TargetActivity.this);
        normalDialog.setTitle("同步布控目标");
        normalDialog.setMessage("请先确认手机与服务器网络畅通");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadingDialog.show();
                        getTargetToString();
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        //显示
        normalDialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.target, menu);
        refreshItem = menu.findItem(R.id.refresh);
        refreshItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    private void initView() {
        //是否有快速侦测
        String str = (String) SharedPreferencesUtils.getParam(TargetActivity.this, "fast_mac_on", "");
        if (!str.equals("")) {
            btnFastScan.setVisibility(View.VISIBLE);
        }
        mFastScanRxBus = RxBus.getDefault().toObservable(FastScan.class)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<FastScan>() {
                    @Override
                    public void onNext(FastScan event) {
                        Log.i(TAG, "onNext: " + event.getStatu());
                        if (event.getStatu() == FastScan.Start) {
                            //开启快速侦测
                            MyApplication.setIsDataRun(true);
                            Snackbar.make(toolbar, "快速侦测开启", Snackbar.LENGTH_SHORT).show();
                            SharedPreferencesUtils.setParam(TargetActivity.this, "fast_mac_on"
                                    , SharedPreferencesUtils.getParam(TargetActivity.this, "fast_mac", ""));
                            btnFastScan.setVisibility(View.VISIBLE);
                        } else if (event.getStatu() == FastScan.Stop) {
                            //停止快速侦测
                            btnFastScan.setVisibility(View.GONE);
                            Snackbar.make(toolbar, "快速侦测停止", Snackbar.LENGTH_SHORT).show();
                            SharedPreferencesUtils.setParam(TargetActivity.this, "fast_mac", "");
                            SharedPreferencesUtils.setParam(TargetActivity.this, "fast_mac_on", "");
                            SharedPreferencesUtils.setParam(TargetActivity.this, "fast_mac_rate", "");
                        }
                    }
                });
    }


    /**
     * 发送停止快速侦测命令
     *
     * @author lishanhui
     * created at 2018-06-28 13:08
     */
    private static void stopZhenCe() {
        MessageEvent event = new MessageEvent(MessageEvent.SEND_DATA);
        event.setData("STOPBK");
        RxBus.getDefault().post(event);
    }

    @OnClick(R.id.btn_fast_scan)
    public void onViewClicked() {
        new AlertDialog.Builder(this)
                .setTitle("快速侦测")
                .setMessage("MAC: " + SharedPreferencesUtils.getParam(TargetActivity.this, "fast_mac_on", "")
                        + "\n频率: " + SharedPreferencesUtils.getParam(TargetActivity.this, "fast_mac_rate", "10"))
                .setPositiveButton("停止", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        // 停止侦测
                        stopZhenCe();
                    }
                }).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFastScanRxBus != null) mFastScanRxBus.unsubscribe();
    }

    public interface GetTarget {
        @GET("app/monitor/getRuleCorrelationGroup.do")
        Call<ResponseBody> getTarget();
    }
    /**
     * 同步网络布控目标
     * @author lish
     * created at 2018-08-21 16:21
     */
    public void getTargetToString(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.TargetUrl)
                .build();
        GetTarget service = retrofit.create(GetTarget.class);
        Call<ResponseBody> call = service.getTarget();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    targets.clear();
                    try {
                        JSONArray array = new JSONArray(response.body().string());
                        for (int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            NoteDo note = new NoteDo();
                            note.setMac(object.getString("valueStr")
                                    .replaceAll("-",":")
                                    .toUpperCase());
                            note.setNote(object.getString("name"));
                            note.setType(1);
                            targets.add(note);
                        }
                        NoteDoDeal.saveToSqlite(targets);
                        EventBus.getDefault().post(targets);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("e",e.toString());
                    }
                    if (loadingDialog!=null)
                        loadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(TargetActivity.this,"同步网络布控目标失败",Toast.LENGTH_SHORT).show();
                if (loadingDialog!=null)
                    loadingDialog.dismiss();
            }
        });

    }
}

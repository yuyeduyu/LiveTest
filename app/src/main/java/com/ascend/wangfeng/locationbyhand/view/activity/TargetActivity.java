package com.ascend.wangfeng.locationbyhand.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.MyItemDecoration;
import com.ascend.wangfeng.locationbyhand.adapter.SwipeAdapter;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.NoteDoDeal;
import com.ascend.wangfeng.locationbyhand.bean.NoteVo;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.NoteDo;
import com.ascend.wangfeng.locationbyhand.contract.TargetContract;
import com.ascend.wangfeng.locationbyhand.dialog.TargetSetDialog;
import com.ascend.wangfeng.locationbyhand.event.FastScan;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.ble.MessageEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.WorkMode;
import com.ascend.wangfeng.locationbyhand.presenter.TargetPresenterImpl;
import com.ascend.wangfeng.locationbyhand.util.DaoUtils;
import com.ascend.wangfeng.locationbyhand.util.RegularExprssion;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 目标Ap设置界面
 */
public class TargetActivity extends AppCompatActivity implements TargetContract.View {

    public static final String TAG = "Targetactivity";
    @BindView(R.id.toolbar)
    Toolbar mAppBar;
    @BindView(R.id.recycler)
    SwipeMenuRecyclerView mRecycler;

    //TargetAdapter adapter;
    SwipeAdapter adapter;
    @BindView(R.id.btn)
    Button mBtn;
    @BindView(R.id.order_by_mac)
    TextView mOrderByMac;
    @BindView(R.id.order_by_note)
    TextView mOrderByNote;
    @BindView(R.id.btn_fast_scan)
    FloatingActionButton btnFastScan;
    private ArrayList<NoteDo> mList;
    private SwipeMenuCreator swipeMenuCreator;
    private TargetPresenterImpl mPresenter;
    private AlertDialog addDialog;
    private Comparator<NoteDo> comparator;
    private Subscription mFastScanRxBus;

    private boolean isZhence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        ButterKnife.bind(this);
        initialData();
        initialLisener();
        initialView();
        initDialog();
        //cplus
        if (MyApplication.AppVersion == Config.C_PLUS) {
            initView();
        }
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
                            Snackbar.make(mAppBar, "快速侦测开启", Snackbar.LENGTH_SHORT).show();
                            SharedPreferencesUtils.setParam(TargetActivity.this, "fast_mac_on"
                                    , SharedPreferencesUtils.getParam(TargetActivity.this, "fast_mac", ""));
                            btnFastScan.setVisibility(View.VISIBLE);
                        } else if (event.getStatu() == FastScan.Stop) {
                            //停止快速侦测
                            btnFastScan.setVisibility(View.GONE);
                            Snackbar.make(mAppBar, "快速侦测停止", Snackbar.LENGTH_SHORT).show();
                            SharedPreferencesUtils.setParam(TargetActivity.this, "fast_mac", "");
                            SharedPreferencesUtils.setParam(TargetActivity.this, "fast_mac_on", "");
                            SharedPreferencesUtils.setParam(TargetActivity.this, "fast_mac_rate", "");
                        }
                    }
                });
    }

    private void initialData() {
        mPresenter = new TargetPresenterImpl(this);
        mList = (ArrayList<NoteDo>) MyApplication.getmNoteDos();
        adapter = new SwipeAdapter(mList);
        comparator = new Comparator<NoteDo>() {
            @Override
            public int compare(NoteDo noteVo, NoteDo t1) {
                return noteVo.getMac().compareTo(t1.getMac());
            }
        };
        mOrderByMac.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.primary));
    }

    private void initialLisener() {
        swipeMenuCreator = new SwipeMenuCreator() {

            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(TargetActivity.this)
                        .setBackgroundColor(R.color.primary_dark)
                        .setText("删除")
                        .setWidth(getResources().getDimensionPixelSize(R.dimen.item_width))
                        .setHeight(getResources().getDimensionPixelSize(R.dimen.item_height));
                SwipeMenuItem changeItem = new SwipeMenuItem(TargetActivity.this)
                        .setBackgroundColor(R.color.accent)
                        .setText("修改")
                        .setWidth(getResources().getDimensionPixelSize(R.dimen.item_width))
                        .setHeight(getResources().getDimensionPixelSize(R.dimen.item_height));
                SwipeMenuItem logItem = new SwipeMenuItem(TargetActivity.this)
                        .setBackgroundColor(R.color.accent)
                        .setText("日志")
                        .setWidth(getResources().getDimensionPixelSize(R.dimen.item_width))
                        .setHeight(getResources().getDimensionPixelSize(R.dimen.item_height));
                SwipeMenuItem zhence = new SwipeMenuItem(TargetActivity.this)
                        .setBackgroundColor(R.color.purple)
                        .setText("快速侦测")
                        .setWidth(getResources().getDimensionPixelSize(R.dimen.item_width))
                        .setHeight(getResources().getDimensionPixelSize(R.dimen.item_height));
                SwipeMenuItem dingwei = new SwipeMenuItem(TargetActivity.this)
                        .setBackgroundColor(R.color.purple)
                        .setText("自动定位")
                        .setWidth(getResources().getDimensionPixelSize(R.dimen.item_width))
                        .setHeight(getResources().getDimensionPixelSize(R.dimen.item_height));
                swipeRightMenu.addMenuItem(deleteItem);
                swipeRightMenu.addMenuItem(changeItem);
                swipeRightMenu.addMenuItem(logItem);
                // cplus
                if (MyApplication.AppVersion == Config.C_PLUS) {
                    swipeRightMenu.addMenuItem(zhence);
                }
                if (MyApplication.isDev)
                    swipeRightMenu.addMenuItem(dingwei);
                if (swipeRightMenu.getMenuItem(3).getText().equals("快速侦测"))
                    isZhence = true;
            }
        };
    }

    private void initDialog() {
        final View layout = getLayoutInflater().inflate(R.layout.dialog_targetactivity
                , null);
        final EditText mac = (EditText) layout.findViewById(R.id.mac);
        final EditText note = (EditText) layout.findViewById(R.id.suspect);
        //final EditText caseOverview= (EditText) layout.findViewById(R.id.caseOverview);
        addDialog = new AlertDialog.Builder(TargetActivity.this).setTitle("添加目标")
                .setView(layout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        if (!RegularExprssion.isMac(mac.getText().toString())) {

                            Snackbar.make(mRecycler, R.string.activity_target_mac_error, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "onClick: " + mac.getText().toString());
                            String macStr = RegularExprssion.macFormat(mac.getText().toString());
                            String noteStr = note.getText().toString() + "";
                            //mPresenter.addMac(macStr, noteStr);
                            NoteDoDeal deal = new NoteDoDeal(mList);
                            NoteDo noteDo = new NoteDo();
                            noteDo.setMac(macStr);
                            noteDo.setNote(noteStr);
                            deal.add(noteDo);
                            adapter.notifyDataSetChanged();
                            anInterface.dismiss();
                            show("success");
                        }
                    }
                }).setNegativeButton("取消", null).create();
    }

    private void initialView() {
        mAppBar.setTitle("布控目标");
        setSupportActionBar(mAppBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        mAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mRecycler.setLayoutManager(new LinearLayoutManager(this));

        mRecycler.setSwipeMenuCreator(swipeMenuCreator);
        mRecycler.setSwipeMenuItemClickListener(new OnSwipeMenuItemClickListener() {
            @Override
            public void onItemClick(Closeable closeable, final int adapterPosition, int menuPosition, int direction) {
                if (adapterPosition == mList.size()) {

                } else {
                    switch (menuPosition) {
                        case 0:
                            final String mac = mList.get(adapterPosition).getMac();
                            new AlertDialog.Builder(TargetActivity.this).setTitle("提醒")
                                    .setMessage("确认删除吗")
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface anInterface, int i) {
                                            //mPresenter.delMac(mac);
                                            NoteDoDeal deal = new NoteDoDeal(mList);
                                            deal.delete(adapterPosition);
                                            adapter.notifyDataSetChanged();
                                            show("success");
                                            DaoUtils.delectTaglog(mac);
                                            anInterface.dismiss();
                                        }
                                    })
                                    .setNegativeButton("取消", null).show();
                            break;
                        case 1:
                            final String mac1 = mList.get(adapterPosition).getMac();
                            final String note = mList.get(adapterPosition).getNote();
                            final View layout = getLayoutInflater().inflate(R.layout.dialog_targetactivity
                                    , null);
                            final EditText macE = (EditText) layout.findViewById(R.id.mac);
                            final EditText noteE = (EditText) layout.findViewById(R.id.suspect);
                            macE.setText(mac1);
                            macE.setEnabled(false);
                            noteE.setText(note);
                            new AlertDialog.Builder(TargetActivity.this).setTitle("修改备注")
                                    .setView(layout)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface anInterface, int i) {
                                            //   mPresenter.update(mac1, macE.getText().toString() + "",noteE.getText().toString() + "");
                                            NoteDoDeal deal = new NoteDoDeal(mList);
                                            NoteDo noteDo = mList.get(adapterPosition);
                                            deal.upDate(noteDo.getMac(), noteE.getText().toString());
                                            adapter.notifyDataSetChanged();
                                            show("success");
                                            anInterface.dismiss();
                                        }
                                    }).setNegativeButton("取消", null).create().show();
                            break;
                        case 2:
                            Intent intent = new Intent(TargetActivity.this, LogActivity.class);
                            Log.i(TAG, "onItemClick: " + mList.get(adapterPosition).getMac());
                            intent.putExtra("mac", mList.get(adapterPosition).getMac());
                            startActivity(intent);
                            break;
                        case 3:
                            if (isZhence)
                                //快速侦测
                                TargetSetDialog.showFastScanDialog(TargetActivity.this, mList.get(adapterPosition).getMac());
                            else
                                //定位
                                startActivity(new Intent(TargetActivity.this, SelectPositionActivity.class)
                                .putExtra("mac",mList.get(adapterPosition).getMac()));
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        mRecycler.setAdapter(adapter);
        mRecycler.addItemDecoration(new MyItemDecoration(this,
                LinearLayoutManager.HORIZONTAL));
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDialog.show();
            }
        });

    }

    @Override
    public void update(List<NoteVo> noteVos) {
        mList.clear();
        // mList.addAll(noteVos);
        Collections.sort(mList, comparator);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void show(String message) {
        Snackbar.make(mAppBar, message, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick({R.id.order_by_mac, R.id.order_by_note})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_by_mac:
                comparator = new Comparator<NoteDo>() {
                    @Override
                    public int compare(NoteDo noteVo, NoteDo t1) {
                        return noteVo.getMac().compareTo(t1.getMac());
                    }
                };
                Collections.sort(mList, comparator);
                adapter.notifyDataSetChanged();
                mOrderByMac.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.primary));
                mOrderByNote.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.secondary_text));

                break;
            case R.id.order_by_note:
                comparator = new Comparator<NoteDo>() {
                    @Override
                    public int compare(NoteDo noteVo, NoteDo t1) {
                        return noteVo.getNote().compareTo(t1.getNote());
                    }
                };
                Collections.sort(mList, comparator);
                adapter.notifyDataSetChanged();
                mOrderByMac.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.secondary_text));
                mOrderByNote.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.primary));
                break;
        }
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
}

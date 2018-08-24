package com.ascend.wangfeng.locationbyhand.view.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ascend.wangfeng.locationbyhand.AppVersionConfig;
import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.Ghz;
import com.ascend.wangfeng.locationbyhand.contract.SetContract;
import com.ascend.wangfeng.locationbyhand.event.RingEvent;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.ble.GhzEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.MainServiceEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.MessageEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.WorkMode;
import com.ascend.wangfeng.locationbyhand.presenter.SetPresenterImpl;
import com.ascend.wangfeng.locationbyhand.util.DataFormat;
import com.ascend.wangfeng.locationbyhand.util.RegularExprssion;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils;
import com.ascend.wangfeng.locationbyhand.view.activity.AboutActivity;
import com.ascend.wangfeng.locationbyhand.view.activity.AnalyseActivity;
import com.ascend.wangfeng.locationbyhand.view.activity.KaiZhanActivity;
import com.ascend.wangfeng.locationbyhand.view.activity.NewLogAllActivity;
import com.ascend.wangfeng.locationbyhand.view.activity.SetftpActivity;
import com.ascend.wangfeng.locationbyhand.view.activity.StatisticsActivity;
import com.ascend.wangfeng.locationbyhand.view.activity.TargetActivity;
import com.ascend.wangfeng.locationbyhand.view.activity.TaskActivity;
import com.ascend.wxldcmenu.MenuMainActivity;
import com.kyleduo.switchbutton.SwitchButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils.getParam;
import static com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils.setParam;

/**
 * Created by fengye on 2017/2/7.
 * email 1040441325@qq.com
 */
public class SetFragment extends BaseFragment implements SetContract.View {

    private static final int UPLOADTIME = 0;
    private static final int UPLOADPATH = 1;
    @BindView(R.id.targetAp)
    RelativeLayout mTargetAp;
    @BindView(R.id.set_switch_isRing)
    SwitchButton mSetSwitchIsRing;
    @BindView(R.id.isSearch)
    RelativeLayout mIsSearch;
    @BindView(R.id.channel_text)
    TextView mChannelText;
    @BindView(R.id.scanChannel)
    RelativeLayout mScanChannel;
    @BindView(R.id.workStyle_text)
    TextView mWorkStyleText;
    @BindView(R.id.workStyle)
    RelativeLayout mWorkStyle;
    @BindView(R.id.set_track_channel_text)
    TextView mSetTrackChannelText;
    @BindView(R.id.set_track_channel)
    RelativeLayout mSetTrackChannel;
    @BindView(R.id.url)
    TextView mUrl;
    @BindView(R.id.urlRe)
    RelativeLayout mUrlRe;
    @BindView(R.id.target_count)
    TextView mTargetCount;
    @BindView(R.id.equipment_text)
    TextView mEquipmentText;
    @BindView(R.id.equipment)
    RelativeLayout mEquipment;
    @BindView(R.id.ghz_text)
    TextView mGhzText;
    @BindView(R.id.ghz)
    RelativeLayout mGhz;
    @BindView(R.id.task)
    RelativeLayout mTask;
    @BindView(R.id.analyse)
    RelativeLayout analyse;
    @BindView(R.id.workMode)
    RelativeLayout Rl_workMode;
    @BindView(R.id.workMode_text)
    TextView workModeText;
    @BindView(R.id.collect_radius_text)
    TextView mCollectRadiusText;
    @BindView(R.id.collect_radius)
    RelativeLayout mCollectRadius;
    @BindView(R.id.about)
    RelativeLayout mAbout;
    @BindView(R.id.upTime)
    RelativeLayout mUpTime;
    @BindView(R.id.upTime_text)
    TextView mTime;
    @BindView(R.id.upPath)
    RelativeLayout mUpPath;
    @BindView(R.id.upPath_text)
    TextView mPath;
    @BindView(R.id.rl_log_all)
    RelativeLayout rlLogAll;
    @BindView(R.id.tongji)
    RelativeLayout tongji;
    @BindView(R.id.view)
    View view;
    @BindView(R.id.kaizhan)
    RelativeLayout kaizhan;


    private String TAG = getClass().getCanonicalName();


    String[] mChannelitems = new String[]{"自动", "1", "6", "11"};
    String[] mWorkStyles = new String[]{"移动模式", "静止模式"};
    String[] mTrackChannels = new String[]{"非自动锁定模式", "自动锁定模式"};
    String[] mWorkMode = new String[]{"采集模式", "升级模式"};
    String[] mCollectRadiuses = new String[]{"2米内", "5米内", "无限制"};

    private int workmode;//采集 升级 模式编号
    private int channel;//channel编号
    private int workstyle;//工作模式编号
    private int trackChannel;//信道锁定模式编号
    private String url;
    private AlertDialog channelDialog;//扫描信道弹框
    private AlertDialog workDialog;//工作模式
    private AlertDialog urlDialog;//网址
    private Subscription rxSub;
    private AlertDialog trackChannelDialog;
    private SetPresenterImpl mPresenter;
    private int targetsCount;
    private String mEquipmentCount;
    private Subscription mGhzRxBus;
    private Subscription mWorkModeRxBus;
    private long lastClicktime;//save ghz change time
    private long CLICK_RATE = 3 * 60 * 1000;
    // is changing GHZ, if true, it can show change success
    private Boolean isChangeGhz = false;
    private AlertDialog collectRadiusDialog;
    private int mCollectRadiusId;


    private Handler guiHandler;

    //判断是否连接成功
    private boolean isConnect = false;
    private Boolean isChangeWorkMode = false;
    private Handler mGhzChangeHandler = new Handler();
    private Runnable mGhzChangeRunable = new Runnable() {
        @Override
        public void run() {
            MessageEvent event = new MessageEvent(MessageEvent.SEND_DATA);
            event.setData("GETMOD");
            RxBus.getDefault().post(event);
            Log.i(TAG, "run: " + "GETMOD");
            mGhzChangeHandler.postDelayed(mGhzChangeRunable, 10 * 1000);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
        initGhz();

        guiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case UPLOADTIME:
                        String time = MyApplication.GetUpLoadTime() / (60 * 1000) + "分钟";
                        mTime.setText(time);
                        break;
                    case UPLOADPATH:
                        if (MyApplication.UpLoadFilePath.equals(""))
                            mPath.setText(MyApplication.UpLoadFtpUrl + ":" + MyApplication.UpLoadFtpPort);
                        else
                            mPath.setText(MyApplication.UpLoadFtpUrl + ":" + MyApplication.UpLoadFtpPort
                                    + "/" + MyApplication.UpLoadFilePath);
                        break;

                }
            }
        };
    }

    private void showGhzChangeDialog() {
        String[] items = new String[]{"2.4GHz", "5.8GHz"};
        int position = 0;
        if (!mGhzText.getText().toString().equals("2.4GHz")) {
            position = 1;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("频段").setSingleChoiceItems(items, position, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface anInterface, int i) {
                if (i == 0) {
                    if (MyApplication.mGhz == Ghz.G58) {
                        changeGhz(Ghz.G24);
                        Log.i(TAG, "onClick: " + i + "2.4");
                    }
                } else {
                    if (MyApplication.mGhz == Ghz.G24) {
                        changeGhz(Ghz.G58);
                        Log.i(TAG, "onClick: " + "5.8");
                    }

                }
                anInterface.cancel();
            }
        });

        builder.show();
    }

    /**
     * 切换频段
     */
    private void changeGhz(Ghz ghz) {
        int ghzNumber = 1;
        if (ghz == Ghz.G24) {
            ghzNumber = 1;
        } else {
            ghzNumber = 2;
        }
        // ischanging  cant't rechange
        if (!isChangeGhz) {
            lastClicktime = System.currentTimeMillis();
            MessageEvent event = new MessageEvent(MessageEvent.SEND_DATA);
            event.setData("SETMOD" + ghzNumber);
            RxBus.getDefault().post(event);
            RxBus.getDefault().post(new MainServiceEvent(MainServiceEvent.CLEAE_DATA));
            if (ghzNumber == 2) {
                Snackbar.make(mEquipment, R.string.hint_change_58, Snackbar.LENGTH_INDEFINITE).show();
            } else {
                Snackbar.make(mEquipment, R.string.hint_change_24, Snackbar.LENGTH_INDEFINITE).show();
            }
            mGhzChangeHandler.postDelayed(mGhzChangeRunable, 10 * 1000);
            MyApplication.setIsDataRun(false);
            isChangeGhz = true;
        } else {
            Snackbar.make(mEquipment, R.string.hint_change_ghz, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void initGhz() {
        setGhzText();
        if (MyApplication.AppVersion != Config.C_MINI) {
            mGhz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showGhzChangeDialog();
                }
            });
        }

        mGhzRxBus = RxBus.getDefault().toObservable(GhzEvent.class)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<GhzEvent>() {
                    @Override
                    public void onNext(GhzEvent event) {
                        Log.i(TAG, "onNext: " + event.getGhz());
                        if (mGhzChangeHandler != null) {
                            mGhzChangeHandler.removeCallbacks(mGhzChangeRunable);
                            MyApplication.setIsDataRun(true);
                        }
                        if (isChangeGhz) {
                            Snackbar.make(mEquipment, "切换成功", Snackbar.LENGTH_SHORT).show();
                            setGhzText();
                            isChangeGhz = false;
                        }
                    }
                });
        //工作模式切换 RxBus
        mWorkModeRxBus = RxBus.getDefault().toObservable(WorkMode.class)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<WorkMode>() {
                    @Override
                    public void onNext(WorkMode event) {
                        Log.i(TAG, "onNext: " + event.getMode());
                        if (mGhzChangeHandler != null) {
                            mGhzChangeHandler.removeCallbacks(mGhzChangeRunable);
                            MyApplication.setIsDataRun(true);
                        }
                        if (isChangeWorkMode) {
                            Snackbar.make(mEquipment, "切换成功", Snackbar.LENGTH_SHORT).show();
                            workmode = event.getMode();
                            workModeText.setText(mWorkMode[event.getMode()]);
                            isChangeWorkMode = false;
                            SharedPreferencesUtils.setParam(getActivity(), "workmode", workmode);

                        }
                    }
                });
    }

    private void setGhzText() {
        if (MyApplication.mGhz == Ghz.G24) {
            mGhzText.setText("2.4GHz");
        } else {
            mGhzText.setText("5.8GHz");
        }
    }

    private void initView() {
        mEquipmentText.setText(MyApplication.mDevicdID == null ? "" : MyApplication.mDevicdID + "");
        mTargetCount.setText(MyApplication.getmNoteDos().size() + "");
        mChannelText.setText(mChannelitems[channel]);
        //隐藏工作模式设置
        mWorkStyleText.setText(mWorkStyles[workstyle]);
        mWorkStyle.setVisibility(View.GONE);
        mUrl.setText(url + "");
        mSetTrackChannelText.setText(mTrackChannels[trackChannel]);
        mSetSwitchIsRing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean b) {
                //mPresenter.setRing(b);
                SharedPreferencesUtils.setParam(MyApplication.mContext, "ring", b);
            }
        });
        //隐藏url设置
        mUrlRe.setVisibility(View.GONE);
        mScanChannel.setVisibility(View.GONE);
        mSetTrackChannel.setVisibility(View.GONE);
        mTask.setVisibility(View.GONE);
        //mGhz.setVisibility(View.GONE);
        if (MyApplication.AppVersion == Config.C_PLUS) {
            //采集模式
            Rl_workMode.setVisibility(View.VISIBLE);
            workmode = (int) SharedPreferencesUtils.getParam(getActivity(), "workmode", 0);
            workModeText.setText(mWorkMode[workmode]);
        }
        if (MyApplication.AppVersion == Config.C_MINI) {
            mCollectRadius.setVisibility(View.VISIBLE);
            mUpTime.setVisibility(View.VISIBLE);
            mUpPath.setVisibility(View.VISIBLE);
//            rlLogAll.setVisibility(View.VISIBLE);
        }
        if (MyApplication.AppVersion != Config.C_MINI)
            mGhz.setVisibility(View.VISIBLE);

        if (AppVersionConfig.appTitle.equals("便携式移动采集")) {
            //便携式车载采集app 设置界面显示 开站信息
            kaizhan.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 为了得到传回的数据，必须在前面的Activity中（指MainActivity类）重写onActivityResult方法
     * <p>
     * requestCode 请求码，即调用startActivityForResult()传递过去的值
     * resultCode 结果码，结果码用于标识返回数据来自哪个新Activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 1:
                MyApplication.ftpData();
                guiHandler.sendEmptyMessage(UPLOADPATH);
                break;
        }
    }

    private void initData() {

        mSetSwitchIsRing.setChecked((Boolean) getParam(getContext(), "ring", false));
        mCollectRadiusId = (int) getParam(getContext(), "collectRadius", 2);
        mCollectRadiusText.setText(mCollectRadiuses[mCollectRadiusId]);
        mTime.setText((MyApplication.GetUpLoadTime() / (60 * 1000) + "分钟"));
        if (MyApplication.UpLoadFilePath.equals(""))
            mPath.setText(MyApplication.UpLoadFtpUrl + ":" + MyApplication.UpLoadFtpPort);
        else
            mPath.setText(MyApplication.UpLoadFtpUrl + ":" + MyApplication.UpLoadFtpPort
                    + "/" + MyApplication.UpLoadFilePath);
        initDialog();
    }

    private void initGPRS() {//网络相关初始化
        mPresenter = new SetPresenterImpl(this);

        rxSub = RxBus.getDefault().toObservable(RingEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<RingEvent>() {
                    @Override
                    public void onNext(RingEvent event) {
                        mSetSwitchIsRing.setChecked(event.isB());
                    }
                });

        if (Config.getAlarmMacListDo() != null) {
            mEquipmentCount = Config.getAlarmMacListDo().getWxld();
            targetsCount = Config.getAlarmMacListDo().getTotal();
            channel = DataFormat
                    .channel_stringToInt(Config.getAlarmMacListDo().getChannel());
            workstyle = Config.getAlarmMacListDo().getCount();
            trackChannel = Config.getAlarmMacListDo().getTrack();
        }
        url = (String) SharedPreferencesUtils.getParam(getContext(),
                "url_equipment", "192.168.111.11");
    }

    private void initDialog() {
        channelDialog = new AlertDialog.Builder(getContext()).setTitle("扫描信道").setSingleChoiceItems(
                mChannelitems, channel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        mChannelText.setText(mChannelitems[i]);
                        mPresenter.setChannel(i);
                        anInterface.dismiss();
                    }
                }
        ).create();
        collectRadiusDialog = new AlertDialog.Builder(getContext()).setTitle("检测范围").setSingleChoiceItems(
                mCollectRadiuses, mCollectRadiusId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        mCollectRadiusText.setText(mCollectRadiuses[i]);
                        setParam(getContext(), "collectRadius", i);
                        anInterface.dismiss();
                    }
                }
        ).create();

        workDialog = new AlertDialog.Builder(getContext()).setTitle("工作模式").setSingleChoiceItems(
                mWorkStyles, workstyle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        mWorkStyleText.setText(mWorkStyles[i]);
                        Snackbar.make(mChannelText, R.string.fun_unfinish, Snackbar.LENGTH_SHORT).show();
                        anInterface.dismiss();
                    }
                }
        ).create();

        final View layout = getActivity().getLayoutInflater().inflate(R.layout.dialog_url, null);
        final EditText urlEdit = (EditText) layout.findViewById(R.id.url);

        //final EditText caseOverview= (EditText) layout.findViewById(R.id.caseOverview);
        urlDialog = new AlertDialog.Builder(getActivity()).setTitle("设备地址")
                .setView(layout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        String text = urlEdit.getText().toString() + "";
                        if (text.length() > 0 && RegularExprssion.isIp(text)) {
                            mPresenter.setUrl(text);
                            //重启软件
                            Intent intent = getActivity().getBaseContext().getPackageManager()
                                    .getLaunchIntentForPackage(getActivity().getBaseContext()
                                            .getPackageName());
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Snackbar.make(mUrl, "非法参数，请重新输入", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("取消", null).create();

        //锁定信道dialog
        trackChannelDialog = new AlertDialog.Builder(getContext()).setTitle("锁定信道").setSingleChoiceItems(
                mTrackChannels, trackChannel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        mSetTrackChannelText.setText(mTrackChannels[i]);
                        mPresenter.setChannelLock(i);
                        anInterface.dismiss();
                        //修改锁定信道
                       /* Snackbar.make(mChannelText, R.string.fun_unfinish, Snackbar.LENGTH_SHORT).show();
                        anInterface.dismiss();*/
                    }
                }
        ).create();
    }

    @OnClick({R.id.targetAp, R.id.scanChannel, R.id.workStyle, R.id.url, R.id.set_track_channel,
            R.id.about, R.id.task, R.id.analyse, R.id.workMode, R.id.upTime, R.id.upPath,
            R.id.collect_radius, R.id.rl_log_all, R.id.tongji})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.targetAp:                 //布控目标
                startActivity(new Intent(getActivity(), TargetActivity.class));
                break;
            case R.id.collect_radius:
                collectRadiusDialog.show();
                break;
            case R.id.rl_log_all:
                startActivity(new Intent(getActivity(), NewLogAllActivity.class));
                break;
            case R.id.scanChannel:
                //startActivity(new Intent(getActivity(), ChannelActivity.class));
                /**
                 * 设置扫描信道的dialog
                 */
                channelDialog.show();
                break;
            case R.id.workStyle:
                workDialog.show();
                break;
            case R.id.url:
                //配置url
                urlDialog.show();
                break;
            case R.id.set_track_channel:
                trackChannelDialog.show();
                break;
            case R.id.about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.task:
                //进入任务界面
                startActivity(new Intent(getActivity(), TaskActivity.class));
                break;
            case R.id.analyse:
                //进入数据分析界面
                startActivity(new Intent(getActivity(), AnalyseActivity.class));
                break;
            case R.id.tongji:
                //进入统计界面
                startActivity(new Intent(getActivity(), StatisticsActivity.class));
                break;
            case R.id.workMode:
                //采集模式 升级模式切换
                showWorkModeDialog();
                break;
            case R.id.upTime:
                //设置上传的时间
                upLoadTimeDialog();
                break;
            case R.id.upPath:
                //设置上传的文件路径
                //得到新打开Activity关闭后返回的数据
                //第二个参数为请求码，可以根据业务需求自己编号
                startActivityForResult(new Intent(getActivity(), SetftpActivity.class), 1);
                break;
            default:
                break;
        }
    }

    //弹窗显示时间间隔显示
    private void upLoadTimeDialog() {
        String[] items = new String[]{"1分钟", "2分钟", "5分钟", "10分钟"};
        int upLoadTime = MyApplication.GetUpLoadTime();                         //得到当前的时间
//        int upLoadTime = MyApplication.upLoadTime;
        int position = 0;
        if (upLoadTime / (60 * 1000) == 1) position = 0;
        else if (upLoadTime / (60 * 1000) == 2) position = 1;
        else if (upLoadTime / (60 * 1000) == 5) position = 2;
        else if (upLoadTime / (60 * 1000) == 10) position = 3;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("上传时间间隔").setSingleChoiceItems(items, position, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface anInterface, int i) {
                //保存上传时间长度
                String[] times = new String[]{60 * 1000 + "", 2 * 60 * 1000 + "", 5 * 60 * 1000 + "", 10 * 60 * 1000 + ""};
                SharedPreferences.Editor editor = getContext().getSharedPreferences("uploadTime", Context.MODE_PRIVATE).edit();
                editor.putString("time", times[i]);
                editor.commit();
                anInterface.cancel();
                guiHandler.sendEmptyMessage(UPLOADTIME);
            }
        });
        builder.show();
    }

    /**
     * 显示切换采集模式dialog
     *
     * @author lishanhui
     * created at 2018-06-27 11:24
     */
    private void showWorkModeDialog() {
        AlertDialog workDialog = new AlertDialog.Builder(getContext()).setTitle("工作模式").setSingleChoiceItems(
                mWorkMode, workmode, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        //默认模式为采集模式，当模式切换时才发送命令，发送命令前判断模式是否切换
                        changeWorkStyle(i);
//                           Snackbar.make(mChannelText, "发送命令:"+mWorkStyles[i], Snackbar.LENGTH_SHORT).show();
                        anInterface.dismiss();
                    }
                }
        ).create();
        workDialog.show();
    }

    /**
     * 切换采集模式
     *
     * @param i 0,采集模式  1，升级模式
     */
    private void changeWorkStyle(int i) {
        String workCommand;
        if (i == 0) {
            workCommand = "SETMOMODE";
        } else {
            workCommand = "SETAPMODE";
        }
        if (!isChangeWorkMode) {
            isChangeWorkMode = true;
            MessageEvent event = new MessageEvent(MessageEvent.SEND_DATA);
            event.setData(workCommand);
            RxBus.getDefault().post(event);
            RxBus.getDefault().post(new MainServiceEvent(MainServiceEvent.CLEAE_DATA));
            if (i == 0) {
                Snackbar.make(mEquipment, R.string.hint_change_workstyle_0, Snackbar.LENGTH_INDEFINITE).show();
            } else {
                Snackbar.make(mEquipment, R.string.hint_change_workstyle_1, Snackbar.LENGTH_INDEFINITE).show();
            }
            MyApplication.setIsDataRun(false);
        } else {
            Snackbar.make(mEquipment, R.string.hint_change_ghz, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGhzRxBus != null) mGhzRxBus.unsubscribe();
    }

    @Override
    public void showMessage(String message) {
        Snackbar.make(mEquipment, message, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getContext().getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "未知";
        }
    }


    @OnClick(R.id.kaizhan)
    public void onViewClicked() {
        startActivity(new Intent(getActivity(), KaiZhanActivity.class));
    }
}

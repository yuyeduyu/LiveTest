package com.ascend.wangfeng.locationbyhand.view.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.anye.greendao.gen.NoteDoDao;
import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.MyItemDecoration;
import com.ascend.wangfeng.locationbyhand.adapter.SwipeAdapter;
import com.ascend.wangfeng.locationbyhand.bean.NoteDoDeal;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.NoteDo;
import com.ascend.wangfeng.locationbyhand.dialog.TargetSetDialog;
import com.ascend.wangfeng.locationbyhand.util.DaoUtils;
import com.ascend.wangfeng.locationbyhand.util.RegularExprssion;
import com.ascend.wangfeng.locationbyhand.view.activity.LogActivity;
import com.ascend.wangfeng.locationbyhand.view.activity.SelectPositionActivity;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 目标Ap设置界面
 */
public class TargetFragment extends Fragment {

    @BindView(R.id.recycler)
    SwipeMenuRecyclerView mRecycler;
    SwipeAdapter adapter;
    @BindView(R.id.btn)
    Button mBtn;
    @BindView(R.id.order_by_mac)
    TextView mOrderByMac;
    @BindView(R.id.order_by_note)
    TextView mOrderByNote;
    private List<NoteDo> mList;
    private List<NoteDo> localList;
    private SwipeMenuCreator swipeMenuCreator;
    private AlertDialog addDialog;
    private Comparator<NoteDo> comparator;

    private int type = 0;//0:本地布控，1:网络布控
    private NoteDoDao noteDoDao;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_target, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        if (getArguments() != null) {
            //取出保存的值
            type = getArguments().getInt("type");
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (type == 1) mBtn.setVisibility(View.GONE);//网络布控，则隐藏添加布控按钮
        noteDoDao = MyApplication.instances.getDaoSession().getNoteDoDao();
        initialData();
        initialLisener();
        initialView();
        initDialog();
    }

    private void initialData() {
        mList = MyApplication.getmNoteDos();
        localList = noteDoDao.queryBuilder().where(NoteDoDao.Properties.Type.eq(type)).list();
        comparator = new Comparator<NoteDo>() {
            @Override
            public int compare(NoteDo noteVo, NoteDo t1) {
                return noteVo.getMac().compareTo(t1.getMac());
            }
        };
        Collections.sort(localList, comparator);
        adapter = new SwipeAdapter(localList);
        mOrderByMac.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary));
    }

    private void initialLisener() {
        swipeMenuCreator = new SwipeMenuCreator() {

            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity())
                        .setBackgroundColor(R.color.primary_dark)
                        .setText("删除")
                        .setWidth(getResources().getDimensionPixelSize(R.dimen.item_width))
                        .setHeight(getResources().getDimensionPixelSize(R.dimen.item_height));
                SwipeMenuItem changeItem = new SwipeMenuItem(getActivity())
                        .setBackgroundColor(R.color.accent)
                        .setText("修改")
                        .setWidth(getResources().getDimensionPixelSize(R.dimen.item_width))
                        .setHeight(getResources().getDimensionPixelSize(R.dimen.item_height));
                SwipeMenuItem logItem = new SwipeMenuItem(getActivity())
                        .setBackgroundColor(R.color.accent)
                        .setText("日志")
                        .setWidth(getResources().getDimensionPixelSize(R.dimen.item_width))
                        .setHeight(getResources().getDimensionPixelSize(R.dimen.item_height));
                SwipeMenuItem zhence = new SwipeMenuItem(getActivity())
                        .setBackgroundColor(R.color.purple)
                        .setText("快速侦测")
                        .setWidth(getResources().getDimensionPixelSize(R.dimen.item_width))
                        .setHeight(getResources().getDimensionPixelSize(R.dimen.item_height));
                SwipeMenuItem dingwei = new SwipeMenuItem(getActivity())
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
            }
        };
    }

    private void initDialog() {
        final View layout = getActivity().getLayoutInflater().inflate(R.layout.dialog_targetactivity
                , null);
        final EditText mac = (EditText) layout.findViewById(R.id.mac);
        final EditText note = (EditText) layout.findViewById(R.id.suspect);
        //final EditText caseOverview= (EditText) layout.findViewById(R.id.caseOverview);
        addDialog = new AlertDialog.Builder(getActivity()).setTitle("添加目标")
                .setView(layout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        if (!RegularExprssion.isMac(mac.getText().toString())) {

                            Snackbar.make(mRecycler, R.string.activity_target_mac_error, Snackbar.LENGTH_SHORT).show();
                        } else {
                            String macStr = RegularExprssion.macFormat(mac.getText().toString());
                            String noteStr = note.getText().toString() + "";
                            //mPresenter.addMac(macStr, noteStr);
                            NoteDoDeal deal = new NoteDoDeal(mList);
                            NoteDo noteDo = new NoteDo();
                            noteDo.setMac(macStr);
                            noteDo.setNote(noteStr);
                            noteDo.setType(type);
                            deal.add(noteDo);
                            if (!localList.contains(noteDo))
                                localList.add(noteDo);
                            adapter.notifyDataSetChanged();
                            anInterface.dismiss();
                            show("success");
                        }
                    }
                }).setNegativeButton("取消", null).create();
    }

    private void initialView() {

        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRecycler.setSwipeMenuCreator(swipeMenuCreator);
        mRecycler.setSwipeMenuItemClickListener(new OnSwipeMenuItemClickListener() {
            @Override
            public void onItemClick(Closeable closeable, final int adapterPosition, int menuPosition, int direction) {
                if (adapterPosition == localList.size()) {

                } else {
                    switch (menuPosition) {
                        case 0:
                            final String mac = localList.get(adapterPosition).getMac();
                            new AlertDialog.Builder(getActivity()).setTitle("提醒")
                                    .setMessage("确认删除吗")
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface anInterface, int i) {
                                            //mPresenter.delMac(mac);
                                            NoteDoDeal deal = new NoteDoDeal(mList);
                                            deal.delete(mac);
                                            localList.remove(adapterPosition);
                                            adapter.notifyDataSetChanged();
                                            show("success");
                                            //删除布控目标日志
                                            DaoUtils.delectTaglog(mac);
                                            anInterface.dismiss();
                                        }
                                    })
                                    .setNegativeButton("取消", null).show();
                            break;
                        case 1:
                            final String mac1 = localList.get(adapterPosition).getMac();
                            final String note = localList.get(adapterPosition).getNote();
                            final View layout = getActivity().getLayoutInflater().inflate(R.layout.dialog_targetactivity
                                    , null);
                            final EditText macE = (EditText) layout.findViewById(R.id.mac);
                            final EditText noteE = (EditText) layout.findViewById(R.id.suspect);
                            macE.setText(mac1);
                            macE.setEnabled(false);
                            noteE.setText(note);
                            new AlertDialog.Builder(getActivity()).setTitle("修改备注")
                                    .setView(layout)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface anInterface, int i) {
                                            //   mPresenter.update(mac1, macE.getText().toString() + "",noteE.getText().toString() + "");
                                            NoteDoDeal deal = new NoteDoDeal(mList);
                                            NoteDo noteDo = localList.get(adapterPosition);
                                            deal.upDate(noteDo.getMac(), noteE.getText().toString());
                                            localList.get(adapterPosition).setNote(noteE.getText().toString());
                                            adapter.notifyDataSetChanged();
                                            show("success");
                                            anInterface.dismiss();
                                        }
                                    }).setNegativeButton("取消", null).create().show();
                            break;
                        case 2:
                            Intent intent = new Intent(getActivity(), LogActivity.class);
                            intent.putExtra("mac", localList.get(adapterPosition).getMac());
                            startActivity(intent);
                            break;
                        case 3:
                                //快速侦测
                                TargetSetDialog.showFastScanDialog(getActivity(), localList.get(adapterPosition).getMac());
//                            else
//                                //定位
//                                startActivity(new Intent(getActivity(), SelectPositionActivity.class)
//                                        .putExtra("mac", localList.get(adapterPosition).getMac()));
                            break;
                        default:
                            break;
                    }
                }
            }
        });

        mRecycler.setAdapter(adapter);
        mRecycler.addItemDecoration(new MyItemDecoration(getActivity(),
                LinearLayoutManager.HORIZONTAL));
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDialog.show();
            }
        });

    }

    public void show(String message) {
        Snackbar.make(mBtn, message, Snackbar.LENGTH_SHORT).show();
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
                Collections.sort(localList, comparator);
                adapter.notifyDataSetChanged();
                mOrderByMac.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary));
                mOrderByNote.setTextColor(ContextCompat.getColor(getActivity(), R.color.secondary_text));

                break;
            case R.id.order_by_note:
                comparator = new Comparator<NoteDo>() {
                    @Override
                    public int compare(NoteDo noteVo, NoteDo t1) {
                        return noteVo.getNote().compareTo(t1.getNote());
                    }
                };
                Collections.sort(localList, comparator);
                adapter.notifyDataSetChanged();
                mOrderByMac.setTextColor(ContextCompat.getColor(getActivity(), R.color.secondary_text));
                mOrderByNote.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary));
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static TargetFragment newInstance(int type) {
        TargetFragment frag = new TargetFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        frag.setArguments(args);
        return frag;
    }
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(List<NoteDo> datas) {
        if (localList!=null){
            localList.clear();
            localList.addAll(datas);
            Collections.sort(localList, comparator);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
}

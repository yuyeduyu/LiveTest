package com.ascend.wangfeng.locationbyhand.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.MyItemDecoration;
import com.ascend.wangfeng.locationbyhand.adapter.SwipeAdapter;
import com.ascend.wangfeng.locationbyhand.bean.NoteDoDeal;
import com.ascend.wangfeng.locationbyhand.bean.NoteVo;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.NoteDo;
import com.ascend.wangfeng.locationbyhand.contract.TargetContract;
import com.ascend.wangfeng.locationbyhand.presenter.TargetPresenterImpl;
import com.ascend.wangfeng.locationbyhand.util.RegularExprssion;
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
    private ArrayList<NoteDo> mList;
    private SwipeMenuCreator swipeMenuCreator;
    private TargetPresenterImpl mPresenter;
    private AlertDialog addDialog;
    private Comparator<NoteDo> comparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        ButterKnife.bind(this);
        initialData();
        initialLisener();
        initialView();
        initDialog();
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
                swipeRightMenu.addMenuItem(deleteItem);
                swipeRightMenu.addMenuItem(changeItem);
                swipeRightMenu.addMenuItem(logItem);
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
                            NoteDoDeal deal =new NoteDoDeal(mList);
                            NoteDo noteDo=new NoteDo();
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

    private void initialData() {
        mPresenter = new TargetPresenterImpl(this);
        mList =(ArrayList<NoteDo>) MyApplication.getmNoteDos();
        adapter = new SwipeAdapter(mList);
        comparator = new Comparator<NoteDo>() {
            @Override
            public int compare(NoteDo noteVo, NoteDo t1) {
                return noteVo.getMac().compareTo(t1.getMac());
            }
        };
        mOrderByMac.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.accent));
    }

    private void initialView() {
        mAppBar.setTitle("设置目标");
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
                                            NoteDoDeal deal =new NoteDoDeal(mList);
                                            deal.delete(adapterPosition);
                                            adapter.notifyDataSetChanged();
                                            show("success");
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
                                            NoteDoDeal deal =new NoteDoDeal(mList);
                                            NoteDo noteDo=mList.get(adapterPosition);
                                            deal.upDate(noteDo.getMac(),noteE.getText().toString());
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
    protected void onDestroy() {
        super.onDestroy();
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
                mOrderByMac.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.accent));
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
                mOrderByNote.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.accent));
                break;
        }
    }
}

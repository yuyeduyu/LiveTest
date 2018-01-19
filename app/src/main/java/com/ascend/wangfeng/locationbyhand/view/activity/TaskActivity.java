package com.ascend.wangfeng.locationbyhand.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.anye.greendao.gen.EventBeanDao;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.MyItemDecoration;
import com.ascend.wangfeng.locationbyhand.adapter.TaskAdapter;
import com.ascend.wangfeng.locationbyhand.bean.EventBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.list)
    RecyclerView mList;
    @BindView(R.id.btn)
    Button mBtn;
    private EventBeanDao dao;
    private ArrayList<EventBean> mData;
    private TaskAdapter adapter;
    private AlertDialog inputDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        ButterKnife.bind(this);
        initData();
        initDialog();
        initTool();
    }

    private void initData() {
        mData = new ArrayList<>();
        dao = MyApplication.getInstances().getDaoSession().getEventBeanDao();
        mData.addAll(dao.loadAll());

        adapter = new TaskAdapter(mData);
    }

    private void initDialog() {
        final EditText editText = new EditText(TaskActivity.this);
        inputDialog =
                new AlertDialog.Builder(TaskActivity.this)
                .setTitle("添加事件")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        String text =editText.getText().toString();
                        if (text!=null&&text.length()>0){
                            dao.insert(new EventBean(text));
                            refreshData();
                        }
                    }
                }).create();

    }

    private void initTool() {
        mToolbar.setTitle("事件列表");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputDialog.show();
            }
        });
        adapter.setListener(new TaskAdapter.IOnListener() {
            @Override
            public void onClick(View view, int position) {
                //进入新页面
                startActivity(new Intent(TaskActivity.this,AnalyeActivity.class));
            }

            @Override
            public void onLongClick(View view, final int position) {
                //删除
                new AlertDialog.Builder(TaskActivity.this).setTitle("提醒")
                        .setMessage("确认删除吗")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface anInterface, int i) {
                                dao.delete(mData.get(position));
                                refreshData();
                                anInterface.cancel();
                            }
                        })
                        .setNegativeButton("取消", null).show();

            }

            @Override
            public void onButtonClick(View view, int position) {
                EventBean bean = mData.get(position);
                if (bean.isRun()) {
                    bean.stop();
                    dao.update(bean);
                    refreshData();
                } else {
                    bean.start();
                    dao.update(bean);
                    refreshData();
                }
            }
        });

        mList.setAdapter(adapter);
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.addItemDecoration(new MyItemDecoration(this, LinearLayoutManager.HORIZONTAL));
    }

    public void refreshData() {
        mData.clear();
        mData.addAll(dao.loadAll());
        adapter.notifyDataSetChanged();
    }
}

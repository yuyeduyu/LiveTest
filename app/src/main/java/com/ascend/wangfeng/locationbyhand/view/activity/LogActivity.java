package com.ascend.wangfeng.locationbyhand.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anye.greendao.gen.ConnectRelationDao;
import com.anye.greendao.gen.LogDao;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.LogAdapter;
import com.ascend.wangfeng.locationbyhand.adapter.MyItemDecoration;
import com.ascend.wangfeng.locationbyhand.bean.NoteDoDeal;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.ConnectRelation;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.Log;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.NoteDo;
import com.ascend.wangfeng.locationbyhand.util.DaoUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.count)
    TextView mCount;
    @BindView(R.id.ltime)
    TextView mLtime;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.activity_log)
    LinearLayout mActivityLog;
    @BindView(R.id.tv_mac)
    TextView tvMac;
    @BindView(R.id.iv_set_beizhu)
    ImageView ivSetBeizhu;
    private NoteDo note;
    private ArrayList<Log> mList;
    private LogAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initView() {
        mToolbar.setTitle("目标详情");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvMac.setText(note.getMac() + "(" + note.getNote() + ")");
        adapter = new LogAdapter(mList);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.addItemDecoration(new MyItemDecoration(this, LinearLayoutManager.HORIZONTAL));
        mRecycler.setAdapter(adapter);
        mCount.setText(mList.size() + "");
    }

    private void initData() {
        note = (NoteDo) getIntent().getSerializableExtra("note");
        LogDao dao = MyApplication.getInstances().getDaoSession().getLogDao();
        mList = (ArrayList<Log>) dao.queryBuilder().where(LogDao.Properties.Mac.eq(note.getMac())).list();

    }

    @OnClick({R.id.iv_set_beizhu})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_set_beizhu:
                //修改备注
                showSetNoteDialog();
                break;
        }
    }

    private void showSetNoteDialog() {
        final View layout = this.getLayoutInflater().inflate(R.layout.dialog_targetactivity
                , null);
        final EditText macE = (EditText) layout.findViewById(R.id.mac);
        final EditText noteE = (EditText) layout.findViewById(R.id.suspect);
        macE.setText(note.getMac());
        macE.setEnabled(false);
        noteE.setText(note.getNote());
        new AlertDialog.Builder(this).setTitle("修改备注")
                .setView(layout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        NoteDoDeal deal = new NoteDoDeal(MyApplication.getmNoteDos());
                        deal.upDate(note.getMac(), noteE.getText().toString());
                        tvMac.setText(note.getMac() + "(" + noteE.getText().toString() + ")");
                        Toast.makeText(LogActivity.this, "修改备注成功", Toast.LENGTH_SHORT).show();
                        anInterface.dismiss();
                    }
                }).setNegativeButton("取消", null).create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.target, menu);
        ConnectRelationDao relationDao = MyApplication.getInstances().getDaoSession().getConnectRelationDao();
        List<ConnectRelation> mRelations = relationDao.queryBuilder().where(ConnectRelationDao.Properties.Ap.eq(note.getMac())).list();
        if (mRelations.size() <= 0)
            menu.findItem(R.id.relation).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delect:
                //删除布控目标
                showDeleteNoteDialog();
                break;
            case R.id.clear:
                //清空日志
                showClearNoteDialog();
                break;
            case R.id.relation:
                //连接关系
                Intent intent = new Intent(LogActivity.this, LogRelationActivity.class);
                intent.putExtra("mac", note.getMac());
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 清空目标日志
     * @author lish
     * created at 2018-08-31 15:06
     */
    private void showClearNoteDialog() {
        new AlertDialog.Builder(LogActivity.this).setTitle("提醒")
                .setMessage("确认清除布控目标日志吗")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        LogDao dao = MyApplication.getInstances().getDaoSession().getLogDao();
                        for (Log log : mList) {
                            dao.delete(log);
                        }
                        mList.clear();
                        adapter.notifyDataSetChanged();
                        mCount.setText(mList.size() + "");
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    /**
     * 删除布控目标
     *
     * @author lish
     * created at 2018-08-31 15:06
     */
    private void showDeleteNoteDialog() {
        final String mac = note.getMac();
        new AlertDialog.Builder(LogActivity.this).setTitle("提醒")
                .setMessage("确认删除布控目标吗")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        //mPresenter.delMac(mac);
                        NoteDoDeal deal = new NoteDoDeal(MyApplication.getmNoteDos());
                        deal.delete(mac);
                        Toast.makeText(LogActivity.this, "删除布控目标成功", Toast.LENGTH_SHORT).show();
                        //删除布控目标日志
                        DaoUtils.delectTaglog(mac);
                        anInterface.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("取消", null).show();
    }
}

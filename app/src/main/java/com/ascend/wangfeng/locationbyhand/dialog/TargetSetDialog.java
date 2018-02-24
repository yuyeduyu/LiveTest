package com.ascend.wangfeng.locationbyhand.dialog;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.api.AppClient;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.AlarmMacListDo;
import com.ascend.wangfeng.locationbyhand.bean.NoteDoDeal;
import com.ascend.wangfeng.locationbyhand.bean.NoteVo;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.NoteDo;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by fengye on 2017/3/11.
 * email 1040441325@qq.com
 * 对布控目标的设置;
 */

public class TargetSetDialog {
    private static final String TAG = "TargetSetDialog";

    public static void showDialog(final AppCompatActivity activity, final NoteVo noteVo, final IShowView view) {
        //添加布控目标；
        final View layout = activity.getLayoutInflater().inflate(R.layout.dialog_targetactivity
                , null);
        final EditText mac = (EditText) layout.findViewById(R.id.mac);
        mac.setText(noteVo.getMac() + "");
        mac.setEnabled(false);
        final EditText note = (EditText) layout.findViewById(R.id.suspect);
        new AlertDialog.Builder(activity).setTitle("设置目标")
                .setView(layout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {

                            Log.d(TAG, "onClick: " + mac.getText().toString());
                            String macStr = mac.getText().toString();
                            String noteStr = note.getText().toString() + "";
                            addMac(macStr, noteStr, view);
                    }
                })
                .setNegativeButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        String macStr = mac.getText().toString() + "";
                        delMac(macStr, view);
                    }
                })
                .setNeutralButton("设置密码", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        showSetPasswordDialog(activity, noteVo, view);
                    }
                })
                .create().show();
    }
    public static void showSetPasswordDialog(final AppCompatActivity activity, NoteVo apVo, final IShowView view) {
        final View layout = activity.getLayoutInflater().inflate(R.layout.dialog_ap_password
                , null);

        final EditText mac = (EditText) layout.findViewById(R.id.edit_mac);
        final EditText name = (EditText) layout.findViewById(R.id.edit_name);
        final EditText password = (EditText) layout.findViewById(R.id.edit_password);
        if (apVo != null) {
            mac.setText(apVo.getMac());
            name.setText(apVo.getName());
        }
        new AlertDialog.Builder(activity).setTitle("设置密码")
                .setView(layout)
                .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        String nameStr = name.getText().toString();
                        String macStr = mac.getText().toString() + "";
                        String passwordStr = password.getText().toString();
                        Config.setApPassword(macStr,nameStr,passwordStr);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        String macStr = mac.getText().toString() + "";
                        if (macStr.equals(Config.getApPasswordMac())) {
                          Config.clearApPassword();
                        } else {
                            view.show(0,activity.getString(R.string.un_set_password));
                        }
                    }
                })
                .create().show();
    }
    private static void addMac(String mac, String note, final IShowView view) {
        NoteDoDeal deal = new NoteDoDeal(MyApplication.getmNoteDos());
        NoteDo noteDo = new NoteDo();
        noteDo.setMac(mac);
        noteDo.setNote(note);
        deal.add(noteDo);
        view.show(1, "添加成功");
    }

    private static void update(String oldMac, String mac, String note, final IShowView view) {
        Log.i(TAG, "update: ");
        AppClient.getWiFiApi().updateMac(oldMac, mac, note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<AlarmMacListDo>() {
                    @Override
                    public void onNext(AlarmMacListDo aDo) {
                        view.show(1, "修改成功");
                        Config.updateConfig();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        view.show(1, e.getMessage());
                    }
                });
    }


    private static void delMac(String mac, final IShowView view) {
        Log.i(TAG, "del_mac: " + mac);
        NoteDoDeal deal = new NoteDoDeal(MyApplication.getmNoteDos());
        NoteDo noteDo = new NoteDo();
        noteDo.setMac(mac);
        deal.delete(mac);
        view.show(1, "删除成功");
    }
}

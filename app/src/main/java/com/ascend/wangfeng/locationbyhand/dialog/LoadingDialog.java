package com.ascend.wangfeng.locationbyhand.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ascend.wangfeng.locationbyhand.R;


/**
 * 加载时弹窗动画对话框
 */
public class LoadingDialog extends BaseDialog {


    private TextView mText;
    private Context mContext;
    private boolean mCancelable;
    private ImageView mImageView;
    private static LoadingDialog mLoadingDialog;



    public LoadingDialog(Context context) {
        super(context, R.style.dim_dialog);
        mContext = context;
    }


    protected void addLinstenr() {

    }

//    public LoadingDialog(Context context, boolean cancelable) {
//        super(context, R.style.dim_dialog);
//        mContext = context;
//        mCancelable = cancelable;
//        setCancelable(mCancelable);
//        setCanceledOnTouchOutside(false);
//    }

    @Override
    public void show() {
        super.show();
        // 需要调用系统的super.show()来调用onCreate来实例化view
        if (mImageView != null)
            mImageView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.progress_anim));
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mImageView != null)
            mImageView.clearAnimation();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.loading_dialog_layout;
    }

    @Override
    protected void findViews() {
        mImageView = (ImageView) findViewById(R.id.loading_image);
        mText = (TextView) findViewById(R.id.load_text);

    }

    public void setText(String str) {
        mText.setText(str);
    }

    @Override
    protected void setWindowParam() {
        setWindowParams(-1, -2, Gravity.CENTER,1);

    }


}

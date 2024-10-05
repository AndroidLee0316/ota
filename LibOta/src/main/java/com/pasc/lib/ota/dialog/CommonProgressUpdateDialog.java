package com.pasc.lib.ota.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pasc.lib.ota.R;

/**
 * @author yangzijian
 * @date 2019/1/16
 * @des
 * @modify
 **/
public class CommonProgressUpdateDialog extends Dialog implements IProgressDialog, IProgressType {
    public static final int STYLE_SPINNER = 0;
    public static final int STYLE_HORIZONTAL = 1;
    private int style = STYLE_HORIZONTAL;
    Context mContext;
    private ProgressBar mProgress;
    private View btn_retry;
    private TextView mProgressNumber;
    private TextView mProgressPercent;
    private TextView tv_title;

    private int mMax = 100;
    private int mProgressVal = 0;
    private IDialogClick dialogClick;

    public CommonProgressUpdateDialog(@NonNull Context context) {
        this (context, STYLE_HORIZONTAL);

    }
    public CommonProgressUpdateDialog(@NonNull Context context, int style){
        super (context, R.style.otaUpdateDialog);
        this.style=style;
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from (mContext);
        if (style == STYLE_HORIZONTAL) {
            View view = inflater.inflate (
                    R.layout.ota_apk_horizontal_progress_v2, null);
            mProgress = view.findViewById (R.id.progress);
            mProgressNumber = view.findViewById (R.id.progress_number);
            mProgressPercent = view.findViewById (R.id.progress_percent);
            tv_title = view.findViewById (R.id.tv_title);
            btn_retry = view.findViewById (R.id.btn_retry);
            btn_retry.setVisibility (View.GONE);
            mProgress.setMax (mMax);
            setContentView (view);
        } else {
            View view = inflater.inflate (
                    R.layout.ota_apk_common_progress_v2, null);
            mProgress = view.findViewById (R.id.progress);
            tv_title = view.findViewById (R.id.tv_title);
            btn_retry = view.findViewById (R.id.btn_retry);
            btn_retry.setVisibility (View.GONE);
            setContentView (view);

        }

        if (btn_retry != null) {
            btn_retry.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    if (dialogClick != null) {
                        dialogClick.download ();
                    }
                    btn_retry.setVisibility (View.GONE);
                }
            });
        }
    }

    public void setProgressStyle(int style) {
        this.style = style;
    }

    public void setMessage(CharSequence text) {
        if (tv_title != null) {
            tv_title.setText (text);
        }
    }

    public void setIndeterminate(boolean f) {

    }


    @Override
    public void setProgress(int progress) {
        this.mProgressVal = progress;
        if (mProgress != null) {
            mProgress.setProgress (mProgressVal);
        }

        if (style == STYLE_HORIZONTAL) {
            int max = mProgress.getMax ();
            if (mProgressPercent!=null){
                mProgressPercent.setText (progress+"%");
            }
            if (mProgressNumber!=null){
                mProgressNumber.setText (progress+"/"+max);

            }
        }
    }

    @Override
    public void setHorizontalType() {
        style = STYLE_HORIZONTAL;
    }

    @Override
    public void setSpinnerType() {
        style = STYLE_SPINNER;
    }

    @Override
    public void setDialogClick(IDialogClick dialogClick) {
        this.dialogClick = dialogClick;
    }

    @Override
    public void showRetry() {
        if (btn_retry != null)
            btn_retry.setVisibility (View.VISIBLE);
    }
}

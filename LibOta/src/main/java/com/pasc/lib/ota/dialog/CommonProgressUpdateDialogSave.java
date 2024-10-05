package com.pasc.lib.ota.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pasc.lib.ota.R;

import java.text.NumberFormat;

/**
 * Copyright (C) 2018 pasc Licensed under the Apache License, Version 2.0 (the "License");
 *
 * @author yangzijian
 * @date 2018/9/3
 * @des
 * @modify
 **/
public class CommonProgressUpdateDialogSave extends AlertDialog implements IProgressDialog,IProgressType {
    /**
     * Creates a ProgressDialog with a circular, spinning progress
     * bar. This is the default.
     */
    public static final int STYLE_SPINNER = 0;

    /**
     * Creates a ProgressDialog with a horizontal progress bar.
     */
    public static final int STYLE_HORIZONTAL = 1;

    private ProgressBar mProgress;
    private TextView mMessageView;
    private View btn_retry;

    private int mProgressStyle = STYLE_SPINNER;
    private TextView mProgressNumber;
    private String mProgressNumberFormat;
    private TextView mProgressPercent;
    private NumberFormat mProgressPercentFormat;

    private int mMax;
    private int mProgressVal;
    private int mSecondaryProgressVal;
    private int mIncrementBy;
    private int mIncrementSecondaryBy;
    private Drawable mProgressDrawable;
    private Drawable mIndeterminateDrawable;
    private CharSequence mMessage;
    private boolean mIndeterminate;

    private boolean mHasStarted;
    private Handler mViewUpdateHandler;
    private Context mContext;

    public CommonProgressUpdateDialogSave(Context context) {
        super (context,R.style.otaUpdateDialog);
        initFormats ();
        mContext = context;
    }

    public CommonProgressUpdateDialogSave(Context context, int theme) {
        super (context, theme);
        initFormats ();
        mContext = context;
    }

    private void initFormats() {
        mProgressNumberFormat = "%1d/%2d";
        mProgressPercentFormat = NumberFormat.getPercentInstance ();
        mProgressPercentFormat.setMaximumFractionDigits (0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from (mContext);
        if (mProgressStyle == STYLE_HORIZONTAL) {

            /* Use a separate handler to update the text views as they
             * must be updated on the same thread that created them.
             */
            mViewUpdateHandler = new Handler () {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage (msg);

                    /* Update the number and percent */
                    int progress = mProgress.getProgress ();
                    int max = mProgress.getMax ();
                    if (mProgressNumberFormat != null) {
                        String format = mProgressNumberFormat;
                        mProgressNumber.setText (String.format (format, progress, max));
                    } else {
                        mProgressNumber.setText ("");
                    }
                    if (mProgressPercentFormat != null) {
                        double percent = (double) progress / (double) max;
                        SpannableString tmp = new SpannableString (mProgressPercentFormat.format (percent));
                        tmp.setSpan (new StyleSpan (android.graphics.Typeface.BOLD),
                                0, tmp.length (), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mProgressPercent.setText (tmp);
                    } else {
                        mProgressPercent.setText ("");
                    }
                }
            };
            View view = inflater.inflate (
                    R.layout.ota_apk_horizontal_progress, null);
            mProgress = (ProgressBar) view.findViewById (R.id.progress);
            mProgressNumber = (TextView) view.findViewById (R.id.progress_number);
            mProgressPercent = (TextView) view.findViewById (R.id.progress_percent);
            btn_retry = view.findViewById (R.id.btn_retry);
            btn_retry.setVisibility (View.GONE);
            setView (view);
        } else {
            View view = inflater.inflate (
                    R.layout.ota_apk_common_progress, null);
            mProgress = (ProgressBar) view.findViewById (R.id.progress);
            mMessageView = (TextView) view.findViewById (R.id.message);
            btn_retry = view.findViewById (R.id.btn_retry);
            btn_retry.setVisibility (View.GONE);
            setView (view);
        }
        if (mMax > 0) {
            setMax (mMax);
        }
        if (mProgressVal > 0) {
            setProgress (mProgressVal);
        }
        if (mSecondaryProgressVal > 0) {
            setSecondaryProgress (mSecondaryProgressVal);
        }
        if (mIncrementBy > 0) {
            incrementProgressBy (mIncrementBy);
        }
        if (mIncrementSecondaryBy > 0) {
            incrementSecondaryProgressBy (mIncrementSecondaryBy);
        }
        if (mProgressDrawable != null) {
            setProgressDrawable (mProgressDrawable);
        }
        if (mIndeterminateDrawable != null) {
            setIndeterminateDrawable (mIndeterminateDrawable);
        }
        if (mMessage != null) {
            setMessage (mMessage);
        }
        setIndeterminate (mIndeterminate);
        onProgressChanged ();
        super.onCreate (savedInstanceState);

        if (btn_retry != null)
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

    @Override
    public void onStart() {
        super.onStart ();
        mHasStarted = true;
    }

    @Override
    protected void onStop() {
        super.onStop ();
        mHasStarted = false;
    }

    public void setProgress(int value) {
        if (mHasStarted) {
            mProgress.setProgress (value);
            onProgressChanged ();
        } else {
            mProgressVal = value;
        }
    }

    public void setSecondaryProgress(int secondaryProgress) {
        if (mProgress != null) {
            mProgress.setSecondaryProgress (secondaryProgress);
            onProgressChanged ();
        } else {
            mSecondaryProgressVal = secondaryProgress;
        }
    }

    public int getProgress() {
        if (mProgress != null) {
            return mProgress.getProgress ();
        }
        return mProgressVal;
    }

    public int getSecondaryProgress() {
        if (mProgress != null) {
            return mProgress.getSecondaryProgress ();
        }
        return mSecondaryProgressVal;
    }

    public int getMax() {
        if (mProgress != null) {
            return mProgress.getMax ();
        }
        return mMax;
    }

    /**
     * Sets the maximum allowed progress value.
     */
    public void setMax(int max) {
        if (mProgress != null) {
            mProgress.setMax (max);
            onProgressChanged ();
        } else {
            mMax = max;
        }
    }

    /**
     * Increments the current progress value.
     *
     * @param diff the amount by which the current progress will be incremented,
     *             up to {@link #getMax()}
     */
    public void incrementProgressBy(int diff) {
        if (mProgress != null) {
            mProgress.incrementProgressBy (diff);
            onProgressChanged ();
        } else {
            mIncrementBy += diff;
        }
    }

    /**
     * Increments the current secondary progress value.
     *
     * @param diff the amount by which the current secondary progress will be incremented,
     *             up to {@link #getMax()}
     */
    public void incrementSecondaryProgressBy(int diff) {
        if (mProgress != null) {
            mProgress.incrementSecondaryProgressBy (diff);
            onProgressChanged ();
        } else {
            mIncrementSecondaryBy += diff;
        }
    }

    /**
     * Sets the drawable to be used to display the progress value.
     *
     * @param d the drawable to be used
     * @see ProgressBar#setProgressDrawable(Drawable)
     */
    public void setProgressDrawable(Drawable d) {
        if (mProgress != null) {
            mProgress.setProgressDrawable (d);
        } else {
            mProgressDrawable = d;
        }
    }

    /**
     * Sets the drawable to be used to display the indeterminate progress value.
     *
     * @param d the drawable to be used
     * @see ProgressBar#setProgressDrawable(Drawable)
     * @see #setIndeterminate(boolean)
     */
    public void setIndeterminateDrawable(Drawable d) {
        if (mProgress != null) {
            mProgress.setIndeterminateDrawable (d);
        } else {
            mIndeterminateDrawable = d;
        }
    }

    public void setIndeterminate(boolean indeterminate) {
        if (mProgress != null) {
            mProgress.setIndeterminate (indeterminate);
        } else {
            mIndeterminate = indeterminate;
        }
    }

    public boolean isIndeterminate() {
        if (mProgress != null) {
            return mProgress.isIndeterminate ();
        }
        return mIndeterminate;
    }

    @Override
    public void setMessage(CharSequence message) {
        if (mProgress != null) {
            if (mProgressStyle == STYLE_HORIZONTAL) {
                super.setMessage (message);
            } else {
                mMessageView.setText (message);
            }
        } else {
            mMessage = message;
        }
    }

    public void setProgressStyle(int style) {
        mProgressStyle = style;
    }

    public void setProgressNumberFormat(String format) {
        mProgressNumberFormat = format;
        onProgressChanged ();
    }

    public void setProgressPercentFormat(NumberFormat format) {
        mProgressPercentFormat = format;
        onProgressChanged ();
    }

    private void onProgressChanged() {
        if (mProgressStyle == STYLE_HORIZONTAL) {
            if (mViewUpdateHandler != null && !mViewUpdateHandler.hasMessages (0)) {
                mViewUpdateHandler.sendEmptyMessage (0);
            }
        }
    }

    private IDialogClick dialogClick;

    @Override
    public void setDialogClick(IDialogClick dialogClick) {
        this.dialogClick = dialogClick;
    }

    @Override
    public void showRetry() {
        if (btn_retry != null)
            btn_retry.setVisibility (View.VISIBLE);

    }

    @Override
    public void setHorizontalType() {
        setProgressStyle (STYLE_HORIZONTAL);
    }

    @Override
    public void setSpinnerType() {
        setProgressStyle (STYLE_SPINNER);


    }
}

package com.pasc.lib.ota.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pasc.lib.ota.R;

/**
 * Copyright (C) 2018 pasc Licensed under the Apache License, Version 2.0 (the "License");
 *
 * @author yangzijian
 * @date 2018/9/3
 * @des
 * @modify
 **/
public class CommonUpdateUpdateDialog extends Dialog implements IUpdateDialog, View.OnClickListener {
    public CommonUpdateUpdateDialog(@NonNull Context context) {
        super(context,R.style.otaUpdateDialog);
        getWindow().
        setContentView(R.layout.ota_dialog_apk_update);
        assignViews();
    }

    IDialogClick iDialogClick;
    private LinearLayout rllContent;
    private TextView tvContent, tv_title;
    private Button tvConfirm;

    private void assignViews() {
        rllContent = (LinearLayout) findViewById(R.id.rll_content);
        tvContent = (TextView) findViewById(R.id.tv_content);
        tvConfirm = (Button) findViewById(R.id.tv_confirm);
        tv_title = findViewById(R.id.tv_title);
        tvConfirm.setOnClickListener(this);
    }

    @Override
    public void setTitle(String title) {
        if (tv_title != null) {
            tv_title.setText(title);
        }

    }

    @Override
    public void setVersionName(String versionName) {

    }

    @Override
    public void setContent(String content) {
        if (tvContent != null) {
            tvContent.setText(content);
            tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());

        }
    }

    @Override
    public void updateType(boolean isForce) {

    }


    @Override
    public void setDialogClick(IDialogClick dialogClick) {
        this.iDialogClick = dialogClick;
    }



    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_confirm) {
            if (iDialogClick != null) {
                iDialogClick.download();
            }
        }
    }
}

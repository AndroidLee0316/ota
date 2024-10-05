package com.pasc.lib.ota.dialog;

/**
 * Copyright (C) 2018 pasc Licensed under the Apache License, Version 2.0 (the "License");
 *
 * @author yangzijian
 * @date 2018/9/3
 * @des
 * @modify
 **/
public interface IUpdateDialog {

    void setTitle(String title);
    void setVersionName(String versionName);
    void setContent(String content);

    void setCancelable(boolean cancel);

    void updateType(boolean isForce);

    void setCanceledOnTouchOutside(boolean cancel);

    void show();

    void dismiss();

    boolean isShowing();

    void setDialogClick(IDialogClick dialogClick);

}

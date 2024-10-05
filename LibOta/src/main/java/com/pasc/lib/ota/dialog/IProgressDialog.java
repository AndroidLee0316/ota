package com.pasc.lib.ota.dialog;

/**
 * Copyright (C) 2018 pasc Licensed under the Apache License, Version 2.0 (the "License");
 *
 * @author yangzijian
 * @date 2018/9/3
 * @des
 * @modify
 **/
public interface IProgressDialog extends IRetryListener{
    void setProgress(int progress);
    void show();
    void setCancelable(boolean cancel);
    void setCanceledOnTouchOutside(boolean cancel);
    void dismiss();
    boolean isShowing();

}

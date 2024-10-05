package com.pasc.lib.ota.callback;

/**
 * Copyright (C) 2018 pasc Licensed under the Apache License, Version 2.0 (the "License");
 *
 * @author yangzijian
 * @date 2018/9/3
 * @des
 * @modify
 **/
public abstract class UpdateCallBack implements DownloadCallBack{
    public void onStart() {

    }

    public void onNoNewApk() {

    }

    public void onHasNewApk(boolean isForceUpdate) {

    }
    public void onHasNewApk(String versionName,String versionCode,boolean isForceUpdate) {

    }
    public void onSuccess() {

    }

    public void onError(String msg) {

    }

    public void onEnd() {
    }

    @Override
    public void startDownload() {

    }

    @Override
    public void finishDownload() {

    }

    @Override
    public void downloadFail(String msg) {

    }

    @Override
    public void downLoadProgress(int progress) {

    }
}
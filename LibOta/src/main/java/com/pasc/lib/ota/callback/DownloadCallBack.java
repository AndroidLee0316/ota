package com.pasc.lib.ota.callback;

/**
<<<<<<< HEAD
 * Created by yintangwen952 on 2018/9/14.
 */

public interface DownloadCallBack {

        void startDownload();


        void finishDownload();


        void downloadFail(String msg);

        void downLoadProgress(int progress);

}

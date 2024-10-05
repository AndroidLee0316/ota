package com.pasc.lib.ota.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.pasc.lib.net.download.DownLoadManager;
import com.pasc.lib.net.download.DownloadInfo;
import com.pasc.lib.net.download.DownloadObserver;
import com.pasc.lib.ota.callback.DownloadCallBack;
import com.pasc.lib.ota.dialog.IDialogClick;
import com.pasc.lib.ota.dialog.IProgressDialog;
import com.pasc.lib.ota.dialog.IRetryListener;

import java.io.File;

/**
 * Created by yintangwen952 on 2018/9/13.
 */

public class DownloadHelper {


    private static Handler handler = new Handler (Looper.getMainLooper ());
    private static DownloadInfo info;
    private static IProgressDialog progressDialog;
    private static int preProgress = 0;

    public static void downLoad(Context ctx, final String downLoadUrl, final String fileSavePathRoo,
                                final String versionName, final boolean resumePoint, final boolean showNotification,
                                final IProgressDialog newProgressDialog, final DownloadCallBack callBack) {
        downLoad (ctx,downLoadUrl,fileSavePathRoo,versionName,resumePoint,showNotification,newProgressDialog,0,callBack);
    }

    public static void downLoad(Context ctx, final String downLoadUrl, final String fileSavePathRoo,
                                final String versionName, final boolean resumePoint,
                                final boolean showNotification, final IProgressDialog newProgressDialog,
                                final int totalLengthFixed, final DownloadCallBack callBack) {
        //不要持有原始的Context,如果是Activity ,可能内存泄露
        final Context context = ctx.getApplicationContext ();
        String apkFileName = versionName + ".apk";
        NotificationHelper.setUpNotification (context, showNotification, "正在下载", apkFileName);
        info = new DownloadInfo (downLoadUrl, apkFileName, fileSavePathRoo, resumePoint);
        info.totalLength (totalLengthFixed);
        dismissDialog ();
        progressDialog = newProgressDialog;

        if (progressDialog != null && progressDialog instanceof IRetryListener) {
            ((IRetryListener) progressDialog).setDialogClick (new IDialogClick () {
                @Override
                public void download() {
                    downLoad (context, downLoadUrl, fileSavePathRoo, versionName, resumePoint, showNotification, progressDialog,totalLengthFixed, callBack);

                }

                @Override
                public void cancel() {

                }
            });
        }

        handler.post (new Runnable () {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.startDownload ();
                }
            }
        });
        if (Looper.myLooper () == Looper.getMainLooper ()) {
            showDialog ();
        } else {
            handler.post (new Runnable () {
                @Override
                public void run() {
                    showDialog ();
                }
            });
        }

        DownLoadManager.getDownInstance ().startDownload (info, new DownloadObserver () {
            @Override
            public void onDownloadStateProgressed(DownloadInfo updateInfo) {

                switch (updateInfo.downloadState) {
                    case DownloadInfo.STATE_DOWNLOADING:
                        int progress = updateInfo.getPercent ();
                        NotificationHelper.updateNotify (context, progress);
                        if (callBack != null) {
                            callBack.downLoadProgress (progress);
                        }
                        setProgress (progress);
                        if (progress > 0) {
                            preProgress = progress;
                        }
                        break;
                    case DownloadInfo.STATE_PAUSED:
                        NotificationHelper.cancelNotify ();
//                        dismissDialog ();
                        showRetry ();
                        if (callBack != null) {
                            callBack.downloadFail ("暂停下载");
                        }
                        setProgress (preProgress);

                        break;
                    case DownloadInfo.STATE_ERROR:
//                        dismissDialog ();
                        showRetry ();
                        NotificationHelper.cancelNotify ();
                        if (callBack != null) {
                            callBack.downloadFail ("网络下载异常");
                        }
                        setProgress (preProgress);

                        break;
                    case DownloadInfo.STATE_FINISH:
                        dismissDialog ();
                        NotificationHelper.cancelNotify ();
                        String filePath = updateInfo.getFilePath (context);
                        install (context, new File (filePath));
                        if (callBack != null) {
                            callBack.finishDownload ();
                        }
                        break;
                    case DownloadInfo.STATE_WAITING:
                        break;
                }

            }
        });

    }

    /***
     * 显示重试
     */
    private static void showRetry() {
        if (progressDialog != null && progressDialog.isShowing () && progressDialog instanceof IRetryListener) {
            ((IRetryListener) progressDialog).showRetry ();
        }
    }

    private static void dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing ()) {
            progressDialog.dismiss ();
        }
        progressDialog = null;
    }

    private static void showDialog() {
        if (progressDialog != null && !progressDialog.isShowing ()) {
            progressDialog.show ();
        }
    }

    private static void setProgress(int progress) {
        if (progressDialog != null && progressDialog.isShowing ()) {
            progressDialog.setProgress (progress);
        }
    }

    public static void install(Context context, File file) {
        if (!file.exists ()) {
            return;
        }
        if (!apkIsOk (context, file.getAbsolutePath ())) {
            Toast.makeText (context, "安装包不完整", Toast.LENGTH_SHORT).show ();
            file.delete ();
            return;
        }

        try {
            Intent intent = new Intent (Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                intent.setDataAndType (Uri.fromFile (file), "application/vnd.android.package-archive");
            } else {
                Uri uri =
                        FileProvider.getUriForFile (context, context.getPackageName () + ".fileprovider",
                                file);
                intent.setDataAndType (uri, "application/vnd.android.package-archive");
                intent.addFlags (Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity (intent);
        } catch (Exception e) {
            e.printStackTrace ();
        }

    }


    /**
     * @param context
     * @param filePath
     * @return
     */
    public static boolean apkIsOk(Context context, String filePath) {
        boolean result = false;
        try {
            PackageManager pm = context.getPackageManager ();
            PackageInfo info = pm.getPackageArchiveInfo (filePath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                result = true;//完整
            }
        } catch (Exception e) {
            result = false;//不完整
        }
        return result;
    }

    public static boolean android8InstallCheck(Activity activity) {

        try {
            /**
             * 判断是否是8.0,8.0需要处理未知应用来源权限问题,否则直接安装
             */
            if (Build.VERSION.SDK_INT >= 26) {
                boolean b = activity.getPackageManager ().canRequestPackageInstalls ();
                if (!b) {

                    Uri packageURI = Uri.parse("package:"+activity.getPackageName());
                    //请求安装未知应用来源的权限
                    String apkName = NotificationHelper.getAppName (activity);
//                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 10001);
                    Toast.makeText (activity, apkName + "需要安装权限", Toast.LENGTH_SHORT).show ();
                    Intent intent = new Intent (Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packageURI);
                    activity.startActivityForResult (intent, 10002);
                    return false;
                }

            }
        } catch (Exception e) {
            e.printStackTrace ();
        }

        return true;
    }

}

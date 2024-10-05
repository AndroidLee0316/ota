package com.pasc.business.ota;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.pasc.business.ota.util.OtaUtil;
import com.pasc.lib.base.AppProxy;
import com.pasc.lib.base.permission.PermissionUtils;
import com.pasc.lib.base.util.AppUtils;
import com.pasc.lib.net.resp.BaseRespObserver;
import com.pasc.lib.ota.IBaseUpdate;
import com.pasc.lib.ota.Ota;
import com.pasc.lib.ota.UpdateType;
import com.pasc.lib.ota.callback.IDownLoadRequirement;
import com.pasc.lib.ota.callback.UpdateCallBack;
import com.pasc.lib.ota.dialog.CommonProgressUpdateDialog;
import com.pasc.lib.ota.dialog.IProgressDialog;
import com.pasc.lib.ota.dialog.IUpdateDialog;

import java.lang.ref.WeakReference;

import io.reactivex.functions.Consumer;

/**
 * 更新逻辑 管理
 */
public class UpdateManager {

    private WeakReference<Activity> activityWeakReference;
    private boolean isMainActivity; //是否是首页调用（首页调用如果是普通更新的话，仅提示一次）
    private boolean showUpdateDialog;
    private boolean showProgressDialog;
    private boolean showNotification;
    private boolean resumePoint;
    private UpdateCallBack updateCallBack;
    private IUpdateDialog customUpdateDialog;
    private IProgressDialog customProgressDialog;
    private UpdateDialogListener iUpdateListener;

    /***没对话框的时候通知 广告****/
    public void setUpdateDialogListener(UpdateDialogListener iUpdateListener) {
        this.iUpdateListener = iUpdateListener;
    }

    private UpdateManager(Builder builder) {
        activityWeakReference = new WeakReference<> (builder.activity);
        isMainActivity = builder.isMainActivity;
        showUpdateDialog = builder.showUpdateDialog;
        showProgressDialog = builder.showProgressDialog;
        showNotification = builder.showNotification;
        updateCallBack = builder.updateCallBack;
        customUpdateDialog = builder.customUpdateDialog;
        customProgressDialog = builder.customProgressDialog;
        resumePoint = builder.resumePoint;
    }


    public void checkUpdate() {
//        PermissionUtils.request (activityWeakReference, needSdcardPermission)
//                .subscribe (new Consumer<Boolean> () {
//                    @Override
//                    public void accept(Boolean aBoolean) throws Exception {
//                        if (aBoolean) {
//
//                        } else {
//                            PermissionSettingUtils.gotoPermissionSetting (activityWeakReference);
//                        }
//                    }
//                });
        update ();
    }

    private boolean isActivityOk(WeakReference<Activity> activityWeakReference) {
        return activityWeakReference != null && activityWeakReference.get () != null && !activityWeakReference.get ().isFinishing ();
    }

    private void update() {
        if (updateCallBack != null) {
            updateCallBack.onStart ();
        }
        final String versionNo = AppUtils.getVersionName (AppProxy.getInstance ().getContext ());
        UpdateBiz.checkVersion (versionNo).subscribe (new BaseRespObserver<AppUpdateResponse> () {
            @Override
            public void onSuccess(AppUpdateResponse newAppInfoBean) {
                if (newAppInfoBean != null && isActivityOk (activityWeakReference)) {
                    //是否有更新
                    String newVersionNo = newAppInfoBean.getVersionName ();
                    boolean isHasNewVersion = compareVersionNames (versionNo, newVersionNo) > 0;
                    if (isHasNewVersion) {
                        if (updateCallBack != null) {
                            updateCallBack.onHasNewApk (newAppInfoBean.isForceUpdate ());
                            updateCallBack.onHasNewApk (newAppInfoBean.getVersionName (), newAppInfoBean.getVersionCode (), newAppInfoBean.isForceUpdate ());
                            updateCallBack.onSuccess ();
                            updateCallBack.onEnd ();
                        }
                    } else {
                        //无更新
                        if (updateCallBack != null) {
                            updateCallBack.onNoNewApk ();
                            updateCallBack.onSuccess ();
                            updateCallBack.onEnd ();
                        }
                        if (iUpdateListener != null && isMainActivity)
                            iUpdateListener.hasUpdateDialog (false);
                        return;
                    }
//                    newAppInfoBean.setPrompt ("3");
//                    newAppInfoBean.setDownloadUrl ("http://gdown.baidu.com/data/wisegame/2c6a60c5cb96c593/QQ_182.apk");
                    //不提示更新
                    UpdateType updateType = newAppInfoBean.getUpdateType ();
                    if (updateType == UpdateType.NoTipsUpdate && isMainActivity) {
                        // //如果是首页进来的，并且是不提示，则不主动提示
                        if (iUpdateListener != null)
                            iUpdateListener.hasUpdateDialog (false);
                        return;
                    }

                    //普通更新
                    if (updateType == UpdateType.CommonUpdate && isMainActivity) {

                        //之前的普通更新版本
                        String preVersion = OtaUtil.getString (KEY_PRE_VERSION_NAME, "");
                        //保存上次普通更新的版本
                        OtaUtil.setString (KEY_PRE_VERSION_NAME, newVersionNo);

                        if (!TextUtils.isEmpty (preVersion) && compareVersionNames (preVersion, newVersionNo) > 0) {
                            //之间忽略频率
                            //之前普通更新的版本和当前普通更新版本不一样需要提示
//                            OtaUtil.setBoolean (UpdateManager.KEY_COMMON_UPDATE_TIP, false);
                            System.out.println ();
                        } else if (newAppInfoBean.isAlwaysRate ()) {
                            //每次都提示
                            OtaUtil.setBoolean (UpdateManager.KEY_COMMON_UPDATE_TIP, false);
                        } else if (newAppInfoBean.isOnlyFirst ()) {
                            //仅仅首次
                            if (OtaUtil.getBoolean (KEY_COMMON_UPDATE_TIP, false)) {
                                // 之前提示过了
                                if (iUpdateListener != null) {
                                    iUpdateListener.hasUpdateDialog (false);
                                }
                                return;
                            }
                            OtaUtil.setBoolean (UpdateManager.KEY_COMMON_UPDATE_TIP, true);
                        }

                    }
                    if (iUpdateListener != null && isMainActivity) {
                        iUpdateListener.hasUpdateDialog (showUpdateDialog);
                    }

                    if (!showUpdateDialog) {
                        return;
                    }

                    Activity ac = activityWeakReference.get ();
                    IUpdateDialog updateDialog = customUpdateDialog;
                    if (updateDialog == null) {
                        updateDialog = new UpdateDialogNew (ac);
                    } else {
                        updateDialog.setCancelable (!newAppInfoBean.isForceUpdate ());
                    }
                    IProgressDialog progressDialog = customProgressDialog;
                    if (showProgressDialog) {
                        if (progressDialog == null) {
                            progressDialog = getDownLoadProgressDialog (ac, newAppInfoBean.isForceUpdate ());
                        } else {
                            progressDialog.setCancelable (!newAppInfoBean.isForceUpdate ());
                        }
                    } else {
                        progressDialog = null;
                    }
                    showUpdate (ac, newAppInfoBean, updateDialog, progressDialog, showNotification, newAppInfoBean.getFileSize (),updateCallBack);

                }
            }

            @Override
            public void onV2Error(String code, String msg) {
                if (updateCallBack != null && isActivityOk (activityWeakReference)) {
                    updateCallBack.onError (msg);
                    updateCallBack.onEnd ();
                }
            }
        });

    }

    private String[] needSdcardPermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    private void showUpdate(final Activity context, final IBaseUpdate iUpdateBean, final IUpdateDialog customUpdateDialog,
                            final IProgressDialog progressDialog, final boolean showNotification,int fileSize, final UpdateCallBack updateCallBack) {
        final Ota ota = Ota.builder (context).addBaseUpdate (iUpdateBean).addUpdateDialog (customUpdateDialog)
                .addProgressDialog (progressDialog).enableNotification (showNotification)
                .callBack (updateCallBack)
                .resumePoint (resumePoint)
                .totalLengthFixed (fileSize)
                .build ();
        ota.setRequirement (new IDownLoadRequirement () {
            @Override
            public boolean requirement() {
                /***是否有权限***/
                boolean needPermission = needPermission (context);
                if (!needPermission) {
                    continueDownload (context, ota);
                }
                return needPermission;
            }
        });
        ota.show ();

    }

    boolean needPermission(final Activity context) {

        boolean sdCardExist = Environment.getExternalStorageState ()
                .equals (Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            if (Build.VERSION.SDK_INT >= 23) {
                // 运行时权限
                if (ContextCompat.checkSelfPermission (context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // 没sdcard 权限
                    return false;
                } else {
                    // 有权限
                    return true;
                }
            } else {
                return true;
            }
        } else {
            //没Sdcard
            return true;
        }
    }

    void continueDownload(final Activity context, final Ota ota) {

        PermissionUtils.request (context, needSdcardPermission)
                .subscribe (new Consumer<Boolean> () {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            ota.continueDownload ();
                        } else {
                            PermissionUtils.showPermissionDialog (context,needSdcardPermission);

                        }
                    }
                });



    }

    /**
     * 展示进度弹窗
     */
    private IProgressDialog getDownLoadProgressDialog(Context context, boolean isForceUpdate) {
        CommonProgressUpdateDialog mProgressDialog = new CommonProgressUpdateDialog (context);
        mProgressDialog.setProgressStyle (ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMessage ("更新中...");
        mProgressDialog.setIndeterminate (false);
        mProgressDialog.setCancelable (!isForceUpdate);
        mProgressDialog.setCanceledOnTouchOutside (false);
        return mProgressDialog;
    }

    public static class Builder {
        private Activity activity;
        private boolean isMainActivity;
        private boolean showNotification = true;
        private boolean resumePoint = false;
        private boolean showUpdateDialog = true;
        private boolean showProgressDialog = true;
        private UpdateCallBack updateCallBack;
        private IUpdateDialog customUpdateDialog;
        private IProgressDialog customProgressDialog;

        public Builder(Activity activity) {
            this.activity = activity;
        }


        public Builder isMainActivity(boolean isMainActivity) {
            this.isMainActivity = isMainActivity;
            return this;
        }


        public Builder showNotification(boolean showNotification) {
            this.showNotification = showNotification;
            return this;
        }

        public Builder showUpdateDialog(boolean showUpdateDialog) {
            this.showUpdateDialog = showUpdateDialog;
            return this;
        }

        public Builder updateCallBack(UpdateCallBack updateCallBack) {
            this.updateCallBack = updateCallBack;
            return this;
        }

        public Builder customUpdateDialog(IUpdateDialog customUpdateDialog) {
            this.customUpdateDialog = customUpdateDialog;
            return this;
        }

        public Builder customProgressDialog(IProgressDialog customProgressDialog) {
            this.customProgressDialog = customProgressDialog;
            return this;
        }

        public Builder showProgressDialog(boolean showProgressDialog) {
            this.showProgressDialog = showProgressDialog;
            return this;
        }

        public Builder resumePoint(boolean resumePoint) {
            this.resumePoint = resumePoint;
            return this;
        }

        public UpdateManager build() {
            return new UpdateManager (this);
        }
    }

    public final static String KEY_COMMON_UPDATE_TIP = "key_common_update_tip_tag";
    public final static String KEY_PRE_VERSION_NAME = "key_pre_version_name_tag";


    public int compareVersionNames(String oldVersionName, String newVersionName) {
        if (TextUtils.isEmpty (newVersionName)) {
            return -1;
        }
        if (TextUtils.isEmpty (oldVersionName)) {
            return 1;
        }
        if (newVersionName.equals (oldVersionName)) {
            return -1;
        }
        int res = 0;

        String[] oldNumbers = oldVersionName.split ("\\.");
        String[] newNumbers = newVersionName.split ("\\.");

        int maxIndex = Math.min (oldNumbers.length, newNumbers.length);

        for (int i = 0; i < maxIndex; i++) {
            int oldVersionPart = 0;
            int newVersionPart = 0;

            try {
                oldVersionPart = Integer.valueOf (oldNumbers[i]);
                newVersionPart = Integer.valueOf (newNumbers[i]);
            } catch (Exception e) {
                e.printStackTrace ();
            }

            if (oldVersionPart < newVersionPart) {
                res = 1;
                break;
            } else if (oldVersionPart > newVersionPart) {
                res = -1;
                break;
            }
        }

        if (res == 0 && oldNumbers.length != newNumbers.length) {
            res = (oldNumbers.length > newNumbers.length) ? -1 : 1;
        }

        return res;
    }


}

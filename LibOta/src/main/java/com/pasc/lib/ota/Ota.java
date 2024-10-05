package com.pasc.lib.ota;

import android.app.Activity;
import android.content.Context;

import com.pasc.lib.ota.callback.IDownLoadRequirement;
import com.pasc.lib.ota.callback.UpdateCallBack;
import com.pasc.lib.ota.dialog.IDialogClick;
import com.pasc.lib.ota.dialog.IProgressDialog;
import com.pasc.lib.ota.dialog.IUpdateDialog;
import com.pasc.lib.ota.helper.DownloadHelper;

/**
 * Created by yintangwen952 on 2018/9/13.
 */

public class Ota {

    private final Context context;
    private final UpdateCallBack updateCallBack;
    private final boolean showNotification;
    private final IUpdateDialog customUpdateDialog;
    private final IBaseUpdate baseUpdate;
    private final IProgressDialog progressDialog;
    private final boolean resumePoint;
    private final String fileSavePathRoo;
    private final int totalLengthFixed;

    private Ota(Builder builder) {
        context = builder.context;
        updateCallBack = builder.updateCallBack;
        showNotification = builder.showNotification;
        customUpdateDialog = builder.customUpdateDialog;
        baseUpdate = builder.baseUpdate;
        progressDialog = builder.progressDialog;
        resumePoint=builder.resumePoint;
        fileSavePathRoo=builder.fileSavePathRoo;
        totalLengthFixed=builder.totalLengthFixed;
    }

    private IDownLoadRequirement requirement;

    public Ota setRequirement(IDownLoadRequirement requirement) {
        this.requirement = requirement;
        return this;
    }

    private boolean requirement(){
        if (requirement!=null){
            return requirement.requirement ();
        }
        return true;
    }

    public void show(){
        if (context == null || baseUpdate == null) {
            return;
        }

        UpdateType updateType = baseUpdate.getUpdateType();
        if (updateType == null) {
            updateType = UpdateType.NoUpdate;
        }

        if (updateType == UpdateType.NoUpdate) {
            return;
        }
        if (updateCallBack != null) {
            updateCallBack.startDownload();
        }
        if (customUpdateDialog != null) {
            customUpdateDialog.setContent(baseUpdate.getDescription());
            customUpdateDialog.setTitle(baseUpdate.getTitle());
            customUpdateDialog.setVersionName (baseUpdate.getVersionName ());
            customUpdateDialog.setCanceledOnTouchOutside(false);
            boolean isForceUpdate=false;
            if (updateType==UpdateType.ForceUpdate){
                isForceUpdate=true;

            }
            customUpdateDialog.updateType (isForceUpdate);
            customUpdateDialog.setCancelable(!isForceUpdate);
            if (progressDialog!=null) {
                progressDialog.setCancelable(!isForceUpdate);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            customUpdateDialog.show();
            customUpdateDialog.setDialogClick(new IDialogClick() {
                @Override
                public void download() {
                    if (!requirement ()){
                        return;
                    }
                    continueDownload ();
                }

                @Override
                public void cancel() {
                    if (customUpdateDialog.isShowing()) {
                        customUpdateDialog.dismiss();
                    }
                    if (updateCallBack != null) {
                        updateCallBack.downloadFail("cancel");
                    }
                }
            });

        } else {
            DownloadHelper.downLoad(context, baseUpdate.getDownloadUrl(),fileSavePathRoo, baseUpdate.getVersionName(),resumePoint, showNotification, progressDialog, totalLengthFixed,updateCallBack);
        }
    }

    public void continueDownload(){
        try {
            if (context==null){
                return;
            }
            if (context instanceof Activity) {
                if (!DownloadHelper.android8InstallCheck((Activity) context)) {
                    return;
                }
            }
            if (customUpdateDialog!=null){
                customUpdateDialog.dismiss();
            }
            if (baseUpdate!=null) {
                DownloadHelper.downLoad (context, baseUpdate.getDownloadUrl (), fileSavePathRoo, baseUpdate.getVersionName (), resumePoint, showNotification, progressDialog,totalLengthFixed, updateCallBack);
            }
        }catch (Exception e){
            e.printStackTrace ();
        }

    }

    public static Builder builder(Context context) {
        return new Builder(context);
    }

    public static class Builder {

        private Context context;
        private IBaseUpdate baseUpdate;
        private IUpdateDialog customUpdateDialog;
        private IProgressDialog progressDialog;
        private boolean showNotification;
        private UpdateCallBack updateCallBack;
        private boolean resumePoint=true; // 是否要断点
        private String fileSavePathRoo; // 存储路径
        private int totalLengthFixed;
        public Builder(Context context) {
            this.context = context;
        }

        public Builder addBaseUpdate(IBaseUpdate baseUpdate) {
            this.baseUpdate = baseUpdate;
            return this;
        }

        public Builder addUpdateDialog(IUpdateDialog customUpdateDialog) {
            this.customUpdateDialog = customUpdateDialog;
            return this;
        }

        public Builder addProgressDialog(IProgressDialog progressDialog) {
            this.progressDialog = progressDialog;
            return this;
        }

        public Builder enableNotification(boolean showNotification) {
            this.showNotification = showNotification;
            return this;
        }

        public Builder callBack(UpdateCallBack updateCallBack) {
            this.updateCallBack = updateCallBack;
            return this;
        }

        public Builder resumePoint(boolean resumePoint){
            this.resumePoint=resumePoint;
            return this;
        }

        public Builder fileSavePathRoo(String fileSavePathRoo){
            this.fileSavePathRoo=fileSavePathRoo;
            return this;
        }

        public Builder totalLengthFixed(int totalLengthFixed){
            this.totalLengthFixed=totalLengthFixed;
            return this;
        }

        public Ota build(){
            return new Ota(this);
        }
    }
}

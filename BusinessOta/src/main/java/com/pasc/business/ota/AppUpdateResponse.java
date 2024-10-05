package com.pasc.business.ota;

import com.google.gson.annotations.SerializedName;
import com.pasc.lib.ota.IBaseUpdate;
import com.pasc.lib.ota.UpdateType;

public class AppUpdateResponse implements IBaseUpdate {
    @SerializedName("versionNo")
    private String versionName;//版本m名

//    @SerializedName("versionCode"), 这个字段已经没有了
//    public int versionCode;//版本code
    @SerializedName("downloadUrl")
    private String downloadUrl;//下载地址
    @SerializedName("description")
    private String description;//更新描述
    @SerializedName("promptType")
    private String prompt;// // 是否强制更新   1 不提示；2 普通更新； 3 强制更新
    @SerializedName("deviceType")
    public String deviceType; //提示频率 1仅首次 2每次启动

    @SerializedName("fileSize")
    public int fileSize; //总大小

    @SerializedName("promptRate")
    private String promptRate;

    public int getFileSize() {
        return fileSize;
    }

    public boolean isAlwaysRate(){
        return "2".equals (promptRate);
    }
    public boolean isOnlyFirst(){
        return "1".equals (promptRate);

    }
    public String getVersionCode() {
        return versionName + "";
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getPromptRate() {
        return promptRate;
    }

    public void setPromptRate(String promptRate) {
        this.promptRate = promptRate;
    }


    @Override
    public UpdateType getUpdateType() {

        if ("1".equals (prompt)) {
            return UpdateType.NoTipsUpdate;
        } else if ("2".equals (prompt)) {
            return UpdateType.CommonUpdate;

        } else if ("3".equals (prompt)) {
            return UpdateType.ForceUpdate;

        }

        return UpdateType.NoUpdate;
    }

    public boolean isForceUpdate() {
        return getUpdateType () == UpdateType.ForceUpdate;
    }

    @Override
    public String getTitle() {
        return "版本更新";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
}

package com.pasc.business.ota;

import com.google.gson.annotations.SerializedName;

/**
 * 校验是否有更新
 * Created by zhangcan603 on 2018年03月09日14:14:54
 */

public class CheckUpdateParam {

    @SerializedName("deviceType")
    public String deviceType;
    @SerializedName("versionNo")
    public String versionNo;

    public CheckUpdateParam(String deviceType, String versionNo) {
        this.deviceType = deviceType;
        this.versionNo = versionNo;
    }
}

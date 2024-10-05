package com.pasc.lib.ota;

/**
 * Copyright (C) 2018 pasc Licensed under the Apache License, Version 2.0 (the "License");
 *
 * @author yangzijian
 * @date 2018/9/3
 * @des
 * @modify
 **/
public interface IBaseUpdate {

    //版本m名
    String getVersionName();
    //版本code
    String getVersionCode();
    //下载地址
    String getDownloadUrl();
    //更新描述
    String getDescription();
    //更新类型
    UpdateType getUpdateType();

    String getTitle();
}

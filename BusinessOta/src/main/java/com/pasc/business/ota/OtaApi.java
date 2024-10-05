package com.pasc.business.ota;

import com.pasc.lib.net.resp.BaseV2Resp;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Copyright (C) 2018 pasc Licensed under the Apache License, Version 2.0 (the "License");
 *
 * @author yangzijian
 * @date 2018/9/5
 * @des
 * @modify
 **/
public interface OtaApi {
    /**
     * 校验是否有更新
     */
//    @POST(OtaUrl.CHECK_VERSION)
    @POST
    Single<BaseV2Resp<AppUpdateResponse>> checkVersion(@Url String url,
                                                       @Body CheckUpdateParam param);
}

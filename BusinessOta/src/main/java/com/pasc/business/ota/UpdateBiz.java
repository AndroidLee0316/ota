package com.pasc.business.ota;

import com.pasc.lib.net.ApiGenerator;
import com.pasc.lib.net.transform.RespV2Transformer;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UpdateBiz {
    /**
     * 检查 app版本
     *
     * @return
     */
    public static Single<AppUpdateResponse> checkVersion(String versionNo) {
        RespV2Transformer<AppUpdateResponse> respTransformer = RespV2Transformer.newInstance();
        return ApiGenerator.createApi(OtaApi.class)
                .checkVersion(OtaManager.instance ().getUrl (),new CheckUpdateParam("1", versionNo))
                .compose(respTransformer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}

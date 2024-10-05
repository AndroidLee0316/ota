package com.pase.lib.ota.debug;

import android.app.Application;

import com.pasc.lib.base.AppProxy;
import com.pasc.lib.net.NetConfig;
import com.pasc.lib.net.NetManager;
import com.pasc.lib.net.download.DownLoadManager;

/**
 * @date 2019/4/29
 * @des
 * @modify
 **/
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate ();
//        AppProxy.getInstance().init(this, false)
//                .setIsDebug(BuildConfig.DEBUG)
//                .setProductType(BuildConfig.PRODUCT_FLAVORS_TYPE)
//                .setUserManager(userManager)
//                .setHost(HiddenDoorManager.getInstance().getApiHost())
//                .setH5Host(HiddenDoorManager.getInstance().getApiHost())
//                .setVersionName(BuildConfig.VERSION_NAME);
    }
    /****初始化网络****/
    private void initNet() {
        // 测试全屏、弹屏广告专用HostUrl
//        NetConfig config = new NetConfig.Builder(this)
//                .baseUrl(HiddenDoorManager.getInstance().getApiHost())
//                .headers(HeaderUtil.getHeaders(BuildConfig.DEBUG, null))
//                .gson(ConvertUtil.getConvertGson())
//                .isDebug(BuildConfig.DEBUG)
//                .build();
//        NetManager.init(config);
//
//        DownLoadManager.getDownInstance().init(this, 3, 5, 0);
    }
}

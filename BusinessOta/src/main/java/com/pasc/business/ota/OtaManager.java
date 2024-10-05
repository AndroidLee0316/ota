package com.pasc.business.ota;

import com.pasc.lib.base.AppProxy;

/**
 * @author yangzijian
 * @date 2018/11/30
 * @des
 * @modify
 **/
public class OtaManager {

    private OtaManager(){}
    private static final class SinglerHolder{
        private static final OtaManager instance=new OtaManager ();
    }
    public abstract static class ApiGet{
        public abstract String getUrl();
    }
    public static OtaManager instance(){
        return SinglerHolder.instance;
    }

    private ApiGet apiGet=new ApiGet () {
        @Override
        public String getUrl() {
            return AppProxy.getInstance ().getHost ()+"/api/platform/appVersion/queryNewVersionInfo";
        }
    };

    public void setApiGet(ApiGet apiGet){
        if (apiGet!=null){
            this.apiGet=apiGet;
        }
    }
    public String getUrl(){
        return apiGet.getUrl ();
    }

}

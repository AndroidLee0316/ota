package com.pase.lib.ota.debug;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pasc.lib.net.download.DownLoadManager;
import com.pasc.lib.ota.IBaseUpdate;
import com.pasc.lib.ota.Ota;
import com.pasc.lib.ota.UpdateType;
import com.pasc.lib.ota.callback.UpdateCallBack;
import com.pasc.lib.ota.dialog.CommonProgressUpdateDialog;
import com.pasc.lib.ota.dialog.CommonUpdateUpdateDialog;
import com.pasc.lib.ota.dialog.IProgressDialog;


public class MainActivity extends AppCompatActivity {


    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1000;
    private static final int GET_UNKNOWN_APP_SOURCES = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownLoadManager.getDownInstance().init(this, 3, 5, 0);
        // todo 特别注意华为手机一定需要sdcard权限，下载后才能安装
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);


    }


    public void viewClick(View view) {
        Ota.builder(this)
                .addBaseUpdate(new MyBean())
                .addUpdateDialog(new CommonUpdateUpdateDialog(this))
                .addProgressDialog(getDownLoadProgressDialog(this))
                .enableNotification(true)
                .fileSavePathRoo (Environment.getExternalStorageDirectory().getAbsolutePath()+"/apk")
                .callBack(new UpdateCallBack() {})
                .build().show();

    }
    /**
     * 展示进度弹窗
     */
    private IProgressDialog getDownLoadProgressDialog(Context context) {
        CommonProgressUpdateDialog mProgressDialog = new CommonProgressUpdateDialog (context);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMessage("下载中...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        return mProgressDialog;
    }
    //final String downLoadUrlTest = "http://gdown.baidu.com/data/wisegame/2c6a60c5cb96c593/QQ_182.apk";
    final String downLoadUrlTest =  "http://isz-cloud.yun.city.pingan.com/cfs/nas/download?bucket=szsc-smt-app-dmz-dev&attKey=897f2a9ac0a544078c86595930312159&attName=autoBeta-official-2.0.0.apk(3)";
    class MyBean implements IBaseUpdate {

        @Override
        public String getVersionName() {
            return "v1.1.1";
        }

        @Override
        public String getVersionCode() {
            return "1.1.1";
        }

        @Override
        public String getDownloadUrl() {
            return downLoadUrlTest;
        }

        @Override
        public String getDescription() {
            return "有版本更新了";
        }

        @Override
        public UpdateType getUpdateType() {
            return UpdateType.CommonUpdate;
        }

        @Override
        public String getTitle() {
            return "版本更新";
        }
    }

}

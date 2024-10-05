package com.pasc.lib.ota.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import android.util.Log;
import com.pasc.lib.ota.R;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yintangwen952 on 2018/9/13.
 */

public class NotificationHelper {
    private final static AtomicInteger atomicInteger = new AtomicInteger(0);
    private static NotificationManager mNotificationManager;
    private static NotificationCompat.Builder mBuilder;
    private static int NOTIFY_ID = 10000001;
    private static ApplicationInfo info;
    private static final String CHANNEL_ID = "app_update_id";
    private static final CharSequence CHANNEL_NAME = "app_update_channel";
    /**
     * 创建通知
     */
    public static void setUpNotification(Context context, boolean showNotify, String tittle, String description) {
        if (context == null) {
            return;
        }

        if (!showNotify) {
            return;
        }
        if (info == null) {
            try {
                 info = context.getPackageManager().getApplicationInfo(context.getPackageName (), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace ();
            }
        }


        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(false);
            channel.enableLights(false);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        }else{
            mBuilder=new NotificationCompat.Builder(context);
        }
        int icon =  R.mipmap.ic_launcher;
        if (info!=null){
            icon=info.icon;
        }
        mBuilder.setContentTitle(tittle)
                .setContentText(description)
                .setSmallIcon(icon)
                .setOngoing(true)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());
        mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
    }
    static long lastUpdateTime;
    private static final long TIME_INTERVAL=2000;
    public static void updateNotify(Context context,int percent) {
      Log.d("notification","percent "+percent);
        if(percent==100||(System.currentTimeMillis()-lastUpdateTime>TIME_INTERVAL)) {
          if (mBuilder != null) {
            mBuilder.setContentTitle("正在下载：" + getAppName(context))
                .setContentText(percent + "%")
                .setProgress(100, percent, false)
                .setWhen(System.currentTimeMillis());
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
            mNotificationManager.notify(NOTIFY_ID, notification);
            lastUpdateTime=System.currentTimeMillis();
          }
        }
    }

    public static void cancelNotify() {
        if (mBuilder != null)
            mNotificationManager.cancel(NOTIFY_ID);
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }
}

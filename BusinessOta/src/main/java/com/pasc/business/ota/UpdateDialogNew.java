package com.pasc.business.ota;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.pasc.lib.base.util.ScreenUtils;
import com.pasc.lib.ota.dialog.IDialogClick;
import com.pasc.lib.ota.dialog.IUpdateDialog;


/**
 * 更新弹窗 by zc  2018年03月26日14:56:00
 */
public class UpdateDialogNew extends Dialog implements IUpdateDialog {
    private TextView tvContent;
    private TextView tvUpdateNow;
    private View ivCancel;
    private View vCenterLine;
    private IDialogClick dialogClick;
    private View ivTitle;
    public UpdateDialogNew(Context context) {
        super (context, R.style.UpdateDialog);
        setContentView (R.layout.ota_update_dialog_new);
        tvContent = findViewById (R.id.tv_content);
        tvUpdateNow = findViewById (R.id.tv_update_now);
        ivCancel = findViewById (R.id.iv_cancel);
        ivTitle=findViewById (R.id.iv_title);
        vCenterLine = findViewById (R.id.v_center_line);
        tvContent.setMaxHeight (ScreenUtils.getScreenHeight () / 3);

        setCanceledOnTouchOutside (false);
        setAttributes ((Activity) context);
        initUpdateListener ();

    }


    void initUpdateListener() {
        tvUpdateNow.setOnClickListener (new View.OnClickListener () {//确认
            @Override
            public void onClick(View v) {
                if (dialogClick != null)
                    dialogClick.download ();
            }
        });

        ivCancel.setOnClickListener (new View.OnClickListener () {//取消
            @Override
            public void onClick(View v) {
                if (dialogClick != null) {
                    dialogClick.cancel ();
                }
            }
        });

    }

    @Override
    public void setTitle(String title) {

    }

    @Override
    public void setContent(String content) {
        tvContent.setText (content);
        tvContent.setMovementMethod (ScrollingMovementMethod.getInstance ());
    }

    @Override
    public void updateType(boolean isForceUpdate) {
        ivCancel.setVisibility (isForceUpdate ? View.GONE : View.VISIBLE);
        vCenterLine.setVisibility (isForceUpdate ? View.GONE : View.VISIBLE);


    }


    @Override
    public void setDialogClick(IDialogClick dialogClick) {
        this.dialogClick = dialogClick;

    }

    @Override
    public void setVersionName(String versionName) {//设置版本名称
    }


    private void setAttributes(Activity activity) {
        Window window = getWindow ();
        WindowManager windowManager = activity.getWindowManager ();
        Display defaultDisplay = windowManager.getDefaultDisplay ();
        WindowManager.LayoutParams params = window.getAttributes ();
        params.width = (int) (defaultDisplay.getWidth () * (270f / 375f));
        window.setAttributes (params);
    }
}
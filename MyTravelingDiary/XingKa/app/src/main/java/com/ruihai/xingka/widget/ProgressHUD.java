package com.ruihai.xingka.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.ruihai.xingka.R;

public class ProgressHUD {
    private static ProgressHUDDialog dialog;
    private static Context context;
    private static SimpleHUDCallback callback;

    public static int dismissDelay = ProgressHUD.DISMISS_DELAY_LOW;
    public static final int DISMISS_DELAY_LOW = 1 * 1000;
    public static final int DISMISS_DELAY_SHORT = 2 * 1000;
    public static final int DISMISS_DELAY_MIDIUM = 4 * 1000;
    public static final int DISMISS_DELAY_LONG = 6 * 1000;

    public static void showLoadingMessage(Context context, String msg, boolean cancelable, SimpleHUDCallback callback) {
        ProgressHUD.callback = callback;
        showLoadingMessage(context, msg, cancelable);
    }

    public static void showLoadingMessage(Context context, String msg, boolean cancelable) {
        dismiss();
        setDialog(context, msg, R.mipmap.progresshud_spinner, cancelable);
        if (dialog != null) dialog.show();
    }


    public static void showErrorMessage(Context context, String msg, SimpleHUDCallback callback) {
        ProgressHUD.callback = callback;
        showErrorMessage(context, msg);
    }

    public static void showErrorMessage(Context context, String msg) {
        dismiss();
        setDialog(context, msg, R.mipmap.progresshud_error, true);
        if (dialog != null) {
            dialog.show();
            dismissAfterSeconds();
        }
    }


    public static void showSuccessMessage(Context context, String msg, SimpleHUDCallback callback) {
        ProgressHUD.callback = callback;
        showSuccessMessage(context, msg);
    }

    public static void showSuccessMessage(Context context, String msg) {
        dismiss();
        setDialog(context, msg, R.mipmap.progresshud_success, true);
        if (dialog != null) {
            dialog.show();
            dismissAfterSeconds();
        }
    }

    public static void showPraiseOrCollectSuccessMessage(Context context, String msg) {
        dismiss();
        setDialog(context, msg, R.mipmap.progresshud_smile, true);
        if (dialog != null) {
            dialog.show();
            dismissAfterSeconds();
        }
    }

    public static void showInfoMessage(Context context, String msg, SimpleHUDCallback callback) {
        ProgressHUD.callback = callback;
        showInfoMessage(context, msg);
    }

    public static void showInfoMessage(Context context, String msg) {
        dismiss();
        setDialog(context, msg, R.mipmap.progresshud_warnning_info, true);
        if (dialog != null) {
            dialog.show();
            dismissAfterSeconds();
        }
    }


    private static void setDialog(Context ctx, String msg, int resId, boolean cancelable) {
        context = ctx;

        if (!isContextValid())
            return;

        dialog = ProgressHUDDialog.createDialog(ctx);
        dialog.setMessage(msg);
        dialog.setImage(ctx, resId);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(cancelable);        // back键是否可dimiss对话框
    }


    public static void dismiss() {
        if (isContextValid() && dialog != null && dialog.isShowing())
            dialog.dismiss();
        dialog = null;
    }

    /**
     * 计时关闭对话框
     */
    private static void dismissAfterSeconds() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(dismissDelay);
                    handler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 计时关闭对话框,带参数
     */
    private static void dismissAfterSeconds(final int dismissDelay) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(dismissDelay);
                    handler.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                dismiss();
                if (ProgressHUD.callback != null) {
                    callback.onSimpleHUDDismissed();
                    callback = null;
                }
            }
        }

        ;
    };

    /**
     * 判断parent view是否还存在
     * 若不存在不能调用dismis，或setDialog等方法
     *
     * @return
     */
    private static boolean isContextValid() {
        if (context == null)
            return false;
        if (context instanceof Activity) {
            Activity act = (Activity) context;
            if (act.isFinishing())
                return false;
        }
        return true;
    }

    public static interface SimpleHUDCallback {
        public void onSimpleHUDDismissed();
    }
}

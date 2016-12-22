package com.ruihai.xingka.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruihai.xingka.R;

class ProgressHUDDialog extends Dialog {

    public ProgressHUDDialog(Context context, int theme) {
        super(context, theme);
    }

    public static ProgressHUDDialog createDialog(Context context) {
        ProgressHUDDialog dialog = new ProgressHUDDialog(context, R.style.ProgressHUD);
        dialog.setContentView(R.layout.progresshud);

        dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        return dialog;
    }

    public void setMessage(String message) {
        TextView msgView = (TextView) findViewById(R.id.simplehud_message);
        msgView.setText(message);
    }

    public void setImage(Context ctx, int resId) {
        ImageView image = (ImageView) findViewById(R.id.simplehud_image);
        image.setImageResource(resId);

        if (resId == R.mipmap.progresshud_spinner) {
            Animation anim = AnimationUtils.loadAnimation(ctx, R.anim.progressbar);
            anim.start();
            image.startAnimation(anim);
        }
    }


}

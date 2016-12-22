package com.ruihai.xingka.widget;

import android.app.Dialog;
import android.content.Context;
import android.widget.LinearLayout;

import static com.mob.tools.utils.R.getStyleRes;

public class CommonDialog {
	/**加载对话框*/
	public static final Dialog ProgressDialog(Context context){
		int resId = getStyleRes(context, "CommonDialog");
		if (resId > 0) {
			final Dialog dialog = new Dialog(context, resId);
			LinearLayout layout = ProgressDialogLayout.create(context);
			if (layout != null) {
				dialog.setContentView(layout);
				return dialog;
			}
		}
		return null;
	}

}

package com.ruihai.xingka.widget;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**进度dialog*/
public class ProgressDialogLayout {

	public static LinearLayout create(Context context) {
		LinearLayout root = new LinearLayout(context);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		root.setLayoutParams(params);
		root.setOrientation(LinearLayout.VERTICAL);

		ProgressBar bar = new ProgressBar(context);
		LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		bar.setLayoutParams(barParams);
		SizeHelper.prepare(context);
		int padding = SizeHelper.fromPxWidth(20);
		bar.setPadding(padding, padding, padding, padding);

		root.addView(bar);

		return root;
	}
}

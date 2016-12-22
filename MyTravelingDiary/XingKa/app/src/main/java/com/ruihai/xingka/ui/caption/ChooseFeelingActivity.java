package com.ruihai.xingka.ui.caption;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.caption.adapter.FeelingAdapter;

/**
 * 图说心情选择页
 */
public class ChooseFeelingActivity extends Activity {
    EditText input;
    GridView gridView;

    int[] face = {};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_feeling);

        gridView=(GridView)findViewById(R.id.gridview);
        input=(EditText)findViewById(R.id.et_search_key);

        final FeelingAdapter adapter = new FeelingAdapter(this,face);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                for (int i = 0; i < parent.getCount(); i++) {
                    View view = parent.getChildAt(i);

                    if (position == i) {
                        //当前选中的Item改变背景颜色
                        view.setBackgroundResource(R.mipmap.face_checked);
                    } else {
//                        view.setBackgroundResource(0);
                    }
                }

                input.setText("当前选择的表情是:" + (position + 1));

//             SpannableString ss = new SpannableString("face");
//             Drawable d = getResources().getDrawable(position);
//             d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//             ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
//             ss.setSpan(span,0,1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//             input.getEditableText().insert(input.getSelectionStart(), ss);
            }
        });

    }

}

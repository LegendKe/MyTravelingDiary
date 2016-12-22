package com.ruihai.xingka.ui.mine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.DraftBoxItem;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.caption.CaptionAddActivity;
import com.ruihai.xingka.ui.mine.adapter.DraftBoxAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 草稿箱
 * <p/>
 * Created by mac on 15/9/29.
 */
public class DraftBoxActivity extends BaseActivity {

    public static void launch(Activity from) {
        Intent intent = new Intent(from, DraftBoxActivity.class);
        from.startActivity(intent);
    }

    @BindView(R.id.tv_title)
    TextView mTitleView;

    @BindView(R.id.tv_right)
    IconicFontTextView mRightView;
    @BindView(R.id.list)
    ListView listView;

    private DraftBoxAdapter adapter;
    private List<DraftBoxItem> draftBoxItems = new ArrayList<>();
    private View emptyView;//内容为空的视图

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draftbox);
        ButterKnife.bind(this);
        //emptyView = LayoutInflater.from(this).inflate(R.layout.profile_list_load_empty, null);
        View emptyLayout = findViewById(R.id.empty_layout);
        TextView emptyText = (TextView) findViewById(R.id.textView1);
        emptyText.setText("草稿箱没有内容哦");
        listView.setEmptyView(emptyLayout);
        initViews();
        setAllListenner();
    }

    private void setAllListenner() {

    }

    private void initViews() {
        mTitleView.setText(R.string.title_draft);
        if (AccountInfo.getInstance().getUserDraftBoxInfo() != null) {
            mRightView.setVisibility(View.VISIBLE);
            mRightView.setText("{xk-close}");
        } else {
            mRightView.setVisibility(View.GONE);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, CaptionAddActivity.class);
                intent.putExtra("FROM_DRAFTBOX_FLAG", true);
                mContext.startActivity(intent);
            }
        });

        adapter = new DraftBoxAdapter(this);
        listView.setAdapter(adapter);
        adapter.notifyDataChanged(AccountInfo.getInstance().getUserDraftBoxInfo(), false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataChanged(AccountInfo.getInstance().getUserDraftBoxInfo(), true);
        if (AccountInfo.getInstance().getUserDraftBoxInfo() != null) {
            mRightView.setVisibility(View.VISIBLE);
            mRightView.setText("{xk-close}");
        } else {
            mRightView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }

    @OnClick(R.id.tv_right)
    void Delete() {
        showDelDialog();

    }

    private void delete(DraftBoxItem draftBoxItem) {
        // delete
        AccountInfo.getInstance().clearUserDraftBoxInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showDelDialog() {
        // 1. 布局文件转换为View对象
        LayoutInflater inflater = LayoutInflater.from(DraftBoxActivity.this);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.dialog_communal, null);
        final Dialog dialog = new AlertDialog.Builder(DraftBoxActivity.this).create();

        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(layout);

        // 3. 消息内容
        TextView dialog_msg = (TextView) layout.findViewById(R.id.update_content);
        dialog_msg.setText("确定清空草稿箱吗?");

        // 4. 确定按钮
        Button btnOK = (Button) layout.findViewById(R.id.umeng_update_id_ok);
        btnOK.setText("确定");
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountInfo.getInstance().clearUserDraftBoxInfo();
                if (AccountInfo.getInstance().getUserDraftBoxInfo() != null) {
                    mRightView.setVisibility(View.VISIBLE);
                    mRightView.setText("{xk-close}");
                } else {
                    mRightView.setVisibility(View.GONE);
                }
                adapter.notifyDataChanged(AccountInfo.getInstance().getUserDraftBoxInfo(), true);
                dialog.dismiss();
            }
        });

        // 5. 取消按钮
        Button btnCancel = (Button) layout.findViewById(R.id.umeng_update_id_cancel);
        btnCancel.setText("取消");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


}

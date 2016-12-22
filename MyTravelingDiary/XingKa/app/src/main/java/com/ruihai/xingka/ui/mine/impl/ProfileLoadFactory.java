package com.ruihai.xingka.ui.mine.impl;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.utils.AppUtility;
import com.shizhefei.mvc.ILoadViewFactory;
import com.shizhefei.view.vary.VaryViewHelper;

/**
 * Created by mac on 16/1/22.
 */
public class ProfileLoadFactory implements ILoadViewFactory {
    private String emptyText,emptyText1;

    public ProfileLoadFactory(String emptyText,String emptyText1) {
        this.emptyText = emptyText;
        this.emptyText1 = emptyText1;
    }

    @Override
    public ILoadMoreView madeLoadMoreView() {
        return new LoadMoreHelper();
    }

    @Override
    public ILoadView madeLoadView() {
        return new LoadViewHelper();
    }

    private class LoadMoreHelper implements ILoadMoreView {
        protected TextView footView;
        protected View.OnClickListener onClickLoadMoreListener;

        @Override
        public void init(FootViewAdder footViewHolder, View.OnClickListener onClickLoadMoreListener) {
            footView = (TextView) footViewHolder.addFootView(R.layout.layout_listview_foot);
            this.onClickLoadMoreListener = onClickLoadMoreListener;
            showNormal();
        }

        @Override
        public void showNormal() {
            footView.setText("点击加载更多");
            footView.setOnClickListener(onClickLoadMoreListener);
        }

        @Override
        public void showNomore() {
            footView.setText("已经加载完毕");
            footView.setOnClickListener(null);
        }

        @Override
        public void showLoading() {
            footView.setText("正在加载中...");
            footView.setOnClickListener(null);
        }

        @Override
        public void showFail(Exception e) {
            footView.setText("加载失败，点击重新加载");
            footView.setOnClickListener(onClickLoadMoreListener);
        }
    }

    private class LoadViewHelper implements ILoadView {
        private VaryViewHelper helper;
        private View.OnClickListener onClickRefreshListener;
        private Context context;

        @Override
        public void init(View switchView, View.OnClickListener onClickRefreshListener) {
            this.context = switchView.getContext().getApplicationContext();
            this.onClickRefreshListener = onClickRefreshListener;
            this.helper = new VaryViewHelper(switchView);
        }

        @Override
        public void showLoading() {
            View layout = helper.inflate(R.layout.profile_list_load_ing);
            TextView textView = (TextView) layout.findViewById(R.id.textView1);
            textView.setText("加载中...");
            helper.showLayout(layout);
        }

        @Override
        public void showFail(Exception e) {
            View layout = helper.inflate(R.layout.load_error);
            layout.setOnClickListener(onClickRefreshListener);
            helper.showLayout(layout);
        }

        @Override
        public void showEmpty() {
            View layout = helper.inflate(R.layout.profile_list_load_empty);
//            FrameLayout emptyLayout = (FrameLayout) layout.findViewById(R.id.empty_layout);
//            emptyLayout.setOnClickListener(onClickRefreshListener);
            TextView textView = (TextView) layout.findViewById(R.id.textView1);
            textView.setText(emptyText);
//            TextView button = (TextView) layout.findViewById(R.id.button1);
//            button.setText("点击刷新");
            TextView textView1 = (TextView) layout.findViewById(R.id.btn_add_friend);
            textView1.setVisibility(View.VISIBLE);
            textView1.setText(emptyText1);

            ImageView button = (ImageView) layout.findViewById(R.id.iv_refress);
            button.setOnClickListener(onClickRefreshListener);
            helper.showLayout(layout);
        }

        @Override
        public void tipFail(Exception e) {
            AppUtility.showToast(context.getString(R.string.common_network_error));
        }

        @Override
        public void restore() {
            helper.restoreView();
        }
    }
}

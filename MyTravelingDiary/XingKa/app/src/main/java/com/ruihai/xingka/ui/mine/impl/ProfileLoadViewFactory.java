package com.ruihai.xingka.ui.mine.impl;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ruihai.xingka.R;
import com.ruihai.xingka.utils.AppUtility;
import com.shizhefei.mvc.ILoadViewFactory;
import com.shizhefei.view.vary.VaryViewHelper;

/**
 * Created by zecker on 15/11/13.
 */
public class ProfileLoadViewFactory implements ILoadViewFactory {
    private String emptyText;

    public ProfileLoadViewFactory(String emptyText) {
        this.emptyText = emptyText;
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
        protected LinearLayout footView;
        protected TextView textView;
        protected ProgressBar progressBar;
        protected View.OnClickListener onClickLoadMoreListener;

        @Override
        public void init(FootViewAdder footViewHolder, View.OnClickListener onClickLoadMoreListener) {
            footView = (LinearLayout) footViewHolder.addFootView(R.layout.layout_listview_foot);
            textView = (TextView) footView.findViewById(R.id.textView);
            progressBar = (ProgressBar) footView.findViewById(R.id.progress_small);
            this.onClickLoadMoreListener = onClickLoadMoreListener;
            showNormal();
        }

        @Override
        public void showNormal() {
            progressBar.setVisibility(View.GONE);
            textView.setText("点击加载更多");
            footView.setOnClickListener(onClickLoadMoreListener);
        }

        @Override
        public void showNomore() {
            progressBar.setVisibility(View.GONE);
            textView.setText("已经加载完毕");
            footView.setOnClickListener(null);
        }

        @Override
        public void showLoading() {
            progressBar.setVisibility(View.VISIBLE);
            textView.setText("正在加载中...");
            footView.setOnClickListener(null);
        }

        @Override
        public void showFail(Exception e) {
            progressBar.setVisibility(View.GONE);
            textView.setText("加载失败，点击重新加载");
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
//            TextView textView1 = (TextView) layout.findViewById(R.id.btn_add_friend);

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

package com.ruihai.xingka.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ruihai.xingka.R;
import com.ruihai.xingka.ui.caption.CaptionAddActivity;
import com.ruihai.xingka.ui.trackway.TrackWayAddActivity;
import com.ruihai.xingka.utils.AnimationHelper;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.KickBackAnimator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoreDialogFragment extends DialogFragment implements View.OnClickListener {

    @BindView(R.id.root_layout)
    FrameLayout mRootView;
    @BindView(R.id.action_layout)
    LinearLayout mActionLayout;
    @BindView(R.id.center_close)
    ImageView mCloseView;

    private View[] itemViews;

    private Animation mRotatePlusAnim;
    private Animation mRotateCloseAnim;

    private Handler mHandler = new Handler();

    private OnFragmentInteractionListener mListener;

    public static MoreDialogFragment newInstance() {
        MoreDialogFragment fragment = new MoreDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);
        mRotatePlusAnim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_around_plus);
        mRotateCloseAnim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_around_close);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.fragment_more_dialog, container, false);
        ButterKnife.bind(this, view);

        initItems(view);
        showAnimation(mActionLayout);
//        Blurry.with(getActivity()).radius(10).sampling(8).onto(mRootView);
        return view;
    }

    /**
     * 初始化PopupWindow上的按钮
     *
     * @param rootView
     */
    private void initItems(View rootView) {
        int[] viewIds = new int[]{
                R.id.more_caption,
                R.id.more_take_picture
        };
        itemViews = new View[viewIds.length];

        for (int i = 0; i < viewIds.length; i++) {
            int id = viewIds[i];
            itemViews[i] = rootView.findViewById(id);
            itemViews[i].setOnClickListener(this);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 点击空白区域关闭
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopup();
            }
        });
        // 关闭按钮
        mCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopup();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        final int viewId = v.getId();
        //背景动画
        mRootView.startAnimation(AnimationHelper.createPopupBgFadeOutAnim(AnimationHelper.TIME_OUT_CLICK));
        //动画结束时隐藏popupWindow
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissPopup();
                //动画结束时响应点击事件
                handleEvent(viewId);
            }
        }, AnimationHelper.TIME_OUT_CLICK + 10);

        // 按钮动画
        for (View item : itemViews) {
            if (item.getId() == v.getId()) {
                // 点击的按钮，放大
                item.startAnimation(AnimationHelper.createPopupItemBiggerAnim(getContext()));
            } else {
                //其它按钮，缩小
                item.startAnimation(AnimationHelper.createPopupItemSmallerAnim(getContext()));
            }
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                super.onBackPressed();
                dialogDismiss();
            }
        };
    }

    /**
     * 按钮的点击事件
     *
     * @param viewId
     */
    private void handleEvent(int viewId) {
        switch (viewId) {
            case R.id.more_caption: { // 图说
                startActivity(new Intent(getContext(), CaptionAddActivity.class));
                break;
            }
            case R.id.more_take_picture: { // 相机
                Intent intent = new Intent(getContext(), TrackWayAddActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    private void showAnimation(ViewGroup layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            final View child = layout.getChildAt(i);
            if (child.getId() == R.id.center_close) {
                continue;
            }
            child.setOnClickListener(this);
            child.setVisibility(View.INVISIBLE);
            mHandler.postDelayed(new Runnable() {

                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void run() {
                    child.setVisibility(View.VISIBLE);
                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 600, 0);
                    fadeAnim.setDuration(300);
                    KickBackAnimator kickAnimator = new KickBackAnimator();
                    kickAnimator.setDuration(150);
                    fadeAnim.setEvaluator(kickAnimator);
                    fadeAnim.start();
                }
            }, i * 100);
        }
        mCloseView.startAnimation(mRotatePlusAnim);
        mRotatePlusAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mCloseView.setImageDrawable(getResources().getDrawable(R.drawable.selector_tab_close));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void closeAnimation(ViewGroup layout) {
        //防止重复点击
        AppUtility.isFastClick();
        for (int i = 0; i < layout.getChildCount(); i++) {
            final View child = layout.getChildAt(i);
            if (child.getId() == R.id.center_close) {
                continue;
            }
            child.setOnClickListener(this);
            mHandler.postDelayed(new Runnable() {

                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void run() {
                    child.setVisibility(View.VISIBLE);
                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 0, 600);
                    fadeAnim.setDuration(200);
                    KickBackAnimator kickAnimator = new KickBackAnimator();
                    kickAnimator.setDuration(100);
                    fadeAnim.setEvaluator(kickAnimator);
                    fadeAnim.start();
                    fadeAnim.addListener(new Animator.AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            child.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }
                    });
                }
            }, (layout.getChildCount() - i - 1) * 30);

            if (child.getId() == R.id.more_caption) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }, (layout.getChildCount() - i) * 30 + 80);
            }
        }
    }

    public void dialogDismiss() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
        dismiss();
    }

    /**
     * 关闭PopupWindow
     */
    public void dismissPopup() {
        closeAnimation(mActionLayout);
        mCloseView.startAnimation(mRotateCloseAnim);
        dialogDismiss();
    }
}

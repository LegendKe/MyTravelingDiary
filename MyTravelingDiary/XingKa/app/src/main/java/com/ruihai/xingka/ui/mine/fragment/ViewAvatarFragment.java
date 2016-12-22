package com.ruihai.xingka.ui.mine.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ruihai.xingka.R;
import com.ruihai.xingka.utils.glide.GlideHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewAvatarFragment extends DialogFragment {

    private static final long PROGRESS_DELAY = 300L;
    private static final String ARG_USER_AVATAR_KEY = "user_avatar_key";
    public static final String DEFAULT_AVATAR_KEY = "00000000-0000-0000-0000-000000000000";

    @BindView(R.id.root_layout)
    RelativeLayout mRootLayout;
    @BindView(R.id.iv_avatar)
    ImageView mAvatarView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    protected String mAvatarKey;

    public static ViewAvatarFragment newInstance(String avatarKey) {
        ViewAvatarFragment fragment = new ViewAvatarFragment();

        Bundle args = new Bundle();
        args.putString(ARG_USER_AVATAR_KEY, avatarKey);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);

        mAvatarKey = savedInstanceState == null ? getArguments().getString(ARG_USER_AVATAR_KEY)
                : savedInstanceState.getString(ARG_USER_AVATAR_KEY);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_avatar, container, false);
        ButterKnife.bind(this, view);


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mProgressBar.animate().setStartDelay(PROGRESS_DELAY).alpha(1f);

        mAvatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        // 加载头像大图
        if (!DEFAULT_AVATAR_KEY.equals(mAvatarKey)) {
            GlideHelper.loadFullImageWithKey(mAvatarKey, mAvatarView, new GlideHelper.ImageLoadingListener() {
                @Override
                public void onLoaded() {
                    mProgressBar.animate().cancel();
                    mProgressBar.animate().alpha(0f);
                }

                @Override
                public void onFailed() {
                    mProgressBar.animate().alpha(0f);
                }
            });
        } else {
            GlideHelper.loadResource(R.mipmap.default_avatar, mAvatarView);
            mProgressBar.animate().cancel();
            mProgressBar.animate().alpha(0f);
            // mAvatarView.setBackground(XKApplication.getInstance().getResources().getDrawable(R.mipmap.default_avatar));
        }

    }
}

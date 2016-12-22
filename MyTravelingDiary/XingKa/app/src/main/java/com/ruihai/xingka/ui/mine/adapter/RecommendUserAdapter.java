package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.RecommendInfo;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.MainActivity;
import com.ruihai.xingka.ui.mine.UserProfileActivity;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ProgressHUD;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by apple on 15/9/11.
 */
public class RecommendUserAdapter extends BaseAdapter {

    public static final String DEFAULT_AVATAR_KEY = "00000000-0000-0000-0000-000000000000";

    List<RecommendInfo> recommendInfoList = new ArrayList<RecommendInfo>();
    Context context;
    private String mMyAccount;

    public RecommendUserAdapter(Context context, List<RecommendInfo> recommendInfoList) {
        this.context = context;
        this.recommendInfoList = recommendInfoList;
        mMyAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
    }

    @Override
    public int getCount() {
        if (null != recommendInfoList) {
            return recommendInfoList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return recommendInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_recommend_user, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        Uri imageUri = Uri.parse(QiniuHelper.getThumbnail96Url(recommendInfoList.get(position).getImg()));
        if (!DEFAULT_AVATAR_KEY.equals(recommendInfoList.get(position).getImg())) {
            holder.mHeadPortrait.setImageURI(imageUri);
        }

        holder.mHeadPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = String.valueOf(recommendInfoList.get(position).getAccount());
                if (mMyAccount.equals(account)) {
                    if (MainActivity.currentTabIndex!=3) {
                        MainActivity.launch(context, 1);
                    }
                } else {
                    UserProfileActivity.launch(context, account, 1,0);//跳转到用户主页
                }
//                UserProfileActivity.launch(context, account);//跳转到用户主页
            }
        });
        holder.mNickName.setText(recommendInfoList.get(position).getNickName());
        holder.mNickName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = String.valueOf(recommendInfoList.get(position).getAccount());
                if (mMyAccount.equals(account)) {
                    if (MainActivity.currentTabIndex !=3) {
                        MainActivity.launch(context, 1);
                    }
                } else {
                    UserProfileActivity.launch(context, account, 1,0);//跳转到用户主页
                }
//                UserProfileActivity.launch(context, account);//跳转到用户主页
            }
        });
        holder.mVip.setVisibility(View.GONE);

        if (recommendInfoList.get(position).getAccount() == AccountInfo.getInstance().loadAccount().getAccount()) {
            holder.mAttentionTv.setVisibility(View.GONE);

        } else {
            if (recommendInfoList.get(position).getIsfriend()) {
                holder.mAttentionTv.setSelected(true);
                holder.mAttentionTv.setText("已关注");
                holder.mAttentionTv.setTextColor(context.getResources().getColor(R.color.gray_text_color));
            } else {
                holder.mAttentionTv.setVisibility(View.VISIBLE);
                holder.mAttentionTv.setSelected(false);
                holder.mAttentionTv.setTextColor(context.getResources().getColor(R.color.orange));
                holder.mAttentionTv.setText(R.string.caption_add_follow);
                holder.mAttentionTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (AppUtility.isFastClick()) {//判断是否连续点击
                            return;
                        }
                        holder.mAttentionTv.setSelected(true);
                        holder.mAttentionTv.setText("已关注");
                        recommendInfoList.get(position).setIsfriend(true);
                        holder.mAttentionTv.setTextColor(context.getResources().getColor(R.color.gray_text_color));
                        holder.mAttentionTv.setClickable(false);
                        //加关注
                        ApiModule.apiService().addFriend(Security.aesEncrypt(AccountInfo.getInstance().loadAccount().getAccount() + ""),
                                Security.aesEncrypt(recommendInfoList.get(position).getAccount() + "")).enqueue(new Callback<XKRepo>() {
                            @Override
                            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                                XKRepo xkRepo = response.body();
                                String msg = xkRepo.getMsg();
                                if (xkRepo.isSuccess()) { // 登录成功
                                    AppUtility.showToast(msg);
                                } else { // 登录失败
                                    ProgressHUD.showErrorMessage(context, msg);
                                }
                            }

                            @Override
                            public void onFailure(Call<XKRepo> call, Throwable t) {
                                ProgressHUD.showErrorMessage(context, t.getLocalizedMessage());
                                holder.mAttentionTv.setSelected(false);
                                holder.mAttentionTv.setText(R.string.caption_add_follow);
                                recommendInfoList.get(position).setIsfriend(false);
                                holder.mAttentionTv.setTextColor(context.getResources().getColor(R.color.orange));
                                holder.mAttentionTv.setClickable(true);
                            }

                        });
                    }
                });

            }


        }
        return convertView;
    }


    class ViewHolder {
        @BindView(R.id.iv_head_portrait)
        SimpleDraweeView mHeadPortrait;
        @BindView(R.id.tv_nickname)
        TextView mNickName;
        @BindView(R.id.iv_vip)
        ImageView mVip;
        @BindView(R.id.tv_attention)
        TextView mAttentionTv;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}

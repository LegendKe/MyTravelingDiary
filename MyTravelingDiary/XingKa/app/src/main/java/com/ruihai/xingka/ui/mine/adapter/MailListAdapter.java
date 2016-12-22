package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.AddInfo;
import com.ruihai.xingka.api.model.MaybeInfo;
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
 * Created by apple on 15/9/7.
 */
public class MailListAdapter extends BaseAdapter {

    public static final String DEFAULT_AVATAR_KEY = "00000000-0000-0000-0000-000000000000";

    private Context context;
    List<AddInfo> addInfoList = new ArrayList<AddInfo>();
    List<MaybeInfo> maybeInfoList = new ArrayList<MaybeInfo>();
    int status;
    private String mMyAccount;

    public MailListAdapter(Context context, List<AddInfo> addInfoList, List<MaybeInfo> maybeInfoList, int status) {
        this.context = context;
        this.addInfoList = addInfoList;
        this.maybeInfoList = maybeInfoList;
        this.status = status;
        mMyAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
    }

    @Override
    public int getCount() {
        if (null != addInfoList) {
            return addInfoList.size();
        }
        return maybeInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        if (null != addInfoList) {
            return addInfoList.get(position);
        }
        return maybeInfoList.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_mail_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        if (status == 1) {
            holder.mHeadPortrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String account = String.valueOf(addInfoList.get(position).getAccount());
                    if (mMyAccount.equals(account)) {
                        if (MainActivity.currentTabIndex != 3) {
                            MainActivity.launch(context, 1);
                        }
                    } else {
                        UserProfileActivity.launch(context, account, 1, 0);
                    }
//                    UserProfileActivity.launch(context, account);
                }
            });
            Uri imageUri = Uri.parse(QiniuHelper.getThumbnail96Url(addInfoList.get(position).getImg()));
//            Log.e("TAG","--->手机通讯录"+addInfoList.get(position).getImg());
            if (!DEFAULT_AVATAR_KEY.equals(addInfoList.get(position).getImg())) {
                holder.mHeadPortrait.setImageURI(imageUri);
            }

            String nickName = addInfoList.get(position).getNick();
            if (TextUtils.isEmpty(nickName)) {
                holder.mNickName.setText(String.valueOf(addInfoList.get(position).getAccount()));
            } else {
                holder.mNickName.setText(nickName);
            }
            holder.mNickName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String account = String.valueOf(addInfoList.get(position).getAccount());
                    if (mMyAccount.equals(account)) {
                        if (MainActivity.currentTabIndex != 3) {
                            MainActivity.launch(context, 1);
                        }
                    } else {
                        UserProfileActivity.launch(context, account, 1, 0);
                    }
//                    UserProfileActivity.launch(context, account);
                }
            });
            holder.mInfoTv.setText("手机通讯录：" + addInfoList.get(position).getName());
            // if (!addInfoList.get(position).isFriend()) {
            holder.mAttentionBtn.setText("+ 关注");
            holder.mAttentionBtn.setClickable(true);
            holder.mAttentionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.mAttentionBtn.setSelected(true);
                    holder.mAttentionBtn.setText("已关注");
                    holder.mAttentionBtn.setClickable(false);
                    ApiModule.apiService().addFriend(Security.aesEncrypt(AccountInfo.getInstance().loadAccount().getAccount() + ""),
                            Security.aesEncrypt(addInfoList.get(position).getAccount() + "")).enqueue(new Callback<XKRepo>() {
                        @Override
                        public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                            XKRepo xkRepo = response.body();
                            String msg = xkRepo.getMsg();

                            if (xkRepo.isSuccess()) { // 关注成功
                                AppUtility.showToast(msg);
                            } else { // 关注失败
                                ProgressHUD.showErrorMessage(context, msg);
                            }
                        }

                        @Override
                        public void onFailure(Call<XKRepo> call, Throwable t) {
                            ProgressHUD.showErrorMessage(context, t.getLocalizedMessage());
                        }

                    });
                }
            });
            //}


        } else {
            holder.mHeadPortrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String account = String.valueOf(maybeInfoList.get(position).getAccount());
                    if (mMyAccount.equals(account)) {
                        if (MainActivity.currentTabIndex != 3) {
                            MainActivity.launch(context, 1);
                        }
                    } else {
                        UserProfileActivity.launch(context, account, 1, 0);
                    }
//                    UserProfileActivity.launch(context, account);
                }
            });
            Uri imageUri = Uri.parse(QiniuHelper.getThumbnail96Url(maybeInfoList.get(position).getImg()));
            if (!DEFAULT_AVATAR_KEY.equals(maybeInfoList.get(position).getImg())) {
                holder.mHeadPortrait.setImageURI(imageUri);
            }
            holder.mNickName.setText(maybeInfoList.get(position).getNick());
            holder.mNickName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String account = String.valueOf(maybeInfoList.get(position).getAccount());
                    if (mMyAccount.equals(account)) {
                        if (MainActivity.currentTabIndex != 3) {
                            MainActivity.launch(context, 1);
                        }
                    } else {
                        UserProfileActivity.launch(context, account, 1, 0);
                    }
//                    UserProfileActivity.launch(context, account);
                }
            });
            holder.mInfoTv.setText("共同好友：" + maybeInfoList.get(position).getCommonFriend());
            holder.mAttentionBtn.setText("+ 关注");
            holder.mAttentionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.mAttentionBtn.setSelected(true);
                    holder.mAttentionBtn.setText("已关注");
                    holder.mAttentionBtn.setClickable(false);
                    ApiModule.apiService().addFriend(Security.aesEncrypt(AccountInfo.getInstance().loadAccount().getAccount() + ""),
                            Security.aesEncrypt(maybeInfoList.get(position).getAccount() + "")).enqueue(new Callback<XKRepo>() {
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
                        }

                    });
                }
            });
        }
        return convertView;
    }

    class ViewHolder {

        @BindView(R.id.iv_head_portrait)
        SimpleDraweeView mHeadPortrait;
        @BindView(R.id.tv_nickname)
        TextView mNickName;
        @BindView(R.id.tv_info)
        TextView mInfoTv;
        @BindView(R.id.btn_add_follow)
        IconicFontTextView mAttentionBtn;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}

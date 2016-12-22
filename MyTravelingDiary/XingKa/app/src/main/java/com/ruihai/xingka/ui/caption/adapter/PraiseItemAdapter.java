package com.ruihai.xingka.ui.caption.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
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
import com.ruihai.xingka.api.model.PraiseItem;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.event.OnItemClickListener;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.QiniuHelper;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.utils.ViewHolder;
import com.ruihai.xingka.widget.ProgressHUD;
import com.shizhefei.mvc.IDataAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 点赞列表页面适配器
 * Created by gjzhang on 16/1/4.
 */
public class PraiseItemAdapter extends BaseAdapter implements IDataAdapter<List<PraiseItem>> {
    private List<PraiseItem> mData = new ArrayList<>();
    private LayoutInflater inflater;
    private String mAccount;
    private Context mContext;
    public OnItemClickListener mOnItemClickListener;

    public static final String DEFAULT_AVATAR_KEY = "00000000-0000-0000-0000-000000000000";

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public PraiseItemAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        mAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.adapter_attention_friend, null);

        final PraiseItem info = mData.get(position);
        //图像
        SimpleDraweeView mHead = ViewHolder.get(convertView, R.id.sdv_avatar);
        Uri imageUri = Uri.parse(QiniuHelper.getThumbnail96Url(info.getAvatar()));
        if (!DEFAULT_AVATAR_KEY.equals(info.getAvatar())) {
            mHead.setImageURI(imageUri);
        }else {
            mHead.setImageURI(Uri.parse("res:///" + R.mipmap.default_avatar));
        }
        //昵称
        TextView mNick = ViewHolder.get(convertView, R.id.tv_nick);
        if (!TextUtils.isEmpty(info.getRemark())) {//判断是否有备注名
            mNick.setText(info.getRemark());

        } else {
            mNick.setText(info.getNick());
        }
        mHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position);
            }
        });
        mNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position);
            }
        });
        //地址
        final TextView mPosition = ViewHolder.get(convertView, R.id.tv_position);
        if (TextUtils.isEmpty(info.getAddress())) {
            mPosition.setVisibility(View.GONE);
        } else {
            mPosition.setVisibility(View.VISIBLE);
            mPosition.setText(info.getAddress());
        }

        //关注按钮
        IconicFontTextView attentionTv = ViewHolder.get(convertView, R.id.btn_add_follow);
        if (AccountInfo.getInstance().loadAccount().getAccount() == info.getAccount()) {
            attentionTv.setVisibility(View.GONE);

        } else {
            if (info.isFriend()) {
                attentionTv.setSelected(true);
                attentionTv.setText("已关注");
                attentionTv.setTextColor(mContext.getResources().getColor(R.color.gray_text_color));
            } else {
                attentionTv.setVisibility(View.VISIBLE);
                attentionTv.setSelected(false);
                //attentionTv.setTextColor(mContext.getResources().getColor(R.color.orange));
                attentionTv.setText(R.string.caption_add_follow);
                attentionTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (AppUtility.isFastClick()) {//判断是否连续点击
                            return;
                        }

                        //加关注
                        ((IconicFontTextView) v).setSelected(true);
                        ((IconicFontTextView) v).setText("已关注");
                        info.setFriend(true);
                        ((IconicFontTextView) v).setTextColor(mContext.getResources().getColor(R.color.gray_text_color));
                        ((IconicFontTextView) v).setClickable(false);
                        ((IconicFontTextView) v).setEnabled(false);
                        ApiModule.apiService().addFriend(Security.aesEncrypt(mAccount),
                                Security.aesEncrypt(info.getAccount() + "")).enqueue(new Callback<XKRepo>() {
                            @Override
                            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                                XKRepo xkRepo = response.body();
                                String msg = xkRepo.getMsg();
                                if (xkRepo.isSuccess()) { // 登录成功

                                } else { // 登录失败
                                    ProgressHUD.showErrorMessage(mContext, msg);
                                }
                            }

                            @Override
                            public void onFailure(Call<XKRepo> call, Throwable t) {
                                ProgressHUD.showErrorMessage(mContext, t.getLocalizedMessage());
                                ((IconicFontTextView) v).setSelected(false);
                                ((IconicFontTextView) v).setText(R.string.caption_add_follow);
                                info.setFriend(false);
                                ((IconicFontTextView) v).setTextColor(mContext.getResources().getColor(R.color.orange));
                                ((IconicFontTextView) v).setClickable(true);
                                ((IconicFontTextView) v).setEnabled(true);
                            }

                        });
                    }
                });

            }


        }
        return convertView;
    }

    // --------------------------- 加关注 ---------------------------

    private void executeAddFollow(final String friendAccount, final int position, final IconicFontTextView view) {
        String account = Security.aesEncrypt(mAccount);
        String mFriendAccount = Security.aesEncrypt(friendAccount);
        ApiModule.apiService().addFriend(account, mFriendAccount).enqueue(new Callback<XKRepo>() {


                                                                              @Override
                                                                              public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                                                                                  XKRepo xkRepo = response.body();
                                                                                  String message = xkRepo.getMsg();
                                                                                  if (xkRepo.isSuccess()) {
                                                                                      ProgressHUD.showSuccessMessage(mContext, message);
                                                                                      view.setSelected(true);
                                                                                      view.setText("已关注");
                                                                                      //此时的点击事件变成了已关注
                                                                                      view.setClickable(false);
                                                                                  } else {
                                                                                      ProgressHUD.showErrorMessage(mContext, message);
                                                                                  }
                                                                              }

                                                                              @Override
                                                                              public void onFailure(Call<XKRepo> call, Throwable t) {
                                                                                  ProgressHUD.showInfoMessage(mContext, mContext.getString(R.string.common_network_error));
                                                                              }


                                                                          }

        );
    }

    @Override
    public void notifyDataChanged(List<PraiseItem> myFriendInfos, boolean isRefresh) {
        if (isRefresh) {
            mData.clear();
        }
        mData.addAll(myFriendInfos);
        notifyDataSetChanged();
    }

    @Override
    public List<PraiseItem> getData() {
        return mData;
    }
}

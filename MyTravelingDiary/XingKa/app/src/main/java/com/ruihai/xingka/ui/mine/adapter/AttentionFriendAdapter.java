package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.MyFriendInfoRepo;
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
 * Created by gjzhang on 15/10/31.
 */
public class AttentionFriendAdapter extends BaseAdapter implements IDataAdapter<List<MyFriendInfoRepo.MyFriendInfo>> {

    public static final String DEFAULT_AVATAR_KEY = "00000000-0000-0000-0000-000000000000";

    private List<MyFriendInfoRepo.MyFriendInfo> mData = new ArrayList<>();
    private LayoutInflater inflater;
    private int type;
    private String mUserAccount;//当前名片的行咖号
    private String myAccount;//我的行咖号
    private Context context;

    public OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    public AttentionFriendAdapter(Context context, int type, String mAccount) {
        inflater = LayoutInflater.from(context);
        this.type = type;
        this.mUserAccount = mAccount;
        this.context = context;
        //获取我的行咖号
        myAccount = String.valueOf(AccountInfo.getInstance().loadAccount().getAccount());

    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.adapter_attention_friend, null);
//        HorizontalListView horizontallistview = ViewHolder.get(convertView, R.id.horizontallistview);
//        HorizontalListViewAdapter adapter = new HorizontalListViewAdapter(context);
//        adapter.notifyDataSetChanged();
//        horizontallistview.setAdapter(adapter);
        final MyFriendInfoRepo.MyFriendInfo info = mData.get(position);
        //图像
        ImageView mHead = ViewHolder.get(convertView, R.id.sdv_avatar);
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

        //是否关注
        IconicFontTextView mAttention = ViewHolder.get(convertView, R.id.btn_add_follow);
        mAttention.setTag(info.getAccount());
        if (myAccount.equals(mUserAccount)) {//我的名片进入的 1:好友 2:关注 3:粉丝 4:好友—关注
            if (type == 4) {//关注列表
//                mAttention.setEnabled(false);
                mAttention.setSelected(true);
                if(info.isEach()){ //如果是好友:相互关注,关注为取消关注
                    mAttention.setText("互相关注");
                }else {
                    mAttention.setText(R.string.cancel_follow);
                }

            } else if (type == 1) {//好友列表
                mAttention.setSelected(true);
//                mAttention.setEnabled(false);
                mAttention.setText(R.string.cancel_follow);
            } else if (type == 3) {//粉丝列表
//                mAttention.setEnabled(false);
//                mAttention.setSelected(true);
                mAttention.setText(R.string.caption_add_follow);
            }
        } else {//好友名片进入的
            if (info.getIsFriend() == 1 || info.getIsFriend() == 2) {//已关注的
                mAttention.setSelected(true);
                mAttention.setText(R.string.cancel_follow);
            } else if (myAccount.equals(String.valueOf(info.getAccount()))) {//自己
                mAttention.setVisibility(View.GONE);
            } else {//未关注的
//                mAttention.setEnabled(false);
                mAttention.setText(R.string.caption_add_follow);
            }

        }


        mAttention.setOnClickListener(new View.OnClickListener()

                                      {
                                          @Override
                                          public void onClick(View v) {
                                              if (AppUtility.isFastClick()) {
                                                  return;
                                              }
                                              if (myAccount.equals(mUserAccount)) {
                                                  if (type == 4) {//好友—关注列表
                                                      executeXCancelFollow(v.getTag().toString(), position, (IconicFontTextView) v);
                                                  } else if (type == 1) {//好友列表
                                                      executeXCancelFollow(v.getTag().toString(), position, (IconicFontTextView) v);
                                                  } else if (type == 3) {//粉丝列表

                                                      executeAddFollow(v.getTag().toString(), position, (IconicFontTextView) v);

                                                  }
                                              } else {
                                                  if (info.getIsFriend() == 1 || info.getIsFriend() == 2) {
                                                      //取消关注
                                                      executeXCancelFollow(v.getTag().toString(), position, (IconicFontTextView) v);
                                                  } else {
                                                      //关注
                                                      executeAddFollow(v.getTag().toString(), position, (IconicFontTextView) v);
                                                  }
                                              }
                                          }
                                      }

        );

        //定位
//        UserCard userCard=new UserCard();
//        holder.mPosition.setText(userCard.getAddress());
        return convertView;

    }


//    public List<MyFriendInfoRepo.MyFriendInfo> getMyFriendInfos() {return this.mData;}


    // --------------------------- 加关注 ---------------------------

    private void executeAddFollow(final String friendAccount, final int position, final IconicFontTextView view) {
        String account = Security.aesEncrypt(myAccount);
        String mFriendAccount = Security.aesEncrypt(friendAccount);
        if (myAccount.equals(mUserAccount)) {//我的名片进入的
            mData.remove(position);
            notifyDataSetChanged();
        } else {
            view.setSelected(true);
            view.setText(R.string.cancel_follow);
            //此时的点击事件变成了取消关注
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AppUtility.isFastClick()) {//判断是否连续点击
                        return;
                    }
                    executeXCancelFollow(friendAccount, position, (IconicFontTextView) v);
                }
            });
        }
        ApiModule.apiService().addFriend(account, mFriendAccount).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();

                String message = xkRepo.getMsg();
                if (xkRepo.isSuccess()) {
                    AppUtility.showToast(message);
                    // friendFragment.loadData(false);
//                    if (myAccount.equals(mUserAccount)) {//我的名片进入的
//                        mData.remove(position);
//                        notifyDataSetChanged();
//                    } else {
//                        view.setSelected(true);
//                        view.setText(R.string.cancel_follow);
//                        //此时的点击事件变成了取消关注
//                        view.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (AppUtility.isFastClick()) {//判断是否连续点击
//                                    return;
//                                }
//                                executeXCancelFollow(friendAccount, position, (IconicFontTextView) v);
//                            }
//                        });
//                    }

                } else {//别人名片进入的
                    ProgressHUD.showInfoMessage(context, message);
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showInfoMessage(context, context.getString(R.string.common_network_error));
            }

        });
    }

    // --------------------------- 取消关注 ---------------------------
    private void executeXCancelFollow(final String friendAccount, final int position, final IconicFontTextView view) {
        String account = Security.aesEncrypt(myAccount);
        String mFriendAccount = Security.aesEncrypt(friendAccount);
        if (myAccount.equals(mUserAccount)) {//我的关注粉丝好友列表
            mData.remove(position);
            notifyDataSetChanged();
        } else {//别人的关注和粉丝列表
            view.setSelected(false);
            view.setText(R.string.caption_add_follow);
            //此时的点击事件变成了加关注
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    executeAddFollow(friendAccount, position, (IconicFontTextView) v);
                }
            });
        }
        ApiModule.apiService().delFriend(account, mFriendAccount).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                String message = xkRepo.getMsg();
                if (xkRepo.isSuccess()) {
                    AppUtility.showToast(message);
                    //friendFragment.loadData(false);
//                    if (myAccount.equals(mUserAccount)) {//我的关注粉丝好友列表
//                        mData.remove(position);
//                        notifyDataSetChanged();
//                    } else {//别人的关注和粉丝列表
//                        view.setSelected(false);
//                        view.setText(R.string.caption_add_follow);
//                        //此时的点击事件变成了加关注
//                        view.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                executeAddFollow(friendAccount, position, (IconicFontTextView) v);
//                            }
//                        });
//                    }

                } else {
                    ProgressHUD.showInfoMessage(context, message);
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.showInfoMessage(context, context.getString(R.string.common_network_error));
            }

        });
    }


    @Override
    public void notifyDataChanged(List<MyFriendInfoRepo.MyFriendInfo> myFriendInfos, boolean isRefresh) {
        if (isRefresh) {
            mData.clear();
        }
        mData.addAll(myFriendInfos);
        notifyDataSetChanged();
    }

    @Override
    public List<MyFriendInfoRepo.MyFriendInfo> getData() {
        return mData;
    }
}




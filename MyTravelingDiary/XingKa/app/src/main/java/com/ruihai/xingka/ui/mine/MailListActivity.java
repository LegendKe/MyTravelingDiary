package com.ruihai.xingka.ui.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ruihai.iconicfontengine.widget.IconicFontTextView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.ApiModule;
import com.ruihai.xingka.api.model.AccountInfo;
import com.ruihai.xingka.api.model.AddInfo;
import com.ruihai.xingka.api.model.ContactsInfo;
import com.ruihai.xingka.api.model.MaybeInfo;
import com.ruihai.xingka.api.model.XKRepo;
import com.ruihai.xingka.ui.BaseActivity;
import com.ruihai.xingka.ui.mine.adapter.MailContactAdapter;
import com.ruihai.xingka.ui.mine.adapter.MailListAdapter;
import com.ruihai.xingka.utils.AppUtility;
import com.ruihai.xingka.utils.GetContactsInfo;
import com.ruihai.xingka.utils.Security;
import com.ruihai.xingka.widget.ListViewForScrollView;
import com.ruihai.xingka.widget.ProgressHUD;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 通讯录好友
 * Created by apple on 15/9/7.
 */
public class MailListActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.tv_right)
    IconicFontTextView mRight;
    @BindView(R.id.ll_new_friends)
    RelativeLayout mNewFriendsLlayout;
    @BindView(R.id.ll_maybe_konw)
    RelativeLayout mMaybeKnowLlayout;
    @BindView(R.id.ll_invite_friends)
    RelativeLayout mInviteFriendLayout;
    @BindView(R.id.lvfs_first)
    ListViewForScrollView mLvFirst;
    @BindView(R.id.lvfs_second)
    ListViewForScrollView mLvSecond;
    @BindView(R.id.lvfs_third)
    ListViewForScrollView mLvThird;

    //List<ContactsInfo> ContactsList = new ArrayList<ContactsInfo>();
    List<ContactsInfo> ContactsInfoList = new ArrayList<ContactsInfo>();
    List<AddInfo> addInfoList = new ArrayList<AddInfo>();
    List<MaybeInfo> maybeInfoList = new ArrayList<MaybeInfo>();
    List<AddInfo> newFriendInfoList = new ArrayList<AddInfo>();
    String phoneValue = "";

    boolean isShowAdd = false;
    boolean isShowMaybe = false;
    MailListActivity mContext;
    int form_add_friends = 1;
    int form_maybe_know = 2;
    private int mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_list);
        ButterKnife.bind(this);
        mContext = this;
        mTitle.setText("通讯录好友");
        mAccount = AccountInfo.getInstance().loadAccount().getAccount();
        mRight.setVisibility(View.GONE);
//        PackageManager pm = getPackageManager();
//        boolean permission = (PackageManager.PERMISSION_GRANTED ==
//                pm.checkPermission("android.permission.READ_CONTACTS", "com.ruihai.xingka"));
//        if (permission) {//有这个权限
//
//        }else {//没有权限
//            finish();
//        }
        ProgressHUD.showLoadingMessage(this, "正在搜索", true);
        initView();
    }

    private void initView() {
        GetContactsInfo getContactsInfo = new GetContactsInfo(this, this);
        //获取通讯录好友列表ContactsInfoList
        ContactsInfoList = getContactsInfo.getLocalContactsInfos();
        //ContactsList = getContactsInfo.getSIMContactsInfos();
        //ContactsInfoList = ContactsList;
//        for (int i = 0; i < ContactsList.size(); i++) {
//            for (int j = i + 1; j < ContactsList.size(); j++) {
//                if (ContactsList.get(i).getContactsName().equals(ContactsList.get(j).getContactsName())) {
//                    ContactsInfoList.remove(j);
//                }
//            }
//        }
        int size = ContactsInfoList.size();
        for (int i = 0; i < size; i++) {
            String str = ContactsInfoList.get(i).getContactsPhone().replace(" ", "").replace("+86", "");
            int k = str.length();
            if (k != 11 || !AppUtility.isMobilePhone(str)) {
                ContactsInfoList.remove(i);
                size = size - 1;
                i = i - 1;
            }
        }

        for (int i = 0; i < ContactsInfoList.size(); i++) {
            if (i < ContactsInfoList.size() - 1) {
                phoneValue = phoneValue + ContactsInfoList.get(i).getContactsPhone() + "|";
            }
            if (i == ContactsInfoList.size() - 1) {
                phoneValue = phoneValue + ContactsInfoList.get(i).getContactsPhone().trim();
            }
        }

        phoneValue = phoneValue.replace(" ", "");

        sendContactsInfo();//查询通讯录中好友和可能认识的人
    }


    private void sendContactsInfo() {
        //Log.e("----->", "------>" + Security.aesEncrypt(AccountInfo.getInstance().loadAccount().getAccount() + "") + "-------- > " + Security.aesEncrypt(phoneValue));
        ApiModule.apiService().phoneBook(Security.aesEncrypt(AccountInfo.getInstance().loadAccount().getAccount() + ""),
                Security.aesEncrypt(phoneValue)).enqueue(new Callback<XKRepo>() {
            @Override
            public void onResponse(Call<XKRepo> call, Response<XKRepo> response) {
                XKRepo xkRepo = response.body();
                String msg = xkRepo.getMsg();
                if (xkRepo.isSuccess()) { // 登录成功
                    addInfoList = xkRepo.getAddInfo();
                    maybeInfoList = xkRepo.getMaybeInfo();
                    if (addInfoList.size() == 0) {
                        isShowAdd = false;
                        mNewFriendsLlayout.setVisibility(View.GONE);
                    } else {
                        isShowAdd = true;
                        mNewFriendsLlayout.setVisibility(View.VISIBLE);
                    }
                    if (maybeInfoList.size() == 0) {
                        isShowMaybe = false;
                        mMaybeKnowLlayout.setVisibility(View.GONE);
                    } else {
                        isShowMaybe = true;
                        mMaybeKnowLlayout.setVisibility(View.VISIBLE);
                    }
                    showInterface();
                } else { // 登录失败
                    ProgressHUD.showErrorMessage(mContext, msg);
                    ProgressHUD.dismiss();
                }
            }

            @Override
            public void onFailure(Call<XKRepo> call, Throwable t) {
                ProgressHUD.dismiss();
                ProgressHUD.showErrorMessage(mContext, t.getLocalizedMessage());
            }

        });
    }


    private void showInterface() {
        if (isShowAdd) {
            int length = ContactsInfoList.size();
            for (int i = 0; i < addInfoList.size(); i++) {
                for (int j = 0; j < length; j++) {
                    if (addInfoList.get(i).getPhone().equals(ContactsInfoList.get(j).getContactsPhone().replace(" ", ""))) {
                        addInfoList.get(i).setName(ContactsInfoList.get(j).getContactsName());
                        ContactsInfoList.remove(j);
                        length = length - 1;
                        j = j - 1;
                    }
                }
            }

            for (int i = 0; i < addInfoList.size(); i++) {//判断是不是好友,是好友就不要显示了.已经是否为自己账号
                if (!addInfoList.get(i).isFriend() && addInfoList.get(i).getAccount() != mAccount) {
                    newFriendInfoList.add(addInfoList.get(i));
                }
            }
            if (newFriendInfoList.size() == 0) {
                mNewFriendsLlayout.setVisibility(View.GONE);
            }
            MailListAdapter adapter_add = new MailListAdapter(this, newFriendInfoList, null, form_add_friends);
            MailContactAdapter adapter_invite = new MailContactAdapter(this, ContactsInfoList);
            mLvFirst.setAdapter(adapter_add);
            mLvThird.setAdapter(adapter_invite);
        } else {
            MailContactAdapter adapter_invite = new MailContactAdapter(this, ContactsInfoList);
            mLvThird.setAdapter(adapter_invite);
        }
        if (ContactsInfoList.size() == 0) {
            mInviteFriendLayout.setVisibility(View.GONE);
        }
        if (isShowMaybe) {
            MailListAdapter adapter_maybe = new MailListAdapter(this, null, maybeInfoList, form_maybe_know);
            mLvSecond.setAdapter(adapter_maybe);
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(500);
                    ProgressHUD.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

    @OnClick(R.id.tv_back)
    void onBack() {
        finish();
    }


}

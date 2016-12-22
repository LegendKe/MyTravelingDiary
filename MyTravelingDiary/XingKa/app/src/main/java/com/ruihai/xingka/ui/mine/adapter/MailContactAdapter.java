package com.ruihai.xingka.ui.mine.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.ContactsInfo;
import com.ruihai.xingka.ui.mine.InviteFriendsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by apple on 15/9/8.
 */
public class MailContactAdapter extends BaseAdapter {

    private Context context;
    List<ContactsInfo> contactsInfoList = new ArrayList<ContactsInfo>();

    public MailContactAdapter(Context context, List<ContactsInfo> contactsInfoList) {
        this.context = context;
        this.contactsInfoList = contactsInfoList;
    }

    @Override
    public int getCount() {
        if (null != contactsInfoList) {
            return contactsInfoList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return contactsInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_mail_contact, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.mNickName.setText(contactsInfoList.get(position).getContactsName());
        holder.mInviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(context, InviteFriendsActivity.class);
                intent.putExtra("contactName", contactsInfoList.get(position).getContactsName());
                intent.putExtra("contactPhone", contactsInfoList.get(position).getContactsPhone());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class ViewHolder {

        @BindView(R.id.iv_head_portrait)
        SimpleDraweeView mHeadPortrait;
        @BindView(R.id.tv_nickname)
        TextView mNickName;
        @BindView(R.id.btn_invite)
        TextView mInviteBtn;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}

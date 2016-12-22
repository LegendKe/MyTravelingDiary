package com.ruihai.xingka.utils;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ruihai.xingka.R;
import com.ruihai.xingka.api.model.ContactsInfo;
import com.ruihai.xingka.ui.mine.MailListActivity;
import com.ruihai.xingka.widget.ProgressHUD;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 15/9/2.
 * 获取联系人信息
 */
public class GetContactsInfo {

    List<ContactsInfo> localList;
    List<ContactsInfo> SIMList;
    Context context;
    ContactsInfo contactsInfo;
    ContactsInfo SIMContactsInfo;
    MailListActivity mContext;

    public GetContactsInfo(Context context, MailListActivity mContext) {
        localList = new ArrayList<ContactsInfo>();
        SIMList = new ArrayList<ContactsInfo>();
        this.context = context;
        this.mContext = mContext;

    }

    // ----------------得到本地联系人信息-------------------------------------
    public List<ContactsInfo> getLocalContactsInfos() {

        ContentResolver cr = context.getContentResolver();
        String str[] = {Phone.CONTACT_ID, Phone.DISPLAY_NAME, Phone.NUMBER,
                Phone.PHOTO_ID};
        Cursor cur = null;
        try {
            cur = cr.query(
                    Phone.CONTENT_URI, str, null,
                    null, null);
            if (cur != null) {
                while (cur.moveToNext()) {
                    contactsInfo = new ContactsInfo();
                    contactsInfo.setContactsPhone(cur.getString(cur.getColumnIndex(Phone.NUMBER)));// 得到手机号码
                    contactsInfo.setContactsName(cur.getString(cur.getColumnIndex(Phone.DISPLAY_NAME)));
//                contactsInfo.setContactsPhotoId(cur.getLong(cur.getColumnIndex(Phone.PHOTO_ID)));
                    //  long contactid = cur.getLong(cur.getColumnIndex(Phone.CONTACT_ID));
                    // long photoid = cur.getLong(cur.getColumnIndex(Phone.PHOTO_ID));
//                // 如果photoid 大于0 表示联系人有头像 ，如果没有给此人设置头像则给他一个默认的
//                if (photoid > 0) {
//                    Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactid);
//                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
//                    contactsInfo.setBitmap(BitmapFactory.decodeStream(input));
//                } else {
//                    contactsInfo.setBitmap(BitmapFactory.decodeResource(context.getResources(),
//                            R.mipmap.ic_launcher));
//                }
                   // Log.e("TAG", "--联系人电话--" + contactsInfo.getContactsPhone());
                    // System.out.println("--联系人电话--" + contactsInfo.getContactsPhone());
                    localList.add(contactsInfo);
                }
                cur.close();
            }

        } catch (SecurityException e) {
            new AlertDialog.Builder(mContext).setMessage("你的手机未对行咖开启读取手机通讯录权限,快去手机应用管理里去设置吧!").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent("com.android.settings.ManageApplications");
                    mContext.startActivity(intent);
                    mContext.finish();
                }
            }).create().show();
        }
        return localList;
    }

    public List<ContactsInfo> getSIMContactsInfos() {
        TelephonyManager mTelephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        System.out.println("--SIM--");
        ContentResolver cr = context.getContentResolver();
        final String SIM_URI_ADN = "content://icc/adn";// SIM卡

        Uri uri = Uri.parse(SIM_URI_ADN);
        Cursor cursor = cr.query(uri, null, null, null, null);
        while (cursor.moveToFirst()) {
            SIMContactsInfo = new ContactsInfo();
            SIMContactsInfo.setContactsName(cursor.getString(cursor.getColumnIndex("name")));
            SIMContactsInfo.setContactsPhone(cursor.getString(cursor.getColumnIndex("number")));
//            SIMContactsInfo.setBitmap(BitmapFactory.decodeResource(context.getResources(),
//                    R.mipmap.ic_launcher));
            SIMList.add(SIMContactsInfo);
        }
        cursor.close();

        return SIMList;
    }

}

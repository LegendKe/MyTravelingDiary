package com.ruihai.xingka.db;

import com.google.gson.Gson;
import com.ruihai.xingka.api.model.User;
import com.ruihai.xingka.ui.caption.publisher.bean.PublishBean;

import org.aisen.orm.extra.Extra;
import org.aisen.orm.utils.FieldUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zecker on 15/10/2.
 */
public class PublishDB {
    public static void addPublish(PublishBean bean, User user) {
        Extra extra = new Extra(user.getIdstr(), null);
        XingKaDB.getSqlite().insert(extra, bean);
    }

    public static void deletePublish(PublishBean bean, User user) {
        Extra extra = new Extra(user.getIdstr(), null);
        XingKaDB.getSqlite().deleteById(extra, PublishBean.class, bean.getId());
    }

    public static void updatePublish(PublishBean bean, User user) {
        Extra extra = new Extra(user.getIdstr(), null);
        XingKaDB.getSqlite().insertOrReplace(extra, bean);
    }

    public static ArrayList<PublishBean> getPublishList(User user) {
        Gson gson = new Gson();
        String selection = String.format(" %s = ? and %s != ? and %s != ? and %s != ? ", FieldUtils.OWNER, "status", "status", "status");
        String[] selectionArgs = { user.getIdstr(),
                gson.toJson(PublishBean.PublishStatus.create),
                gson.toJson(PublishBean.PublishStatus.sending),
                gson.toJson(PublishBean.PublishStatus.waiting) };
        return (ArrayList<PublishBean>) XingKaDB.getSqlite().select(PublishBean.class, selection, selectionArgs, null, null, FieldUtils.CREATEAT + " desc ", null);
    }

    /**
     * 获取添加状态的发布消息
     *
     * @param user
     * @return
     */
    public static List<PublishBean> getPublishOfAddStatus(User user) {
        try {
            String selection = String.format(" %s = ? and %s = ? ", FieldUtils.OWNER, "status");
            String[] selectionArgs = { user.getIdstr(), PublishBean.PublishStatus.create.toString() };

            return XingKaDB.getSqlite().select(PublishBean.class, selection, selectionArgs);
        } catch (Exception e) {
        }

        return new ArrayList<PublishBean>();
    }
}

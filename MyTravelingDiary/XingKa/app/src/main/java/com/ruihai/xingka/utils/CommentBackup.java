package com.ruihai.xingka.utils;

import com.ruihai.xingka.api.model.CommentItem;

import java.util.HashMap;

/**
 * Created by chaochen on 15/1/27.
 */
public class CommentBackup {

    public enum Type {
        Caption
    }

    public static class BackupParam {
        Type type;
        String id;
        String owner_id;

        public BackupParam(Type type, String id, String owner_id) {
            this.type = type;
            this.id = id;
            this.owner_id = owner_id;
        }

        public static BackupParam create(Object object) {
            if (object == null) {
                return null;
            }

            if (object instanceof CommentItem) {
                CommentItem captionComment = (CommentItem) object;
                String ownerId = captionComment.getReplyUid();
                if (!captionComment.isReply()) { // 表示直接回复冒泡，没有@某人
                    ownerId = "0";
                }
                return new BackupParam(Type.Caption, captionComment.getGuid(), ownerId);
            } else if (object instanceof BackupParam) {
                return (BackupParam) object;
            }

            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BackupParam)) return false;

            BackupParam param = (BackupParam) o;

            if (id != param.id) return false;
            if (owner_id != param.owner_id) return false;
            if (type != param.type) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    private CommentBackup() {
    }

    private static CommentBackup sCommentBackup;

    public static CommentBackup getInstance() {
        if (sCommentBackup == null) {
            sCommentBackup = new CommentBackup();
        }
        return sCommentBackup;
    }

    private HashMap<BackupParam, String> mData = new HashMap();

    public void save(BackupParam param, String comment) {
        mData.put(param, comment);
    }

    public String load(BackupParam param) {
        String comment = mData.get(param);
        if (comment == null) {
            comment = "";
        }

        return comment;
    }

    public void delete(BackupParam param) {
        mData.remove(param);
    }
}

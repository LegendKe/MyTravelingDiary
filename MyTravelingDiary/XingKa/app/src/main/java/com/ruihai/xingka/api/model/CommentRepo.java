package com.ruihai.xingka.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by zecker on 15/9/23.
 */
public class CommentRepo extends XKRepo {

    @SerializedName("recordCount")
    private int recordCount;

    @SerializedName("commentMessage")
    private List<CommentItem> commentItemList;

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public List<CommentItem> getCommentItemList() {
        return commentItemList;
    }

    public void setCommentItemList(List<CommentItem> commentItemList) {
        this.commentItemList = commentItemList;
    }
}

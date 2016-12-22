package com.ruihai.xingka.api.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 15/9/3.
 */
public class UserAccount {

    public String userTalk = ""; // 一句话说说
    public String userNick = ""; // 昵称
    public int userSex = 0; // 性别 1-男 2-女 0-未选择
    public String userAdress = ""; // 当前地理位置
    public List<UserLabel> userTag = new ArrayList<>(); //用户标签


}

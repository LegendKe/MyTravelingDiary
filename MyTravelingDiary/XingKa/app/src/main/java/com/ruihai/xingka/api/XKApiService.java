package com.ruihai.xingka.api;

import com.ruihai.xingka.api.model.BannerInfoRepo;
import com.ruihai.xingka.api.model.CarBrandRepo;
import com.ruihai.xingka.api.model.CommentRepo;
import com.ruihai.xingka.api.model.MessageNoReadNum;
import com.ruihai.xingka.api.model.OfficialMessageRepo;
import com.ruihai.xingka.api.model.OfficialPhotoTopicTypeRepo;
import com.ruihai.xingka.api.model.PraiseListRepo;
import com.ruihai.xingka.api.model.PraiseRepo;
import com.ruihai.xingka.api.model.ReportInfo;
import com.ruihai.xingka.api.model.SignInRepo;
import com.ruihai.xingka.api.model.TrackwayCollectionRepo;
import com.ruihai.xingka.api.model.TrackwayInfoListRepo;
import com.ruihai.xingka.api.model.TrackwayInfoRepo;
import com.ruihai.xingka.api.model.UnReadMesageInfo;
import com.ruihai.xingka.api.model.UserCollectionRepo;
import com.ruihai.xingka.api.model.MyFriendInfoRepo;
import com.ruihai.xingka.api.model.UserPhotoTopicRepo;
import com.ruihai.xingka.api.model.PhotoTopicListRepo;
import com.ruihai.xingka.api.model.PhotoTopicRepo;
import com.ruihai.xingka.api.model.PushMessageRepo;
import com.ruihai.xingka.api.model.UserCard;
import com.ruihai.xingka.api.model.UserRepo;
import com.ruihai.xingka.api.model.MyTrackwayRepo;
import com.ruihai.xingka.api.model.XKRepo;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


/**
 * Created by zecker on 15/8/11.
 */
public interface XKApiService {

    /**
     * 用户注册
     *
     * @param phoneNum 手机号码
     * @param password 用户密码
     */
    @FormUrlEncoded
    @POST("UserRegister")
    Call<XKRepo> userRegister(
            @Field("phoneNum") String phoneNum,
            @Field("userPwd") String password
    );

    /**
     * 用户注册2.0
     * 增加国家码
     *
     * @param phoneNum   手机号码
     * @param password   用户密码
     * @param countryNum 国家码
     */
    @FormUrlEncoded
    @POST("UserRegister2")
    Call<XKRepo> userRegister2(
            @Field("phoneNum") String phoneNum,
            @Field("userPwd") String password,
            @Field("countryNum") String countryNum
    );

    /**
     * 邀请注册
     *
     * @param phoneNum 手机号码
     * @param userPwd  密码
     * @param code     邀请码
     */
    @FormUrlEncoded
    @POST("InvitationRegister")
    Call<XKRepo> invitationRegister(

            @Field("phoneNum") String phoneNum,
            @Field("userPwd") String userPwd,
            @Field("code") String code
    );

    /**
     * 用户登录
     *
     * @param account  账号或手机号码
     * @param password 用户密码
     */
    @FormUrlEncoded
    @POST("UserLogin")
    Call<UserRepo> userLogin(
            @Field("account") String account,
            @Field("userPwd") String password
    );

    /**
     * 用户登录2.0
     * 新增设备型号
     *
     * @param account  账号或手机号码
     * @param password 用户密码
     * @param platform 操作系统，1：安卓；2：iOS
     * @param device   设备型号
     */
    @FormUrlEncoded
    @POST("UserLogin2")
    Call<UserRepo> userLogin2(
            @Field("account") String account,
            @Field("userPwd") String password,
            @Field("OS") String platform,
            @Field("equip") String device
    );

    /**
     * 用户登录3.0
     * 新增设备型号
     * 增加国家码
     *
     * @param account    账号或手机号码
     * @param password   用户密码
     * @param platform   操作系统，1：安卓；2：iOS
     * @param device     设备型号
     * @param countryNum 国家码
     */
    @FormUrlEncoded
    @POST("UserLogin3")
    Call<UserRepo> userLogin3(
            @Field("account") String account,
            @Field("userPwd") String password,
            @Field("OS") String platform,
            @Field("equip") String device,
            @Field("countryNum") String countryNum
    );

    /**
     * 用户登录3.0
     * 新增设备型号
     * 增加国家码
     *
     * @param account    账号或手机号码
     * @param password   用户密码
     * @param platform   操作系统，1：安卓；2：iOS
     * @param device     设备型号
     * @param countryNum 国家码
     * @param osVersion  手机系统版本号
     * @param appVersion 应用版本号
     */
    @FormUrlEncoded
    @POST("UserLogin4")
    Call<UserRepo> userLogin4(
            @Field("account") String account,
            @Field("userPwd") String password,
            @Field("OS") String platform,
            @Field("equip") String device,
            @Field("countryNum") String countryNum,
            @Field("osVersion") String osVersion,
            @Field("appVersion") String appVersion
    );

    /**
     * 写入设备令牌值
     *
     * @param account 用户行咖号
     * @param token   令牌值
     */
    @FormUrlEncoded
    @POST("EditDeviceToken")
    Call<XKRepo> editDeviceToken(
            @Field("account") String account,
            @Field("token") String token
    );

    /**
     * 举报用户接口
     *
     * @param account   举报人行咖号
     * @param toAccount 被举报用户行咖号
     * @param type      被举报用户行咖号
     */
    @FormUrlEncoded
    @POST("UserReport")
    Call<XKRepo> reportUser(
            @Field("account") String account,
            @Field("toAccount") String toAccount,
            @Field("type") String type
    );

    /**
     * 图说分享回写
     *
     * @param pGuid        图说主表外键
     * @param shareAccount 分享图说的人
     * @param os           设备（1:android、2:iOS,3:Web）
     * @param toPlace      分享渠道：（微信、朋友圈、QQ、QQ空间、微博）
     */
    @FormUrlEncoded
    @POST("PhotoTopicShareRecord")
    Call<XKRepo> getPhotoTopicShareRecord(
            @Field("pGuid") String pGuid,
            @Field("shareAccount") String shareAccount,
            @Field("os") String os,
            @Field("toPlace") String toPlace
    );


    /**
     * 返回推送消息条数
     *
     * @param account  行咖号
     * @param pushtype 消息类型
     * @param readtype 阅读类型
     */
    @FormUrlEncoded
    @POST("PhotoTopicPushNum")
    Call<UnReadMesageInfo> getPhotoTopicPushNum(
            @Field("account") String account,
            @Field("pushtype") String pushtype,
            @Field("readtype") String readtype
    );

    /**
     * 忘记密码
     *
     * @param phoneNum 11位手机号码
     * @param password 6-18位用户密码
     */
    @FormUrlEncoded
    @POST("FindPassWord")
    Call<XKRepo> findPassWord(
            @Field("phoneNum") String phoneNum,
            @Field("newPwd") String password
    );

    /**
     * 忘记密码2.0
     * 增加国家码
     *
     * @param phoneNum   11位手机号码
     * @param password   6-18位用户密码
     * @param countryNum 国家码
     */
    @FormUrlEncoded
    @POST("FindPassWord2")
    Call<XKRepo> findPassWord2(
            @Field("phoneNum") String phoneNum,
            @Field("newPwd") String password,
            @Field("countryNum") String countryNum
    );


    /**
     * 修改密码
     *
     * @param account     行咖帐号
     * @param oldpassword 6-18位用户旧密码
     * @param newpassword 6-18位用户新密码
     */
    @FormUrlEncoded
    @POST("EditPassWord")
    Call<XKRepo> editPassWord(

            @Field("account") String account,
            @Field("oldPwd") String oldpassword,
            @Field("newPwd") String newpassword
    );

    /**
     * 更换手机号码
     *
     * @param account  行咖帐号
     * @param password 6-18位用户密码
     * @param newPhone 11位手机号码
     */
    @FormUrlEncoded
    @POST("ChangePhone")
    Call<XKRepo> changePhone(
            @Field("account") String account,
            @Field("userPwd") String password,
            @Field("newPhone") String newPhone
    );

    /**
     * 更换手机号码2.0
     * 增加国家码
     *
     * @param account    行咖帐号
     * @param password   6-18位用户密码
     * @param newPhone   11位手机号码
     * @param countryNum 国家码
     */
    @FormUrlEncoded
    @POST("ChangePhone2")
    Call<XKRepo> changePhone2(
            @Field("account") String account,
            @Field("userPwd") String password,
            @Field("newPhone") String newPhone,
            @Field("countryNum") String countryNum
    );

    /**
     * 修改用户资料(单个)
     *
     * @param account 行咖帐号
     * @param type    资料类型 (brand,img,location,talk,nick,sex,tag,address)
     * @param value   对应的值（车品牌、头像、定位、说说、昵称、性别、标签、所在地）
     */
    @FormUrlEncoded
    @POST("EditUserInfo")
    Call<XKRepo> editUserInfo(
            @Field("account") String account,
            @Field("type") String type,
            @Field("value") String value
    );

    /**
     * 批量修改用户资料
     *
     * @param account   行咖帐号
     * @param jsonValue 对应的值
     */
    @FormUrlEncoded
    @POST("EditUserInfoMore")
    Call<UserRepo> editUserInfoMore(
            @Field("account") String account,
            @Field("jsonValue") String jsonValue
    );

    /**
     * 获取系统标签列表
     *
     * @param type    系统标签类型 1:心情标签 2:地理标签 3:用户标签
     * @param version 版本匹配是否有更新
     */
    @FormUrlEncoded
    @POST("GetSysTagList")
    Call<XKRepo> getSysTagList(
            @Field("type") String type,
            @Field("version") String version
    );

    /**
     * 我的图说
     *
     * @param account  行账号
     * @param isHidden 控制查看仅自己可见内容,0:不允许显示(查看别人); 1.允许查看(查看自己)
     * @param page     页码
     * @param perpage  每页条数
     */
    @FormUrlEncoded
    @POST("MyPhotoTopic")
    Call<UserPhotoTopicRepo> myPhotoTopic(
            @Field("account") String account,
            @Field("isHidden") String isHidden,
            @Field("page") String page,
            @Field("perpage") String perpage
    );

    /**
     * 我的图说
     *
     * @param account  行账号
     * @param type     图说类型
     * @param isHidden 控制查看仅自己可见内容,0:不允许显示(查看别人); 1.允许查看(查看自己)
     * @param page     页码
     * @param perpage  每页条数
     */
    @FormUrlEncoded
    @POST("MyPhotoTopicWithType")
    Call<UserPhotoTopicRepo> myPhotoTopicWithType(
            @Field("account") String account,
            @Field("type") String type,
            @Field("isHidden") String isHidden,
            @Field("page") String page,
            @Field("perpage") String perpage
    );

    /**
     * 活动/广告列表
     *
     * @param account  行账号
     * @param type     类型。0：全部；1：活动；2：广告
     * @param platform 平台类型。0：全部；1：手机；2：PC端
     */
    @FormUrlEncoded
    @POST("BannerList")
    Call<BannerInfoRepo> bannerList(
            @Field("account") String account,
            @Field("type") String type,
            @Field("platform") String platform
    );

    /**
     * 通讯录面板
     *
     * @param account    行账号
     * @param phoneValue 手机号码串,用|隔开
     */
    @FormUrlEncoded
    @POST("PhoneBook")
    Call<XKRepo> phoneBook(
            @Field("account") String account,
            @Field("phoneValue") String phoneValue
    );

    /**
     * 获取车品牌列表
     *
     * @param parentId 上级品牌ID，顶级为0
     * @param version  车品牌数据版本
     */
    @FormUrlEncoded
    @POST("GetCarBrandList")
    Call<CarBrandRepo> getCarBrandList(
            @Field("parentId") String parentId,
            @Field("version") String version
    );

    /**
     * 最新一条官方图说
     *
     * @param account 行账号，未登录-0
     */
    @FormUrlEncoded
    @POST("PhotoTopicOfficialList")
    Call<PhotoTopicListRepo> getPhotoTopicOfficialList(
            @Field("account") String account
    );

    /**
     * 图说主页
     *
     * @param account     行账号，未登录-0
     * @param onlyFriends 是否只显示关注，1-是，0-否
     * @param page        页面
     * @param pageSize    每页条数
     */
    @FormUrlEncoded
    @POST("PhotoTopicMain")
    Call<PhotoTopicListRepo> getPhotoTopicList(
            @Field("account") String account,
            @Field("onlyFriends") String onlyFriends,
            @Field("page") String page,
            @Field("perpage") String pageSize
    );

    /**
     * 发布图说
     *
     * @param account   行账号
     * @param jsonValue 图说内容json格式数据
     */
    @FormUrlEncoded
    @POST("PhotoTopicAdd")
    Call<XKRepo> photoTopicAdd(
            @Field("account") String account,
            @Field("jsonValue") String jsonValue
    );

    /**
     * 图说详情（含评论）
     *
     * @param account       行账号
     * @param pGuid         图说主键
     * @param authorAccount 图说作者行账号
     */
    @FormUrlEncoded
    @POST("PhotoTopicDetail")
    Call<PhotoTopicRepo> photoTopicDetail(
            @Field("account") String account,
            @Field("P_Guid") String pGuid,
            @Field("authorAccount") String authorAccount
    );

    /**
     * 图说详情（不含评论）
     *
     * @param account       行账号
     * @param pGuid         图说主键
     * @param authorAccount 图说作者行账号
     */
    @FormUrlEncoded
    @POST("PhotoTopicDetailNoC")
    Call<XKRepo> PhotoTopicDetailNoC(
            @Field("account") String account,
            @Field("P_Guid") String pGuid,
            @Field("authorAccount") String authorAccount
    );

    /**
     * 图说详情2.1（不含评论）（点赞列表仅返回最新十条，并返回点赞总条数）
     *
     * @param account       行账号
     * @param pGuid         图说主键
     * @param authorAccount 图说作者行账号
     */
    @FormUrlEncoded
    @POST("PhotoTopicDetailNoC2_1")
    Call<PhotoTopicRepo> PhotoTopicDetailNoC2_1(
            @Field("account") String account,
            @Field("P_Guid") String pGuid,
            @Field("authorAccount") String authorAccount
    );

    /**
     * 图说点赞/取消赞
     *
     * @param account   行账号
     * @param pGuid     图说主键
     * @param isPraise  1-点赞 0-取消赞
     * @param toAccount 图说作者行账号
     */
    @FormUrlEncoded
    @POST("PhotoTopicPraiseAdd")
    Call<XKRepo> photoTopicPraiseAdd(
            @Field("account") String account,
            @Field("P_Guid") String pGuid,
            @Field("isPraise") String isPraise,
            @Field("toAccount") String toAccount
    );

    /**
     * 图说评论点赞/取消赞
     *
     * @param account   行账号
     * @param pGuid     图说主键
     * @param pcGuid    图说评论主键
     * @param toAccount 图说作者行账号
     * @param isPraise  1-点赞 0-取消赞
     */
    @FormUrlEncoded
    @POST("PhotoTopicCommentPraiseAdd")
    Call<XKRepo> photoTopicCommentPraiseAdd(
            @Field("account") String account,
            @Field("P_Guid") String pGuid,
            @Field("PC_Guid") String pcGuid,
            @Field("toAccount") String toAccount,
            @Field("isPraise") String isPraise
    );

    /**
     * 图说收藏/取消收藏
     *
     * @param account   行账号
     * @param pGuid     图说主键
     * @param isCollect 1-收藏 0-取消收藏
     * @param toAccount 图说作者行账号
     */
    @FormUrlEncoded
    @POST("PhotoTopicCollectionAdd")
    Call<XKRepo> photoTopicCollectionAdd(
            @Field("account") String account,
            @Field("P_Guid") String pGuid,
            @Field("isCollect") String isCollect,
            @Field("toAccount") String toAccount
    );


    /**
     * 发布评论
     *
     * @param account   行账号
     * @param pGuid     图说主键
     * @param content   评论内容
     * @param isReply   1-是回帖，0-非回帖
     * @param toUser    回复谁
     * @param toGuid    回复评论顶级序列
     * @param toAccount 图说作者行账号
     */
    @FormUrlEncoded
    @POST("PhotoTopicCommentAdd")
    Call<XKRepo> photoTopicCommentAdd(
            @Field("account") String account,
            @Field("P_Guid") String pGuid,
            @Field("content") String content,
            @Field("isReply") String isReply,
            @Field("toUser") String toUser,
            @Field("toGuid") String toGuid,
            @Field("toAccount") String toAccount,
            @Field("pushAccount") String pushAccount
    );


    /**
     * 删除单个评论或回复
     *
     * @param account       行账号
     * @param pcGuid        评论主键Guid
     * @param authorAccount 图说发布人行账号
     */
    @FormUrlEncoded
    @POST("DeletePhotoTopicComment")
    Call<XKRepo> deletePhotoTopicComment(
            @Field("account") String account,
            @Field("PC_Guid") String pcGuid,
            @Field("authorAccount") String authorAccount
    );

    /**
     * 加关注
     *
     * @param account       行账号
     * @param friendAccount 被加关注的账号
     */
    @FormUrlEncoded
    @POST("AddFriend")
    Call<XKRepo> addFriend(
            @Field("account") String account,
            @Field("friendAccount") String friendAccount
    );

    /**
     * 取消关注
     *
     * @param account       行账号
     * @param friendAccount 被取消的账号
     */
    @FormUrlEncoded
    @POST("DelFriend")
    Call<XKRepo> delFriend(
            @Field("account") String account,
            @Field("friendAccount") String friendAccount
    );

    /**
     * 我的各种统计信息
     *
     * @param account 行账号
     */
    @FormUrlEncoded
    @POST("GetUserCount")
    Call<XKRepo> getUserCount(
            @Field("account") String account
    );

    /**
     * 官方推荐用户
     *
     * @param tagId   用户标签id,0是所有
     * @param version 客户端当前版本号
     * @param account 行账号
     */
    @FormUrlEncoded
    @POST("RecommendUserList")
    Call<XKRepo> recommendUserList(
            @Field("account") String account,
            @Field("tagId") String tagId,
            @Field("version") String version
    );

    /**
     * 图说详情（仅评论带翻页）
     *
     * @param account
     * @param pGuid
     * @param authorAccount
     * @param page
     * @param perpage
     */
    @FormUrlEncoded
    @POST("PhotoTopicDetailOnlyCPages")
    Call<CommentRepo> photoTopicDetailOnlyCPages(
            @Field("account") String account,
            @Field("P_Guid") String pGuid,
            @Field("authorAccount") String authorAccount,
            @Field("page") String page,
            @Field("perpage") String perpage
    );

    /**
     * 我的图说收藏
     *
     * @param account 行账号
     * @param page    页码
     * @param perpage 每页条数
     */
    @FormUrlEncoded
    @POST("MyPhotoTopicCollections")
    Call<UserCollectionRepo> myPhotoTopicCollections(
            @Field("account") String account,
            @Field("page") String page,
            @Field("perpage") String perpage
    );

    /**
     * 获取我关注的人列表
     *
     * @param account 行账号
     */
    @FormUrlEncoded
    @POST("MyEachList")
    Call<MyFriendInfoRepo> myEachList(
            @Field("account") String account
    );

    /**
     * 获取我的粉丝列表
     *
     * @param account
     */
    @FormUrlEncoded
    @POST("MyFansList")
    Call<MyFriendInfoRepo> myFansList(
            @Field("account") String account
    );

    /**
     * 获取我的好友列表
     *
     * @param account 行账号
     */
    @FormUrlEncoded
    @POST("MyFriendList")
    Call<MyFriendInfoRepo> myFriendList(
            @Field("account") String account
    );


    /**
     * 获取关注的人,好友,粉丝列表
     *
     * @param myAccount 查看人账号
     * @param account   被查看行帐号(和myAccount值一样则是若查看自己的)
     * @param type      返回列表类型：1是好友列表，2是关注列表，3是粉丝列表
     * @param page      页码
     * @param perpage   每页条数
     */
    @FormUrlEncoded
    @POST("UsersList")
    Call<MyFriendInfoRepo> myUsersList(
            @Field("myAccount") String myAccount,
            @Field("account") String account,
            @Field("type") String type,
            @Field("page") String page,
            @Field("perpage") String perpage
    );

    /**
     * 获取图说点赞列表
     *
     * @param account       行账号
     * @param pGuid         图说主表外键
     * @param authorAccount 图说主表外键
     * @param page          页码
     * @param perPage       每页条数
     */
    @FormUrlEncoded
    @POST("PhotoTopicPraiseListPage")
    Call<PraiseRepo> photoTopicPraiseListPage(
            @Field("account") String account,
            @Field("P_Guid") String pGuid,
            @Field("authorAccount") String authorAccount,
            @Field("page") String page,
            @Field("perpage") String perPage
    );

    /**
     * 获取我的图说点赞人列表
     *
     * @param account   行账号
     * @param toAccount 要查看的行账号
     * @param page      页码
     * @param perPage   每页条数
     */
    @FormUrlEncoded
    @POST("MyPhotoTopicPraiseListPage")
    Call<PraiseListRepo> myPhotoTopicPraiseListPage(
            @Field("account") String account,
            @Field("toAccount") String toAccount,
            @Field("page") String page,
            @Field("perpage") String perPage
    );

    /**
     * 获取七牛Token
     *
     * @param randomStr 随机一个加密字符串
     */
    @FormUrlEncoded
    @POST("GetQiniuToken")
    Call<XKRepo> getQiniuToken(
            @Field("randomStr") String randomStr
    );

    /**
     * 提交图说举报
     *
     * @param account       举报人行账号
     * @param pGuid         被举报图说主表外键
     * @param authorAccount 被举报图说发布人行账号
     */
    @FormUrlEncoded
    @POST("PhotoTopicReportListAdd")
    Call<XKRepo> photoTopicReportListAdd(
            @Field("account") String account,
            @Field("P_Guid") String pGuid,
            @Field("toAccount") String authorAccount
    );

    /**
     * 提交图说举报2.0
     *
     * @param account       举报人行账号
     * @param pGuid         被举报图说主表外键
     * @param authorAccount 被举报图说发布人行账号
     * @param type          举报类型
     */
    @FormUrlEncoded
    @POST("PhotoTopicReportListAdd2")
    Call<XKRepo> photoTopicReportListAdd2(
            @Field("account") String account,
            @Field("P_Guid") String pGuid,
            @Field("toAccount") String authorAccount,
            @Field("type") String type
    );

    /**
     * 图说举报类型列表
     *
     * @param version 客户端当前版本号
     */
    @FormUrlEncoded
    @POST("PhotoTopicReportTypeList")
    Call<ReportInfo> photoTopicReportTypeList(
            @Field("version") String version
    );

    /**
     * 根据行账号或昵称搜索用户
     *
     * @param account 用户行账号
     * @param pGuid   搜索关键字
     * @param page    页码
     * @param perPage 每页条数
     */
    @FormUrlEncoded
    @POST("ReturnSearchUser")
    Call<XKRepo> returnSearchUser(
            @Field("account") String account,
            @Field("searchStr") String pGuid,
            @Field("page") String page,
            @Field("perpage") String perPage
    );

    /**
     * 推送消息列表
     *
     * @param account  行账号
     * @param pushType 消息类型, 0表示全部；1：发布图说@；2：评论图说@；3：评论图说；4：评论图说下的评论；5：图说点赞；6：图说评论点赞；7：图说收藏；8：关注。
     * @param readType 阅读类型, 0：未读；1：已读；-1：全部
     * @param page     页码
     * @param perPage  每页条数
     */
    @FormUrlEncoded
    @POST("PhotoTopicPushList")
    Call<PushMessageRepo> photoTopicPushList(
            @Field("account") String account,
            @Field("pushtype") String pushType,
            @Field("readtype") String readType,
            @Field("page") String page,
            @Field("perpage") String perPage
    );

    /**
     * 官方系统消息列表
     *
     * @param attr    违规筛选。9：全部；0：普通；1：违规通知（2.0以上版本新增参数）
     * @param os      操作系统。9：取出所有记录；0：所有操作系统；1：Android；2：iOS（2.0以上版本新增参数）
     * @param account 行账号
     * @param type    消息类型 0：全部；1：外部链接；2：跳转到图说
     * @param page    页码
     * @param perPage 每页条数
     * @return
     */
    @FormUrlEncoded
    @POST("OfficialMessageList2")
    Call<OfficialMessageRepo> officialMessageList(
            @Field("account") String account,
            @Field("type") String type,
            @Field("attr") String attr,
            @Field("os") String os,
            @Field("page") String page,
            @Field("perpage") String perPage
    );

    /**
     * 官方系统消息详情
     *
     * @param account 行账号
     * @param o_ID    官方系统消息表主键ID
     */
    @FormUrlEncoded
    @POST("OfficialMessageDetail")
    Call<XKRepo> officialMessageDetail(
            @Field("account") String account,
            @Field("o_ID") String o_ID
    );

    /**
     * 分类推送消息未读数（其中官方消息返回总数）
     *
     * @param account 行账号
     * @param os      操作系统。9：取出所有记录；0：所有操作系统；1：Android；2：iOS
     */
    @FormUrlEncoded
    @POST("PushNumMessageNoRead")
    Call<MessageNoReadNum> getPushNumMessageNoRead(
            @Field("account") String account,
            @Field("os") String os
    );

    /**
     * 用户名片
     *
     * @param myAccount 查看者行账号
     * @param account   被查看者行账号
     */
    @FormUrlEncoded
    @POST("UserBusinessCard")
    Call<UserCard> getUserBusinessCard(
            @Field("myAccount") String myAccount,
            @Field("account") String account
    );

    /**
     * 删除单篇图说
     *
     * @param account 行账号
     * @param pGuid   图说主键
     * @param author  图说作者
     */
    @FormUrlEncoded
    @POST("DeletePhotoTopic")
    Call<XKRepo> deletePhotoTopic(
            @Field("account") String account,
            @Field("P_Guid") String pGuid,
            @Field("authorAccount") String author
    );

    /**
     * 图说阅读回写,用于记录图说阅读量
     *
     * @param pGuid  图说主键
     * @param author 图说发布人
     */
    @FormUrlEncoded
    @POST("PhotoTopicReadAdd")
    Call<XKRepo> photoTopicReadAdd(
            @Field("P_Guid") String pGuid,
            @Field("toAccount") String author
    );

    /**
     * 修改好友备注
     *
     * @param account       用户帐号
     * @param friendAccount 被修改帐号
     * @param value         备注
     */
    @FormUrlEncoded
    @POST("EditRemark")
    Call<XKRepo> editRemark(
            @Field("account") String account,
            @Field("friendAccount") String friendAccount,
            @Field("value") String value

    );

    /**
     * 更新最近在线时间
     *
     * @param account 用户行咖号
     */
    @FormUrlEncoded
    @POST("EditLastAliveTime")
    Call<XKRepo> editLastAliveTime(
            @Field("account") String account
    );

    /**
     * 签到
     *
     * @param account 用户行咖号
     */
    @FormUrlEncoded
    @POST("SignIn")
    Call<SignInRepo> signIn(
            @Field("account") String account
    );

    /**
     * 分享获取抽奖机会
     *
     * @param account 用户行咖号
     */
    @FormUrlEncoded
    @POST("ShareLotteryChance")
    Call<XKRepo> shareLotteryChance(
            @Field("account") String account
    );

    /**
     * 旅拼主页
     *
     * @param account     行账号，未登录-0
     * @param onlyFriends 是否只显示关注，0：所有；1：关注；2：最新（不包括关注）
     * @param timeStamp   时间戳，格式“yyyy-MM-dd HH:mm:ss”
     * @param page        页面
     * @param pageSize    每页条数
     */
    @FormUrlEncoded
    @POST("TravelTogetherMain")
    Call<TrackwayInfoListRepo> getTravelTogetherMain(
            @Field("account") String account,
            @Field("onlyFriends") String onlyFriends,
            @Field("timeStamp") String timeStamp,
            @Field("page") String page,
            @Field("perpage") String pageSize
    );


    /**
     * 删除单篇旅拼
     *
     * @param account       行账号，未登录-0
     * @param T_Guid        旅拼主键ID
     * @param authorAccount 旅拼发布人行账号
     */
    @FormUrlEncoded
    @POST("DeleteTravelTogether")
    Call<XKRepo> deleteTravelTogether(
            @Field("account") String account,
            @Field("T_Guid") String T_Guid,
            @Field("authorAccount") String authorAccount
    );

    /**
     * 旅拼详情（不含评论）
     *
     * @param account       行账号
     * @param T_Guid        旅拼主键
     * @param authorAccount 旅拼作者行账号
     */
    @FormUrlEncoded
    @POST("TravelTogetherDetailNoC")
    Call<TrackwayInfoRepo> travelTogetherDetailNoC(
            @Field("account") String account,
            @Field("T_Guid") String T_Guid,
            @Field("authorAccount") String authorAccount
    );

    /**
     * 旅拼详情（仅评论带翻页）
     *
     * @param account       行账号
     * @param T_Guid        旅拼主键
     * @param authorAccount 旅拼作者行账号
     */
    @FormUrlEncoded
    @POST("TravelTogetherDetailOnlyCPages")
    Call<CommentRepo> travelTogetherDetailOnlyCPages(
            @Field("account") String account,
            @Field("T_Guid") String T_Guid,
            @Field("authorAccount") String authorAccount,
            @Field("page") String page,
            @Field("perpage") String perpage
    );

    /**
     * 我的旅拼
     *
     * @param account  行账号
     * @param isHidden 控制查看仅自己可见内容,0:不允许显示(查看别人); 1.允许查看(查看自己)
     * @param page     页码
     * @param perpage  每页条数
     */
    @FormUrlEncoded
    @POST("MyTravelTogether")
    Call<MyTrackwayRepo> myTravelTogether(
            @Field("account") String account,
            @Field("isHidden") String isHidden,
            @Field("page") String page,
            @Field("perpage") String perpage
    );

    /**
     * 旅拼阅读回写
     *
     * @param T_Guid 旅拼主键
     * @param author 旅拼发布人
     */
    @FormUrlEncoded
    @POST("TravelTogetherReadAdd")
    Call<XKRepo> travelTogetherReadAdd(
            @Field("T_Guid") String T_Guid,
            @Field("toAccount") String author
    );

    /**
     * 旅拼分享回写
     *
     * @param T_Guid 旅拼主键
     * @param author 旅拼发布人
     */
    @FormUrlEncoded
    @POST("TravelTogetherShareAdd")
    Call<XKRepo> travelTogetherShareAdd(
            @Field("T_Guid") String T_Guid,
            @Field("toAccount") String author
    );


    /**
     * 旅拼点赞/取消赞
     *
     * @param account   行账号
     * @param T_Guid    旅拼主键
     * @param isPraise  1-点赞 0-取消赞
     * @param toAccount 旅拼作者行账号
     */
    @FormUrlEncoded
    @POST("TravelTogetherPraiseAdd ")
    Call<XKRepo> travelTogetherPraiseAdd(
            @Field("account") String account,
            @Field("T_Guid") String T_Guid,
            @Field("isPraise") String isPraise,
            @Field("toAccount") String toAccount
    );

    /**
     * 旅拼点赞列表
     *
     * @param account       行账号
     * @param T_Guid        旅拼主表外键
     * @param authorAccount 旅拼主表外键
     * @param page          页码
     * @param perPage       每页条数
     */
    @FormUrlEncoded
    @POST("TravelTogetherPraiseListPage")
    Call<PraiseRepo> travelTogetherPraiseListPage(
            @Field("account") String account,
            @Field("T_Guid") String T_Guid,
            @Field("authorAccount") String authorAccount,
            @Field("page") String page,
            @Field("perpage") String perPage
    );

    /**
     * 获取我的旅拼点赞人列表
     *
     * @param account   行账号
     * @param toAccount 要查看的行账号
     * @param page      页码
     * @param perPage   每页条数
     */
    @FormUrlEncoded
    @POST("MyTravelTogetherPraiseListPage")
    Call<PraiseListRepo> myTravelTogetherPraiseListPage(
            @Field("account") String account,
            @Field("toAccount") String toAccount,
            @Field("page") String page,
            @Field("perpage") String perPage
    );

    /**
     * 旅拼收藏/取消收藏
     *
     * @param account   行账号
     * @param T_Guid    旅拼主键
     * @param isCollect 1-收藏 0-取消收藏
     * @param toAccount 旅拼作者行账号
     */
    @FormUrlEncoded
    @POST("TravelTogetherCollectionAdd")
    Call<XKRepo> travelTogetherCollectionAdd(
            @Field("account") String account,
            @Field("T_Guid") String T_Guid,
            @Field("isCollect") String isCollect,
            @Field("toAccount") String toAccount
    );


    /**
     * 发布评论
     *
     * @param account   行账号
     * @param T_Guid    旅拼主键
     * @param content   评论内容
     * @param isReply   1-是回帖，0-非回帖
     * @param toUser    回复谁
     * @param toGuid    回复评论顶级序列
     * @param toAccount 旅拼作者行账号
     */
    @FormUrlEncoded
    @POST("TravelTogetherCommentAdd ")
    Call<XKRepo> travelTogetherCommentAdd(
            @Field("account") String account,
            @Field("T_Guid") String T_Guid,
            @Field("content") String content,
            @Field("isReply") String isReply,
            @Field("toUser") String toUser,
            @Field("toGuid") String toGuid,
            @Field("toAccount") String toAccount
    );


    /**
     * 删除单个评论或回复
     *
     * @param account       行账号
     * @param TC_Guid       评论主键Guid
     * @param authorAccount 旅拼发布人行账号
     */
    @FormUrlEncoded
    @POST("DeleteTravelTogetherComment")
    Call<XKRepo> deleteTravelTogetherComment(
            @Field("account") String account,
            @Field("TC_Guid") String TC_Guid,
            @Field("authorAccount") String authorAccount
    );

    /**
     * 提交旅拼举报
     *
     * @param account       举报人行账号
     * @param T_Guid        被举报旅拼主表外键
     * @param authorAccount 被举报旅拼发布人行账号
     * @param type          举报类型
     */
    @FormUrlEncoded
    @POST("TravelTogetherReportListAdd")
    Call<XKRepo> travelTogetherReportListAdd(
            @Field("account") String account,
            @Field("T_Guid") String T_Guid,
            @Field("toAccount") String authorAccount,
            @Field("type") String type
    );

    /**
     * 我的旅拼收藏
     *
     * @param account 行账号
     * @param page    页码
     * @param perpage 每页条数
     */
    @FormUrlEncoded
    @POST("MyTravelTogetherCollections ")
    Call<TrackwayCollectionRepo> myTravelTogetherCollections(
            @Field("account") String account,
            @Field("page") String page,
            @Field("perpage") String perpage
    );

    /**
     * 三方帐号登录
     *
     * @param sign     识别码
     * @param img      头像字符
     * @param isNewImg 是否要换新头像
     * @param nick     昵称
     * @param type     三方类型(QQ,WX,SH,XL)
     */
    @FormUrlEncoded
    @POST("OtherLogin")
    Call<UserRepo> otherLogin(
            @Field("sign") String sign,
            @Field("img") String img,
            @Field("isNewImg") String isNewImg,
            @Field("nick") String nick,
            @Field("type") String type

    );

    /**
     * 三方帐号是否已经绑定
     *
     * @param sign 识别码
     * @param type 三方类型(QQ,WX,SH,XL)
     */
    @FormUrlEncoded
    @POST("IsOtherAccountRegister")
    Call<XKRepo> isOtherAccountRegister(
            @Field("type") String type,
            @Field("sign") String sign

    );

    /**
     * 手机号码是否已经被注册
     *
     * @param phoneNum   手机号
     * @param countryNum 国家码
     */
    @FormUrlEncoded
    @POST("IsRegisterByPhone")
    Call<XKRepo> isRegisterByPhone(
            @Field("countryNum") String countryNum,
            @Field("phoneNum") String phoneNum

    );

    /**
     * 用户登录(带第三方账号绑定)
     *
     * @param countryNum 国家码（版本3以后新增）
     * @param account    帐号或手机号码
     * @param userPwd    6-18位用户密码
     * @param OS         操作系统 1：安卓，2：iOS（版本2以后新增）
     * @param equip      设备型号（版本2以后新增）
     * @param type       三方类型(QQ,WX,SH,XL)
     * @param sign       识别码
     */
    @FormUrlEncoded
    @POST("UserLoginBindOtherAcc")
    Call<UserRepo> userLoginBindOtherAcc(
            @Field("countryNum") String countryNum,
            @Field("account") String account,
            @Field("userPwd") String userPwd,
            @Field("OS") String OS,
            @Field("equip") String equip,
            @Field("type") String type,
            @Field("sign") String sign

    );

    /**
     * 发布旅拼
     *
     * @param account   行账号
     * @param jsonValue 图说内容json格式数据
     */
    @FormUrlEncoded
    @POST("TravelTogetherAdd")
    Call<XKRepo> addTravelTogether(
            @Field("account") String account,
            @Field("jsonValue") String jsonValue
    );


    /**
     * 图说主页推荐列表
     *
     * @param account 行账号，未登录用0
     * @param page    页码
     * @param perpage 每页条数
     * @return
     */
    @FormUrlEncoded
    @POST("PhotoTopicRecommendList")
    Call<PhotoTopicListRepo> photoTopicRecommendList(
            @Field("account") String account,
            @Field("page") String page,
            @Field("perpage") String perpage
    );

    @FormUrlEncoded
    @POST("PhotoTopicMain2_2")
    Call<PhotoTopicListRepo> photoTopicMain(
            @Field("account") String account,
            @Field("onlyFriends") String onlyFriends,
            @Field("page") String page,
            @Field("perpage") String perpage,
            @Field("timeStamp") String timeStamp
    );

    /**
     * 图说图说类型列表列表
     *
     * @param version 客户端当前版本号
     * @return
     */
    @FormUrlEncoded
    @POST("OfficialPhotoTopicTypeList")
    Call<OfficialPhotoTopicTypeRepo> officialPhotoTopicTypeList(
            @Field("version") String version
    );



    /**
     * 云信加好友
     *
     * @param accid  行账号
     * @param faccid 对方行账号
     */
    @FormUrlEncoded
    @POST("IM_AddFriend")
    Call<XKRepo> addIMFriend(
            @Field("accid") String accid,
            @Field("faccid") String faccid
    );

    /**
     * 云信好友添加备注
     *
     * @param accid  行账号
     * @param faccid 对方行账号
     * @param alias  备注名
     */
    @FormUrlEncoded
    @POST("IM_UpdateFriend")
    Call<XKRepo> addIMFriendRemark(
            @Field("accid")  String accid,
            @Field("faccid") String faccid,
            @Field("alias")  String alias
    );

    /**
     * 云信删除好友
     *
     * @param accid  行账号
     * @param faccid 对方行账号
     */
    @FormUrlEncoded
    @POST("IM_DeleteFriend")
    Call<XKRepo> delIMFriend(
            @Field("accid") String accid,
            @Field("faccid") String faccid
    );

    /**
     * 云信拉黑好友
     *
     * @param accid  行账号
     * @param taccid 对方行账号
     */
    @FormUrlEncoded
    @POST("IM_AddBlacklist")
    Call<XKRepo> addBlacklist(
            @Field("accid") String accid,
            @Field("taccid") String taccid
    );

    /**
     * 取消黑名单
     *
     * @param accid  行账号
     * @param taccid 对方行账号
     */
    @FormUrlEncoded
    @POST("IM_DeleteBlacklist")
    Call<XKRepo> deleteBlacklist(
            @Field("accid") String accid,
            @Field("taccid") String taccid
    );

    /**
     * 注册云信用户
     *
     * @param accid  行账号
     */
    @FormUrlEncoded
    @POST("IM_CreateUser")
    Call<XKRepo> createIMUser(
            @Field("accid") String accid
    );


}

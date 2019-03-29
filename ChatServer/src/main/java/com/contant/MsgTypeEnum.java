package com.contant;

/**
 * description：
 *
 * @author ajie
 * data 2018/11/26 21:05
 */
public enum MsgTypeEnum {
    /**
     * 用户登录消息
     */
    EN_MSG_LOGIN,
    /**
     *  用户注册消息
     */
    EN_MSG_REGISTER,
    /**
     * 用户忘记密码消息
     */
    EN_MSG_FORGET_PWD,
    /**
     * 修改密码休息
     */
    EN_MSG_MODIFY_PWD,
    /**
     * 一对一聊天消息
     */
    EN_MSG_CHAT,
    /**
     * 用户注销
     */
    EN_MSG_LOGOUTACCOUNT,
    /**
     * 退出登录
     */
    EN_MSG_SIGNOUT,
    /**
     * 更换邮箱
     */
    EN_MSG_MODIFY_EMAIL,
    /**
     * 用户信息查看
     */
    EN_MSG_USER_INFO,
    /**
     * 群聊消息
     */
    EN_MSG_CHAT_ALL,
    /**
     * 群聊确认消息
     */
    EN_MSG_CHAT_ALL_ACK,
    /**
     * 单聊确认信息
     */
    EN_MSG_CHAT_ACK,
    /**
     * 群发用户上线消息
     */
    EN_MSG_NOTIFY_ONLINE,
    /**
     * 群发用户下线消息
     */
    EN_MSG_NOTIFY_OFFLINE,
    /**
     * 用户下线消息
     */
    EN_MSG_OFFLINE,
    /**
     * 获取所有在线用户信息
     */
    EN_MSG_GET_ALL_USERS,
    /**
     * 传输文件消息
     */
    EN_MSG_TRANSFER_FILE,
    /**
     * 传输文件确认消息
     */
    EN_MSG_TRANSFER_FILE_ACK,
    /**
     * 用户是否存在【新增】
     */
    EN_MSG_CHECK_USER_EXIST,
    /**
     * 是否存在离线消息【新增】
     */
    EN_MSG_OFFLINE_MSG_EXIST,
    /**
     * 是否存在离线文件【新增】
     */
    EN_MSG_OFFLINE_FILE_EXIST,
    /**
     * 响应消息
     */
    EN_MSG_ACK;
}

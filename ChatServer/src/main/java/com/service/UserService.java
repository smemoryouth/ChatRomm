package com.service;

import com.bean.User;

/**
 * description：
 *
 * @author ajie
 * data 2018/11/29 18:18
 */
public interface UserService {
    /**
     * 用户登录验证
     * @param name
     * @param psw
     * @return
     */
    boolean loginVerification(String name, String psw);

    /**
     * 用户注册验证
     * @param name
     * @param psw
     * @param email
     * @return
     */
    boolean registerVerification(String name, String psw, String email);

    /**
     * 忘记密码验证
     * @param name
     * @param email
     * @return
     */
    boolean retrievePassword(String name, String email);

    /**
     * 修改密码验证
     * @param name
     * @param oldPsw
     * @param newPsw
     * @param email
     * @return
     */
    boolean modifyPswVerification(String name, String oldPsw, String newPsw, String email);

    /**
     * 注销账户验证
     * @param name
     * @param psw
     * @param email
     * @return
     */
    boolean logoutVerification(String name, String psw, String email);

    /**
     * 修改邮箱验证
     * @param name
     * @param psw
     * @param email
     * @param newEmail
     * @return
     */
    boolean modifyEmailVerification(String name, String psw, String email, String newEmail);

    /**
     * 查看用户信息
     * @param name
     * @return
     */
    User getUserInfo(String name);

    /**
     * 查看用户是否存在
     * @param name
     * @return
     */
    boolean isExist(String name);
}

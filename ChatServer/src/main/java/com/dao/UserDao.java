package com.dao;

import com.bean.User;

/**
 * description：操作数据库的方法
 *
 * @author ajie
 * data 2018/12/3 20:05
 */
public interface UserDao {
    /**
     * 获取用户信息
     * @param name
     * @return
     */
    User getUser(String name);

    /**
     * 插入新用户信息
     * @param user
     * @return
     */
    int insertUser(User user);

    /**
     * 删除用户信息
     * @param name
     * @return
     */
    int deleteUser(String name);

    /**
     * 更新用户信息
     * @param name
     * @param password
     * @param email
     * @return
     */
    int updateUser(String name, String password, String email);
}

package com.dao.impl;

import com.bean.User;
import com.dao.DaoException;
import com.dao.UserDao;
import com.util.C3p0Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * description：操作数据库的具体操作
 *
 * @author ajie
 * data 2018/11/29 20:05
 */
public class UserDaoImpl implements UserDao {

        @Override
        public User getUser(String name) {
            Connection conn = null;
            PreparedStatement st = null;
            ResultSet rs = null;
            User user = null;
            try {
                // 建立连接
                conn = C3p0Utils.getConnection();
                // 创建语句
                String sql = "select * from user where name = ?";
                st = conn.prepareStatement(sql);
                st.setString(1, name);
                // 执行语句
                rs = st.executeQuery();
                while (rs.next()){
                    user = new User(rs.getString("name"),
                            rs.getString("psw"),
                            rs.getString("email"));
                }
            } catch (SQLException e) {
                throw new DaoException(e.getMessage(), e);
            } finally {
                C3p0Utils.closeConn(conn, st, rs);
            }
            return user;
    }

    @Override
    public int insertUser(User user) {
        Connection conn = null;
        PreparedStatement st = null;
        try {
            // 建立连接
            conn = C3p0Utils.getConnection();
            // 创建语句
            String sql = "insert into user(name, psw, email) values (?, ?, ?)";
            st = conn.prepareStatement(sql);
            st.setString(1, user.getName());
            st.setString(2, user.getPsw());
            st.setString(3, user.getEmail());
            // 执行语句
            return st.executeUpdate();
        }catch (Exception e){
            throw new DaoException(e.getMessage(), e);
        }finally {
            C3p0Utils.closeConn(conn, st, null);
        }
    }

    @Override
    public int deleteUser(String name) {
        Connection conn = null;
        PreparedStatement st = null;
        try {
            // 建立连接
            conn = C3p0Utils.getConnection();
            // 创建语句
            String sql = "delete from user where name = ?";
            st = conn.prepareStatement(sql);
            st.setString(1, name);
            // 执行语句
            return st.executeUpdate();
        }catch (Exception e){
            throw new DaoException(e.getMessage(), e);
        }finally {
            C3p0Utils.closeConn(conn, st, null);
        }
    }


    @Override
    public int updateUser(String name, String password, String email) {
        Connection conn = null;
        PreparedStatement st = null;
        try {
            // 建立连接
            conn = C3p0Utils.getConnection();
            // 创建语句
            if (password != null) {
                String sql = "update user set psw = ? where name = ?";
                st = conn.prepareStatement(sql);
                st.setString(1, password);
                st.setString(2, name);
            } else {
                String sql = "update user set email = ? where name = ?";
                st = conn.prepareStatement(sql);
                st.setString(1, email);
                st.setString(2, name);
            }
            // 执行语句
            return st.executeUpdate();
        }catch (Exception e){
            throw new DaoException(e.getMessage(), e);
        }finally {
            C3p0Utils.closeConn(conn, st, null);
        }
    }
}

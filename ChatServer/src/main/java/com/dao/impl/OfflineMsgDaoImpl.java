package com.dao.impl;

import com.bean.OfflineMsg;
import com.dao.DaoException;
import com.dao.OfflineMsgDao;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.util.C3p0Utils;
import com.util.JsonUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description：
 *
 * @author ajie
 * data 2018/12/13 0:12
 */
public class OfflineMsgDaoImpl implements OfflineMsgDao {

    @Override
    public int insertChatMsg(Object msg) {
        Connection conn = null;
        PreparedStatement st = null;
        try {
            ObjectNode node = JsonUtil.getObjectNode(msg.toString());
            String fromName = node.get("fromName").asText();
            String toName = node.get("toName").asText();
            String messsage = node.get("msg").asText();
            int type = 1;
            // 建立连接
            conn = C3p0Utils.getConnection();
            // 创建语句
            String sql = "insert into offline_msg(to_name, from_name, msg_type, msg) values (?, ?, ?, ?)";
            st = conn.prepareStatement(sql);
            st.setString(1, toName);
            st.setString(2, fromName);
            st.setInt(3, type);
            st.setString(4, messsage);
            // 执行语句
            return st.executeUpdate();
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        } finally {
            C3p0Utils.closeConn(conn, st, null);
        }
    }

    @Override
    public int insertFileMsg(OfflineMsg msg) {
        Connection conn = null;
        PreparedStatement st = null;
        try {
            // 建立连接
            conn = C3p0Utils.getConnection();
            // 创建语句
            String sql = "insert into offline_msg(to_name, from_name, fileName, msg_type, msg) values (?, ?, ?, ?, ?)";
            st = conn.prepareStatement(sql);
            st.setString(1, msg.getToName());
            st.setString(2, msg.getFromName());
            st.setString(3, msg.getFileName());
            st.setInt(4, msg.getType());
            st.setString(5, msg.getMsg());
            // 执行语句
            return st.executeUpdate();
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        } finally {
            C3p0Utils.closeConn(conn, st, null);
        }
    }

    @Override
    public void deleteMsg(String name, int type) {
        Connection conn = null;
        PreparedStatement st = null;
        try {
            // 建立连接
            conn = C3p0Utils.getConnection();
            // 创建语句
            String sql = "delete from  offline_msg where to_name = ?";
            st = conn.prepareStatement(sql);
            st.setString(1, name);
            // 执行语句
            if (type == 1) {
                st.executeUpdate();
            }
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        } finally {
            C3p0Utils.closeConn(conn, st, null);
        }
    }

    @Override
    public void deleteFile(String name, int type) {
        Connection conn = null;
        PreparedStatement st = null;
        try {
            // 建立连接
            conn = C3p0Utils.getConnection();
            // 创建语句
            String sql = "delete from  offline_msg where to_name = ?";
            st = conn.prepareStatement(sql);
            st.setString(1, name);
            // 执行语句
            if (type == 2) {
                st.executeUpdate();
            }
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        } finally {
            C3p0Utils.closeConn(conn, st, null);
        }
    }

    @Override
    public ConcurrentHashMap<Integer, OfflineMsg> getMsg(String name) {
        ConcurrentHashMap<Integer, OfflineMsg> map;
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs;
        OfflineMsg msg;
        try {
            map = new ConcurrentHashMap<>();
            // 建立连接
            conn = C3p0Utils.getConnection();
            // 创建语句
            String sql = "select * from  offline_msg where to_name = ?";
            st = conn.prepareStatement(sql);
            st.setString(1, name);
            // 执行语句
            rs = st.executeQuery();
            while (rs.next()) {
                msg = new OfflineMsg(rs.getInt("id"),
                        rs.getString("to_name"),
                        rs.getString("from_name"),
                        rs.getInt("msg_type"),
                        rs.getString("fileName"),
                        rs.getString("msg"));

                if (msg.getType() == 1) {
                    map.put(msg.getId(), msg);
                }
            }
            return map;
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        } finally {
            C3p0Utils.closeConn(conn, st, null);
        }
    }

    @Override
    public ConcurrentHashMap<Integer, OfflineMsg> getFile(String name) {
        ConcurrentHashMap<Integer, OfflineMsg> map;
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs;
        OfflineMsg msg;
        try {
            map = new ConcurrentHashMap<>();
            // 建立连接
            conn = C3p0Utils.getConnection();
            // 创建语句
            String sql = "select * from  offline_msg where to_name = ?";
            st = conn.prepareStatement(sql);
            st.setString(1, name);
            // 执行语句
            rs = st.executeQuery();
            while (rs.next()) {
                msg = new OfflineMsg(rs.getInt("id"),
                        rs.getString("to_name"),
                        rs.getString("from_name"),
                        rs.getInt("msg_type"),
                        rs.getString("fileName"),
                        rs.getString("msg"));

                if (msg.getType() == 2) {
                    map.put(msg.getId(), msg);
                }
            }
            return map;
        } catch (Exception e) {
            throw new DaoException(e.getMessage(), e);
        } finally {
            C3p0Utils.closeConn(conn, st, null);
        }
    }
}

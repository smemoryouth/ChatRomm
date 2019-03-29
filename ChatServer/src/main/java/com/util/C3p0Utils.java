package com.util;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * description：
 *
 * @author ajie
 * data 2018/11/29 18:15
 */
public class C3p0Utils {

    private static DataSource dataSource;

    static {
        dataSource = new ComboPooledDataSource("mysql");
    }

    /**
     * 获取数据库连接
     *
     * @return
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     * @param conn
     * @param par
     * @param re
     */
    public static void closeConn(Connection conn, PreparedStatement par, ResultSet re) {
        try {
            if (conn != null && conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (par != null) {
            try {
                par.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (re != null) {
            try {
                re.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}


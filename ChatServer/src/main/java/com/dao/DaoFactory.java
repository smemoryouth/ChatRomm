package com.dao;

import java.io.InputStream;
import java.util.Properties;

/**
 * description：dao工厂获取对象
 *
 * @author ajie
 * data 2018/11/29 18:22
 */
public class DaoFactory {
    private static UserDao userDao = null;
    private static OfflineMsgDao msgDao = null;
    private static DaoFactory daoFactory = new DaoFactory();

    private DaoFactory() {
        try {
            Properties prop = new Properties();
            InputStream inStream = DaoFactory.class.getClassLoader()
                    .getResourceAsStream("dao-config.properties");
            prop.load(inStream);
            String userDaoClass = prop.getProperty("userDaoClass");
            String msgDaoClass = prop.getProperty("offline_msgDaoClass");
            Class userClazz = Class.forName(userDaoClass);
            Class msgClazz = Class.forName(msgDaoClass);
            userDao = (UserDao) userClazz.getDeclaredConstructor().newInstance();
            msgDao = (OfflineMsgDao)msgClazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static DaoFactory getDaoFactory() {
        return daoFactory;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public OfflineMsgDao getMsgDao() {
        return msgDao;
    }
}

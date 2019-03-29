package com.dao;

import com.bean.OfflineMsg;

import java.util.concurrent.ConcurrentHashMap;

/**
 * description：
 *
 * @author ajie
 * data 2018/12/13 0:10
 */
public interface OfflineMsgDao {
    ConcurrentHashMap<Integer, OfflineMsg> getMsg(String toName);

    ConcurrentHashMap<Integer, OfflineMsg> getFile(String toName);

    int insertChatMsg(Object msg);

    int insertFileMsg(OfflineMsg msg);

    void deleteMsg(String name, int type);

    void deleteFile(String name, int type);

}

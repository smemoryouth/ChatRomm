package com.service;

import com.bean.OfflineMsg;

import java.util.concurrent.ConcurrentHashMap;

/**
 * description：
 *
 * @author ajie
 * data 2018/12/13 0:16
 */
public interface MsgService {

    boolean addChatMsg(Object msg);

    boolean addFileMsg(OfflineMsg msg);

    ConcurrentHashMap<Integer, OfflineMsg> checkMsg(String name);
    ConcurrentHashMap<Integer, OfflineMsg> checkFile(String name);

    void removeFile(String name, int type);
    void removeMsg(String name, int type);
}

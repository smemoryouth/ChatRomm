package com.service.serviceimpl;

import com.bean.OfflineMsg;
import com.dao.DaoFactory;
import com.dao.OfflineMsgDao;
import com.service.MsgService;

import java.util.concurrent.ConcurrentHashMap;

/**
 * description：
 *
 * @author ajie
 * data 2018/12/13 0:17
 */
public class MsgServiceImpl implements MsgService {
    private OfflineMsgDao msgDao = DaoFactory.getDaoFactory().getMsgDao();
    @Override
    public boolean addChatMsg(Object msg) {
        return msgDao.insertChatMsg(msg) == 1;
    }

    @Override
    public boolean addFileMsg(OfflineMsg msg) {
        return msgDao.insertFileMsg(msg) == 1;
    }

    @Override
    public ConcurrentHashMap<Integer, OfflineMsg> checkMsg(String name) {
        return msgDao.getMsg(name);
    }

    @Override
    public ConcurrentHashMap<Integer, OfflineMsg> checkFile(String name) {
        return msgDao.getFile(name);
    }

    @Override
    public void removeFile(String name, int type) {
        msgDao.deleteFile(name, type);
    }

    @Override
    public void removeMsg(String name, int type) {
        msgDao.deleteMsg(name, type);
    }
}

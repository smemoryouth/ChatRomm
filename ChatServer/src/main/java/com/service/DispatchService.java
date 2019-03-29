package com.service;

import com.bean.OfflineMsg;
import com.bean.User;
import com.contant.MsgTypeEnum;
import com.controller.FileHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.service.serviceimpl.MsgServiceImpl;
import com.service.serviceimpl.UserServiceImpl;
import com.util.JsonUtil;
import com.util.PortUtils;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

/**
 * description：负责具体业务实现
 *
 * @author ajie
 * data 2018/12/3 20:07
 */
public class DispatchService {
    /**
     * 用于返回在线用户信息和个人信息
     */
    public static ConcurrentHashMap<ChannelHandlerContext, String> userValueMap = new ConcurrentHashMap<>();
    /**
     * 记录用户名和通道的关系，用于转发消息
     */
    public static ConcurrentHashMap<String, ChannelHandlerContext> userKeyMap = new ConcurrentHashMap<>();

    static SynchronousQueue<Integer> queue = new SynchronousQueue<>();

    public static ConcurrentHashMap<Integer, OfflineMsg> fileMap = new ConcurrentHashMap<>();

    private static DispatchService ds = new DispatchService();
    private UserServiceImpl usi = new UserServiceImpl();
    private MsgServiceImpl msi = new MsgServiceImpl();
    private String name;
    private String psw;
    private String email;
    private boolean flag;
    public static OfflineMsg msg;
    public static boolean isOnline;


    private DispatchService() {
    }

    public static DispatchService getInstance() {
        return ds;
    }

    /**
     * 客户端请求处理流程
     *
     * @param ctx
     * @param msg
     * @return
     */
    public ObjectNode process(ChannelHandlerContext ctx, Object msg) {

        ObjectNode node = JsonUtil.getObjectNode(msg.toString());
        MsgTypeEnum type = MsgTypeEnum.valueOf(node.get("type").asText());
        ObjectNode objectNode = JsonUtil.getObjectNode();

        switch (type) {
            // 登录
            case EN_MSG_LOGIN:
                objectNode = login(ctx, node, objectNode);
                return objectNode;
            // 注册
            case EN_MSG_REGISTER:
                objectNode = register(node, objectNode);
                return objectNode;
            // 忘记密码
            case EN_MSG_FORGET_PWD:
                objectNode = forgetPassword(node, objectNode);
                return objectNode;
            // 修改密码
            case EN_MSG_MODIFY_PWD:
                objectNode = modifyPassword(ctx, node, objectNode);
                return objectNode;
            // 注销账户
            case EN_MSG_LOGOUTACCOUNT:
                objectNode = logoutAccount(ctx, node, objectNode);
                return objectNode;
            // 修改邮箱
            case EN_MSG_MODIFY_EMAIL:
                objectNode = modifyEmail(node, objectNode);
                return objectNode;
            // 个人信息
            case EN_MSG_USER_INFO:
                objectNode = userInfoView(ctx, objectNode);
                return objectNode;
            // 一对一聊天
            case EN_MSG_CHAT:
                objectNode = chat(node, objectNode, msg);
                return objectNode;
            // 离线消息的检查
            case EN_MSG_OFFLINE_MSG_EXIST:
                checkOfflineMsg(node, ctx);
                break;
            // 离线文件的检查
            case EN_MSG_OFFLINE_FILE_EXIST:
                checkOfflineFile(node, ctx);
                break;
            // 群聊
            case EN_MSG_CHAT_ALL:
                objectNode = chatAll(node, objectNode, msg);
                return objectNode;
            // 在线用户信息
            case EN_MSG_GET_ALL_USERS:
                objectNode = getOnlineUsers(objectNode);
                return objectNode;
            // 发送文件
            case EN_MSG_TRANSFER_FILE:
                objectNode = transferFile(node, objectNode);
                return objectNode;
            // 发送文件接收方确认
            case EN_MSG_TRANSFER_FILE_ACK:
                transferFileConfirm(node);
                break;
            // 退出登录
            case EN_MSG_SIGNOUT:
                objectNode = signOut(ctx, objectNode);
                return objectNode;
            default:
                break;
        }
        return null;
    }

    /**
     * 离线文件的检查
     *
     * @param node
     * @param ctx
     */
    private void checkOfflineFile(ObjectNode node, ChannelHandlerContext ctx) {
        isOnline = true;
        name = node.get("name").asText();
        fileMap = msi.checkFile(name);
        int size = fileMap.size();
        Iterator<OfflineMsg> it = fileMap.values().iterator();
        if (size > 0) {
            ObjectNode nodes = JsonUtil.getObjectNode();
            int fromPort = PortUtils.getFreePort();
            int toPort = PortUtils.getFreePort();
            nodes.put("type", MsgTypeEnum.EN_MSG_OFFLINE_FILE_EXIST.toString());
            nodes.put("size", size);
            nodes.put("port", toPort);
            OfflineMsg msg;
            for (int i = 0; i < size; i++) {
                if (it.hasNext()) {
                    msg = it.next();
                    nodes.put(String.valueOf(i), msg.getFromName() + "向您发送文件：" + msg.getFileName());
                }
            }

            ctx.channel().writeAndFlush(nodes.toString());
            msi.removeFile(name, 2);
            new Thread(new FileHandler(fromPort, toPort)).start();


        }
    }

    /**
     * 发送文件接收方确认
     */
    private void transferFileConfirm(ObjectNode node) {
        int code = node.get("code").asInt();
        try {
            System.out.println("接受方返回确认码" + code);
            queue.put(code);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送文件
     *
     * @param node
     * @param objectNode
     * @return
     */
    private ObjectNode transferFile(ObjectNode node, ObjectNode objectNode) {
        String toName = node.get("toName").asText();
        name = node.get("fromName").asText();
        // 判断用户是否存在
        if (!usi.isExist(toName)) {
            objectNode.put("type", MsgTypeEnum.EN_MSG_TRANSFER_FILE_ACK.toString());
            objectNode.put("code", 400);
            return objectNode;
        }
        // 分配端口
        int fromPort = PortUtils.getFreePort();
        int toPort = PortUtils.getFreePort();
        String file = node.get("file").asText();
        long size = node.get("size").asLong();
        String path = node.get("path").asText();
        // 用户在线
        if (userKeyMap.containsKey(toName)) {
            isOnline = true;
            objectNode.put("type", MsgTypeEnum.EN_MSG_TRANSFER_FILE.toString());
            objectNode.put("port", toPort);
            objectNode.put("fromName", name);
            objectNode.put("file", file);
            objectNode.put("size", size);
            System.out.println("接收方发送端口");
            userKeyMap.get(toName).channel().writeAndFlush(objectNode.toString());
        } else {
            isOnline = false;
            System.out.println("用户不在线");
            msg = new OfflineMsg(0, toName, name, 2, file, null);
        }
        objectNode.put("type", MsgTypeEnum.EN_MSG_TRANSFER_FILE_ACK.toString());
        objectNode.put("code", 102);
        objectNode.put("path", path);
        objectNode.put("port", fromPort);
        System.out.println("开启子线程");
        new Thread(new FileHandler(fromPort, toPort)).start();
        System.out.println("发送方发送端口");
        return objectNode;
    }

    /**
     * 离线消息处理
     *
     * @param node
     * @param ctx
     */
    private void checkOfflineMsg(ObjectNode node, ChannelHandlerContext ctx) {
        name = node.get("name").asText();
        ConcurrentHashMap<Integer, OfflineMsg> map = msi.checkMsg(name);
        int size = map.size();
        Iterator<OfflineMsg> it = map.values().iterator();
        if (size > 0) {
            ObjectNode nodes = JsonUtil.getObjectNode();
            nodes.put("type", MsgTypeEnum.EN_MSG_OFFLINE_MSG_EXIST.toString());
            nodes.put("size", size);
            OfflineMsg msg;
            for (int i = 0; i < size; i++) {
                if (it.hasNext()) {
                    msg = it.next();
                    nodes.put(String.valueOf(i), msg.getFromName() + "[私密]:" + msg.getMsg());
                }
            }

            msi.removeMsg(name, 1);
            ctx.channel().writeAndFlush(nodes.toString());

        }

    }

    /**
     * 一对一聊天
     *
     * @param node
     * @param objectNode
     * @return
     */
    private ObjectNode chat(ObjectNode node, ObjectNode objectNode, Object msg) {
        objectNode.put("type", MsgTypeEnum.EN_MSG_CHAT_ACK.toString());
        String toName = node.get("toName").asText();
        name = node.get("fromName").asText();
        // 数据库看用户是否存在
        if (!usi.isExist(toName)) {
            objectNode.put("code", 400);
            return objectNode;
        }
        if (!userKeyMap.containsKey(toName)) {
            flag = msi.addChatMsg(msg);
            if (flag) {
                System.out.println("消息缓存入数据库");
                objectNode.put("code", 911);
            } else {
                objectNode.put("code", 402);
            }
            return objectNode;
        }

        userKeyMap.get(toName).channel().writeAndFlush(msg);
        objectNode.put("type", MsgTypeEnum.EN_MSG_CHAT_ACK.toString());
        objectNode.put("code", 910);
        return objectNode;
    }

    /**
     * 上线通知
     *
     * @param node
     */
    private void onlineNotification(ObjectNode node) {
        if (userKeyMap.size() > 0) {
            ObjectNode objectNode = JsonUtil.getObjectNode();
            objectNode.put("type", MsgTypeEnum.EN_MSG_NOTIFY_ONLINE.toString());
            objectNode.put("msg", "您的好友 " + node.get("name").asText() + " 上线啦");
            for (ChannelHandlerContext channel : userKeyMap.values()) {
                channel.channel().writeAndFlush(objectNode.toString());
            }
        }
    }

    /**
     * 下线通知
     *
     * @param node
     */
    public void offLineNotification(ObjectNode node) {
        if (userKeyMap.size() > 0) {
            ObjectNode objectNode = JsonUtil.getObjectNode();
            objectNode.put("type", MsgTypeEnum.EN_MSG_NOTIFY_OFFLINE.toString());
            objectNode.put("msg", "您的好友 " + node.get("name").asText() + " 下线了");
            for (ChannelHandlerContext channel : userKeyMap.values()) {
                channel.channel().writeAndFlush(objectNode.toString());
            }
        }
    }

    /**
     * 群聊
     *
     * @param node
     * @param objectNode
     * @param msg
     * @return
     */
    private ObjectNode chatAll(ObjectNode node, ObjectNode objectNode, Object msg) {
        if (userKeyMap.size() == 1) {
            return null;
        }
        for (HashMap.Entry<String, ChannelHandlerContext> next : userKeyMap.entrySet()) {
            String name = next.getKey();
            ChannelHandlerContext channel = next.getValue();
            if (!name.equals(node.get("fromName").asText())) {
                channel.channel().writeAndFlush(msg);
            }
        }
        objectNode.put("type", MsgTypeEnum.EN_MSG_CHAT_ALL_ACK.toString());
        objectNode.put("code", 912);
        return objectNode;
    }

    /**
     * 登录
     *
     * @param node
     * @param objectNode
     * @return
     */
    private ObjectNode login(ChannelHandlerContext ctx, ObjectNode node, ObjectNode objectNode) {
        name = node.get("name").asText();
        psw = node.get("psw").asText();
        flag = usi.loginVerification(name, psw);
        objectNode.put("type", MsgTypeEnum.EN_MSG_LOGIN.toString());
        if (flag) {
            if (userValueMap.containsValue(name)) {
                objectNode.put("code", 401);
            } else {
                // 用户登录后保存账户信息
                objectNode.put("code", 200);
                onlineNotification(node);
                userValueMap.put(ctx, name);
                if (!userKeyMap.containsKey(name)) {
                    userKeyMap.put(name, ctx);
                }
            }
        } else {
            objectNode.put("code", 400);
        }
        return objectNode;
    }

    /**
     * 注册
     *
     * @param node
     * @param objectNode
     * @return
     */
    private ObjectNode register(ObjectNode node, ObjectNode objectNode) {
        email = node.get("email").asText();
        name = node.get("name").asText();
        psw = node.get("psw").asText();
        flag = usi.registerVerification(name, psw, email);
        objectNode.put("type", MsgTypeEnum.EN_MSG_REGISTER.toString());
        if (flag) {
            objectNode.put("code", 300);
        } else {
            objectNode.put("code", 400);
        }
        return objectNode;
    }

    /**
     * 忘记密码
     *
     * @param node
     * @param objectNode
     * @return
     */
    private ObjectNode forgetPassword(ObjectNode node, ObjectNode objectNode) {
        name = node.get("name").asText();
        email = node.get("email").asText();
        flag = usi.retrievePassword(name, email);

        objectNode.put("type", MsgTypeEnum.EN_MSG_FORGET_PWD.toString());
        if (flag) {
            objectNode.put("code", 600);
        } else {
            objectNode.put("code", 400);
        }
        return objectNode;
    }

    /**
     * 修改密码
     *
     * @param node
     * @param objectNode
     * @return
     */
    private ObjectNode modifyPassword(ChannelHandlerContext ctx, ObjectNode node, ObjectNode objectNode) {
        name = node.get("name").asText();
        email = node.get("email").asText();
        psw = node.get("oldPsw").asText();
        String newPsw = node.get("newPsw").asText();
        flag = usi.modifyPswVerification(name, psw, newPsw, email);

        objectNode.put("type", MsgTypeEnum.EN_MSG_MODIFY_PWD.toString());
        if (flag) {
            objectNode.put("code", 500);
            // 修改完密码用户会处在未登陆状态
            userValueMap.remove(ctx);
            userKeyMap.remove(name);
            offLineNotification(node);

        } else {
            objectNode.put("code", 400);
        }
        return objectNode;
    }

    /**
     * 注销账户
     *
     * @param node
     * @param objectNode
     * @return
     */
    private ObjectNode logoutAccount(ChannelHandlerContext ctx, ObjectNode node, ObjectNode objectNode) {
        name = node.get("name").asText();
        psw = node.get("psw").asText();
        email = node.get("email").asText();
        flag = usi.logoutVerification(name, psw, email);
        objectNode.put("type", MsgTypeEnum.EN_MSG_LOGOUTACCOUNT.toString());
        if (flag) {
            objectNode.put("code", 800);
            // 用户注销后清除其登陆信息
            userValueMap.remove(ctx);
            userKeyMap.remove(name);
            offLineNotification(node);
        } else {
            objectNode.put("code", 400);
        }
        return objectNode;
    }


    /**
     * 更换邮箱
     *
     * @param node
     * @param objectNode
     * @return
     */
    private ObjectNode modifyEmail(ObjectNode node, ObjectNode objectNode) {
        name = node.get("name").asText();
        psw = node.get("psw").asText();
        email = node.get("email").asText();
        String newEmail = node.get("newEmail").asText();
        objectNode.put("type", MsgTypeEnum.EN_MSG_MODIFY_EMAIL.toString());
        flag = usi.modifyEmailVerification(name, psw, email, newEmail);
        if (flag) {
            objectNode.put("code", 700);
        } else {
            objectNode.put("code", 400);
        }
        return objectNode;
    }

    /**
     * 查看用户信息
     *
     * @param ctx
     * @param objectNode
     * @return
     */
    private ObjectNode userInfoView(ChannelHandlerContext ctx, ObjectNode objectNode) {
        name = userValueMap.get(ctx);
        User user = usi.getUserInfo(name);
        System.out.println(user);
        if (user != null) {
            objectNode.put("type", MsgTypeEnum.EN_MSG_USER_INFO.toString());
            objectNode.put("code", 900);
            objectNode.put("name", user.getName());
            objectNode.put("psw", user.getPsw());
            objectNode.put("email", user.getEmail());
        } else {
            objectNode.put("code", 400);
        }
        return objectNode;
    }

    /**
     * 获取在线用户信息
     *
     * @return
     */
    private ObjectNode getOnlineUsers(ObjectNode objectNode) {
        int size = userValueMap.size();
        if (size > 0) {
            objectNode.put("size", size);
            objectNode.put("code", 901);
            objectNode.put("type", MsgTypeEnum.EN_MSG_GET_ALL_USERS.toString());
            Iterator<String> it = userValueMap.values().iterator();
            String userName;
            for (int i = 0; i < size; i++) {
                if (it.hasNext()) {
                    userName = it.next();
                    objectNode.put(String.valueOf(i), userName);
                }
            }
        } else {
            objectNode.put("code", 400);
        }
        return objectNode;
    }

    /**
     * 退出登录
     *
     * @param ctx
     * @param objectNode
     * @return
     */
    private ObjectNode signOut(ChannelHandlerContext ctx, ObjectNode objectNode) {
        objectNode.put("type", MsgTypeEnum.EN_MSG_SIGNOUT.toString());
        objectNode.put("code", 801);
        name = userValueMap.get(ctx);
        userValueMap.remove(ctx);
        userKeyMap.remove(name);
        ObjectNode nodes = JsonUtil.getObjectNode();
        nodes.put("name", name);
        offLineNotification(nodes);
        return objectNode;
    }
}

package com.controller;

import com.contant.MsgTypeEnum;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

/**
 * description：
 *
 * @author ajie
 * data 2018/11/26 20:43
 */
public class ClientHandler extends SimpleChannelInboundHandler<String> {
    public static int size;
    /**
     * 用户登录成功后保存其信息
     */
    static ConcurrentHashMap<String, String> localUserMap = new ConcurrentHashMap<>();
    /**
     * 服务器返回的在线用户信息
     */
    static ConcurrentHashMap<Integer, String> userMap = new ConcurrentHashMap<>();
    /**
     * 线程通讯队列
     */
    static SynchronousQueue<Integer> queue = new SynchronousQueue<>();
    static SynchronousQueue<Integer> portqueue = new SynchronousQueue<>();
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 利用bytebuf获取服务器返回的信息
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        // info保存的是服务器返回的信息
        String info = new String(bytes, "UTF-8");
        ObjectNode jsonNodes = JsonUtil.getObjectNode(info);

        MsgTypeEnum type = MsgTypeEnum.valueOf(jsonNodes.get("type").asText());

        switch (type) {
            case EN_MSG_CHAT_ACK:
            case EN_MSG_LOGIN:
            case EN_MSG_SIGNOUT:
            case EN_MSG_REGISTER:
            case EN_MSG_MODIFY_EMAIL:
            case EN_MSG_FORGET_PWD:
            case EN_MSG_MODIFY_PWD:
            case EN_MSG_LOGOUTACCOUNT:
            case EN_MSG_CHAT_ALL_ACK:
                int code = jsonNodes.get("code").asInt();
                queue.put(code);
                break;
            case EN_MSG_USER_INFO:
                code = jsonNodes.get("code").asInt();
                getOwnInfo(jsonNodes);
                queue.put(code);
                break;
            // 下线通知
            case EN_MSG_NOTIFY_OFFLINE:
                System.out.println("系统提示：" + jsonNodes.get("msg").asText());
                break;
            case EN_MSG_NOTIFY_ONLINE:
                System.out.println("系统提示：" + jsonNodes.get("msg").asText());
                break;
            // 获取在线用户信息
            case EN_MSG_GET_ALL_USERS:
                code = jsonNodes.get("code").asInt();
                size = jsonNodes.get("size").asInt();
                getAllOnlineUser(jsonNodes, code);
                queue.put(code);
                break;
            // 一对一聊天
            case EN_MSG_CHAT:
                String name = jsonNodes.get("fromName").asText();
                String text = jsonNodes.get("msg").asText();
                System.out.println(name + " [私密]:" + text);
                break;
            // 离线消息
            case EN_MSG_OFFLINE_MSG_EXIST:
                offlineMsgShow(jsonNodes);
                break;
                // 群聊
            case EN_MSG_CHAT_ALL:
                name = jsonNodes.get("fromName").asText();
                text = jsonNodes.get("msg").asText();
                System.out.println(name + " [all]:" + text);
                break;
                // 文件传输确认
            case EN_MSG_TRANSFER_FILE_ACK:
                code = jsonNodes.get("code").asInt();
                queue.put(code);
                int port = jsonNodes.get("port").asInt();
                String path = jsonNodes.get("path").asText();
                File file = new File(path);
                FileSendHandler fsh = new FileSendHandler("127.0.0.1", port);
                fsh.sendFile(file);
                break;
                // 文件传输
            case EN_MSG_TRANSFER_FILE:
//                fileDeal(jsonNodes, ctx);
                String fileName = jsonNodes.get("file").asText();
                long fileSize = jsonNodes.get("size").asLong();
                name = jsonNodes.get("fromName").asText();
                System.out.println("您的好友" + name + "向您发送文件" + fileName + "，大小" + fileSize + "kb");
                port = jsonNodes.get("port").asInt();
                FileRecvHandler fileRecvHandler = new FileRecvHandler("127.0.0.1", port);
                fileRecvHandler.recv();
                break;

                // 离线文件
            case EN_MSG_OFFLINE_FILE_EXIST:
                port = jsonNodes.get("port").asInt();
                FileRecvHandler recvHandler = new FileRecvHandler("127.0.0.1", port);
                size = jsonNodes.get("size").asInt();
                for (int i = 0; i < size; i++) {
                    System.out.println(jsonNodes.get(String.valueOf(i)).asText());
                }
                recvHandler.recv();
                break;
            default:
                break;
        }

    }

    /**
     * 文件处理
     * @param jsonNodes
     */
    private void fileDeal(ObjectNode jsonNodes, ChannelHandlerContext ctx) {
        ObjectNode node = JsonUtil.getObjectNode();
        Scanner in = new Scanner(System.in);
        String fromName = jsonNodes.get("fromName").asText();
        String file = jsonNodes.get("file").asText();
        long fileSize = jsonNodes.get("size").asLong();
        System.out.println("您的好友\"" + fromName + "\"向您发送文件\"" + file + "\",大小" + fileSize + "kb");
        System.out.println("是否接收?Y/N");
        char chose = in.nextLine().charAt(0);
        switch (chose) {
            case 'Y':
            case 'y':
                int port = jsonNodes.get("port").asInt();
                String ip = "127.0.0.1";
                node.put("type", MsgTypeEnum.EN_MSG_TRANSFER_FILE_ACK.toString());
                node.put("code", 100);
                ctx.channel().writeAndFlush(node.toString());
                new FileRecvHandler(ip, port).recv();
                break;
            default:
                node.put("type", MsgTypeEnum.EN_MSG_TRANSFER_FILE_ACK.toString());
                node.put("code", 400);
                ctx.channel().writeAndFlush(node.toString());
                break;
        }
    }

    /**
     * 离线消息处理
     * @param jsonNodes
     */
    private void offlineMsgShow(ObjectNode jsonNodes) {
        int size = jsonNodes.get("size").asInt();
        for (int i = 0; i < size; i++) {
            System.out.println(jsonNodes.get(String.valueOf(i)).asText());
        }
    }

    /**
     * 获取所有在线用户信息
     *
     * @param jsonNodes
     * @param code
     */
    private void  getAllOnlineUser(ObjectNode jsonNodes, int code) {
        if (code == 901) {
            for (int i = 0; i < size; i++) {
                userMap.put(i, jsonNodes.get(String.valueOf(i)).asText());
            }
        }
    }

    /**
     * 个人信息的获取
     *
     * @param jsonNodes
     */
    private void getOwnInfo(ObjectNode jsonNodes) {
        String name = jsonNodes.get("name").asText();
        String password = jsonNodes.get("psw").asText();
        String email = jsonNodes.get("email").asText();
        localUserMap.put("name", name);
        localUserMap.put("password", password);
        localUserMap.put("email", email);

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
    }
}

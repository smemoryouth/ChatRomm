package com.controller;

import com.contant.MsgTypeEnum;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.util.JsonUtil;
import io.netty.channel.ChannelFuture;

import java.io.File;
import java.util.Scanner;

/**
 * description：客户端的具体业务
 *
 * @author ajie
 * data 2018/11/26 21:06
 */
class ClientController {
    private ChannelFuture future;
    private Scanner in = new Scanner(System.in);

    ClientController() {
    }

    ClientController(ChannelFuture future) {
        this.future = future;
    }

    /**
     * 客户端起始页
     */
    void run() {
        while (true) {
            String buffer = "========菜单========\n" +
                    "1.登录\n" +
                    "2.注册\n" +
                    "3.忘记密码\n" +
                    "0.退出系统\n" +
                    "===================";
            System.out.println(buffer);
            System.out.println("请选择您要进行的操作编号：");

            String line = in.nextLine();
            while ("".equals(line)) {
                line = in.nextLine();
            }
            while (digitalCheck(line)) {
                System.out.println("命令有误，请重新输入:");
                line = in.nextLine();
            }
            Integer choice = Integer.parseInt(line);
            switch (choice) {
                case 1:
                    // 登录
                    doLogin();
                    break;
                case 2:
                    // 注册
                    doRegistered();
                    break;
                case 3:
                    // 忘记密码
                    retrievePassword();
                    break;
                case 0:
                    // 退出系统
                    System.exit(0);
                    break;
                default:
                    System.out.println("命令有误，请重新输入");
            }
        }

    }

    /**
     * 主页面显示内容
     */
    private void mainShow() {
        while (true) {
            String buffer = "========主菜单========\n" +
                    "主菜单功能介绍:\n" +
                    "1.单人聊天\n" +
                    "2.多人聊天\n" +
                    "3.发送文件\n" +
                    "4.在线用户\n" +
                    "5.个人信息\n" +
                    "6.修改密码\n" +
                    "7.更换邮箱\n" +
                    "8.注销账号\n" +
                    "9.退出登录\n" +
                    "0.关闭系统\n" +
                    "请注意：您可以在任何界面输入\"main\"回到菜单页\n" +
                    "=====================";
            System.out.println(buffer);
            System.out.println("请输入：");
            String line;
            if (in.hasNext()) {
                line = in.nextLine();
                // 直接回车的处理
                while ("".equals(line)) {
                    line = in.nextLine();
                }
                // 添加非数字的验证
                while (digitalCheck(line)) {
                    System.out.println("命令有误，请重新输入:");
                    line = in.nextLine();
                }
                Integer choice = Integer.parseInt(line);
                switch (choice) {
                    case 1:
                        // 单人聊天
                        chatIndividual();
                        break;
                    case 2:
                        // 多人聊天
                        chatAll();
                        break;
                    case 3:
                        // 发送文件
                        sendFile();
                        break;
                    case 4:
                        // 获取当前在线用户
                        getOnlineUsers();
                        break;
                    case 5:
                        // 个人信息
                        userInfo();
                        break;
                    case 6:
                        // 修改密码
                        changePassword();
                        break;
                    case 7:
                        // 修改邮箱
                        changeEmail();
                        break;
                    case 8:
                        // 注销账户
                        logoutAccount();
                        break;
                    case 9:
                        // 退出登录
                        signOut();
                        break;
                    case 0:
                        // 退出系统
                        ClientHandler.localUserMap.clear();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("命令有误，请重新输入");
                }
            }
        }
    }

    /**
     * 发送文件
     */
    private void sendFile() {
        System.out.println("请输入对方用户名");
        String toName = in.nextLine();
        System.out.println("请输入文件路径");
        String path = in.nextLine();
        try{
            File file = new File(path);
            if (!file.exists() || file.isDirectory()) {
                System.out.println("未知文件或文件夹，请重新选择");
                mainShow();
            }
            ObjectNode node = JsonUtil.getObjectNode();
            node.put("type", MsgTypeEnum.EN_MSG_TRANSFER_FILE.toString());
            node.put("fromName", ClientHandler.localUserMap.get("name"));
            node.put("toName", toName);
            node.put("file", file.getName());
            node.put("path", path);
            node.put("size", file.length());
            future.channel().writeAndFlush(node.toString());

            if (getCode() == 102){
                System.out.println("发送成功");
            }else {
                System.out.println("不存在的用户");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 群聊
     */
    private void chatAll() {
        System.out.println("请输入消息：");
        String msg = in.nextLine();
        ObjectNode node = JsonUtil.getObjectNode();
        node.put("type", MsgTypeEnum.EN_MSG_CHAT_ALL.toString());
        node.put("fromName", ClientHandler.localUserMap.get("name"));
        node.put("msg", msg);
        System.out.println(node.toString());
        future.channel().writeAndFlush(node.toString());
        if (getCode() == 912){
            System.out.println("发送成功");
        }else {
            System.out.println("发送失败");
        }
    }

    /**
     * 登录操作
     */
    private void doLogin() {
        System.out.println("请输入用户名：");
        String name = in.nextLine();

        System.out.println("请输入密码");
        String psw = in.nextLine();
        ClientHandler.localUserMap.put("name", name);
        ClientHandler.localUserMap.put("password", psw);
        ObjectNode node = JsonUtil.getObjectNode();
        node.put("type", MsgTypeEnum.EN_MSG_LOGIN.toString());
        node.put("name", name);
        node.put("psw", psw);

        future.channel().writeAndFlush(node.toString());

        switch (getCode()) {
            case 200:
                System.out.println();
                System.out.println("登录成功...");
                System.out.println();
                try {
                    Thread.sleep(2000);
                    node.put("type", MsgTypeEnum.EN_MSG_OFFLINE_MSG_EXIST.toString());
                    future.channel().writeAndFlush(node.toString());
                    node.put("type", MsgTypeEnum.EN_MSG_OFFLINE_FILE_EXIST.toString());
                    future.channel().writeAndFlush(node.toString());
                    mainShow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 401:
                System.out.println();
                System.out.println("当前用户已在线，请不要重复登录");
                System.out.println();
                break;
            case 400:
                System.out.println();
                System.out.println("用户名或密码错误");
                System.out.println();
                break;
            default:
                break;
        }
    }

    /**
     * 用户注册
     */
    private void doRegistered() {
        System.out.println("请注意：您可以通过输入\"quit\"取消本次注册操作");

        System.out.println("请输入您要注册的用户名：");
        String name = in.nextLine();
        quitCheck(1, name);

        System.out.println("请输入密码");
        String psw = in.nextLine();
        quitCheck(1, psw);
        psw = passwordLengthCheck(psw);

        System.out.println("请再次输入密码");
        String pswCheck = in.nextLine();
        quitCheck(1, pswCheck);

        while (!psw.equals(pswCheck)) {
            System.out.println("两次输入的密码不一致，请重新输入：");
            psw = in.nextLine();
            quitCheck(1, psw);
            psw = passwordLengthCheck(psw);

            System.out.println("请再次输入密码");
            pswCheck = in.nextLine();
            quitCheck(1, pswCheck);
        }

        System.out.println("请输入您的常用邮箱:");
        String email = in.nextLine();
        quitCheck(1, email);
        while (emailFormatCheck(email)) {
            System.out.println("邮箱格式不正确，请重新输入：");
            email = in.nextLine();
            quitCheck(1, email);
        }

        ObjectNode node = JsonUtil.getObjectNode();
        node.put("type", MsgTypeEnum.EN_MSG_REGISTER.toString());
        node.put("name", name);
        node.put("psw", psw);
        node.put("email", email);

        future.channel().writeAndFlush(node.toString());

        if (getCode() == 300) {
            try {
                System.out.println();
                System.out.println("恭喜您注册成功，将在3秒后跳转到登录页面");
                Thread.sleep(3000);
                System.out.println();
                System.out.println("请登录");
                doLogin();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println();
            System.out.println("用户已存在, 请重新输入");
            doRegistered();
        }
    }

    /**
     * 找回密码
     */
    private void retrievePassword() {
        System.out.println("请注意：您可以通过输入\"quit\"取消本次操作");
        System.out.println("请输入您的用户名：");
        String name = in.nextLine();
        quitCheck(1, name);

        System.out.println("请输入您注册时绑定的邮箱");
        String email = in.nextLine();
        quitCheck(1, email);

        ObjectNode node = JsonUtil.getObjectNode();
        node.put("type", MsgTypeEnum.EN_MSG_FORGET_PWD.toString());
        node.put("name", name);
        node.put("email", email);

        future.channel().writeAndFlush(node.toString());

        System.out.println();
        System.out.println("正在处理，请稍候...");

        if (getCode() == 600) {
            try {
                System.out.println();
                System.out.println("密码已发送至您邮箱，请注意查收");
                System.out.println();
                Thread.sleep(2000);
            } catch (Exception e) {
                throw new RuntimeException("error");
            }
        } else {
            System.out.println();
            System.out.println("用户信息不正确，请重新选择您的操作：");
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                throw new RuntimeException("error");
            }
        }
    }

    /**
     * 修改密码
     */
    private void changePassword() {
        System.out.println("请注意：您可以通过输入\"quit\"取消本次修改操作");
        System.out.println("请确认您的用户名：");
        String name = in.nextLine();
        infoMonitoring(2, name);
        quitCheck(2, name);

        System.out.println("请输入您当前密码：");
        String oldPsw = in.nextLine();
        infoMonitoring(2, oldPsw);
        quitCheck(2, oldPsw);

        System.out.println("请输入您的新密码：");
        String newPsw = in.nextLine();
        infoMonitoring(2, newPsw);
        quitCheck(2, newPsw);
        newPsw = passwordLengthCheck(newPsw);

        System.out.println("请确认您的新密码：");
        String newPswCheck = in.nextLine();
        infoMonitoring(2, newPswCheck);
        quitCheck(2, newPswCheck);

        while (!newPswCheck.equals(newPsw)) {
            System.out.println("新密码两次输入不一致，请重新输入：");
            newPsw = in.nextLine();
            infoMonitoring(2, newPsw);
            quitCheck(2, newPsw);
            newPsw = passwordLengthCheck(newPsw);

            System.out.println("请确认您的新密码：");
            newPswCheck = in.nextLine();
            infoMonitoring(2, newPswCheck);
            quitCheck(2, newPswCheck);
        }

        System.out.println("请输入您的验证邮箱");
        String email = in.nextLine();
        infoMonitoring(2, email);
        quitCheck(2, email);

        ObjectNode node = JsonUtil.getObjectNode();
        node.put("type", MsgTypeEnum.EN_MSG_MODIFY_PWD.toString());
        node.put("name", name);
        node.put("oldPsw", oldPsw);
        node.put("newPsw", newPsw);
        node.put("email", email);
        future.channel().writeAndFlush(node.toString());

        System.out.println();
        System.out.println("正在处理，请稍候...");

        if (getCode() == 500) {
            System.out.println();
            // 修改密码后用户处于不在线状态，清除本地记录信息
            ClientHandler.localUserMap.clear();
            System.out.println("密码修改成功，请重新登录");
            System.out.println();
            doLogin();
            System.out.println();
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println();
            System.out.println("用户信息不正确，请重新选择您的操作");
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 账户注销
     */
    private void logoutAccount() {
        System.out.println("请注意：您可以通过输入\"quit\"取消本次注销操作");
        System.out.println("请输入您的用户名");
        String name = in.nextLine();
        quitCheck(2, name);
        infoMonitoring(2, name);

        System.out.println("请输入您的密码");
        String psw = in.nextLine();
        quitCheck(2, psw);
        infoMonitoring(2, psw);

        System.out.println("请输入您的邮箱");
        String email = in.nextLine();
        infoMonitoring(2, email);
        quitCheck(2, email);

        System.out.println("您的账号即将注销，一旦确认不可更改，是否确认此操作？Y/N");
        ObjectNode node = JsonUtil.getObjectNode();

        node.put("type", MsgTypeEnum.EN_MSG_LOGOUTACCOUNT.toString());
        node.put("name", name);
        node.put("psw", psw);
        node.put("email", email);
        actionVerify(node);

        System.out.println("正在处理，请稍候...");

        if (getCode() == 800) {
            System.out.println();
            ClientHandler.localUserMap.clear();
            System.out.println("您的账户已注销，期待您的再次使用，再见");
            System.out.println();
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            run();
        } else {
            System.out.println();
            System.out.println("用户信息不正确，注销失败，请重新选择您的操作：");
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }

    /**
     * 退出登录
     */
    private void signOut() {
        System.out.println("确认退出登录？Y/N");
        ObjectNode node = JsonUtil.getObjectNode();
        node.put("type", MsgTypeEnum.EN_MSG_SIGNOUT.toString());
        actionVerify(node);
        if (getCode() == 801) {
            run();
        }
    }

    /**
     * 获取在线用户信息
     */
    private void getOnlineUsers() {
        ObjectNode node = JsonUtil.getObjectNode();
        node.put("type", MsgTypeEnum.EN_MSG_GET_ALL_USERS.toString());
        future.channel().writeAndFlush(node.toString());
        if (getCode() == 901) {
            int size = ClientHandler.userMap.size();
            System.out.println("当前在线用户人数是：" + size);
            for (int i = 0; i < size; i++) {
                System.out.println((i + 1) + "." + ClientHandler.userMap.get(i));
            }
            ClientHandler.userMap.clear();
            System.out.println("按任意键返回主菜单");
            while (true) {
                if (in.hasNext()) {
                    in.nextLine();
                    return;
                }
            }
        } else {
            System.out.println("获取在线用户列表失败");
        }
    }

    /**
     * 修改邮箱
     */
    private void changeEmail() {
        System.out.println("请注意：您可以通过输入\"quit\"取消本次修改操作");
        System.out.println("请确认您的用户名：");
        String name = in.nextLine();
        infoMonitoring(2, name);
        quitCheck(2, name);

        System.out.println("请输入您的密码");
        String psw = in.nextLine();
        infoMonitoring(2, psw);
        quitCheck(2, psw);

        System.out.println("请输入您当前绑定的的邮箱");
        String email = in.nextLine();
        infoMonitoring(2, email);
        quitCheck(2, email);

        System.out.println("请输入您的新邮箱");
        String newEmail = in.nextLine();
        infoMonitoring(2, newEmail);
        quitCheck(2, newEmail);

        while (emailFormatCheck(email)) {
            System.out.println("邮箱格式不正确，请重新输入！");
            email = in.nextLine();
            infoMonitoring(2, email);
            quitCheck(2, email);
        }

        ObjectNode node = JsonUtil.getObjectNode();
        node.put("type", MsgTypeEnum.EN_MSG_MODIFY_EMAIL.toString());
        node.put("name", name);
        node.put("psw", psw);
        node.put("email", email);
        node.put("newEmail", newEmail);
        future.channel().writeAndFlush(node.toString());

        System.out.println("正在处理，请稍候...");

        if (getCode() == 700) {
            System.out.println();
            System.out.println("更换邮箱成功");
            System.out.println();
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println();
            System.out.println("用户信息不正确，修改失败，请重新选择您的操作");
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 用户信息查看
     */
    private void userInfo() {
        ObjectNode node = JsonUtil.getObjectNode();
        node.put("type", MsgTypeEnum.EN_MSG_USER_INFO.toString());
        future.channel().writeAndFlush(node.toString());
        if (getCode() == 900) {
            System.out.print("[ 用户名:" + ClientHandler.localUserMap.get("name") + "\t");
            System.out.print("密码:" + ClientHandler.localUserMap.get("password") + "\t");
            System.out.println("邮箱:" + ClientHandler.localUserMap.get("email") + " ]");
        }
        System.out.println("按任意键返回主菜单");
        while (true) {
            if (in.hasNext()) {
                in.nextLine();
                return;
            }
        }
    }

    /**
     * 一对一聊天
     */
    private void chatIndividual() {
        System.out.println("请输入您要进行对话的用户名：");
        String toName = in.nextLine();
        ObjectNode node = JsonUtil.getObjectNode();
        node.put("type", MsgTypeEnum.EN_MSG_CHAT.toString());
        node.put("fromName", ClientHandler.localUserMap.get("name"));
        node.put("toName", toName);
        System.out.println("请输入您的消息：");
        String msg = in.nextLine();
        node.put("msg", msg);
        future.channel().writeAndFlush(node.toString());
        switch (getCode()) {
            case 910:
                System.out.println("发送成功");
                break;
            case 911:
                System.out.println("当前用户不在线, 已转为离线消息");
                break;
            case 402:
                System.out.println("消息发送失败");
            case 400:
                System.out.println("不存在的用户，请重新选择您的操作");
                break;
            default:
                break;
        }
    }

    /**
     * 操作确认
     *
     * @param node
     */
    private void actionVerify(ObjectNode node) {
        char chose = in.nextLine().charAt(0);
        switch (chose) {
            case 'Y':
            case 'y':
                future.channel().writeAndFlush(node.toString());
                break;
            default:
                mainShow();
        }
    }

    /**
     * 返回码的获取
     *
     * @return
     */
    private int getCode() {
        int code = 0;
        try {
            code = ClientHandler.queue.take();

            ClientHandler.queue.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * 主页面的返回
     *
     * @param msg
     */
    private void infoMonitoring(int num, String msg) {
        // 这里的num主要用来区分密码长度的控制，在注册时和修改密码时
        if ("main".equals(msg)) {
            if (num == 1) {
                doLogin();
            } else {
                mainShow();
            }
        }
    }

    /**
     * 邮箱格式检验
     *
     * @param email
     * @return
     */
    private boolean emailFormatCheck(String email) {
        // 定义邮箱格式
        //String regex = "[a-zA-Z_0-9]{5,16}+@[a-zA-Z_0-9]{2,6}(\\.[a-zA-Z_0-9]{2,3})+";
        String regex = "\\w{5,16}+@\\w{2,6}(\\.\\w{2,3})+";
        return !email.matches(regex);
    }

    /**
     * 密码长度控制
     *
     * @param password
     * @return
     */
    private String passwordLengthCheck(String password) {
        int length = password.length();
        if (length < 6 || length > 16) {
            System.out.println("密码长度应在6-16位之间，请重新输入：");
            password = in.nextLine();
            quitCheck(1, password);
            infoMonitoring(1, password);
            passwordLengthCheck(password);
        }
        return password;
    }

    /**
     * 取消检测
     *
     * @param str
     */
    private void quitCheck(int num, String str) {
        if ("quit".equals(str)) {
            // 注册时输入quit，返回到dologin方法
            if (num == 1) {
                run();
            } else {
                // 修改用户信息时输入quit，返回到mainShow()方法
                mainShow();
            }
        }
    }

    /**
     * 非数字检测
     *
     * @param str
     * @return
     */
    private boolean digitalCheck(String str) {
        // 登录后在主菜单输入的main不在过滤范围内
        if ("main".equals(str)) {
            infoMonitoring(2, str);
        }
        // 只要出现一个非数字字符，输出错误指令信息
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}

package com;

import com.controller.NettyServer;

/**
 * Hello world!
 * @author 阿劼
 */
public class ServerApp {
    public static void main(String[] args) {
        // 调用netty启动服务
        int port = 7000;
        new NettyServer().serverStart(port);
    }
}

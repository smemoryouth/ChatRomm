package com;

import com.controller.NettyClient;

/**
 * description：
 *
 * @author ajie
 * data 2018/12/8 17:01
 */
public class ClientApp2 {
    public static void main(String[] args){
        String ip = "169.254.33.23";
        int port = 7000;
        new NettyClient().clientStart(ip, port);
    }
}

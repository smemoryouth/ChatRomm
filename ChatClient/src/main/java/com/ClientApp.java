package com;

import com.controller.NettyClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Hello world!
 *
 * @author 阿劼
 */
public class ClientApp {
    public static void main(String[] args) {

        try {
            Properties properties = new Properties();
            InputStream inStream = ClientApp.class.getClassLoader()
                    .getResourceAsStream("user.properties");
            properties.load(inStream);
            String ip = properties.getProperty("userIp");
            String port = properties.getProperty("port");
            new NettyClient().clientStart(ip, Integer.parseInt(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

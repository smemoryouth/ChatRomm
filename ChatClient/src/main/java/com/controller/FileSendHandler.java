package com.controller;

import java.io.*;
import java.net.Socket;

/**
 * description：
 *
 * @author ajie
 * data 2018/12/17 19:34
 */
public class FileSendHandler {
    private Socket socket;

    public FileSendHandler(){}

    FileSendHandler(String ip, int port){
        try {
            socket = new Socket(ip,port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送文件操作
     * @param file
     */
    public void sendFile(File file){
        try {
            FileInputStream fis = new FileInputStream(file);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            String name = file.getName();
            long length = file.length();
            dos.writeUTF(name);
            dos.writeLong(length);
            dos.flush();

            byte[] bytes = new byte[1024];
            int len;
            while ((len = fis.read(bytes, 0, bytes.length)) != -1){
                dos.write(bytes, 0, len);
                dos.flush();
            }
            dos.close();
            fis.close();
        } catch (Exception e) {
            System.out.println("文件发送异常");
            e.printStackTrace();
        }
    }
}

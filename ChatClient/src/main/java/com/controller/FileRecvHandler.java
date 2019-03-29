package com.controller;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * description：
 *
 * @author ajie
 * data 2018/12/17 19:47
 */
public class FileRecvHandler {
    private Socket socket;

    public FileRecvHandler(){}

    FileRecvHandler(String ip, int port){
        try {
            this.socket = new Socket(ip, port);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        System.out.println(FileRecvHandler.getDeafultPath());
    }
    private static String getDeafultPath(){
        String path =  FileRecvHandler.class.getResource("").getPath();
        int index = path.indexOf("/target");
        path = path.substring(0, index);
        return path;
    }


    void recv() {
        try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                String name = dis.readUTF();

                // 存储路径
                String path = getDeafultPath() + File.separator + name;
                FileOutputStream fos = new FileOutputStream(path);
                // 读取文件
                byte[] bytes = new byte[1024];
                int len;
                System.out.println("开始接收文件" + name);
                while ((len = dis.read(bytes, 0, bytes.length)) != -1) {
                    fos.write(bytes, 0, len);
                    fos.flush();
                }
                fos.close();
                dis.close();
                System.out.println("文件" + name + "完成接收，存储在" + path);
        }catch (Exception e){
            System.out.println("接受异常");
            e.printStackTrace();
        }
    }
}

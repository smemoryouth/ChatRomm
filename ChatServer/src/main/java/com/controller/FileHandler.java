package com.controller;

import com.bean.OfflineMsg;
import com.service.DispatchService;
import com.service.serviceimpl.MsgServiceImpl;
import com.util.PortUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Objects;


/**
 * description：
 *
 * @author ajie
 * data 2018/12/17 20:20
 */
public class FileHandler implements Runnable {

    private int fromPort;
    private int toPort;
    private ServerSocket fromSocket;
    private ServerSocket toSocket;

    public FileHandler(int fromPort, int toPort) {
        try {
            this.fromPort = fromPort;
            this.toPort = toPort;
            this.fromSocket = new ServerSocket(fromPort);
            this.toSocket = new ServerSocket(toPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        DataInputStream dis = null;
        DataOutputStream dos = null;
        try {
            if (DispatchService.fileMap.isEmpty()) {
                Socket fSocket = fromSocket.accept();
                System.out.println("is" + DispatchService.isOnline);
                dis = new DataInputStream(fSocket.getInputStream());
                if (DispatchService.isOnline) {
                    Socket tSocket = toSocket.accept();
                    dos = new DataOutputStream(tSocket.getOutputStream());
                    // 具体实现文件的转发,在线
                    String name = dis.readUTF();
                    System.out.println("[服务器] 转发文件：" + name + " 开始。。。");
                    dos.writeUTF(name);
                    dos.writeLong(dis.readLong());
                    byte[] bytes = new byte[1024];
                    int len;
                    while ((len = dis.read(bytes, 0, bytes.length)) != -1) {
                        dos.write(bytes, 0, len);
                    }
                    dos.close();
                    dis.close();
                    System.out.println("服务器转发文件结束");
                } else {
                    BufferedInputStream bis = new BufferedInputStream(dis);
                    StringBuilder buffer = new StringBuilder();
                    byte[] bytes = new byte[1024];
                    while (bis.read(bytes) != -1) {
                        buffer.append(new String(bytes));
                    }
                    DispatchService.msg.setMsg(buffer.toString());
                    System.out.println(new MsgServiceImpl().addFileMsg(DispatchService.msg) ? "文件存入数据库" : "存储文件失败");
                }
            } else {
                FileWriter fileWriter = null;
                Iterator<OfflineMsg> iterator = DispatchService.fileMap.values().iterator();
                while (iterator.hasNext()) {
                    OfflineMsg msg = iterator.next();
                    try {
                        // 创建临时文件
                        File temp = File.createTempFile("max", ".tmp", new File(""));
                        // 程序运行结束时该临时文件自动删除
                        temp.deleteOnExit();
                        // 向文件写入对象信息
                        fileWriter = new FileWriter(temp);
                        fileWriter.write(msg.getMsg());
                        fileWriter.flush();

                        Socket tSocket = toSocket.accept();
                        dis = new DataInputStream(new FileInputStream(temp));
                        dos = new DataOutputStream(tSocket.getOutputStream());
                        dos.writeUTF(msg.getFileName());
                        dos.writeLong(msg.getMsg().length());
                        byte[] bytes = new byte[1024];
                        int len;
                        while ((len = dis.read(bytes, 0, bytes.length)) != -1) {
                            dos.write(bytes, 0, len);
                            dos.flush();
                        }
                        System.out.println("文件" + msg.getFileName() + "转发完成");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                DispatchService.fileMap.clear();
                Objects.requireNonNull(dos).close();
                Objects.requireNonNull(dis).close();
                Objects.requireNonNull(fileWriter).close();

            }

            //将占用的端口释放掉
            PortUtils.closePort(fromPort);
            PortUtils.closePort(toPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

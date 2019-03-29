package com.controller;

import com.contant.MsgTypeEnum;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.service.DispatchService;
import com.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.SocketAddress;

/**
 * description：
 *
 * @author ajie
 * data 2018/11/26 20:04
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> {
    private DispatchService ds;
    ServerHandler(){
        this.ds = DispatchService.getInstance();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        // 获取传送信息的主机ip
        SocketAddress remoteAddress = channel.remoteAddress();
        // 利用bytebuf获取客户端发送的信息
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);

        // info中保存的是客户端传过来的信息
        String info = new java.lang.String(bytes, "UTF-8");
        System.out.println(remoteAddress + ":" + info);

        // 服务器处理完毕，返回状态码
        ObjectNode node = ds.process(ctx, info);
        if (node != null){
            System.out.println("处理完成");
            channel.writeAndFlush(node.toString());
        }
    }

//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        String name = DispatchService.userValueMap.get(ctx);
//        DispatchService.userValueMap.removeFile(ctx);
//        DispatchService.userKeyMap.removeFile(name);
//        System.out.println(ctx.channel().remoteAddress() + "下线了222");
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "上线了");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 用户下线
        String name = DispatchService.userValueMap.get(ctx);
        DispatchService.userValueMap.remove(ctx);
        DispatchService.userKeyMap.remove(name);
        DispatchService ds = DispatchService.getInstance();
        ObjectNode objectNode = JsonUtil.getObjectNode();
        objectNode.put("type", MsgTypeEnum.EN_MSG_NOTIFY_OFFLINE.toString());
        objectNode.put("name", name);
        ds.offLineNotification(objectNode);

        System.out.println(ctx.channel().remoteAddress() + "下线了");
    }

}

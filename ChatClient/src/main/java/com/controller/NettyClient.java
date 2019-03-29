package com.controller;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;


/**
 * description：
 *
 * @author ajie
 * data 2018/11/26 20:38
 */
public class NettyClient {
    public void clientStart(String ip, int port){
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap;
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(ip, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("handler", new ClientHandler());
                            pipeline.addLast("decoder", new StringDecoder());
                            pipeline.addLast("encoder", new StringEncoder());
                        }
                    });
            ChannelFuture channel = bootstrap.connect().sync();
            new ClientController(channel).run();
            channel.channel().closeFuture().sync();
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}

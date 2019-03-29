package com.controller;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * description：服务器启动类
 *
 * @author ajie
 * data 2018/11/26 20:04
 */
public class NettyServer {
    /**
     * 服务器启动,绑定端口
     * @param port 端口号
     */
    public void serverStart(int port){
        // 连接接受组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // 连接处理组
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        // 辅助启动类对象
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    // 对应的是tcp/ip协议listen函数中的backlog参数,指定了阻塞队列的大小
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    /*
                    对应于套接字选项中的SO_KEEPALIVE，该参数用于设置TCP连接，当设置该选项以后，
                    连接会测试链接的状态，这个选项用于可能长时间没有数据交流的连接。当设置该选项
                    以后，如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文。
                     */
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    /*
                    ChannelHandler主要作用是处理I/O事件或拦截I/O操作，
                    并将事件转发到其所属ChannelPipeline中的下一个ChannelHandler。
                    ChannelHandler通过ChannelHandlerContext对象与其所属的ChannelPipeline
                    进行交互。 使用ChannelHandlerContext对象，ChannelHandler可以在上游或下游传递事件，
                    执行I/O操作，动态修改流水线，或使用AttributeKeys存储ChannelHandler特有的信息等等。
                     */
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ServerHandler());
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                        }
                    });
            // 阻塞等待绑定，等待客户端连接
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("服务器启动成功,使用端口" + port);
            // 同步阻塞关闭连接通道
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //释放线程池资源
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}

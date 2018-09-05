package top.thevsk.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import top.thevsk.utils.LogKit;

/**
 * @author thevsk
 * @Title: NettyWebSocketBootstrap
 * @ProjectName police-link-netty
 * @date 2018-08-30 18:00
 */
public class NettyWebSocketBootstrap {

    private int port;

    public NettyWebSocketBootstrap(Integer port) throws Exception {
        this.port = port;
        bind();
    }

    private void bind() throws Exception {
        // 连接处理group
        EventLoopGroup boss = new NioEventLoopGroup();
        // 事件处理group
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        // 绑定处理group
        bootstrap.group(boss, worker);
        bootstrap.channel(NioServerSocketChannel.class);
        // 保持连接数
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024 * 1024);
        // 保持连接
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        // 处理新连接
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel sc) throws Exception {
                // 增加任务处理
                ChannelPipeline channelPipeline = sc.pipeline();
                channelPipeline.addLast(new HttpServerCodec());
                channelPipeline.addLast(new HttpObjectAggregator(65536));
                channelPipeline.addLast(new ChunkedWriteHandler());
                channelPipeline.addLast(new NettyServerHandler());
            }
        });

        ChannelFuture f = bootstrap.bind(port).sync();
        if (f.isSuccess()) {
            LogKit.info("开启WebSocket服务,端口: ? ", port);
        } else {
            LogKit.info("fail");
        }
    }
}

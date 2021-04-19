package my.cloud.server.service.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import my.cloud.server.factory.Factory;
import my.cloud.server.service.DbService;
import my.cloud.server.service.ServerService;
import my.cloud.server.service.handler.MainInboundHandler;

public class NettyServerService implements ServerService {

    private final int PORT = 8189;
    private DbService db;
    private ChannelFuture future;

    private static NettyServerService instance;

    public static ServerService getInstance() {
        if (instance == null) {
            instance = new NettyServerService();
        }
        return instance;
    }

    private NettyServerService() {}

    @Override
    public void startServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        db = Factory.getDbService();
//        db.addUser("user", "name", "qwerty");
        System.out.println(db.login("user", "qwerty"));

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(getClass().getClassLoader())),
                                    new ObjectEncoder(),
                                    new MainInboundHandler());
                        }
                    });
            future = b.bind(PORT).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            db.closeConnection();
        }
    }

    @Override
    public void stopServer() {
        if (future != null && future.channel().isOpen()) {
            future.channel().close();
        }
    }
}

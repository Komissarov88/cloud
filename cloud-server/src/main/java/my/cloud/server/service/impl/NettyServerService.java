package my.cloud.server.service.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import my.cloud.server.factory.Factory;
import my.cloud.server.service.DbService;
import my.cloud.server.service.ServerService;
import utils.PropertiesReader;

import java.util.concurrent.ConcurrentHashMap;

public class NettyServerService implements ServerService {

    private final int PORT = Integer.parseInt(PropertiesReader.getProperty("server.port"));
    private DbService db;
    private ChannelFuture future;
    private ConcurrentHashMap<Channel, String> users;

    private static NettyServerService instance;

    public static ServerService getInstance() {
        if (instance == null) {
            instance = new NettyServerService();
        }
        return instance;
    }

    private NettyServerService() {}

    private void defaultPipeline(ChannelPipeline pipeline) {
        pipeline.addLast("ObjectDecoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
        pipeline.addLast("ObjectEncoder", new ObjectEncoder());
        pipeline.addLast("MainInboundHandler", new MainInboundHandler());
    }

    @Override
    public boolean isUserLoggedIn(Channel channel) {
        return users.containsKey(channel);
    }

    @Override
    public void subscribeUser(String login, Channel channel) {
        users.putIfAbsent(channel, login);
    }

    @Override
    public void unsubscribeUser(Channel channel) {
        users.remove(channel);
    }

    @Override
    public void startServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        db = Factory.getDbService();
        users = new ConcurrentHashMap<>();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            defaultPipeline(socketChannel.pipeline());
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

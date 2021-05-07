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
import my.cloud.server.service.DBService;
import my.cloud.server.service.ServerService;
import utils.Logger;
import utils.PathUtils;
import utils.PropertiesReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class NettyServerServiceImpl implements ServerService {

    private final int PORT = Integer.parseInt(PropertiesReader.getProperty("server.port"));
    private final Path serverDataRoot = Paths.get(PropertiesReader.getProperty("server.data.root.path"));
    private DBService db;
    private ChannelFuture future;
    private ConcurrentHashMap<Channel, String> users;

    private static NettyServerServiceImpl instance;

    public static ServerService getInstance() {
        if (instance == null) {
            instance = new NettyServerServiceImpl();
        }
        return instance;
    }

    private NettyServerServiceImpl() {}

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
    public Path getUserRootPath(Channel channel) {
        if(!isUserLoggedIn(channel)) {
            return null;
        }
        return serverDataRoot.resolve(Paths.get(users.get(channel)));
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
            Logger.info("Server started.");
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            db.closeConnection();
        }
    }

    private void createUserFolder(String login) {
        try {
            Files.createDirectories(serverDataRoot.resolve(login));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getUserFreeSpace(Channel channel) {
        String login = users.get(channel);
        createUserFolder(login);
        Path path = serverDataRoot.resolve(login);
        List<File> files = PathUtils.getFilesListRecursively(path);
        long spaceLimit = Math.min(db.getSpaceLimit(login), path.toFile().getFreeSpace());
        return spaceLimit - PathUtils.getSize(files);
    }

    @Override
    public void stopServer() {
        if (future != null && future.channel().isOpen()) {
            future.channel().close();
        }
    }
}

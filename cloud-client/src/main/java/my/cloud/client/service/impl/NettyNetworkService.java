package my.cloud.client.service.impl;

import command.Command;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import my.cloud.client.service.NetworkService;
import utils.PropertiesReader;

import java.util.concurrent.ConcurrentLinkedDeque;

public class NettyNetworkService implements NetworkService {

    private final int PORT = Integer.parseInt(PropertiesReader.getProperty("server.port"));;
    private final String ADDRESS = PropertiesReader.getProperty("server.address");
    private SocketChannel socketChannel;
    private final ConcurrentLinkedDeque<Command> income;

    private static NettyNetworkService instance;

    public static NetworkService getInstance() {
        if (instance == null) {
            instance = new NettyNetworkService();
        }
        return instance;
    }

    private NettyNetworkService() {
        income = new ConcurrentLinkedDeque<>();
    }

    private void defaultPipeline(ChannelPipeline pipeline) {
        pipeline.addLast("ObjectDecoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
        pipeline.addLast("ObjectEncoder", new ObjectEncoder());
        pipeline.addLast("MainInboundHandler", new MainInboundHandler());
    }

    @Override
    public void connect() {
        if (isConnected()) {
            throw new RuntimeException("Channel already open");
        }
        new Thread(()-> {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                NettyNetworkService.this.socketChannel = socketChannel;
                                defaultPipeline(socketChannel.pipeline());
                            }
                        });
                ChannelFuture future = b.connect(ADDRESS, PORT).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        }).start();
    }

    @Override
    public void sendCommand(Command command) {
        if (isConnected()) {
            socketChannel.writeAndFlush(command);
        }
    }

    @Override
    public Command readCommandResult() {
        synchronized (income){
            try {
                if (income.isEmpty()) {
                    Thread.currentThread().wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return income.poll();
    }

    @Override
    public void closeConnection() {
        if (isConnected()) {
            socketChannel.close();
        }
    }

    public boolean isConnected() {
        return socketChannel != null && socketChannel.isOpen();
    }
}

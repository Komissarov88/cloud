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
import utils.Logger;
import utils.PropertiesReader;

/**
 * Class used for all connection to server
 */
public class CloudConnection implements Runnable{

    private final int PORT = Integer.parseInt(PropertiesReader.getProperty("server.port"));
    private final String ADDRESS = PropertiesReader.getProperty("server.address");
    private SocketChannel socketChannel;
    private Command initialCommand;

    /**
     * @param initialCommand sends to server right after connection
     */
    public CloudConnection(Command initialCommand) {
        this.initialCommand = initialCommand;
    }

    private void defaultPipeline(ChannelPipeline pipeline) {
        pipeline.addLast("ObjectDecoder", new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
        pipeline.addLast("ObjectEncoder", new ObjectEncoder());
        pipeline.addLast("MainInboundHandler", new MainInboundHandler());
    }

    @Override
    public void run() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            CloudConnection.this.socketChannel = socketChannel;
                            defaultPipeline(socketChannel.pipeline());
                        }
                    });
            ChannelFuture future = b.connect(ADDRESS, PORT).sync();
            if (initialCommand != null) {
                socketChannel.writeAndFlush(initialCommand);
            }
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            Logger.info("connection closed");
        }
    }

    public void sendCommand(Command command) {
        if (isConnected()) {
            socketChannel.writeAndFlush(command);
        }
    }

    public void disconnect() {
        if (isConnected()) {
            socketChannel.close();
        }
    }

    public boolean isConnected() {
        if (socketChannel == null) {
            return false;
        }
        return socketChannel.isOpen();
    }
}

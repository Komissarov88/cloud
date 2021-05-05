package my.cloud.client.service.impl;

import command.domain.Command;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import my.cloud.client.factory.Factory;
import utils.Logger;

/**
 * Translates incoming messages to command dictionary command.service
 */
public class MainInboundHandler extends SimpleChannelInboundHandler<Command> {

    private final Runnable onChannelInactive;

    public MainInboundHandler(Runnable onChannelInactive) {
        this.onChannelInactive = onChannelInactive;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Logger.info("server connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Logger.info("server disconnected");
        if (onChannelInactive != null) {
            onChannelInactive.run();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command msg) {
        Logger.info(msg.toString());
        Factory.getCommandDictionaryService().processCommand(msg, ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Logger.error(cause.getMessage());
        ctx.close();
    }
}

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

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Logger.info("server connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Logger.info("server disconnected");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command msg) {
        Factory.getCommandDictionaryService().processCommand(msg, ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

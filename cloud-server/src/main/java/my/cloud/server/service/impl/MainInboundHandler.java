package my.cloud.server.service.impl;

import command.domain.Command;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import my.cloud.server.factory.Factory;
import utils.Logger;

/**
 * Translates incoming messages to command dictionary command.service
 */
public class MainInboundHandler extends SimpleChannelInboundHandler<Command> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Logger.info("client connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Logger.info("client disconnected");
        Factory.getServerService().unsubscribeUser(ctx.channel());
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

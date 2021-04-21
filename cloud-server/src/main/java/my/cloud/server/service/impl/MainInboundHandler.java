package my.cloud.server.service.impl;

import command.Command;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import my.cloud.server.factory.Factory;

import java.util.logging.Logger;

public class MainInboundHandler extends SimpleChannelInboundHandler<Command> {

    private static Logger logger = Logger.getLogger(MainInboundHandler.class.getName());

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("client connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("client disconnected");
        Factory.getServerService().unsubscribeUser(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command msg) {
        System.out.println(msg);
        Factory.getCommandDictionaryService().processCommand(msg, ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

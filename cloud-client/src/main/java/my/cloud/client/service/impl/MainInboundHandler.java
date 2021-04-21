package my.cloud.client.service.impl;

import command.Command;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Logger;

public class MainInboundHandler extends SimpleChannelInboundHandler<Command> {

    private static Logger logger = Logger.getLogger(MainInboundHandler.class.getName());

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command msg) {
        logger.info(msg.getCode().toString());
        for (String arg : msg.getArgs()) {
            System.out.println(arg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}

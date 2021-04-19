package my.cloud.server.service.handler;

import command.Command;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MainInboundHandler extends SimpleChannelInboundHandler<Command> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command msg) throws Exception {
        System.out.println(msg.getClass().getName());
        System.out.println(msg.getMsg());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

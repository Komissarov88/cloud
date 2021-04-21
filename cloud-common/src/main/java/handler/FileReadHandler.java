package handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileReadHandler extends SimpleChannelInboundHandler<ByteBuf> {

    public final OutputStream outputStream;
    private long bytesReceived;

    public FileReadHandler(Path path) {
        OutputStream os = null;
        try {
            os = Files.newOutputStream(path, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            e.printStackTrace();
        }
        outputStream = os;
    }

    public long getBytesReceived() {
        return bytesReceived;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        outputStream.flush();
        outputStream.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        outputStream.write(msg.array());
        bytesReceived += msg.readableBytes();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        outputStream.close();
    }
}

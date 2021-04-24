package handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes incoming bytes to file with overwrite
 */
public class FileReadHandler extends ChannelInboundHandlerAdapter {

    public final OutputStream outputStream;
    private long bytesReceived;

    public FileReadHandler(Path path) {
        OutputStream os = null;
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            os = Files.newOutputStream(path);
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
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        if (buf.isReadable()) {
            buf.readBytes(outputStream, buf.readableBytes());
            bytesReceived += buf.readableBytes();
        }
        buf.release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        outputStream.close();
        ctx.close();
    }
}

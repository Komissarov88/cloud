package files.handler;

import files.domain.FileTransferStatus;
import files.domain.TransferId;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.io.FileUtils;
import utils.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;

/**
 * Writes incoming bytes to file with overwrite
 */
public class FileReadHandlerWithCallback extends ChannelInboundHandlerAdapter {

    public final OutputStream outputStream;
    private final TransferId transferId;
    private BiConsumer<Path, Integer> transferListener;

    public FileReadHandlerWithCallback(TransferId transferId) {
        OutputStream os = null;
        Path path = transferId.destination;
        this.transferId = transferId;
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

    public void setTransferListener(BiConsumer<Path, Integer> transferListener) {
        this.transferListener = transferListener;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        outputStream.flush();
        outputStream.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        if (transferListener != null) {
            transferListener.accept(transferId.origin, buf.readableBytes());
        }
        buf.readBytes(outputStream, buf.readableBytes());
        buf.release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Logger.error(cause.getMessage());
        outputStream.close();
        ctx.close();
        if (transferListener != null) {
            transferListener.accept(transferId.origin, FileTransferStatus.INTERRUPTED.value);
        }
        FileUtils.deleteQuietly(transferId.destination.toFile());
    }
}

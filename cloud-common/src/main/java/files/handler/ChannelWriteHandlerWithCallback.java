package files.handler;

import files.domain.TransferId;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.nio.file.Path;
import java.util.function.BiConsumer;

public class ChannelWriteHandlerWithCallback extends ChannelOutboundHandlerAdapter {

    private final TransferId transferId;
    private BiConsumer<Path, Integer> transferListener;

    public ChannelWriteHandlerWithCallback(TransferId transferId) {
        this.transferId = transferId;
    }

    public void setTransferListener(BiConsumer<Path, Integer> transferListener) {
        this.transferListener = transferListener;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        if (transferListener != null) {
            transferListener.accept(transferId.origin, buf.readableBytes());
        }
        ctx.write(msg, promise);
    }
}

package files.handler;

import files.domain.Transfer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.nio.file.Path;
import java.util.function.BiConsumer;

public class ChannelWriteHandlerWithCallback extends ChannelOutboundHandlerAdapter {

    private final Transfer transfer;
    private BiConsumer<Path, Integer> transferListener;

    public ChannelWriteHandlerWithCallback(Transfer transfer) {
        this.transfer = transfer;
    }

    public void setTransferListener(BiConsumer<Path, Integer> transferListener) {
        this.transferListener = transferListener;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        if (transferListener != null) {
            transferListener.accept(transfer.origin, buf.readableBytes());
        }
        ctx.write(msg, promise);
    }
}

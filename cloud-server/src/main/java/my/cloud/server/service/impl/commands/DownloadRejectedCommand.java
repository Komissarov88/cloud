package my.cloud.server.service.impl.commands;

import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;

import static my.cloud.server.service.impl.commands.util.ServerCommandUtil.disconnectIfUnknown;

/**
 * Called when client cant accept files
 */
public class DownloadRejectedCommand implements CommandService {

    private boolean notCorrectCommand(ChannelHandlerContext ctx, String[] args) {
        return disconnectIfUnknown(ctx);
    }

    @Override
    public void processCommand(ChannelHandlerContext ctx, String[] args) {
        if (notCorrectCommand(ctx, args)) {
            return;
        }
        for (String arg : args) {
            Factory.getFileTransferAuthService().remove(arg);
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.OFFER_REFUSED;
    }

}

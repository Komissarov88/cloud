package my.cloud.client.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import files.domain.TransferId;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.client.factory.Factory;

/**
 * Called when server cant accept upload
 */
public class FilesOfferRefusedCommand implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        if (command.getArgs().length > 0) {
            TransferId t = Factory.getFileTransferAuthService().getTransferIfValid(command.getArgs()[0]);
            Factory.getUploadProgressService().remove(t.origin);
        }
        for (int i = 1; i < command.getArgs().length; i++) {
            TransferId t = Factory.getFileTransferAuthService().getTransferIfValid(command.getArgs()[i]);
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.OFFER_REFUSED;
    }

}

package my.cloud.client.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.client.factory.Factory;

/**
 * Called on successful authentication
 */
public class FilesOfferRefusedCommand implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        for (int i = 0; i < command.getArgs().length; i++) {
            Factory.getFileTransferAuthService().remove(command.getArgs()[i]);
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.OFFER_REFUSED;
    }

}

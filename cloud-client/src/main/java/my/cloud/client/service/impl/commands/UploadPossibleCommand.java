package my.cloud.client.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.impl.CloudConnection;
import utils.Logger;

/**
 * Called when server sends client authentication key for upload channel
 */
public class UploadPossibleCommand implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {

        if (command.getArgs() == null
                || command.getArgs().length != 2) {
            Logger.warning("wrong arguments");
            return;
        }

        String[] args = {
                command.getArgs()[0], //channel auth key
                command.getArgs()[1]  //client job key
        };

        CloudConnection uploadConnection = new CloudConnection(new Command(CommandCode.UPLOAD, args));
        Factory.getNetworkService().submitConnection(uploadConnection);
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.UPLOAD_POSSIBLE;
    }

}

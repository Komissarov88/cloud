package my.cloud.client.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.client.service.impl.CloudConnection;
import utils.Logger;

/**
 * Called when server sends client authentication key for upload channel
 */
public class UploadRequestCommand implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        Logger.info(command.toString());

        if (command.getArgs() == null
                || command.getArgs().length != 2) {
            Logger.warning("wrong arguments");
            return;
        }

        String[] args = {
                command.getArgs()[0],
                command.getArgs()[1]
        };

        CloudConnection uploadConnection = new CloudConnection(new Command(CommandCode.UPLOAD, args));
        new Thread(uploadConnection).start();
    }

    @Override
    public CommandCode getCommand() {
        return CommandCode.UPLOAD_REQUEST;
    }

}

package my.cloud.client.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.client.service.impl.CloudConnection;
import utils.Logger;

import java.io.File;

/**
 * Called when server sends key to authenticate download channel
 */
public class DownloadRequestCommand implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        Logger.info(command.toString());

        if (command.getArgs() == null
                || command.getArgs().length != 3) {
            Logger.warning("wrong arguments");
            return;
        }

        String key = command.getArgs()[0];
        String fileName = command.getArgs()[1];
        long size = Long.parseLong(command.getArgs()[2]);
        File file = new File("./" + fileName);
        if (file.getParentFile().getFreeSpace() > size) {
            Command initialCommand = new Command(CommandCode.DOWNLOAD, key, file.toString());
            CloudConnection downloadConnection = new CloudConnection(initialCommand);
            new Thread(downloadConnection).start();
        } else {
            Logger.warning("no free space");
        }
    }

    @Override
    public CommandCode getCommand() {
        return CommandCode.DOWNLOAD_REQUEST;
    }

}

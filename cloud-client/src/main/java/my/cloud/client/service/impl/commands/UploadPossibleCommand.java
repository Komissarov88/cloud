package my.cloud.client.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.client.factory.Factory;
import my.cloud.client.service.impl.CloudConnection;
import utils.Logger;

import java.util.function.Consumer;

/**
 * Called when server sends client authentication key for upload channel
 */
public class UploadPossibleCommand implements CommandService {

    private Consumer<String[]> consumer;

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

        CloudConnection uploadConnection = new CloudConnection(new Command(CommandCode.UPLOAD, args), null);
        Factory.getNetworkService().submitConnection(uploadConnection);

        if (consumer != null) {
            consumer.accept(command.getArgs());
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.UPLOAD_POSSIBLE;
    }

    @Override
    public void setListener(Consumer<String[]> consumer) {
        this.consumer = consumer;
    }
}

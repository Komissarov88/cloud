package my.cloud.server.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import command.service.CommandService;
import utils.PropertiesReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Called when client want to login
 */
public class AuthenticateUserCommand implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {

        if (command.getArgs() == null || command.getArgs().length != 2) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "wrong arguments"));
            ctx.close();
            return;
        }

        String login = command.getArgs()[0];
        String password = command.getArgs()[1];

        if (Factory.getDbService().login(login, password)) {
            Factory.getServerService().subscribeUser(login, ctx.channel());
            ctx.writeAndFlush(new Command(CommandCode.SUCCESS, "authenticated"));

            try {
                Files.createDirectories(Paths.get(PropertiesReader.getProperty("server.data.root.path"), login));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "authentication fails"));
            ctx.close();
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.AUTH;
    }

}

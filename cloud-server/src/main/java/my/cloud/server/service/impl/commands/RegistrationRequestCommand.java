package my.cloud.server.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import utils.PropertiesReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Called when client want to register
 */
public class RegistrationRequestCommand implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {

        if (command.getArgs() == null || command.getArgs().length != 2) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "wrong arguments, expected login password pair"));
            ctx.close();
            return;
        }

        String login = command.getArgs()[0];
        String password = command.getArgs()[1];

        if (login.length() == 0 || password.length() == 0) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "login/password expected not empty"));
            ctx.close();
            return;
        }

        if (Factory.getDbService().addUser(login, password)) {
            Factory.getServerService().subscribeUser(command.getArgs()[0], ctx.channel());

            try {
                Files.createDirectories(Paths.get(PropertiesReader.getProperty("server.data.root.path"), login));
            } catch (IOException e) {
                e.printStackTrace();
            }

            ctx.writeAndFlush(new Command(CommandCode.SUCCESS, "registered"));
        } else {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "user already exists"));
            ctx.close();
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.REGISTER_REQUEST;
    }

}

package my.cloud.server.service.impl.commands;

import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import utils.PropertiesReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static my.cloud.server.service.impl.commands.util.ServerCommandUtil.*;

/**
 * Called when client want to register
 */
public class RegistrationRequestCommand implements CommandService {

    private boolean notCorrectCommand(ChannelHandlerContext ctx, String[] args) {
        return disconnectIfArgsLengthNotEqual(ctx, 2, args);
    }

    @Override
    public void processCommand(ChannelHandlerContext ctx, String[] args) {

        if (notCorrectCommand(ctx, args)) {
            return;
        }

        String login = args[0];
        String password = args[1];

        if (login.length() == 0 || password.length() == 0) {
            sendFailMessage(ctx,"login/password expected not empty");
            ctx.close();
            return;
        }

        if (Factory.getDbService().addUser(login, password)) {
            Factory.getServerService().subscribeUser(login, ctx.channel());
            createUserFolder(login);
            sendResponse(ctx, CommandCode.SUCCESS, "registered");
        } else {
            sendFailMessage(ctx,"user already exists");
            ctx.close();
        }
    }

    private void createUserFolder(String login) {
        try {
            Files.createDirectories(Paths.get(PropertiesReader.getProperty("server.data.root.path"), login));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return CommandCode.REGISTER_REQUEST;
    }

}

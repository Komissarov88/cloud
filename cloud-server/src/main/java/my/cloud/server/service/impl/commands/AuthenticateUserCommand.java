package my.cloud.server.service.impl.commands;

import command.domain.Command;
import command.domain.CommandCode;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import command.service.CommandService;

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
        if (Factory.getDbService().login(command.getArgs()[0], command.getArgs()[1])) {
            Factory.getServerService().subscribeUser(command.getArgs()[0], ctx.channel());
            ctx.writeAndFlush(new Command(CommandCode.SUCCESS, "authenticated"));
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

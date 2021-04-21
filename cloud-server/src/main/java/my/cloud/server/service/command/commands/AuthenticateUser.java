package my.cloud.server.service.command.commands;

import command.Command;
import command.CommandCode;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import my.cloud.server.service.command.CommandService;

public class AuthenticateUser implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {

        if (command.getArgs() == null || command.getArgs().length != 2) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL));
            return;
        }
        if (Factory.getDbService().login(command.getArgs()[0], command.getArgs()[1])) {
            Factory.getServerService().subscribeUser(command.getArgs()[0], ctx.channel());
            ctx.writeAndFlush(new Command(CommandCode.OK));
        } else {
            ctx.writeAndFlush(new Command(CommandCode.FAIL));
        }
    }

    @Override
    public CommandCode getCommand() {
        return CommandCode.AUTH;
    }

}

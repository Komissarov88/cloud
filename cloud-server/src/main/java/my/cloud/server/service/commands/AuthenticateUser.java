package my.cloud.server.service.commands;

import command.Command;
import command.CommandCode;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import command.CommandService;

public class AuthenticateUser implements CommandService {

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {

        if (command.getArgs() == null || command.getArgs().length != 2) {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "wrong arguments"));
            return;
        }
        if (Factory.getDbService().login(command.getArgs()[0], command.getArgs()[1])) {
            Factory.getServerService().subscribeUser(command.getArgs()[0], ctx.channel());
            ctx.writeAndFlush(new Command(CommandCode.OK, "authenticated"));
        } else {
            ctx.writeAndFlush(new Command(CommandCode.FAIL, "authentication fails"));
        }
    }

    @Override
    public CommandCode getCommand() {
        return CommandCode.AUTH;
    }

}

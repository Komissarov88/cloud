package my.cloud.client.service.impl.commands.base;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;
import utils.Logger;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class BaseClientCommand implements CommandService {

    protected boolean disconnectOnFail;
    protected Predicate<Integer> expectedArgumentsCountCheck;
    protected ChannelHandlerContext ctx;
    private Command command;
    private Consumer<String[]> consumer;

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        this.command = command;
        this.ctx = ctx;
        if (expectedArgumentsCountCheck == null) {
            throw new IllegalStateException("expectedArgumentsCountCheck predicate is null");
        }
        if (isCommandArgumentsCorrect()){
            processArguments(command.getArgs());
            if (consumer != null) {
                consumer.accept(command.getArgs());
            }
        } else if (disconnectOnFail) {
            ctx.close();
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return null;
    }

    protected void processArguments(String[] args) {}

    private boolean isCommandArgumentsCorrect() {

        if (command.getArgs() == null
                || !expectedArgumentsCountCheck.test(command.getArgs().length) ) {
            sendFailMessage("wrong arguments");
            return false;
        }
        return true;
    }

    protected void sendFailMessage(String msg) {
        Logger.error(msg);
    }

    @Override
    public void setListener(Consumer<String[]> consumer) {
        this.consumer = consumer;
    }
}

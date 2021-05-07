package my.cloud.server.service.impl.commands.base;

import command.domain.Command;
import command.domain.CommandCode;
import command.service.CommandService;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import utils.PathUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

public class BaseServerCommand implements CommandService {

    protected boolean isAuthNeeded;
    protected boolean disconnectOnFail;
    protected Predicate<Integer> expectedArgumentsCountCheck;
    protected ChannelHandlerContext ctx;
    private Command command;

    @Override
    public void processCommand(Command command, ChannelHandlerContext ctx) {
        this.command = command;
        this.ctx = ctx;
        if (expectedArgumentsCountCheck == null) {
            throw new IllegalStateException("expectedArgumentsCountCheck predicate is null");
        }
        if (isCommandArgumentsCorrect()){
            processArguments(command.getArgs());
        } else if (disconnectOnFail) {
            ctx.close();
        }
    }

    @Override
    public CommandCode getCommandCode() {
        return null;
    }

    protected void processArguments(String[] args) {
        throw new UnsupportedOperationException();
    }

    protected Path getUserRootPath() {
        return Factory.getServerService().getUserRootPath(ctx.channel());
    }

    protected File getFileFromClientRequest(String requestedFile) {
        Path rootUserPath = getUserRootPath();
        Path requestPath = Paths.get(rootUserPath.toString(), requestedFile);

        if (PathUtils.isPathsParentAndChild(rootUserPath, requestPath)) {
            return requestPath.toFile();
        }
        return null;
    }

    private boolean isCommandArgumentsCorrect() {
        if (isAuthNeeded && !Factory.getServerService().isUserLoggedIn(ctx.channel())) {
            sendFailMessage("channel supposed to be authenticated");
            return false;
        }

        if (command.getArgs() == null
                || !expectedArgumentsCountCheck.test(command.getArgs().length) ) {
            sendFailMessage("wrong arguments");
            return false;
        }
        return true;
    }

    protected void sendResponse(CommandCode code, String... args) {
        ctx.writeAndFlush(new Command(code, args));
    }

    protected void sendFailMessage(String... args) {
        sendResponse(CommandCode.FAIL, args);
    }
}

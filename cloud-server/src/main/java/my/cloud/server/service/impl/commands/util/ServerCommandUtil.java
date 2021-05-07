package my.cloud.server.service.impl.commands.util;

import command.domain.Command;
import command.domain.CommandCode;
import io.netty.channel.ChannelHandlerContext;
import my.cloud.server.factory.Factory;
import utils.PathUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

public class ServerCommandUtil {

    private ServerCommandUtil() {}

    public static Path getUserRootPath(ChannelHandlerContext ctx) {
        return Factory.getServerService().getUserRootPath(ctx.channel());
    }

    public static File getFileFromClientRequest(ChannelHandlerContext ctx, String requestedFile) {
        Path rootUserPath = getUserRootPath(ctx);
        Path requestPath = Paths.get(rootUserPath.toString(), requestedFile);

        if (PathUtils.isPathsParentAndChild(rootUserPath, requestPath)) {
            return requestPath.toFile();
        }
        return null;
    }

    public static boolean disconnectIfUnknown(ChannelHandlerContext ctx) {
        if (!isLoggedIn(ctx)) {
            sendFailMessage(ctx, "Authorisation required");
            ctx.close();
            return true;
        }
        return false;
    }

    public static boolean disconnectIfArgsLengthNotEqual(ChannelHandlerContext ctx, int length, String[] args) {
        if (args == null || args.length != length) {
            sendFailMessage(ctx, "Wrong arguments");
            ctx.close();
            return true;
        }
        return false;
    }

    public static boolean wrongArgumentsLength(ChannelHandlerContext ctx, String[] args, Predicate<Integer> predicate) {
        if (args == null || predicate.test(args.length)) {
            sendFailMessage(ctx, "Wrong arguments");
            return true;
        }
        return false;
    }

    public static boolean isLoggedIn(ChannelHandlerContext ctx) {
        return Factory.getServerService().isUserLoggedIn(ctx.channel());
    }

    public static void sendResponse(ChannelHandlerContext ctx, CommandCode code, String... args) {
        ctx.writeAndFlush(new Command(code, args));
    }

    public static void sendFailMessage(ChannelHandlerContext ctx, String... args) {
        sendResponse(ctx, CommandCode.FAIL, args);
    }
}

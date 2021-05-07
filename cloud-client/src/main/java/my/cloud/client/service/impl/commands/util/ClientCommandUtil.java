package my.cloud.client.service.impl.commands.util;

import utils.Logger;

import java.util.function.Predicate;

public class ClientCommandUtil {

    public static boolean wrongArgumentsLength(String[] args, Predicate<Integer> predicate) {
        if (args == null || predicate.test(args.length)) {
            sendFailMessage("Wrong arguments");
            return true;
        }
        return false;
    }

    public static void sendFailMessage(String msg) {
        Logger.warning(msg);
    }
}

package utils;

import java.nio.charset.StandardCharsets;

public class Logger {

    private static String wrapMsg(String s) {
        return new String(s.getBytes(StandardCharsets.UTF_8));
    }

    public static void info(String string) {
        System.out.println("info: " + wrapMsg(string));
    }

    public static void warning(String string) {
        System.out.println("warring: " + wrapMsg(string));
    }

    public static void error(String string) {
        System.out.println(wrapMsg(string));
    }
}

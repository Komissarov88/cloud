package utils;

public class Logger {

    public static void info(String string) {
        System.out.println("info: " + string);
    }

    public static void warning(String string) {
        System.out.println("warring: " + string);
    }

    public static void error(String s) {
        System.out.println(s);
    }
}

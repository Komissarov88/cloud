package my.cloud.server;

import my.cloud.server.factory.Factory;

import java.util.Scanner;
import java.util.logging.Logger;

public class MainServer {

    private static Logger logger = Logger.getLogger(MainServer.class.getName());

    public static void main(String[] args) {

        new Thread(() -> {
            Factory.getServerService().startServer();
        }).start();

        Scanner scanner = new Scanner(System.in);
        while(true) {
            String s = scanner.nextLine();
            if (s.equals("stop")) {
                logger.info("stopping server...");
                Factory.getServerService().stopServer();
                break;
            }
        }
    }
}

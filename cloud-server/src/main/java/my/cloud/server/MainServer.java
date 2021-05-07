package my.cloud.server;

import my.cloud.server.factory.Factory;
import utils.Logger;

import java.util.Scanner;

public class MainServer {

    public static void main(String[] args) {
        Migrations.migrate();

        Thread t = new Thread(() -> {
            Logger.info("Print \"stop\" to quit");
            Scanner scanner = new Scanner(System.in);
            while(true) {
                String s = scanner.nextLine();
                if (s.equals("stop")) {
                    Logger.info("stopping server...");
                    Factory.getServerService().stopServer();
                    break;
                }
            }
        });
        t.setDaemon(true);
        t.start();

        Factory.getServerService().startServer();
    }
}

package my.cloud.server;

import my.cloud.server.factory.Factory;
import utils.Logger;

import java.util.Scanner;

public class MainServer {

    public static void main(String[] args) {
        Migrations.migrate();

        new Thread(() -> {
            Factory.getServerService().startServer();
        }).start();

        Logger.info("Server started. Print \"stop\" to quit");
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String s = scanner.nextLine();
            if (s.equals("stop")) {
                Logger.info("stopping server...");
                Factory.getServerService().stopServer();
                break;
            }
        }
    }
}

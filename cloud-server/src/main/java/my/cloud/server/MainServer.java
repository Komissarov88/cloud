package my.cloud.server;

import my.cloud.server.factory.Factory;
import utils.Logger;

import java.util.Scanner;

public class MainServer {

    public static void main(String[] args) {
        Migrations.migrate();
        Factory.getServerService().startServer();
    }
}

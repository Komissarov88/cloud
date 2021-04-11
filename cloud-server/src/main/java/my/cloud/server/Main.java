package my.cloud.server;

import my.cloud.server.factory.Factory;

public class Main {

    public static void main(String[] args) {
        Factory.getServerService().startServer();
    }
}

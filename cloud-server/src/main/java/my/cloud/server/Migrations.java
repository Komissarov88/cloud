package my.cloud.server;

import org.flywaydb.core.Flyway;
import utils.PropertiesReader;

public class Migrations {

    private static int PORT = Integer.parseInt(PropertiesReader.getProperty("db.port"));
    private static String HOST = PropertiesReader.getProperty("db.address");
    private static String DB = PropertiesReader.getProperty("db.name");
    private static String USER = PropertiesReader.getProperty("db.user");
    private static String PWD = PropertiesReader.getProperty("db.password");

    public static void migrate() {

        String url = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB;

        Flyway flyway = Flyway.configure()
                .mixed(true)
                .dataSource(url, USER, PWD).load();
        flyway.migrate();
    }
}

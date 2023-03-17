package my.cloud.server;

import org.flywaydb.core.Flyway;
import utils.PropertiesReader;

public class Migrations {

    private static final int PORT = Integer.parseInt(PropertiesReader.getProperty("db.port"));
    private static final String HOST = PropertiesReader.getProperty("db.address");
    private static final String DB = PropertiesReader.getProperty("db.name");
    private static final String USER = PropertiesReader.getProperty("db.user");
    private static final String PWD = PropertiesReader.getProperty("db.password");

    public static void migrate() {
        String url = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB;
        Flyway flyway = Flyway.configure()
                .mixed(true)
                .dataSource(url, USER, PWD).load();
        flyway.migrate();
    }
}

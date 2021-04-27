package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    private static final String RESOURCE = "properties/common.properties";
    private static final Properties properties;

    static  {
        InputStream is = PropertiesReader.class.getClassLoader()
                .getResourceAsStream(RESOURCE);
        properties = new Properties();

        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }
}

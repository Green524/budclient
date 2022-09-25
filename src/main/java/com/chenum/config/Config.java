package com.chenum.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class Config {

    private static Properties properties = new Properties();
    static {

        try {
            InputStream is = Config.class.getResourceAsStream("/config/config-dev.properties");
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static String get(String key){
        return properties.getProperty(key);
    }
}

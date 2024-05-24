package com.email.mailclient.visuals;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class TestConfig {
    private static Properties config = new Properties(); // Initialize the Properties object

    public static void main(String[] args) throws IOException {
        FileReader reader = new FileReader("config/mailclient.properties");
        config.load(reader);
        System.out.println(config.getProperty("default.domain"));
    }
}

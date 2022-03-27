package com.crypto.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigFileReader {

    private static final Logger logger = LogManager.getLogger(ConfigFileReader.class);

    InputStream input = null;
    Properties prop = new Properties();
    private static ConfigFileReader readConfig;

    private ConfigFileReader() {
    }

    public static ConfigFileReader getInstance() {

        return (readConfig == null) ? new ConfigFileReader() : readConfig;

    }

    public String getPropertyValue(String property) {

        String propertyValue = "";

        try {
            logger.info("Reading property :" + property);
            readFile();
            propertyValue = prop.getProperty(property).trim();
            logger.info("Value of " + property + " = " + propertyValue);

        } catch (Exception ex) {
            logger.error("Error occurred while reading property : " + property);
        } finally {
            if (input != null) {
                closeFile();
            }
        }
        return propertyValue;
    }

    private void closeFile() {
        try {
            input.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void readFile() {
        try {
            input = new FileInputStream(getClass().getClassLoader().getResource(Constants.CONFIG_FILE).getFile());
            prop.load(input);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private Properties config;
    private final Logger LOGGER = LoggerFactory.getLogger(Config.class.getName());

    /**
     * Read config file and create Object to retrieve data.
     */
    public Config(String configFilePath) {
        config = new Properties();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configFilePath);
            if (inputStream != null) {
                config.load(inputStream);
            } else {
                LOGGER.error("Read config file failed.", new FileNotFoundException("property file '" + configFilePath + "' not found."));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return config.getProperty(key);
    }

    public void setProperty(String key, String value) {
        config.setProperty(key, value);
    }
}

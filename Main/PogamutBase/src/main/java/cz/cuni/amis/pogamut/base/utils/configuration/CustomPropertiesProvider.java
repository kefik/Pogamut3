package cz.cuni.amis.pogamut.base.utils.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.configuration.PropertyProvider;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Loads properties from the working directory.
 * @author ik
 */
public class CustomPropertiesProvider extends PropertyProvider {
	
	LogCategory log = new LogCategory("CustomPropertiesProvider");

    static final String resource = "PogamutPlatformCustom.properties";
    Properties properties = new Properties();
    File propFile;
    
    public CustomPropertiesProvider() {
    	log.addConsoleHandler();
        try {
            propFile = new File(resource);
            properties.load(new FileInputStream(propFile));
        } catch (FileNotFoundException e) {
            // TODO how to pass logger
        	log.warning("Custom property file not found in " + propFile.getAbsolutePath() + ".");
        } catch (IOException ex) {
             throw new PogamutException("I/O exception while reading the custom Pogamut platform property file 'PogamutPlatformCustom.properties', absolute location '" + propFile.getAbsolutePath() + "'.", ex, log, this);
        }
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public int getPriority() {
        return 1000;
    }

    @Override
    public String toString() {
        return "CustomPropertiesProvider[source='" + propFile.getAbsolutePath() + "']";
    }
}

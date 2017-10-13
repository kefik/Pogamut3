package cz.cuni.amis.utils.configuration.providers;

import cz.cuni.amis.utils.configuration.PropertyProvider;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Wrapper of java.util.Properties class.
 * @author ik
 */
public abstract class AbstractPropertiesProvider extends PropertyProvider {

    Properties props = new Properties();
    String sourceDescription;

    /**
     * @param sourceDescription human readable decription of resource from which the properties were read
     * @param is Reader from which the Properties can be loaded.
     */
    public AbstractPropertiesProvider(InputStream is, String sourceDescription) {
        try {
            props.load(is);
            this.sourceDescription = sourceDescription;
        } catch (Exception ex) {
             throw new RuntimeException("Properties cannot be read from '" + sourceDescription +"'.", ex);
        }
    }

    @Override
    public String getProperty(String key) {
        return props.getProperty(key);
    }

    @Override
    public String toString() {
        return "Properties from " + sourceDescription;
    }
}

package cz.cuni.amis.utils.configuration;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import cz.cuni.amis.utils.exception.PogamutException;
import cz.cuni.amis.utils.logging.DefaultLogFormatter;

/**
 * Root object for obtaining property values. Properties are obtained from a chain
 * of PropertyProviders. PropertyProviders are loaded through SPI.
 * @author ik
 */
public class PropertiesManager {

    Logger log = Logger.getLogger("PropertiesManager");
    private List<PropertyProvider> providers = null;
    
    public PropertiesManager() {
    	log.addHandler(new Handler() {
    		
    		DefaultLogFormatter logFormatter = new DefaultLogFormatter("Platform", false);

			@Override
			public void close() throws SecurityException {
			}

			@Override
			public void flush() {
			}

			@Override
			public void publish(LogRecord record) {
				System.out.println(logFormatter.format(record));
			}
    		
    	});
    	log.setLevel(Level.INFO);
    	log.info("Instantiated");
    }

    protected List<PropertyProvider> getProvidersList() {
        try {
        	if (providers == null) {
	            providers = new LinkedList<PropertyProvider>();
	            ServiceLoader<PropertyProvider> loader = ServiceLoader.load(PropertyProvider.class);
	            Set<Class> loaded = new HashSet<Class>();
	            for (PropertyProvider provider : loader) {
	            	if (loaded.contains(provider.getClass())) {
	            		if (log.isLoggable(Level.WARNING)) log.warning("Attempt to register PropertyProvider of class " + provider.getClass() + " twice, ignoring.");
	            	} else {
	            		providers.add(provider);
	            		loaded.add(provider.getClass());
	            	}
	            }
	            Collections.sort(providers);
	            
	            logProvidersOrder();
	        }
        } catch (Exception e) {
        	throw new PogamutException("Could not initialize PropertiesManager: " + e.getMessage(), e, log, this);
        }
        return providers;
    }

    protected void logProvidersOrder() {
        if (log.isLoggable(Level.INFO)) log.info("Property providers order:");
        if (getProvidersList() != null && getProvidersList().size() != 0) {
	        for (PropertyProvider provider : getProvidersList()) {
	            String str = "[" + provider.getPriority() + "] \t " + provider.toString();
	            if (log.isLoggable(Level.INFO)) log.info(str);
	        }
        } else {
        	if (log.isLoggable(Level.WARNING)) log.warning("There are no PropertyProvider(s) registered inside PropertiesManager! No properties will be available!");
        }
    }

    /**
     * Returns property for given key.
     * Asks property providers ordered by their priority. When first of them
     * returns a value then it will be returned and later providers wont be asked.
     * @param key
     * @return null if the property value wasn't found
     */
    public String getProperty(String key) {
        for (PropertyProvider provider : getProvidersList()) {
            String val = provider.getProperty(key);
            if (val != null) {
                if (log.isLoggable(Level.FINEST)) log.finest("Property " + key + " was loaded from " + provider.toString() + ".");
                return val;
            }
        }
        return null;
    }
}

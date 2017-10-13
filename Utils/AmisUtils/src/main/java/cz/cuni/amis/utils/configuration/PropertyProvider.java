package cz.cuni.amis.utils.configuration;

/**
 * Property provider is responsible for one type of property storage.
 * Eg. some specific file.
 * @author ik
 */
public abstract class PropertyProvider implements Comparable<PropertyProvider> {

    /**
     * Priority of this provider. It will be asked for property value before all
     * providers with lower priority.
     * @return
     */
    abstract public int getPriority();

    /**
     * Searches for given property key.
     * @param key
     * @return null if the property wasn't found
     */
    abstract public String getProperty(String key);

    @Override
    public int compareTo(PropertyProvider o) {
        return o.getPriority() - getPriority();
    }
}

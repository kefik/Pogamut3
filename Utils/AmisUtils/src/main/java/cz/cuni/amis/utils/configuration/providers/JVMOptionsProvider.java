
package cz.cuni.amis.utils.configuration.providers;

import cz.cuni.amis.utils.configuration.PropertyProvider;

/**
 * Reads property from -D options passed to the JVM.
 * @author ik
 */
public class JVMOptionsProvider extends PropertyProvider {

    @Override
    public int getPriority() {
        return 9000;
    }

    @Override
    public String getProperty(String key) {
        return System.getProperty(key);
    }

    @Override
    public String toString() {
        return "JVM -D option provider";
    }


}

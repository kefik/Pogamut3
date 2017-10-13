/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.utils.configuration.providers;

import cz.cuni.amis.utils.configuration.PropertyProvider;

/**
 * Read property from system variable.
 * @author ik
 */
public class EnvironmentVariableProvider extends PropertyProvider {

    @Override
    public int getPriority() {
        return 10000;
    }

    @Override
    public String getProperty(String key) {
        return System.getenv(key);
    }

    @Override
    public String toString() {
        return "Environment variable provider";
    }
}

package cz.cuni.amis.pogamut.ut2004.utils;

import cz.cuni.amis.utils.configuration.providers.AbstractPropertiesProvider;

/**
 * UT 2004 specific props.
 * @author ik
 */
public class UT2004PropertiesProvider extends AbstractPropertiesProvider {

    static final String resource = "/cz/cuni/amis/pogamut/ut2004/PogamutUT2004.properties";

    public UT2004PropertiesProvider() {
        super(UT2004PropertiesProvider.class.getResourceAsStream(resource), resource);
    }

    @Override
    public int getPriority() {
        return 500;
    }
}

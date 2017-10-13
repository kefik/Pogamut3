package cz.cuni.amis.pogamut.base.utils.configuration;

import java.io.IOException;

import cz.cuni.amis.utils.configuration.providers.AbstractPropertiesProvider;

/**
 * Default Pogamut platform properties loader.
 * @author ik
 */
public class PlatformPropertiesProvider extends AbstractPropertiesProvider {

    static final String resource = "/cz/cuni/amis/pogamut/PogamutPlatform.properties";

    public PlatformPropertiesProvider() throws IOException {
        super(PlatformPropertiesProvider.class.getResourceAsStream(resource), resource);
    }

    @Override
    public int getPriority() {
        return 1;
    }
}

package cz.cuni.amis.pogamut.ut2004.tournament.utils;

import cz.cuni.amis.utils.configuration.providers.AbstractPropertiesProvider;

/**
 * UT 2004 Tournament specific props.
 * @author ik
 */
public class UT2004TournamentPropertiesProvider extends AbstractPropertiesProvider {

    static final String resource = "/cz/cuni/amis/pogamut/ut2004/tournament/PogamutUT2004Tournament.properties";

    public UT2004TournamentPropertiesProvider() {
        super(UT2004TournamentPropertiesProvider.class.getResourceAsStream(resource), resource);
    }

    @Override
    public int getPriority() {
        return 600;
    }
}

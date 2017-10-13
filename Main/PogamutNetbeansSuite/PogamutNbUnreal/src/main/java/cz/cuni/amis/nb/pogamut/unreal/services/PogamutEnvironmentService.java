package cz.cuni.amis.nb.pogamut.unreal.services;

import java.util.HashMap;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;

/**
 * This is a service for all environments and their selections in the Pogamut.
 *
 * TODO: Maybe use Adapter pattern and encapsulate Server and timeline to one interface
 * @author Honza
 */
@ServiceProvider(service=IPogamutEnvironments.class)
public final class PogamutEnvironmentService implements IPogamutEnvironments {
    private HashMap<Object, EnvironmentSelection> environments;

    public PogamutEnvironmentService() {
        environments = new HashMap<Object, EnvironmentSelection>();
    }

    @Override
    public synchronized EnvironmentSelection getEnvironmentSelection(Object environment) {
        if (! environments.containsKey(environment)) {
            this.addEnvironment(environment);
        }
        return environments.get(environment);
    }

    @Override
    public synchronized boolean addEnvironment(Object environment) {
        if (environments.containsKey(environment)) {
            return false;
        }

        environments.put(environment, new EnvironmentSelection(new InstanceContent()));
        return true;
    }
}

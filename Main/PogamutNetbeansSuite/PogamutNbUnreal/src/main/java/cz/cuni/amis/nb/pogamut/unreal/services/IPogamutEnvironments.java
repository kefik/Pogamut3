
package cz.cuni.amis.nb.pogamut.unreal.services;

/**
 * Service for storing info about available environments in pogamut (timelines,
 * servers ect.) and selections of entities in them.
 * @author Honza
 */
public interface IPogamutEnvironments {
    /**
     * Notify the store about new server.
     * @param server server we want to add to the store
     * @return true if store wasn't aware of server, false if server already knew about it.
     */
//    public boolean addServer(IUT2004Server server);

    /**
     * Notify the store about new timeline.
     * @param db timeline we want to add to the store
     * @return true if store wasn't aware of timeline, false if server already knew about it.
     */
//    public boolean addTimeline(TLDatabase db);


    /**
     * Get set of all servers store is aware of.
     * @return Unmodifiable set,
     * TODO: Implement unmodifiable wrapper ObservableSet that is transparent for listeners
     */
//    public ObservableSet<IUT2004Server> getServers();

    /**
     * Get set of all timelines store is aware of.
     * @return Unmodifiable set
     */
//    public ObservableSet<TLDatabase> getTimelines();

    /**
     * Get selection object for some server.
     * <p>
     * If server wasn't known in the store, add it and create new selection object for it.
     * @param server Server we want selection  for
     * @return selection for passed server.
     */
//    public AgentSelection<IUTAgent> getServerSelection(IUT2004Server server);

    /**
     * Get selection object for some timeline.
     * <p>
     * If timeline wasn't known in the store, add it and create new selection object for it.
     * @param db Timeline we want selection  for
     * @return selection object for passed timeline.
     */
//    public AgentSelection<TLEntity> getTimelineSelection(TLDatabase db);


    /**
     * Add environment to register of environments and initialize its selection
     * @param environment
     */
    public boolean addEnvironment(Object environment);

    /**
     * Get selection object for some environment.
     * <p>
     * If environment wasn't known in the register, add it and create new selection object for it.
     * @param environment Environment we want selection  for, e.g. UT2004Map
     * @return selection object for passed timeline.
     */
    public EnvironmentSelection getEnvironmentSelection(Object environment);

}

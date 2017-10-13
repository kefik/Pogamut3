package cz.cuni.amis.nb.pogamut.unreal.timeline.records;

import cz.cuni.amis.introspection.Folder;
import cz.cuni.amis.introspection.IntrospectionException;
import cz.cuni.amis.introspection.Property;
import cz.cuni.amis.pogamut.base.agent.IAgent;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.Rotation;
import cz.cuni.amis.pogamut.base3d.worldview.object.Velocity;
import cz.cuni.amis.pogamut.unreal.bot.IUnrealBot;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Logger;
import org.openide.util.Exceptions;

/**
 * TLEntity with infrastructure to gather info from some agent
 * @author Honza
 */
public class TLAgentEntity extends TLEntity {

    private String agentName;
    private transient IAgent agent;
    /**
     * Location of agent during recording.
     */
    protected TLLocation botLocation;
    /**
     * Rotation of agent during recording.
     */
    protected TLRotation botRotation;
    /**
     * Velocity of agent during recording.
     */
    protected TLVelocity botVelocity;
    /**
     * Updater to pass info from introspection to {@link TLProperty properties}.
     */
    protected transient TLEntityUpdater updateWorker;

    /**
     * Create a new {@link TLEntity} that takes its data from the
     * {@link IUnrealBot unreal agent}.
     * 
     * @param db
     * @param agent
     */
    public TLAgentEntity(TLDatabase db, final IUnrealBot agent) {
        super(db, Calendar.getInstance().getTimeInMillis());

        Logger.getLogger("TL").info("New TLAgentEntity " + agent.getName());

        this.agent = agent;
        this.agentName = getAgent().getName();

        this.botLocation = new TLLocation(agent);
        this.botRotation = new TLRotation(agent);
        this.botVelocity = new TLVelocity(agent);

        Map<String, LogCategory> categories = agent.getLogger().getCategories();

        // this won't emit a thing, because nothing is listening (we are
        // still in the constructor)
        String[] names = categories.keySet().toArray(new String[0]);
        Arrays.sort(names);
        for (String categoryName : names) {
            this.addLogRecording(categories.get(categoryName));
        }

        // make root folder with proper name, the synchronization of content
        // is done in TLEntityUpdater
        storageFolder = new TLFolder(agent.getIntrospection().getName());

        // Create updater of entity from live agent to our data collection
        updateWorker = new TLEntityUpdater(this, agent);
        updateWorker.execute();
    }

    public IAgent getAgent() {
        return agent;
    }

    @Override
    public String getDisplayName() {
        return agentName;
    }

    /**
     * Return location of bot.
     * @return null if folder not found or data not available, location otherwise
     */
    @Override
    public Location getLocation(long time) {
        Location location = botLocation.getValue(time);
        return location == null ? null : new Location(location);
    }

    /**
     * Return location of bot.
     * @return null if folder not found or data not available, location otherwise
     */
    @Override
    public Rotation getRotation(long time) {
        return new Rotation(botRotation.getValue(time));
    }

    /**
     * Return location of bot.
     * @return null if folder not found or data not available, location otherwise
     */
    @Override
    public Velocity getVelocity(long time) {
        return new Velocity(botVelocity.getValue(time));
    }

    @Override
    public void finish() {
        updateWorker.cancel(false);
        super.finish();
    }

    // Debug methods
    public void printFolders(PrintStream stream) {
        try {
            printFolders(agent.getIntrospection(), stream);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void printTimelinedata() throws IntrospectionException {
        Folder root = getAgent().getIntrospection();

        Folder tl = root.getFolder("timelineData");
        if (tl == null) {
            System.out.println("Not timelinedata folder");
        }

        System.out.println("List properties " + tl.getProperties().length);
        for (Property prop : tl.getProperties()) {
            System.out.println(prop.getName() + " " + prop.getType() + " " + prop.getValue());
        }
    }

    /**
     * Test funstion so I can see what is provided by JMX
     * @param folder
     * @throws cz.cuni.amis.introspection.IntrospectionException
     */
    private void printFolders(Folder folder, PrintStream stream) throws IntrospectionException {
        stream.println("Folder " + folder.getName());
        for (Property prop : folder.getProperties()) {
            stream.println("Property " + prop.getName() + " type " + prop.getType());
        }
        for (Folder subfolder : folder.getFolders()) {
            stream.println("Subfolder " + subfolder.getName());
            printFolders(subfolder, stream);
        }
        stream.println("Folder " + folder.getName() + " end");
    }

    private void printTLFolders(TLFolder folder) {
        System.out.println("TLFolder " + folder.getName());
        for (TLProperty prop : folder.getProperties()) {
            System.out.println("TLProperty " + prop.getName() + " type " + prop.getType());
        }
        for (TLFolder subfolder : folder.getSubfolders()) {
            System.out.println("TLSubfolder " + subfolder.getName());
            printTLFolders(subfolder);
        }
        System.out.println("TLFolder " + folder.getName() + " end");
    }

    /**
     * Debug method for printing variables from folder, recursive.
     * @param folder
     */
    private void printVariables(TLFolder folder) {
        System.out.println("Write info from variables of folder " + folder.getName());

        for (TLProperty property : folder.getProperties()) {
            property.printDebug();
        }

        for (TLFolder subfolder : folder.getSubfolders()) {
            System.out.println("List subfolder " + subfolder.getName());
            printVariables(subfolder);
        }
        System.out.println("EOL for folder " + folder.getName());
    }
}

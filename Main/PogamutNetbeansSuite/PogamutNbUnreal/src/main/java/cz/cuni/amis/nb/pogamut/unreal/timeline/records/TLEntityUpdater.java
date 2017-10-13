package cz.cuni.amis.nb.pogamut.unreal.timeline.records;

import cz.cuni.amis.introspection.Folder;
import cz.cuni.amis.introspection.IntrospectionException;
import cz.cuni.amis.introspection.Property;
import cz.cuni.amis.pogamut.base.agent.state.level1.IAgentStateUp;
import cz.cuni.amis.pogamut.unreal.bot.IUnrealBot;
import java.util.Calendar;
import java.util.List;
import javax.swing.SwingWorker;

/**
 * This class is a Swing Thread Safe worker that retrieves info from introspection,
 * puts it into respective {@link TLProperty properties} (in worker thread) and
 * updates end time (AWT).
 * <p/><p/>
 * The retrieval of introspection will continue as long as agent is in state
 * {@link IAgentStateUp}.
 * 
 * @author Honza Havlicek
 */
public class TLEntityUpdater extends SwingWorker<Boolean, Long> {
    protected TLAgentEntity entity;
    protected IUnrealBot agent;

    /**
     * How many ms should pass between consequent retrival from introspection.
     * In ms, default 100 ms.
     */
    protected int interval = 100;

    public TLEntityUpdater(TLAgentEntity entity, IUnrealBot agent) {
        this.entity = entity;
        this.agent = agent;
    }

    // in worker thread
    @Override
    protected Boolean doInBackground() throws Exception {
        while (agent.getState().getFlag().isState(IAgentStateUp.class) && !isCancelled()) {
            long timestamp = Calendar.getInstance().getTimeInMillis();

            entity.botLocation.update(timestamp);
            entity.botRotation.update(timestamp);
            entity.botVelocity.update(timestamp);

            syncFolder(agent.getIntrospection(), entity.storageFolder, timestamp);

            publish(timestamp);
            Thread.sleep(interval);
        }
        return true;
    }

    /**
     * When everything is done, notify
     */
    @Override
    protected void done() {
        // in AWT
        entity.finish();
    }

    // in AWT
    @Override
    protected void process(List<Long> endTimestamps) {
        long endTime = endTimestamps.get(endTimestamps.size()-1);
        entity.setEndTime(endTime);
    }


    /**
     * Take the folder recursively traverse directory structure.
     * For each subfolder:
     * <ul>
     *   <li>sync properties - add previously missing to {@link TLFolder}</li>
     *   <li>synch values of properties - put current value of {@link Property introspection properties} to {@link TLProperty TLProperties}</li>
     *   <li>synch subfolders, if some subfolder is missing, create it.</li>
     * <ul>
     * @param folder
     * @param tlFolder
     * @param time
     */
    protected void syncFolder(Folder folder, TLFolder tlFolder, long time) throws IntrospectionException {
        syncProperties(folder, tlFolder, time);

        for (Folder subFolder : folder.getFolders()) {
            String folderName = subFolder.getName();
            TLFolder tlSubFolder = tlFolder.findFolder(folderName);

            // if folder didn't exist, create it
            if (tlSubFolder == null) {
                tlSubFolder = new TLFolder(folderName);
                tlFolder.addFolder(tlSubFolder);
            }
            syncFolder(subFolder, tlSubFolder, time);
        }
    }

    /**
     * Synchronize properties in folder.
     * <p/>
     * Only properties and their values, NOT subfolders.
     */
    private void syncProperties(Folder folder, TLFolder tlFolder, long time) throws IntrospectionException {
        for (Property property : folder.getProperties()) {
            String propertyName = property.getName();
            Class propertyType = property.getType();
            Object propertyValue = property.getValue();

            // add previously non-existent properties to tlFolder
            TLProperty tlProperty = tlFolder.findProperty(propertyName);
            if (tlProperty == null) {
                tlProperty = new TLProperty(propertyName, propertyType);
                tlFolder.addProperty(tlProperty);
            }
            // synch values of properties
            tlProperty.addValue(propertyValue, time);
        }
    }

}

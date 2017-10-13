package cz.cuni.amis.nb.pogamut.unreal.timeline.records;

import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLEntity.State;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.IUnrealMap;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.IUnrealMapInfo;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.MapInfo;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Structure containing all information about whole experiment and 
 * all agents inside.
 * 
 * @author Honza
 */
public class TLDatabase implements Serializable {

    /**
     * All entities that are in db
     */
    private LinkedList<TLEntity> agentsRecords = new LinkedList<TLEntity>();
    /**
     * When did db start recording
     */
    private Long startTime;
    /**
     * What time is "current" time, which we are showing. On this depends variables
     * or state of the map.
     * Is in range between startMilis and endMilis.
     */
    private Long currentTime;
    /**
     * timestamp marking end of any data recieved. During generation it changes
     */
    private Long endTime;
    private transient ColorManager colorManager = null;
    private IUnrealMap map;
    /**
     * List of listeners that are getting messages from the db.
     */
    private transient HashSet<TLDatabaseListener> listeners = new HashSet<TLDatabaseListener>();

    /**
     * Adapter for {@link TLDatabaseListener}. All bodies of methods are empty.
     */
    public static class Adapter implements TLDatabaseListener {

        @Override
        public void startTimeChanged(long previousStartTime, long startTime) {
        }

        @Override
        public void currentTimeChanged(long previousCurrentTime, long currentTime) {
        }

        @Override
        public void endTimeChanged(long previousEndTime, long endTime) {
        }

        @Override
        public void onEntityEntered(TLDatabase db, TLEntity entity) {
        }

        @Override
        public void onEntityLeft(TLDatabase db, TLEntity entity) {
        }
    }

    public interface TLDatabaseListener {

        void startTimeChanged(long previousStartTime, long startTime);

        void currentTimeChanged(long previousCurrentTime, long currentTime);

        void endTimeChanged(long previousEndTime, long endTime);

        /**
         * Called when new entity (Agent in most cases) is added to databse.
         * @param db
         * @param entity
         */
        void onEntityEntered(TLDatabase db, TLEntity entity);

        void onEntityLeft(TLDatabase db, TLEntity entity);
    }

    public long getStartTime() {
        assert startTime != null;
        return startTime;
    }

    public long getCurrentTime() {
        assert currentTime != null;
        return currentTime;
    }

    public long getEndTime() {
        assert endTime != null;
        return endTime;
    }

    /**
     * Get time interval between start of recording and end of recording.
     * If recording hasn't started, return 0
     * @return time interval represented int db, in ms
     */
    public long getElapsedTime() {
        if (endTime == null || startTime == null) {
            return 0;
            
        }
        return endTime - startTime;
    }

    /**
     * Get time interval between start of recording and current time
     * If recording hasn't started, return 0
     * @return time interval in ms
     */
    public long getDeltaTime() {
        if (currentTime == null || startTime == null) {
            return 0;
            
        }
        return currentTime - startTime;
    }

    /**
     * When entity enters, add it to list of watched entities and notify
     * all listeners that we have new entity in database.
     *
     * If db is not in recording state, throw new RuntimeException
     * @param entity
     * @return
     */
    public TLEntity entityEntered(TLEntity entity) {
        this.setEndTime(Calendar.getInstance().getTimeInMillis());


        Logger.getLogger("TL").info(MessageFormat.format("Entity {0} has entered the map.", entity.toString()));

        if (agentsRecords.add(entity)) {
            entity.setColor(getColorManager().getNewColor());
            //updateStartMilis(entity.getStartTimestamp());
            //this.agentsRecords.add(entity); WTF? Double insert is soo idiotic. Why did I do that.
            entity.addListener(new TLEntity.Adapter() {

                @Override
                public void endTimeChanged(TLEntity entity, long previousEndTime, long endTime) {
                    setEndTime(endTime);
                }
            });

            emitEntityEntered(entity);

            return agentsRecords.getLast();
        }
        return null;
    }

    private void emitEntityEntered(TLEntity entity) {
        for (TLDatabaseListener listener : getListeners()) {
            listener.onEntityEntered(this, entity);
        }
    }

    protected void emitEntityLeft(TLEntity entity) {
        for (TLDatabaseListener listener : getListeners()) {
            listener.onEntityLeft(this, entity);
        }
    }

    private TLDatabaseListener[] getListeners() {
        if (listeners == null) {
            listeners = new HashSet<TLDatabaseListener>();
        }

        return listeners.toArray(new TLDatabaseListener[listeners.size()]);
    }

    public void addDBListener(TLDatabaseListener listener) {
        if (listeners == null) {
            listeners = new HashSet<TLDatabaseListener>();
        }
        listeners.add(listener);
    }

    public void removeDBListener(TLDatabaseListener listener) {
        if (listeners == null) {
            listeners = new HashSet<TLDatabaseListener>();
        }
        listeners.remove(listener);
    }

    public List<TLEntity> getEntities() {
        return Collections.unmodifiableList(this.agentsRecords);
    }

    private void setEndTime(long newEnd) {
        assert newEnd >= startTime;

        if (newEnd > endTime) {
            long previousEndTime = endTime;
            endTime = newEnd;
            for (TLDatabaseListener listener : getListeners()) {
                listener.endTimeChanged(previousEndTime, endTime);
            }
        }
    }

    /**
     * Start recording info from the environment.
     * This function only sets proper start, current and end time
     */
    public void startRecording() {
        long now = Calendar.getInstance().getTimeInMillis();

        startTime = now;
        currentTime = now;
        endTime = now;
    }

    /**
     * Stop recording.
     * Update endTime and stop recording all entities.
     */
    public void stopRecording() {
        long now = Calendar.getInstance().getTimeInMillis();

        this.endTime = now;

        for (TLEntity entity : getEntities()) {
            entity.finish();
        }
    }

    /**
     * Change current time and notify all listeners
     * @param currentTime
     */
    public void setCurrentTime(long currentTime) {
        assert startTime <= currentTime && endTime >= currentTime;

        long previousCurrentTime = this.currentTime;
        this.currentTime = currentTime;

        for (TLDatabaseListener listener : getListeners()) {
            listener.currentTimeChanged(previousCurrentTime, currentTime);
        }
    }

    /**
     * Get entities that are/were present at the specified time.
     * Use start and last timestamps of entities to determine which ones.
     * @param newCurrent
     */
    public Set<TLEntity> getEntities(long time) {
        HashSet<TLEntity> entitiesPresent = new HashSet<TLEntity>();

        for (TLEntity entity : agentsRecords) {
            if (entity.getStartTime() <= time) {
                if (entity.getEndTime() >= time) {
                    entitiesPresent.add(entity);
                } else if (entity.getState() == State.RECORDING) {
                    entitiesPresent.add(entity);
                }
            }
        }

        return Collections.unmodifiableSet(entitiesPresent);
    }

    private ColorManager getColorManager() {
        if (colorManager == null) {
            colorManager = new ColorManager();
        }
        return colorManager;
    }

    private Date milisToDate(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        return c.getTime();
    }

    /**
     * Print info about database and all entities into specified stream.
     * @param stream stream where to print info.
     */
    public void printInfo(PrintStream stream) {
        stream.println("Info about db (entities: " + getEntities().size() + " ) [" + milisToDate(getStartTime()) + ", " + milisToDate(getEndTime()) + "]");
        for (TLEntity entity : getEntities()) {
            entity.printInfo(stream);
        }
    }

    public IUnrealMap getMap() {
        return map;
    }

    public void setMap(IUnrealMap map) {
        this.map = map;
        try {
            updateMap(map);
            // Now
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Unable to add info to the map: " + ex.getMessage());
        }
    }

    private void updateMap(IUnrealMap mapToUpdate) throws ParserConfigurationException, SAXException, IOException {
        String pogamutDirString = System.getProperty("user.home") + "/" + ".pogamut";
        File pogamutDir = new File(pogamutDirString);
        if (!pogamutDir.exists()) {
            pogamutDir.mkdir();
        }

        File mapFile = new File(pogamutDirString + "/" + "maps.xml");
        if (!mapFile.exists()) {
            return;
        }

        if (!mapFile.isFile()) {
            return;
        }


        File file = mapFile;//new File("C:/temp/maps.xml");


        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(file);

        NodeList maps = doc.getElementsByTagName("map");

        for (int i = 0; i < maps.getLength(); i++) {
            Node map = maps.item(i);
            IUnrealMapInfo info = readMap(file, (Element) map.getChildNodes());
            if (mapToUpdate.getName().equals(info.getName())) {
                mapToUpdate.addInfo(info);
                return;
            }
        }
    }

    private IUnrealMapInfo readMap(File file, Element element) throws SAXException, IOException {
        IUnrealMapInfo info = new MapInfo();

        NodeList mapname = element.getElementsByTagName("mapname");
        if (mapname.getLength() != 1) {
            throw new SAXException("Not one mapname.");
        }
        String mapNameText = mapname.item(0).getTextContent();
        info.setName(mapNameText);
//        System.out.println("name " + mapNameText);

        NodeList imageList = element.getElementsByTagName("image");
        if (imageList.getLength() != 1) {
            throw new SAXException("Not one image.");
        }
        String imageUrl = imageList.item(0).getTextContent();
//        System.out.println("url " + imageUrl);

        // get directory


        info.setImage(file.getParent() + "/" + imageUrl);

        NodeList imagePosTag = element.getElementsByTagName("imagePos");

        if (imagePosTag.getLength() != 1) {
            throw new SAXException("Not one imagePos tag.");
        }

        NodeList imagePoints = ((Element) imagePosTag.item(0)).getElementsByTagName("point");
        if (imagePoints.getLength() != 3) {
            throw new SAXException("Not three points in imagePos tag.");
        }

        for (int i = 0; i < imagePoints.getLength(); i++) {
            Location l = readPoint((Element) imagePoints.item(i));
//            System.out.println(" IMG " + l);
            info.setImagePoint(i, l);
        }


        NodeList worldPosTag = element.getElementsByTagName("worldPos");
        if (worldPosTag.getLength() != 1) {
            throw new SAXException("Not three points in imagePos tag.");
        }

        NodeList worldPoints = ((Element) worldPosTag.item(0)).getElementsByTagName("point");
        if (worldPoints.getLength() != 3) {
            throw new SAXException("Not three points in worldPos");
        }
        for (int i = 0; i < worldPoints.getLength(); i++) {
            Location l = readPoint((Element) worldPoints.item(i));
            info.setWorldPos(i, l);
        }

        return info;
    }

    private Location readPoint(Element item) {
        String x = item.getAttribute("x");
        String y = item.getAttribute("y");
        String z = item.getAttribute("z");

        return new Location(Double.valueOf(x), Double.valueOf(y), Double.valueOf(z));
    }
}

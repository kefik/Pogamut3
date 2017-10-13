package cz.cuni.amis.nb.pogamut.unreal.timeline.dataobject;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLDatabase;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.IUnrealMap;
import cz.cuni.amis.pogamut.unreal.server.IUnrealServer;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

public class TLDataObject extends MultiDataObject {

    private TLDatabase database;
    private IUnrealServer server;
    //private String mapName = "";
    private SaveDatabase saveDB = new SaveDatabase();

    // When db changes, update save dialog
    private final TLDatabase.Adapter changeListener = new TLDatabase.Adapter() {
        @Override
        public void currentTimeChanged(long previousCurrentTime, long currentTime) {
                getCookieSet().assign(SaveCookie.class, saveDB);
                setModified(true);
        }

        @Override
        public void endTimeChanged(long previousEndTime, long endTime) {
                getCookieSet().assign(SaveCookie.class, saveDB);
                setModified(true);
        }
    };

    public TLDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
                getCookieSet().assign(SaveCookie.class, saveDB);
                setModified(true);

        if (pf.getSize() > 0) {
            database = getDBFromFile(pf);
        } else {
            database = new TLDatabase();
        }

        CookieSet cookies = getCookieSet();
        cookies.assign(TLDatabase.class, database);
        cookies.add(new TLOpenSupport(this.getPrimaryEntry()));


        cookies.assign(Node.class, this.getNodeDelegate());

        // Set modified if current time of db changes
        database.addDBListener(changeListener);
    }

    public IUnrealServer getSourceServer() {
        return server;
    }

    /**
     * Used for empty/newly created TLDataObject.
     * Does more or less nothing, until timeline recording is started. When it is, 
     * passed server is used for getting a map of level, list of agents and all that other stuff.
     * 
     * Also, it allows TLDataNode to keep track of possible states, when it can 
     * and can't record.
     *
     * @param server
     */
    public void setSourceServer(IUnrealServer server) {
        this.server = server;

        String mapName = server.getMapName();
        System.out.println("SERVERMAPNAME: " + mapName);
        
        IUnrealMap map = this.server.getMap();

        map.setName(mapName);
        System.out.println("GETMAPNAME: " + map.getName());
        //this.server.getMap().getFlag().setName(server.getMapFlag().getFlag());
        this.database.setMap(map);

        /*
         * TODO: Tohle nebude dobre fungovat, chci aby se menil i nazev v node,
         * ale to neni jasne predtim, nez se zacne nahravat... divne.
         */
   /*     server.getAgentState().addListener(new FlagListener<AgentState>() {

            @Override
            public void flagChanged(AgentState changedValue) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });*/
        /*
        this.mapName = server.getMapFlag().getFlag();
        server.getMapFlag().addListener(new FlagListener<String>() {

            @Override
            public void flagChanged(String changedValue) {
                mapName = changedValue;
            }
        });*/
    }

    @Override
    protected Node createNodeDelegate() {
        return new DataNode(this, Children.LEAF, getLookup());
    }

    @Override
    public Lookup getLookup() {
        return getCookieSet().getLookup();
    }

    /**
     *
     * @return Database holding info about the entities and other stuff.
     */
    public TLDatabase getDatabase() {
        return database;
    }

    /**
     *
     * @return Name of map this timeline was recording.
     */
    public String getMapName() {
        return database.getMap().getName();
    }

    private TLDatabase getDBFromFile(FileObject pf) throws FileNotFoundException, IOException {
        FileInputStream in = null;
        try {
            in = new FileInputStream(FileUtil.toFile(pf));
            XStream xstream = new XStream(new DomDriver());
            TLDatabase db = (TLDatabase) xstream.fromXML(in);

            db.printInfo(System.out);

            return db;
        } finally {
            if (in != null)
                in.close();
        }
    }

    private class SaveDatabase implements SaveCookie {

        @Override
        public void save() throws IOException {
            XStream xstream = new XStream();

            FileLock lock = getPrimaryFile().lock();
            OutputStream stream = null;
            try {
                stream = getPrimaryFile().getOutputStream(lock);
                xstream.toXML(getDatabase(), stream);
            } finally {
                if (stream != null)
                    stream.close();
                lock.releaseLock();
            }
            /*
            ObjectOutputStream out = new ObjectOutputStream(getPrimaryFile().getOutputStream());

            out.writeObject(getDatabase());

            out.close();
*/
            // We are don, database is saved
            getCookieSet().assign(SaveCookie.class);
            setModified(false);
        }
    }

}

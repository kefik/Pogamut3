package cz.cuni.amis.nb.api.pogamut.base.server;

import cz.cuni.amis.utils.collections.ObservableCollection;
import cz.cuni.amis.utils.collections.ObservableList;
import cz.cuni.amis.utils.collections.SimpleListener;
import cz.cuni.amis.utils.flag.Flag;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.Repository;
import org.openide.util.Exceptions;

/**
 *
 * @author ik
 */
public abstract class DefaultServersManager<T extends ServerDefinition> implements ServersManager<T> {

    String serverType = null;
    protected String serversListFileName;

    public DefaultServersManager(String serverType) {
        this.serverType = serverType;
        serversListFileName = serverType + "ServersList.bin";
        deserialize();
        servers.addCollectionListener(new SimpleListener() {

            @Override
            protected void changed(Collection collection, Collection added, Collection removed) {
                serialize();
            }
        });
    }
    protected ObservableList<T> servers = new ObservableList<T>(new ArrayList<T>());
    Flag<T> defaultServer = new Flag<T>();

    @Override
    public Flag<T> getDefaultServer() {
        return defaultServer;
    }

    @Override
    public ObservableCollection<T> getAllServers() {
        return servers;
    }

    /**
     * Saves the server definition in a binary file.
     */
    public abstract void deserialize();

    @Override
    public void serialize() {
        try {
            FileObject writeTo = getServerListFile();
            FileLock lock = writeTo.lock();
            try {
                ObjectOutputStream str = new ObjectOutputStream(writeTo.getOutputStream(lock));
                try {
                    str.writeObject(servers.getList());
                } finally {
                    str.close();
                }
            } finally {
                lock.releaseLock();
            }

        } catch (IOException ioe) {
            Exceptions.printStackTrace(ioe);
        }
    }

    protected FileObject getServerListFile() throws IOException {
        FileObject root = Repository.getDefault().getDefaultFileSystem().getRoot();
        FileObject folder = root.getFileObject(serverType);
        if (folder == null) {
            folder = root.createFolder(serverType);
        }

        FileObject serversFile = folder.getFileObject(serversListFileName);
        if (serversFile == null) {
            serversFile = folder.createData(serversListFileName);
        }

        return serversFile;
    }

    @Override
    public String getServerType() {
        return serverType;
    }

    @Override
    public void removeServer(T server) {
        server.stopServer();
        servers.remove(server);
    }
}

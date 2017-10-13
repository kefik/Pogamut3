package cz.cuni.amis.nb.pogamut.unreal.map;

import cz.cuni.amis.nb.api.pogamut.base.server.ServerDefinition;
import cz.cuni.amis.pogamut.unreal.server.IUnrealServer;
import cz.cuni.amis.utils.flag.FlagListener;
import java.awt.Label;
import java.net.URI;
import javax.swing.SwingUtilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;

/**
 * This is a overview component that shows "live" map from server.
 * It tries to utilize a lot of things common with TimelineMap
 * @author Honza
 */
public class PureMapTopComponent extends TopComponent implements FlagListener<IUnrealServer> {

    protected ServerDefinition<IUnrealServer> serverDef;
    protected PureMapGLPanel mapPanel;
    protected InstanceContent lookupContent;

    public PureMapTopComponent(ServerDefinition<IUnrealServer> serverDef) {
        this.serverDef = serverDef;

        setUpMapPanel();

        // Associate our lookup with this TC
        lookupContent = new InstanceContent();
        associateLookup(new AbstractLookup(lookupContent));

        // set proper name
        handleServerName();

        serverDef.getServerFlag().addListener(this);
    }

    protected void handleServerName() {
        setPanelName();
        serverDef.getServerNameFlag().addListener(new FlagListener<String>() {

            @Override
            public void flagChanged(String changedValue) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        setPanelName();
                    }
                });
            }
        });

    }

    protected void setPanelName() {
        String map = "no map";
        if (serverDef.getServerFlag().getFlag() != null) {
            map = serverDef.getServerFlag().getFlag().getMapName();
        }
        setDisplayName(serverDef.getServerName() + " [" + map + "]");
    }

    protected void setUpMapPanel() {
        // Create map panel
        IUnrealServer server = serverDef.getServerFlag().getFlag();
        mapPanel = new PureMapGLPanel(server.getMap(), server);
        if (isOpened()) {
            mapPanel.startDisplayLoop();
        }
        // add map panel to the TC
        setLayout(new java.awt.BorderLayout());
        add(mapPanel, java.awt.BorderLayout.CENTER);
    }

    /**
     * When server changes, get message
     * @param changedValue
     */
    @Override
    public void flagChanged(IUnrealServer server) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                removeAll();
                handleServerName();
                if (serverDef.getServerFlag().getFlag() == null) {
                    add(new Label("Server not available ...", Label.CENTER), java.awt.BorderLayout.CENTER);
                } else {
                    setUpMapPanel();
                }
                revalidate();
                repaint();
            }
        }); /*
        String msg = "Map changed to " + changedValue.getName() + " " + changedValue.getName();
        JOptionPane.showMessageDialog(this, msg);
         */

    }

    @Override
    protected String preferredID() {
        URI uri = serverDef.getUri();
        return "PureMapTC_" + uri.getHost() + ":" + uri.getPort();
    }

    /**
     * This component is not persistent
     * @return
     */
    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    protected void componentOpened() {
        mapPanel.startDisplayLoop();
        super.componentOpened();
    }

    @Override
    protected void componentClosed() {
        mapPanel.stopDisplayLoop();
        super.componentClosed(); 
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.nb.pogamut.unreal.map;

import cz.cuni.amis.nb.pogamut.unreal.services.EnvironmentSelection;
import cz.cuni.amis.nb.pogamut.unreal.services.IPogamutEnvironments;
import cz.cuni.amis.nb.pogamut.unreal.timeline.map.IRenderableUTAgent;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.IUnrealMap;
import cz.cuni.amis.pogamut.unreal.communication.worldview.map.MapInfo;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Set;
import java.util.logging.Logger;
import javax.media.opengl.GLCapabilities;
import org.openide.util.Lookup;

/**
 * This is MapGLPanel that is adding selection behavior, when bot is
 * clicked in the map, list of selected bots in this map
 * IPogamutEnvironments global lookup will change.
 *
 * @author Honza
 */
abstract public class SelectableMapGLPanel extends MapGLPanel implements MouseListener {

    public SelectableMapGLPanel(GLCapabilities caps, IUnrealMap<MapInfo> map, Logger log) {
        super(caps, map, log);
        this.addMouseListener(this);
    }

    public SelectableMapGLPanel(IUnrealMap map, Logger log) {
        super(map, log);
        this.addMouseListener(this);
    }

    @Override
    public void destroy() {
        this.removeMouseListener(this);
        super.destroy();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // we only want to select/deselect on left click, not middle click used during drag or something like that
        if (e.getButton() != MouseEvent.BUTTON1) {
            return;
        }
        // get global selection object for this map
        IPogamutEnvironments environments = Lookup.getDefault().lookup(IPogamutEnvironments.class);
        if (environments == null) {
            return;
        }
        IUnrealMap map = mapRenderer.getObject();

        EnvironmentSelection mapSelection = environments.getEnvironmentSelection(map);

        // Get list of selected bots
        Set<IRenderableUTAgent> clickedBots = this.getAgentsAt(e.getPoint());

        mapSelection.clearSelection();
        
        for (IRenderableUTAgent selectedAgent : clickedBots) {
            mapSelection.addSelected(selectedAgent.getDataSource());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}

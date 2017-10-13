package cz.cuni.amis.nb.pogamut.unreal.timeline.view;

import cz.cuni.amis.nb.pogamut.unreal.services.AgentSelection;
import cz.cuni.amis.nb.pogamut.unreal.services.IPogamutEnvironments;
import cz.cuni.amis.nb.pogamut.unreal.timeline.dataobject.TLDataObject;
import cz.cuni.amis.nb.pogamut.unreal.timeline.map.IRenderableUTAgent;
import cz.cuni.amis.nb.pogamut.unreal.timeline.map.TLMapGLPanel;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLAgentEntity;
import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLEntity;
import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.Beans;
import java.util.Collection;
import java.util.Set;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.openide.actions.SaveAction;
import org.openide.awt.UndoRedo;
import org.openide.util.Lookup;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;

/**
 * Representation 
 * 
 * @author Honza
 */
public class MVMapElement implements MultiViewElement {

    private MultiViewElementCallback callback;
    private TLDataObject dataObject;
    private JToolBar toolbar;	// fixme: remove after we add proper timeline
    private JPanel elementPanel;
    private TLMapGLPanel glPanel;
    private JSlider slider;

    private ProxyLookup lookup;
    private InstanceContent lookupContent;

    
    MVMapElement(TLDataObject dataObject) {
        // Hack
        if (Beans.isDesignTime()) {
            Beans.setDesignTime(false);
        }
        this.dataObject = dataObject;

        glPanel = new TLMapGLPanel(dataObject.getDatabase().getMap(), dataObject.getDatabase());

        slider = new TLSlider(dataObject.getDatabase());

        elementPanel = new JPanel(new BorderLayout());
        elementPanel.add(glPanel, BorderLayout.CENTER);
        elementPanel.add(slider, BorderLayout.PAGE_END);

        ActionMap map = new ActionMap();
        map.put("save", SystemAction.get(SaveAction.class));
        elementPanel.setActionMap(map);

        lookupContent = new InstanceContent();
        lookup = new ProxyLookup(dataObject.getLookup(), new AbstractLookup(lookupContent));
    }

    @Override
    public JComponent getVisualRepresentation() {
        return elementPanel;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        if (toolbar == null) {
            toolbar = new MapToolbar(this.dataObject.getDatabase(), glPanel);
        }
        return toolbar;
    }

    /**
     * Array of actions that will appear in context menu of TC caption
     * that encloses this MV element.
     *
     * @return Array of actions, not null
     */
    @Override
    public Action[] getActions() {
        return new Action[]{};
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void componentOpened() {
//        glPanel.addMouseListener(this);
    }

    @Override
    public void componentClosed() {
//        glPanel.removeMouseListener(this);
    }

    @Override
    public void componentShowing() {
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentActivated() {
    }

    @Override
    public void componentDeactivated() {
    }

    @Override
    public UndoRedo getUndoRedo() {
        return UndoRedo.NONE;
    }

    /**
     * Set the callback, called by netbeans internal organs or something.
     * @param callback
     */
    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
        this.callback = callback;
    }

    /**
     * Can I close this element?
     *
     * @return Always can close
     */
    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

/*
    @Override
    public synchronized void mouseClicked(MouseEvent e) {

        Set<IRenderableUTAgent> selectedAgents = glPanel.getAgentsAt(e.getPoint());

        // remove all lookup object
        Collection allInLookup = lookup.lookupAll(Object.class);
        for (Object o : allInLookup) {
            lookupContent.remove(o);
        }

        // Add new ones
        for (IRenderableUTAgent selectedAgent : selectedAgents) {
            lookupContent.add(selectedAgent.getDataSource());
        }

        // Now update selection in the environment
        IPogamutEnvironments environments = Lookup.getDefault().lookup(IPogamutEnvironments.class);
        if (environments == null) {
            return;
        }
        AgentSelection<TLEntity> selection = environments.getTimelineSelection(dataObject.getDatabase());

        TLEntity newlySelected = null;

        for (IRenderableUTAgent selectedAgent : selectedAgents) {
            if (selectedAgent.getDataSource() instanceof TLEntity) {
                newlySelected = (TLEntity) selectedAgent.getDataSource();
            }
        }
        selection.changeSelected(newlySelected);
    }

    @Override
    public void mousePressed(MouseEvent e) {
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
*/
}

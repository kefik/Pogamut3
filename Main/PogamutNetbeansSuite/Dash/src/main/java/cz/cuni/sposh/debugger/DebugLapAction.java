package cz.cuni.sposh.debugger;

import cz.cuni.amis.pogamut.sposh.dbg.view.EngineSelectionComponent;
import cz.cuni.amis.pogamut.sposh.engine.PoshEngine;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.windows.TopComponent;

/**
 * This is a high-level action used in the menu/toolbar. It opens a window
 * where user can see running lap-plans (the ones that pass 
 * {@link PoshEngine#evaluatePlan(cz.cuni.amis.pogamut.sposh.executor.IWorkExecutor) 
 * lap engine evaluation method) and gradually displayes them as a list. User 
 * can select which one he wants to debug and open a debugger for the plan.
 * @author Honza
 */
@ActionID(id = "cz.cuni.sposh.debugger.DebugLapAction", category = "Debug")
@ActionRegistration(displayName = "#CTL_DebugLapAction")
@ActionReference(path = "Menu/RunProject", position = 100)
public class DebugLapAction implements ActionListener {

    /**
     * Create and open {@link EngineSelectionComponent}.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        assert SwingUtilities.isEventDispatchThread();

        TopComponent view = new EngineSelectionComponent();

        view.open();
        view.requestActive();
    }
}

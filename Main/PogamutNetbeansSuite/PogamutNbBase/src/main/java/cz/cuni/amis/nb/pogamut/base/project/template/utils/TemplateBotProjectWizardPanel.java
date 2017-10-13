package cz.cuni.amis.nb.pogamut.base.project.template.utils;

import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Panel just asking for basic info.
 */
public class TemplateBotProjectWizardPanel implements WizardDescriptor.Panel,
        WizardDescriptor.ValidatingPanel, WizardDescriptor.FinishablePanel {
    
    private WizardDescriptor wizardDescriptor;
    private TemplateBotProjectPanelVisual component;
    
    /** Creates a new instance of templateWizardPanel */
    public TemplateBotProjectWizardPanel() {
    }
    
    @Override
    public Component getComponent() {
        if (component == null) {
            component = new TemplateBotProjectPanelVisual(this);
            component.setName(NbBundle.getMessage(TemplateBotProjectWizardPanel.class, "LBL_CreateProjectStep"));
        }
        return component;
    }
    
    @Override
    public HelpCtx getHelp() {
        return new HelpCtx(TemplateBotProjectWizardPanel.class);
    }
    
    @Override
    public boolean isValid() {
        getComponent();
        return component.valid(wizardDescriptor);
    }
    
    private final Set/*<ChangeListener>*/ listeners = new HashSet(1);
    @Override
    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }
    @Override
    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }
    protected final void fireChangeEvent() {
        Iterator it;
        synchronized (listeners) {
            it = new HashSet(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            ((ChangeListener) it.next()).stateChanged(ev);
        }
    }
    
    @Override
    public void readSettings(Object settings) {
        wizardDescriptor = (WizardDescriptor) settings;
        component.read(wizardDescriptor);
    }
    
    @Override
    public void storeSettings(Object settings) {
        WizardDescriptor d = (WizardDescriptor) settings;
        component.store(d);
        
    }
    
    @Override
    public boolean isFinishPanel() {
        return true;
    }
    
    @Override
    public void validate() throws WizardValidationException {
        getComponent();
        component.validate(wizardDescriptor);
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.pogamut.posh.widget.menuactions;

import cz.cuni.amis.pogamut.sposh.elements.ActionPattern;
import cz.cuni.amis.pogamut.sposh.elements.ParseException;
import cz.cuni.amis.pogamut.sposh.elements.PoshPlan;
import cz.cuni.amis.pogamut.sposh.elements.TriggeredAction;
import cz.cuni.pogamut.posh.widget.kidview.AbstractMenuAction;
import java.awt.event.ActionEvent;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Honza
 */
public class AddAP2Plan extends AbstractMenuAction<PoshPlan> {

    public AddAP2Plan(PoshPlan plan) {
        super("Add action pattern", plan);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String id = getIdentifierFromDialog("Name of new AP");
        if (id == null) {
            return;
        }

        try {
            ActionPattern ap = new ActionPattern(id);
            ap.addTriggeredAction(new TriggeredAction("doNothing"));

            dataNode.addActionPattern(ap);
        } catch (ParseException ex) {
            NotifyDescriptor.Message error = new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(error);
        }
    }
}


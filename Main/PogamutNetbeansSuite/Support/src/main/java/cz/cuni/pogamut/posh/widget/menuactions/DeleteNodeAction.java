package cz.cuni.pogamut.posh.widget.menuactions;

import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.pogamut.posh.widget.kidview.AbstractMenuAction;
import java.awt.event.ActionEvent;

/**
 *
 * @author Honza
 */
public class DeleteNodeAction<T extends PoshElement> extends AbstractMenuAction<T> {

    public DeleteNodeAction(String desc, T dataNode) {
        super(desc, dataNode);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dataNode.neutralize();
    }
}

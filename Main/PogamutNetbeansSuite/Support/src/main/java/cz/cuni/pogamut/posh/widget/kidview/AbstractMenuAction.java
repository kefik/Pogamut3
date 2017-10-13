package cz.cuni.pogamut.posh.widget.kidview;

import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.pogamut.posh.widget.PoshWidget;
import java.util.regex.Pattern;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Ancestor of (most) Shed menu actions. Basically every menu action will modify
 * underlying data node in some way, this class provides link to the data node
 * ({@link AbstractMenuAction#dataNode}) and possibly to the widget
 * ({@link AbstractMenuAction#widget}) on which the action is being performed
 * on. It also provides handy method ({@link AbstractMenuAction#errorDialog(java.lang.String)
 * }) for displaying message to the user if action is for some reason impossible
 * to perform.
 *
 * @author HonzaH
 * @param <T> Type of data node on which the menu action will be performed
 */
public abstract class AbstractMenuAction<T extends PoshElement> extends AbstractAction {

    protected T dataNode;
    protected PoshWidget widget;

    public AbstractMenuAction(String desc, T dataNode, PoshWidget widget) {
        this.setDescription(desc);
        this.dataNode = dataNode;
        this.widget = widget;
    }

    public AbstractMenuAction(String desc, T dataNode) {
        this(desc, dataNode, null);
    }

    public final String getDescription() {
        return (String) this.getValue(Action.LONG_DESCRIPTION);
    }

    protected final void setDescription(String newDesc) {
        this.putValue(Action.LONG_DESCRIPTION, newDesc);
    }

    protected final String getIdentifierFromDialog(String purposeTitle) {
        NotifyDescriptor.InputLine desc = new NotifyDescriptor.InputLine("Please write new identifiew. Name cannot have whitespaces in it.", purposeTitle);
        DialogDisplayer.getDefault().notify(desc);

        if (desc.getValue() != NotifyDescriptor.OK_OPTION) {
            return null;
        }
        if (!Pattern.compile("[a-zA-Z0-9_-]+").matcher(desc.getInputText().trim()).matches()) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Identifier wasn't valid."));
            return null;
        }
        return desc.getInputText().trim();
    }

    /**
     * Display user an error that occured during
     *
     * @param message message with error
     */
    protected final void errorDialog(String message) {
        NotifyDescriptor.Message error = new NotifyDescriptor.Message(message, NotifyDescriptor.ERROR_MESSAGE);
        DialogDisplayer.getDefault().notify(error);
    }
}

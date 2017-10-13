package cz.cuni.pogamut.shed.presenter;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.Action;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;

/**
 * {@link WidgetAction} wrapper for delete action from {@link ShedMenuActionFactory}.
 *
 * @author Honza
 */
final class DeleteFocusedNodeAction extends WidgetAction.Adapter {

    private final Action deleteAction;

    DeleteFocusedNodeAction(Action deleteAction) {
        this.deleteAction = deleteAction;
    }

    @Override
    public State keyPressed(Widget widget, WidgetKeyEvent event) {
        if (widget.getState().isObjectFocused() && event.getKeyCode() == KeyEvent.VK_DELETE) {
            ActionEvent actionEvent = new ActionEvent(widget, (int) event.getEventID(), null, event.getWhen(), event.getModifiers());
            deleteAction.actionPerformed(actionEvent);
            return State.CONSUMED;
        }
        return State.REJECTED;
    }
}

package cz.cuni.pogamut.shed.presenter;

import cz.cuni.amis.pogamut.sposh.elements.DriveElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshElementListener;
import cz.cuni.pogamut.shed.widget.ShedScene;
import cz.cuni.pogamut.shed.widget.ShedWidget;
import cz.cuni.pogamut.shed.widget.editor.ShedInplaceEditorFactory;
import java.beans.PropertyChangeEvent;
import javax.swing.*;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;

/**
 * Presenter for widget representing the {@link DriveElement} in the {@link ShedScene}.
 * This presenter changes only the widget representing the {@link DriveElement}
 * using the properties changes, not its expanded action, nor trigger.
 *
 * @author HonzaH
 */
class DrivePresenter extends AbstractPresenter implements IPresenter, PoshElementListener<DriveElement> {

    private final DriveElement drive;
    private final ShedWidget shedWidget;

    /**
     * Create new presenter for drive widget only. Presents the properties of
     * the drive.
     *
     * @param scene Scene of the drive widget.
     * @param presenter Main presenter.
     * @param drive Drive on which the presenters listens. If property changes,
     * so does the widget.
     * @param widget The widget that will be modifed according to property
     * changes of the @drive.
     */
    DrivePresenter(ShedScene scene, ShedPresenter presenter, DriveElement drive, ShedWidget widget) {
        super(scene, presenter);
        this.drive = drive;
        this.shedWidget = widget;
    }

    @Override
    public void register() {
        shedWidget.setPresenter(this);
        drive.addElementListener(this);

        shedWidget.getActions().addAction(FocusActionFactory.createFocusAction());
        shedWidget.getActions().addAction(new DeleteFocusedNodeAction(ShedMenuActionFactory.deleteDriveAction(drive)));
    }

    @Override
    public void unregister() {
        drive.removeElementListener(this);
        shedWidget.setPresenter(null);
    }

    @Override
    public Action[] getMenuActions() {
        return new Action[]{
                    ShedMenuActionFactory.appendDriveAction(drive),
                    ShedMenuActionFactory.deleteDriveAction(drive),
                    ShedMenuActionFactory.createSenseAction(drive)
                };
    }

    @Override
    public void childElementAdded(DriveElement parent, PoshElement child) {
        // Do nothing, senses are taken care of by trigger presenter and action is not addable
    }

    @Override
    public void childElementMoved(DriveElement parent, PoshElement child, int oldIndex, int newIndex) {
        // sense are taken care of by trigger presenter
    }

    @Override
    public void childElementRemoved(DriveElement parent, PoshElement child, int removedChildIndex) {
        // only senses are removed, not job of this presenter
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(DriveElement.deName)) {
            String newDriveName = evt.getNewValue().toString();
            shedWidget.setDisplayName(newDriveName);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public AbstractAcceptAction[] getAcceptProviders() {
        return new AbstractAcceptAction[]{
                    AcceptActionFactory.createSense2Drive(drive),
                    AcceptActionFactory.createDrive2Drive(drive),
                    AcceptActionFactory.createChoice2Drive(drive)
                };
    }

    @Override
    public WidgetAction getEditAction() {
        return ActionFactory.createInplaceEditorAction(ShedInplaceEditorFactory.createDriveEditor(drive));
    }
}

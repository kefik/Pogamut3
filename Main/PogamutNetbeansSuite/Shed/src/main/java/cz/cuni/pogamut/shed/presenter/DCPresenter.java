package cz.cuni.pogamut.shed.presenter;

import cz.cuni.amis.pogamut.sposh.elements.DriveCollection;
import cz.cuni.amis.pogamut.sposh.elements.DriveElement;
import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.LapType;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshElementListener;
import cz.cuni.pogamut.shed.widget.*;
import java.beans.PropertyChangeEvent;
import javax.swing.Action;
import org.netbeans.api.visual.action.WidgetAction;

/**
 * Presenter of {@link DriveCollection}. It is supposed to take care about
 * changes of view on following events:
 * <p/>
 *
 * <ul><li>Change name of DC </li>
 *
 * <li>Add goal sense </li>
 *
 * <li>Move goal sense </li>
 *
 * <li>Remove goal sense</li>
 *
 * <li>Add drive</li>
 *
 * <li>Move drive</li>
 *
 * <li>Remove drive</li></ul>
 *
 * @author HonzaH
 */
final class DCPresenter extends AbstractPresenter implements PoshElementListener<DriveCollection> {

    private final DriveCollection dc;

    DCPresenter(ShedScene scene, ShedPresenter presenter, DriveCollection dc) {
        super(scene, presenter);
        this.dc = dc;
    }

    @Override
    public void register() {
        this.dc.addElementListener(this);
    }

    @Override
    public void unregister() {
        this.dc.removeElementListener(this);
    }

    @Override
    public Action[] getMenuActions() {
        return null;
    }

    @Override
    public void childElementAdded(DriveCollection dc, PoshElement child) {
        assert isDrive(child) || isSense(child);

        if (isDrive(child)) {
            DriveElement drive = extractDrive(dc.getDrives(), child);
            int drivePosition = getPosition(dc.getDrives(), drive);
            LapPath drivePath = LapPath.DRIVE_COLLECTION_PATH.concat(LapType.DRIVE_ELEMENT, drivePosition);
            ShedCreationContainer<SlotEnvelope> driveEnvelope = scene.getWidgetFactory().createDriveEnvelope(drivePath, drive);
            scene.getDrivesEnvelope().add(driveEnvelope.getWidget(), drivePosition);
            scene.update();
            scene.addArrows(driveEnvelope.getArrows());
            scene.addArrow(scene.getRootAnchor(), driveEnvelope.getWidget().getAnchor());
            scene.update();
        }
    }

    @Override
    public void childElementMoved(DriveCollection dc, PoshElement child, int oldIndex, int newIndex) {
        assert isDrive(child) || isSense(child);
        
        if (isDrive(child)) {
            ShedDrivesEnvelope drivesEnvelope = scene.getDrivesEnvelope();
            SlotEnvelope driveEnvelope = drivesEnvelope.getChild(oldIndex);
            drivesEnvelope.move(newIndex, driveEnvelope);
        }
    }

    @Override
    public void childElementRemoved(DriveCollection dc, PoshElement child, int removedChildIndex) {
        assert isDrive(child) || isSense(child);

        if (isDrive(child)) {
            SlotEnvelope removedDriveWidget = scene.getDrivesEnvelope().getChild(removedChildIndex);
            scene.removeBranch(removedDriveWidget);
            scene.update();
        }
        // trigger are taken care of by TriggerPresenter
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public WidgetAction getEditAction() {
        return null;
    }
}

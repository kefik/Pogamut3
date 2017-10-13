package cz.cuni.pogamut.shed.widget;

import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.pogamut.shed.presenter.IPresenter;
import cz.cuni.pogamut.shed.presenter.IPresenterFactory;
import cz.cuni.pogamut.shed.presenter.ShedPresenterFactory;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import org.netbeans.api.visual.action.ActionFactory;

/**
 * Factory for creating different scenes of YAPOSH tree. There should be two of
 * them, first is scene modifiable for Shed editor and second nonmodifiable
 * should be for Dash debugger.
 *
 * @author Honza
 */
public class LapSceneFactory {

    /**
     * Create scene for editor Shed.
     *
     * @param plan Empty plan object that will be visualized in the scene. Must
     * be empty (no goals of DC and no drives).
     * @return Created empty scene.
     */
    public static ShedScene createShedScene(PoshPlan plan) {
        assert plan.getDriveCollection().getDrives().isEmpty();
        assert plan.getDriveCollection().getGoal().isEmpty();

        ShedScene scene = new ShedScene(plan);
        IPresenterFactory presenterFactory = new ShedPresenterFactory(scene, plan, scene.getPresenter());
        ShedWidgetFactory widgetFactory = new ShedWidgetFactory(scene, plan, presenterFactory);

        scene.setWidgetFactory(widgetFactory);

        // create presenter for updating the drives and DC goal senses
        registerDriveCollectionPresenters(scene, presenterFactory, plan.getDriveCollection());

        return scene;
    }

    /**
     * Create scene that will show the expanded tree of the @plan. This scene is
     * not for editing, but for observing.
     *
     * @param plan Plan that will be displayed in the scene.
     * @param scene Empty scene.
     * @param presenterFactory Presenter factory used for creation of tree in
     * the scene.
     * @return same scene as was passed in parameter @scene, but filled with
     * displayed tree.
     */
    public static ShedScene createDashScene(PoshPlan plan, ShedScene scene, IPresenterFactory presenterFactory) {
        ShedWidgetFactory widgetFactory = new ShedWidgetFactory(scene, plan, presenterFactory);

        scene.setWidgetFactory(widgetFactory);

        // create presenter for updating the drives and DC goal senses
        registerDriveCollectionPresenters(scene, presenterFactory, plan.getDriveCollection());

        int driveId = 0;
        for (DriveElement drive : plan.getDriveCollection().getDrives()) {
            LapPath drivePath = LapPath.DRIVE_COLLECTION_PATH.concat(LapType.DRIVE_ELEMENT, driveId++);
            ShedCreationContainer<SlotEnvelope> driveContainer = widgetFactory.createDriveEnvelope(drivePath, drive);
            scene.getDrivesEnvelope().add(driveContainer.getWidget());
            scene.addArrows(driveContainer.getArrows());
            scene.addArrow(scene.getRootAnchor(), driveContainer.getWidget().getAnchor());
        }
        scene.update();

        return scene;
    }

    private static void registerDriveCollectionPresenters(ShedScene scene, IPresenterFactory presenterFactory, DriveCollection driveCollection) {
        IPresenter dcPresenter = presenterFactory.createDriveCollectionPresenter(LapPath.DRIVE_COLLECTION_PATH);
        dcPresenter.register();

        IPresenter goalPresenter = presenterFactory.createTriggerPresenter(LapPath.DRIVE_COLLECTION_PATH, scene.getGoalEnvelope());
        goalPresenter.register();
    }
}

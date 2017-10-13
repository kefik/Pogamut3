package cz.cuni.pogamut.shed.presenter;

import cz.cuni.amis.pogamut.sposh.elements.DriveCollection;
import cz.cuni.amis.pogamut.sposh.elements.LapChain;
import cz.cuni.amis.pogamut.sposh.elements.LapPath;
import cz.cuni.amis.pogamut.sposh.elements.PoshElement;
import cz.cuni.amis.pogamut.sposh.elements.PoshElementListener;
import cz.cuni.amis.pogamut.sposh.elements.Sense;
import cz.cuni.amis.pogamut.sposh.elements.Trigger;
import cz.cuni.pogamut.shed.widget.*;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.Set;
import javax.swing.Action;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.layout.LayoutFactory;

/**
 * Tris class is a presenter for {@link ShedTriggerEnvelope}. Its job is to keep
 * widgets representing the {@link Sense senses} in sync with the model, i.e. to
 * add, move and delete widgets. The methods respond only when {@link Sense} is
 * added, moved or removed. If some other child is modified (IIRC it can only
 * happen in {@link DriveCollection}), it ignores them.
 *
 * It does not reflect changes of senses themselves, see {@link SensePresenter}
 * for that.
 *
 * @param TRIGGER_OWNER Trigger owner is the {@link PoshElement} that
 * contains the {@link Trigger}, we can't use {@link Trigger} itself, because it
 * is not {@link PoshElement}
 * @author Honza H
 */
final class TriggerPresenter<TRIGGER_OWNER extends PoshElement> extends AbstractPresenter implements IPresenter, PoshElementListener<TRIGGER_OWNER> {
    private final ShedTriggerEnvelope triggerEnvelope;
    private final TRIGGER_OWNER triggerOwner;
    private final Trigger<TRIGGER_OWNER> trigger;
    private final LapChain ownerChain;

    /**
     * Create new presenter for the trigger. XXX: Create special interface so I
     * don;t have to pass the trigger in separate field.
     *
     * @param scene Scene the presenter will modify.
     * @param presenter Main presenter of the scene
     * @param triggerEnvelope The envelope the presenter will add/move/remove
     * sense widgets
     * @param triggerOwner The {@link PoshElement} that has the trigger. The presenter
     * is listening for changes on it.
     * @param trigger The trigger itself. It must be trigger of @triggerOwner.
     * @param ownerChain Chain up to trigger. If drive trigger, empty chain, if
     * choice trigger, chain is up to competence, inclusive.
     */
    TriggerPresenter(ShedScene scene, ShedPresenter presenter, ShedTriggerEnvelope triggerEnvelope, TRIGGER_OWNER triggerOwner, Trigger<TRIGGER_OWNER> trigger, LapChain ownerChain) {
        super(scene, presenter);
        
        this.triggerEnvelope = triggerEnvelope;
        this.triggerOwner = triggerOwner;
        this.trigger = trigger;
        this.ownerChain = ownerChain;
    }

    @Override
    public void register() {
        triggerEnvelope.setPresenter(this);
        triggerOwner.addElementListener(this);
    }

    @Override
    public void unregister() {
        triggerOwner.removeElementListener(this);
        triggerEnvelope.setPresenter(null);
    }

    @Override
    public Action[] getMenuActions() {
        return null;
    }

    @Override
    public void childElementAdded(TRIGGER_OWNER triggerOwner, PoshElement child) {
        if (isSense(child)) {
            // XXX: This is rather unpleasant hack, I should use custom layout or something. What I want is to have gap when trigger has at least one sense in it, but no gap, when there is not sense in the trigger.
            triggerEnvelope.getParentWidget().setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP, ShedWidgetFactory.VERTICAL_GAP));
            
            Sense addedSense = extractSense(trigger, child);
            LapPath branchToSensePath = LapPath.getLinkPath(addedSense);
            LapPath senseFragmentPath = branchToSensePath.subpath(1, branchToSensePath.length());
            LapPath addedSensePath = ownerChain.toPath().concat(senseFragmentPath); 
            ShedSenseWidget newSenseWidget = scene.getWidgetFactory().createSenseWidget(addedSensePath);

            int sensePosition = getPosition(trigger, addedSense);
            Anchor arrowSourceAnchor = getAnchorBeforePosition(sensePosition);
            boolean addedSenseReplacesAnother =
                    triggerEnvelope.numberOfChildren() > 0
                    && sensePosition != triggerEnvelope.numberOfChildren();

            if (addedSenseReplacesAnother) {
                // remove arrow connecting old sense at senseWidgetPosition with its predecesor
                ShedSenseWidget originalWidget = triggerEnvelope.getChild(sensePosition);
                Anchor oldSenseAnchor = originalWidget.getCommonAnchor();
                Set<ArrowWidget> previousSenseArrow = scene.findArrows(arrowSourceAnchor, oldSenseAnchor);
                scene.removeArrows(previousSenseArrow);
            }

            triggerEnvelope.add(newSenseWidget, sensePosition);
            scene.update();

            if (addedSenseReplacesAnother) {
                ShedSenseWidget oldSenseWidget = triggerEnvelope.getChild(sensePosition + 1);
                scene.addArrow(newSenseWidget.getCommonAnchor(), oldSenseWidget.getCommonAnchor());
            }

            // add arrow connecting new widget with its predecesor (either sense or source anchor of trigger)
            Anchor arrowTargetAnchor = newSenseWidget.getCommonAnchor();
            scene.addArrow(arrowSourceAnchor, arrowTargetAnchor);

            scene.update();
        }
    }

    @Override
    public void childElementMoved(TRIGGER_OWNER triggerOwner, PoshElement child, int oldIndex, int newIndex) {
        if (isSense(child)) {
            ShedSenseWidget senseWidget = triggerEnvelope.getChild(oldIndex);

            scene.removeArrows(getSenseLeftArrow(oldIndex));
            scene.removeArrows(getSenseRightArrow(oldIndex));

            scene.removeArrows(getSenseLeftArrow(newIndex));
            scene.removeArrows(getSenseRightArrow(newIndex));

            triggerEnvelope.move(newIndex, senseWidget);

            for (int senseIndex = 0; senseIndex < triggerEnvelope.numberOfChildren(); ++senseIndex) {
                Set<ArrowWidget> leftArrow = getSenseLeftArrow(senseIndex);
                if (leftArrow.isEmpty()) {
                    addSenseLeftArrow(senseIndex);
                }
            }
            scene.update();
        }
    }

    private void addSenseLeftArrow(int senseIndex) {
        ShedSenseWidget sense = triggerEnvelope.getChild(senseIndex);
        Anchor sourceAnchor = getAnchorBeforePosition(senseIndex);
        Anchor targetAnchor = sense.getCommonAnchor();

        scene.addArrow(sourceAnchor, targetAnchor);
    }

    private Set<ArrowWidget> getSenseLeftArrow(int senseIndex) {
        ShedSenseWidget senseWidget = triggerEnvelope.getChild(senseIndex);
        Anchor beforeSenseAnchor = getAnchorBeforePosition(senseIndex);
        return scene.findArrows(beforeSenseAnchor, senseWidget.getCommonAnchor());
    }

    private Set<ArrowWidget> getSenseRightArrow(int senseIndex) {
        boolean senseIsLast = (senseIndex == triggerEnvelope.numberOfChildren() - 1);
        if (!senseIsLast) {
            ShedSenseWidget senseWidget = triggerEnvelope.getChild(senseIndex);
            ShedSenseWidget rightSenseWidget = triggerEnvelope.getChild(senseIndex + 1);
            return scene.findArrows(senseWidget.getCommonAnchor(), rightSenseWidget.getCommonAnchor());
        }
        return Collections.<ArrowWidget>emptySet();
    }

    @Override
    public void childElementRemoved(TRIGGER_OWNER triggerOwner, PoshElement child, int removedChildIndex) {
        if (isSense(child)) {
            // XXX: This is rather unpleasant hack, I should use custom layout or something. What I want is to have gap when trigger has at least one sense in it, but no gap, when there is not sense in the trigger.
            if (trigger.isEmpty()) {
                triggerEnvelope.getParentWidget().setLayout(LayoutFactory.createVerticalFlowLayout(LayoutFactory.SerialAlignment.LEFT_TOP, 0));
            }
            
            ShedSenseWidget removedSenseWidget = triggerEnvelope.getChild(removedChildIndex);
            triggerEnvelope.remove(removedSenseWidget);

            boolean triggerIsEmpty = (triggerEnvelope.numberOfChildren() == 0);
            boolean removedSenseWasLast = (removedChildIndex == triggerEnvelope.numberOfChildren());
            boolean triggerIsMissingArrow = (!triggerIsEmpty && !removedSenseWasLast);

            if (triggerIsMissingArrow) {
                Anchor sourceAnchor = getAnchorBeforePosition(removedChildIndex);
                Anchor targetAnchor = triggerEnvelope.getChild(removedChildIndex).getCommonAnchor();

                scene.addArrow(sourceAnchor, targetAnchor);
            }
            scene.update();
        }
    }

    /**
     * Trigger presenter is only concerned with adding/moving/deleting senses in
     * the trigger owner, not properties of the owner.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

    /**
     * Get anchor that is used for arrow on the left side of sense at @position.
     *
     * @param position Position of widget for which we want source anchor of
     * arrow on its left side.
     * @return Either anchor of some sense or source anchor of trigger.
     */
    private Anchor getAnchorBeforePosition(int position) {
        if (position == 0) {
            return triggerEnvelope.getCommonAnchor();
        } else {
            return triggerEnvelope.getChild(position - 1).getCommonAnchor();
        }
    }

    @Override
    public WidgetAction getEditAction() {
        return null;
    }
}

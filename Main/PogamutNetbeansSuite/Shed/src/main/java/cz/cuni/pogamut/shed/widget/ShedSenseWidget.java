package cz.cuni.pogamut.shed.widget;

import cz.cuni.amis.pogamut.sposh.elements.Result;
import cz.cuni.amis.pogamut.sposh.elements.Sense;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.visual.util.GeomUtil;

/**
 * Widget representing the {@link Sense} in the {@link ShedScene}.
 *
 * @author HonzaH
 */
public class ShedSenseWidget extends ShedWidget implements IAnchorProvider {

    private final Anchor anchor = new LeveledHorizontalAnchor(this);

    public ShedSenseWidget(ShedScene scene, Sense element, Color color) {
        super(scene, createDisplayName(element), ShedWidgetColors.SENSE.color);
    }

    /**
     * Create human readable string representing what is {@link Sense} doing.
     * <p/>
     * <ul> <li>If operand is {@link Boolean#TRUE} (=its default value) and
     * predicate is {@link Sense.Predicate} is {@link Sense.Predicate#DEFAULT},
     * show only senseCall<li> <li>Otherwise show senseCall predicateString
     * operandString (note that operand can be null [nil in the plan]).</li>
     * <li></li> </ul>
     * <p/>
     * This is static to be able to call before parent's constructor.
     *
     * @param sense Sense for which to construct the representation.
     * @return String representing what the sense is doing
     */
    public static String createDisplayName(Sense sense) {
        Object operand = sense.getOperand();
        Sense.Predicate predicate = sense.getPredicate();

        boolean predicateIsEqual = predicate.equals(Sense.Predicate.DEFAULT) || predicate.equals(Sense.Predicate.EQUAL);
        if (predicateIsEqual && Boolean.TRUE.equals(operand)) {
            return sense.getCall().toString();
        }
        String operandString = Result.toLap(operand);

        StringBuilder sb = new StringBuilder(sense.getCall().toString());
        sb.append(predicate.toString());
        sb.append(operandString);
        return sb.toString();
    }

    @Override
    public Anchor getCommonAnchor() {
        return anchor;
    }
}

final class LeveledHorizontalAnchor extends Anchor {

    public LeveledHorizontalAnchor(Widget widget) {
        super(widget);
        assert widget != null;
    }

    @Override
    public Result compute(Entry entry) {
        Point relatedLocation = getRelatedSceneLocation();
        Point oppositeLocation = getOppositeSceneLocation(entry);

        Widget widget = getRelatedWidget();
        Rectangle bounds = widget.convertLocalToScene(widget.getBounds());

        if (relatedLocation.x >= oppositeLocation.x) {
            return new Anchor.Result(new Point(bounds.x, bounds.y + ShedWidget.height / 2), Direction.LEFT);
        } else {
            return new Anchor.Result(new Point(bounds.x + bounds.width, bounds.y + ShedWidget.height / 2), Direction.RIGHT);
        }
    }
    
}

package cz.cuni.amis.nb.pogamut.unreal.timeline.widgets;

import cz.cuni.amis.nb.pogamut.unreal.timeline.records.TLEntity;
import java.awt.Color;
import org.netbeans.api.visual.border.Border;
import org.netbeans.api.visual.border.BorderFactory;

/**
 * Widget that show name of widget on the left and progress strip on the right.
 * 
 * @author Honza
 */
class EntityAxisStrip extends AxisWidget {

    private static final int STRIP_HEIGHT = 10;

    /**
     * Create two subwidgets, one for name and other for strip.
     * Set size and position of the strip to the size provided by entity.
     *
     * @param scene
     * @param entity
     */
    EntityAxisStrip(TLScene scene, TLEntity entity) {
        super(scene, entity, entity.getDisplayName(), STRIP_HEIGHT, getBorder(entity.getColor()));
    }

    private static Border getBorder(Color c) {
        Color fillColor = getFillColor(c);
        Color borderColor = getBorderColor(c);

        return BorderFactory.createRoundedBorder(5, 5, fillColor, borderColor);
    }

    private static Color getFillColor(Color c) {
        return c;
    }

    private static Color getBorderColor(Color c) {
        return c.darker();
    }
}

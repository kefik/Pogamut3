package cz.cuni.pogamut.shed.widget;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.widget.Widget;

/**
 * The creation of widgets representing some part of plan consists from two
 * parts, the widget itself and the arrows that connect them. Due to constraints
 * of {@link Widget.Dependency}, the {@link Anchor} of the widget must be
 * inserted into the scene before the arrows that use the widget position can be
 * inserted into the scene (otherwise arrow has no idea to determine where it
 * should start and where to end). Failure to do so (to first insert widget and
 * then arrows) will result in something linke {@link IllegalStateException} -
 * Widget was not added into the scene or {@link AssertionError} -
 * Scene.validate was not called after last change.
 *
 * This container is used by {@link ShedWidgetFactory} for passing back created
 * widgets.
 *
 * @author Honza
 */
public class ShedCreationContainer<WIDGET extends Widget> {
    private WIDGET createdWidget;
    private Set<ArrowWidget> createdArrows;

    /**
     * Create container for passed widget. Arrows can be later added using {@link #addArrow(cz.cuni.pogamut.shed.widget.ArrowWidget) }.
     */
    public ShedCreationContainer(WIDGET createdWidget) {
        this.createdWidget = createdWidget;
        this.createdArrows = new HashSet<ArrowWidget>();
    }
    
    /**
     * Add arrow to this container.
     * @param arrow Arrow to be added.
     */
    public void addArrow(ArrowWidget arrow) {
        createdArrows.add(arrow);
    }

    /**
     * @return The widget of this container.
     */
    public WIDGET getWidget() {
        return createdWidget;
    }

    /**
     * Get arrows of this container.
     */
    public Set<ArrowWidget> getArrows() {
        return createdArrows;
    }

    /**
     * Add passed arrows to the set of all arrows of this container.
     * @param newArrows arrows that will be added to the set.
     */
    void addArrows(Set<ArrowWidget> newArrows) {
        this.createdArrows.addAll(newArrows);
    }
    
}

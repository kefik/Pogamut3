package cz.cuni.pogamut.shed.widget;

import cz.cuni.pogamut.shed.presenter.IPresenter;
import java.util.List;
import org.netbeans.api.visual.widget.Widget;

/**
 * Envelope that should contain multiple widgets of same type (e.g. drives,
 * choices, senses...).
 *
 * @author Honza
 * @param <T> Type of children the envelope will contain.
 */
public abstract class AbstractShedEnvelope<T extends Widget> extends Widget implements IShedEnvelope<T>, IPresentedWidget {

    private IPresenter presenter;
    protected final ShedScene scene;

    protected AbstractShedEnvelope(ShedScene scene) {
        super(scene);
        this.scene = scene;
    }

    /**
     * Method called when child widget is added/moved/deleted. Its job is to
     * make sure that children are correctly positioned. What it does depend on
     * layout of the envelope. In many cases, there is no need to do anything,
     * but e.g. absolute layout would require to manually update position of
     * children. Use {@link Widget#setPreferredLocation(java.awt.Point) }.
     */
    protected abstract void updateChildrenPositions();

    /**
     * Add widget at specified position.
     * @param newWidget
     * @param position 
     */
    @Override
    public final void add(T newWidget, int position) {
        assert !getChildren().contains(newWidget);
        addChild(position, newWidget);
        updateChildrenPositions();
    }

    /**
     * Add widget to the envelope behind the last element
     *
     * @param newWidget Widget that wll be added to the envelope at the last
     * place
     */
    public final void add(T newWidget) {
        add(newWidget, getChildren().size());
    }

    @Override
    public final void move(int absoluteIndex, T movedWidget) {
        assert getChildren().contains(movedWidget);

        int oldPosition = getChildren().indexOf(movedWidget);
        int newPosition = absoluteIndex;
        if (newPosition < 0 || newPosition >= getChildren().size()) {
            throw new IndexOutOfBoundsException("New position is " + newPosition + ", but allowed range is 0.." + getChildren().size());
        }

        T child = getChild(oldPosition);
        assert child == movedWidget;
        removeChild(child);
        addChild(newPosition, movedWidget);

        updateChildrenPositions();
    }

    /**
     * Remove widget and its branch from the scene.
     * @param removedWidget Branch to remove
     */
    @Override
    public final void remove(T removedWidget) {
        assert getChildren().contains(removedWidget);
        scene.removeBranch(removedWidget);
        updateChildrenPositions();
    }

    /**
     * Typed Wrapper for {@link #getChildren() }.{@link List#get(int) }. Get
     * child at specified position and return it.
     *
     * @param position position of the child we want.
     * @return Properly typed child.
     */
    public final T getChild(int position) {
        return (T) getChildren().get(position);
    }

    /**
     * Get number of children, used for type safe looping.
     */
    public final int numberOfChildren() {
        return getChildren().size();
    }

    @Override
    public final IPresenter getPresenter() {
        return this.presenter;
    }

    public final void setPresenter(IPresenter newPresenter) {
        this.presenter = newPresenter;
    }
}
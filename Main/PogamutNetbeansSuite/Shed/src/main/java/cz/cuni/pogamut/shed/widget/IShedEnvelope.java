package cz.cuni.pogamut.shed.widget;

/**
 * Interface for envelopes containing only one type of widgets inside.
 *
 * @author HonzaH
 * @param <T> Type of widgets this envelope contains.
 */
public interface IShedEnvelope<T> {

    /**
     * Add widget to the envelope at the specified position.
     *
     * @param widget widget to add
     * @param position where to put widget
     */
    void add(T widget, int position);

    /**
     * Move the widget relative to the other widgets.
     *
     * @param absoluteIndex New index of the widget in the envelope. Once move
     * is done, the index of the @widget is @absoluteIndex and the rest of
     * widgets change their position in a way that will keep the order they were
     * in before move, while having the widget at its new position.
     * @param widget widget to move
     */
    void move(int index, T widget);

    /**
     * Remove widget from the envelope.
     *
     * @param widget widget to remove from the envelope.
     */
    void remove(T widget);
}

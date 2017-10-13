package cz.cuni.amis.nb.util.flag;

import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.flag.FlagListener;
import org.openide.nodes.Children;

/**
 * Used for changing set of children each time the flag value changes.
 * @author ik
 */
public abstract class FlagChildren<T> extends Children.Keys {

    /**
     * Used to indicate empty list of children.
     */
    public static final Object EMPTY = new Object();

    public FlagChildren(Flag<T> flag) {
        FlagListener<T> listener = null;
        flag.addListener(listener = new FlagListener<T>() {

            @Override
            public void flagChanged(T changedValue) {
                setKey(changedValue);
            }
        });

        setKey(flag.getFlag());
    }

    protected void setKey(T key) {
        if (key == null) {
            setKeys(new Object[]{EMPTY});
        } else {
            setKeys(new Object[]{key});
        }
    }
}

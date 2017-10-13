package cz.cuni.amis.utils.flag;

import java.io.Serializable;

import cz.cuni.amis.utils.flag.Flag.DoInSync;

/**
 * Interface for flags. Flags is a reference that raises events each time the
 * referenced object is changed.
 * <p><p>
 * Note that flag implementation must be {@link Serializable}!
 * 
 * @author ik
 * @author Jimmy
 */
public interface IFlag<T> extends Serializable {

    public void addListener(FlagListener<T> listener);

    public void addStrongListener(FlagListener<T> listener);

    public void clearListeners();

    public T getFlag();

    public boolean isListenning(FlagListener<T> listener);

    public void removeListener(FlagListener<T> listener);

    public void removeAllListeners();

    public void setFlag(T newValue);

    public ImmutableFlag<T> getImmutable();

    public void inSync(DoInSync<T> command);

    public boolean isFrozen();

    public void freeze();

    public void defreeze();
}

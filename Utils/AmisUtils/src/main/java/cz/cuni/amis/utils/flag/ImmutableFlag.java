package cz.cuni.amis.utils.flag;

import java.io.Serializable;


/**
 * {@link Flag} whhich value cannot be set.
 * 
 * @author ik
 */
public class ImmutableFlag<T> extends Flag<T> implements Serializable {
    
    protected IFlag<T> flag = null;
    
    /** Creates a new instance of ImmutableFlag */
    public ImmutableFlag(IFlag<T> flag) {
        this.flag = flag;
    }

    @Override
    public void addListener(FlagListener<T> listener) {
        flag.addListener(listener);
    }
    
    @Override
    public void addStrongListener(FlagListener<T> listener) {
    	flag.addStrongListener(listener);
    }
        
    @Override
    public void clearListeners() {
        flag.clearListeners();
    }
    
    @Override
    public T getFlag(){
        return flag.getFlag();
    }
    
    @Override
    public boolean isListenning(FlagListener<T> listener){
        return flag.isListenning(listener);
    }
        
    @Override
    public void removeListener(FlagListener<T> listener){
        flag.removeListener(listener);
    }

    @Override
    public void setFlag(T newValue) {
        throw new UnsupportedOperationException("Trying to set value of immutable flag.");
    }
    
    @Override
    public ImmutableFlag<T> getImmutable() {
        return this;
    }
    
    @Override
    public void inSync(DoInSync<T> command) {
    	flag.inSync(command);
    }
    
    @Override
    public boolean isFrozen() {
    	return flag.isFrozen();
    }
    
    @Override
    public void freeze() {
    	throw new UnsupportedOperationException("Trying to freeze immutable flag.");
    }
    
    @Override
    public void defreeze() {
    	throw new UnsupportedOperationException("Trying to defreeze immutable flag.");
    }
    
}

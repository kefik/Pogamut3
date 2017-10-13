package cz.cuni.amis.pogamut.base.component.bus.event;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cz.cuni.amis.pogamut.base.component.IComponent;
import cz.cuni.amis.pogamut.base.component.bus.IComponentBus;
import cz.cuni.amis.pogamut.base.component.bus.IComponentEventListener;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.exception.PogamutInterruptedException;
import cz.cuni.amis.utils.token.IToken;

/**
 * Extends CoundDownLatch with ability to stop waiting when some component connected
 * to the bus fails which may indicate termination of all other components on the bus.
 * Thus further waiting doesn't make sense.
 * @author ik
 */
public class BusAwareCountDownLatch extends CountDownLatch {

    public static class BusStoppedInterruptedException extends PogamutInterruptedException {
        public BusStoppedInterruptedException(Object origin) {
            super("Interrupted because bus was stopped (fatal error, or watched component stopped) while waiting on the latch.", origin);
        }
    }
    
    private static IToken[] getTokens(IComponent... components) {
    	if (components == null) return null;
    	IToken[] tokens = new IToken[components.length];
    	int i = 0;
    	for (IComponent component : components) {
    		tokens[i++] = component.getComponentId();
    	}
    	return tokens;
    }
    
    private final IComponentBus bus;
    
    private final IToken[] componentIds;
    
    /**
     * Indication that bus (or one of dependent component) stopped before the latch was raised externaly.
     */
    private boolean stoppedEvent = false;
    
    private Object stopMutex = new Object();
    
    /**
     * Wether the listeners has been removed from component bus."
     */
    private boolean removed = false;
    
    private IComponentEventListener componentListener = new IComponentEventListener() {
        @Override
        public void notify(Object event) {
        	stopped();            
        }
    };
    
    //
    //
    // CONSTRUCTORS
    //
    //
    
    public BusAwareCountDownLatch(int count, IComponentBus bus) {
        this(count, bus, (IToken[])null);
    }
    
    public BusAwareCountDownLatch(int count, IComponentBus bus, IComponent... components) {
        this(count, bus, getTokens(components));
    }
    
    public BusAwareCountDownLatch(int count, IComponentBus bus, IToken... componentIds) {
        super(count);
        this.bus = bus;
        NullCheck.check(bus, "bus");        
        this.componentIds  = componentIds;   
        NullCheck.check(componentIds, "componentIds");
        if (!bus.isRunning()) {        	
        	removed = true;
        	stopped();
        } else {        
	        if (count > 0) {
	        	removed = false;
	        	bus.addEventListener(IFatalErrorEvent.class, componentListener);
		        synchronized(this.componentIds) {
			        for (IToken componentId : componentIds) {
			        	bus.addEventListener(IStoppingEvent.class, componentId, componentListener); 
			        	bus.addEventListener(IStoppedEvent.class, componentId, componentListener);
			        }
		        }
	        }        
	        if (!bus.isRunning()) stopped();
        }
    }
    
    private void stopped() {
    	if (stoppedEvent) return;
    	synchronized(stopMutex) {
    		if (stoppedEvent) return;
    		stoppedEvent = true;
    	}
        removeListeners();
        totalCountDown();
    }
   
    private void totalCountDown() {
    	while (getCount() > 0) countDown();
    }
    
    private void removeListeners() {
    	if (removed) return;
    	synchronized(componentListener) {
    		if (removed) return;
    		removed = true;
    	}
    	
    	bus.removeEventListener(IFatalErrorEvent.class, componentListener);
		if (componentIds != null) {
           	synchronized(componentIds) {
	           	for (IToken token : componentIds) {
	           		bus.removeEventListener(IStoppingEvent.class, token, componentListener);
	           		bus.removeEventListener(IStoppedEvent.class, token, componentListener);
	           	}
           	}
		}
    }
    
    
    
    @Override
    public void countDown() {
    	super.countDown();
    	if (getCount() <= 0) removeListeners();
    }

    /**
     * @throws cz.cuni.amis.pogamut.base.component.bus.event.BusAwareCountDownLatch.BusStoppedInterruptedException when the waiting was stopped because some component of the bus stopped
     * @throws PogamutInterruptedException
     */
    @Override
    public void await() throws BusStoppedInterruptedException, PogamutInterruptedException {
        try {
			super.await();
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException(e, this);
		}
        checkBusStop();
    }

    /**
     * @param timeout
     * @param unit
     * @return
     * @throws cz.cuni.amis.pogamut.base.component.bus.event.BusAwareCountDownLatch.BusStoppedInterruptedException when the waiting was stopped because some component of the bus stopped
     * @throws PogamutInterruptedException
     */
    @Override
    public boolean await(long timeout, TimeUnit unit) throws BusStoppedInterruptedException, PogamutInterruptedException {
        boolean val;
		try {
			val = super.await(timeout, unit);
		} catch (InterruptedException e) {
			throw new PogamutInterruptedException(e, this);
		}
        checkBusStop();
        return val;
    }

    protected void checkBusStop() throws BusStoppedInterruptedException {
    	if (stoppedEvent) {
            throw new BusStoppedInterruptedException(this);
        }        
    }
    
    public String toString() {
    	return "BusAwareCountDownLatch";
    }
    
}

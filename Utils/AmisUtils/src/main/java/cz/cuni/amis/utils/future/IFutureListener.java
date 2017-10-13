package cz.cuni.amis.utils.future;

import java.util.EventListener;

public interface IFutureListener<RESULT> extends EventListener {
	
	public void futureEvent(FutureWithListeners<RESULT> source, FutureStatus oldStatus, FutureStatus newStatus);
	
}

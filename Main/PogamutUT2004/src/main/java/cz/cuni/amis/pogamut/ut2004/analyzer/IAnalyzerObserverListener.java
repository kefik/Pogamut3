package cz.cuni.amis.pogamut.ut2004.analyzer;

import java.util.EventListener;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.utils.listener.Listeners;

public interface IAnalyzerObserverListener extends EventListener {

	public void observerAdded(UnrealId botId, IUT2004AnalyzerObserver observer);
	public void observerRemoved(UnrealId botId, IUT2004AnalyzerObserver observer);
	
	public static class ObserverAddedNotifier implements Listeners.ListenerNotifier<IAnalyzerObserverListener> {

		private UnrealId botId;
		private IUT2004AnalyzerObserver observer;
		
		public void setBotId(UnrealId botId) {
			this.botId = botId;
		}

		public void setObserver(IUT2004AnalyzerObserver observer) {
			this.observer = observer;
		}

		@Override
		public Object getEvent() {			
			return new Object() {
				@Override
				public String toString() {
					return "ObserverAdded";
				}
			};
		}

		@Override
		public void notify(IAnalyzerObserverListener listener) {
			listener.observerAdded(botId, observer);
		}
		
	}
	
	public static class ObserverRemovedNotifier implements Listeners.ListenerNotifier<IAnalyzerObserverListener> {

		private UnrealId botId;
		private IUT2004AnalyzerObserver observer;
		
		public void setBotId(UnrealId botId) {
			this.botId = botId;
		}

		public void setObserver(IUT2004AnalyzerObserver observer) {
			this.observer = observer;
		}

		@Override
		public Object getEvent() {			
			return new Object() {
				@Override
				public String toString() {
					return "ObserverRemoved";
				}
			};
		}

		@Override
		public void notify(IAnalyzerObserverListener listener) {
			listener.observerRemoved(botId, observer);
		}
		
	}
	
}

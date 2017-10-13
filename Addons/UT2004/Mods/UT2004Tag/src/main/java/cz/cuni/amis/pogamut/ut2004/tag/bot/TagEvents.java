package cz.cuni.amis.pogamut.ut2004.tag.bot;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ControlMessage;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.TagMessagesTranslator;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagGameEnd;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagGameRunning;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagGameStart;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagMessage;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagPassed;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagPlayerImmunity;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagPlayerScoreChanged;
import cz.cuni.amis.pogamut.ut2004.tag.protocol.messages.TagPlayerStatusChanged;

/**
 * Listens to {@link IWorldView} for {@link TagMessage} descendant events (note that {@link TagMessagesTranslator} must be enabled from the outside to translate
 * {@link ControlMessage} into {@link TagMessage}s).
 * <p><p>
 * 
 * Intended to be subclassed and appropriate methods {@link #tagGameStart(TagGameStart)}, {@link #tagGameEnd(TagGameEnd)}, {@link #tagPlayerImmunity(TagPlayerImmunity)},
 * {@link #tagPlayerScoreChanged(TagPlayerScoreChanged)}, {@link #tagPlayerStatusChanged(TagPlayerStatusChanged)} overridden.
 * <p><p>  
 * 
 * Default state: DISABLED, must be {@link #enableTagEvents()}ed manually in order to translate {@link TagMessage} events into method calls.
 * 
 * @author Jimmy
 */
public class TagEvents {
	
	protected boolean enabled = false;

	protected IWorldView worldView;
	
	protected IWorldEventListener<TagGameStart> tagGameStartListener = new IWorldEventListener<TagGameStart>() {

		@Override
		public void notify(TagGameStart event) {
			tagGameStart(event);
		}
		
	};
	
	protected IWorldEventListener<TagGameRunning> tagGameRunningListener = new IWorldEventListener<TagGameRunning>() {

		@Override
		public void notify(TagGameRunning event) {
			tagGameRunning(event);
		}
		
	};
	
	protected IWorldEventListener<TagGameEnd> tagGameEndListener = new IWorldEventListener<TagGameEnd>() {

		@Override
		public void notify(TagGameEnd event) {
			tagGameEnd(event);
		}
		
	};
	
	protected IWorldEventListener<TagPlayerImmunity> tagPlayerImmunityListener = new IWorldEventListener<TagPlayerImmunity>() {

		@Override
		public void notify(TagPlayerImmunity event) {
			tagPlayerImmunity(event);
		}
		
	};
	
	protected IWorldEventListener<TagPlayerScoreChanged> tagPlayerScoreChangedListener = new IWorldEventListener<TagPlayerScoreChanged>() {

		@Override
		public void notify(TagPlayerScoreChanged event) {
			tagPlayerScoreChanged(event);
		}
		
	};
	
	protected IWorldEventListener<TagPlayerStatusChanged> tagPlayerStatusChangedListener = new IWorldEventListener<TagPlayerStatusChanged>() {

		@Override
		public void notify(TagPlayerStatusChanged event) {
			tagPlayerStatusChanged(event);
		}
		
	};

	protected IWorldEventListener<TagPassed> tagPassedListener = new IWorldEventListener<TagPassed>() {

		@Override
		public void notify(TagPassed event) {
			tagPassed(event);
		}
		
	};
	
	public TagEvents(IWorldView worldView) {
		this.worldView = worldView;
	}
	
	public void enableTagEvents() {
		if (enabled) return;
		enabled = true;
		
		worldView.addEventListener(TagGameStart.class, tagGameStartListener);
		worldView.addEventListener(TagGameRunning.class, tagGameRunningListener);
		worldView.addEventListener(TagGameEnd.class, tagGameEndListener);
		worldView.addEventListener(TagPassed.class, tagPassedListener);
		worldView.addEventListener(TagPlayerImmunity.class, tagPlayerImmunityListener);
		worldView.addEventListener(TagPlayerScoreChanged.class, tagPlayerScoreChangedListener);
		worldView.addEventListener(TagPlayerStatusChanged.class, tagPlayerStatusChangedListener);
	}
	
	public void disableTagEvents() {
		if (!enabled) return;
		enabled = false;
		
		worldView.removeEventListener(TagGameStart.class, tagGameStartListener);
		worldView.removeEventListener(TagGameRunning.class, tagGameRunningListener);
		worldView.removeEventListener(TagGameEnd.class, tagGameEndListener);
		worldView.removeEventListener(TagPassed.class, tagPassedListener);
		worldView.removeEventListener(TagPlayerImmunity.class, tagPlayerImmunityListener);
		worldView.removeEventListener(TagPlayerScoreChanged.class, tagPlayerScoreChangedListener);
		worldView.removeEventListener(TagPlayerStatusChanged.class, tagPlayerStatusChangedListener);
	}
	
	// ================
	// EVENTS TO HANDLE
	// ================
	
	public void tagGameStart(TagGameStart event) {
	}
	
	public void tagGameRunning(TagGameRunning event) {
	}
	
	public void tagGameEnd(TagGameEnd event) {
	}
	
	public void tagPassed(TagPassed event) {		
	}
	
	public void tagPlayerImmunity(TagPlayerImmunity event) {
	}
	
	public void tagPlayerScoreChanged(TagPlayerScoreChanged event) {
	}
	
	public void tagPlayerStatusChanged(TagPlayerStatusChanged event) {
	}
	
}

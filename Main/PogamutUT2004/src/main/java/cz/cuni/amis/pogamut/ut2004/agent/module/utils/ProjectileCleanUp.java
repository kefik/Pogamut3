package cz.cuni.amis.pogamut.ut2004.agent.module.utils;

import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.communication.translator.event.IWorldObjectUpdatedEvent.DestroyWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.react.ObjectEventReact;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectDisappearedEvent;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.IncomingProjectile;

/**
 * Clean-up module for destroying {@link IncomingProjectile} objects within worldview.
 * 
 * Whenever projectile disappears from the view (becomes non-visible), it is immediately destroyed (this class rises {@link DestroyWorldObject} event for it)
 * as bot does not know what happens to projectile afterwards (whether it is still flying or already hit something). If we would not do this,
 * world view would have got littered with in-fact-non-existing {@link IncomingProjectile} instances leaking memory. 
 * 
 * Automatically used by {@link UT2004Bot}.
 * 
 * You may at any time {@link ProjectileCleanUp#enable()} or {@link ProjectileCleanUp#disable()} this clean-up behavior (it is enabled by default).
 * 
 * @author Jimmy
 */
public class ProjectileCleanUp {
	
	private UT2004Bot bot;
	
	private ObjectEventReact<IncomingProjectile, WorldObjectDisappearedEvent<IncomingProjectile>> destroyProjectile;

	public ProjectileCleanUp(UT2004Bot bot) {
		this.bot = bot;
		destroyProjectile = new ObjectEventReact<IncomingProjectile, WorldObjectDisappearedEvent<IncomingProjectile>>(IncomingProjectile.class, WorldObjectDisappearedEvent.class, bot.getWorldView()) {

			@Override
			protected void react(WorldObjectDisappearedEvent<IncomingProjectile> event) {
				ProjectileCleanUp.this.bot.getWorldView().notifyAfterPropagation(
						new IWorldObjectUpdatedEvent.DestroyWorldObject(event.getObject(), event.getSimTime())
				);				
			}
			
		};
		destroyProjectile.enable();
	}
	
	public void enable() {
		destroyProjectile.enable();
	}
	
	public void disable() {
		destroyProjectile.disable();
	}

}

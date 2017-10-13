package cz.cuni.amis.pogamut.ut2004.examples.responsivebot;

import cz.cuni.amis.pogamut.base.communication.worldview.IWorldView;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.AnnotationListenerRegistrator;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.EventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectClassEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectClassListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectEventListener;
import cz.cuni.amis.pogamut.base.communication.worldview.listener.annotation.ObjectListener;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObject;
import cz.cuni.amis.pogamut.base.communication.worldview.object.IWorldObjectEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectDestroyedEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectFirstEncounteredEvent;
import cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent;
import cz.cuni.amis.pogamut.base.utils.guice.AgentScoped;
import cz.cuni.amis.pogamut.base.utils.math.DistanceUtils;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectAppearedEvent;
import cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectDisappearedEvent;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004BotModuleController;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Initialize;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotDamaged;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.BotKilled;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Bumped;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Player;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004BotRunner;
import cz.cuni.amis.pogamut.ut2004.utils.UnrealUtils;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * Example of Simple Pogamut bot, that can listen to some events coming from
 * the game engine and respond to them appropriately. The logic of the bot is
 * completely event driven. 
 * 
 * <p><p> 
 * Notice that we're using a special annotations
 * for various methods, i.e., for the method {@link ResponsiveBot#bumped(Bumped)}
 * the annotation {@link EventListener}, for the method {@link ResponsiveBot#playerAppeared(WorldObjectAppearedEvent)}
 * we're using {@link ObjectClassEventListener}, etc. You may perceive is as a bit magic as
 * some methods are called as a response to some event, e.g., the method {@link ResponsiveBot#bumped(Bumped)}
 * is called whenever GameBots2004 sends a message {@link Bumped} to the bot. 
 *
 * <p><p>
 * How is this possible? 
 * 
 * <p><p> 
 * Well, the {@link UT2004BotModuleController}
 * is using {@link AnnotationListenerRegistrator} that introspects (via Java
 * Reflection API) the methods declared by the {@link ResponsiveBot} looking for
 * annotations: {@link EventListener}, {@link ObjectClassEventListener},
 * {@link ObjectClassListener}, {@link ObjectEventListener} or {@link ObjectListener}
 * (I recommend you to read javadoc for all of them) automatically registering a
 * listener inside {@link ResponsiveBot#getWorldView()} using one of the
 * "addListener" methods (e.g. {@link EventListener} annotated method is recalled
 * from listener added via {@link IWorldView#addEventListener(java.lang.Class, cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener)}.
 * It provides you with a simple way how to create methods that reacts on certain events inside the game.
 * 
 * <p><p>
 * WARNING: these annotations works only in THIS class only. If you want other of your classes to have the
 * same option, you will have to instantiate {@link AnnotationListenerRegistrator} for yourself
 * within that class.
 * 
 * <p><p>
 * We advise you to read through all comments carefully and try to understand
 * when {@link EventListener} suffices and when you need to use one
 * of {@link ObjectClassEventListener} /
 * {@link ObjectClassListener} / {@link ObjectEventListener} / {@link ObjectListener}s.
 * 
 * <p><p>
 * The trick is that some messages from GB2004 are {@link IWorldEvent}s only and some of them are {@link IWorldObject}s as well.
 * For listening {@link IWorldEvent}s only you must use {@link EventListener}, for listening to object updates,
 * you must use one of {@link ObjectClassEventListener} / {@link ObjectClassListener} / {@link ObjectEventListener} / {@link ObjectListener}s.
 * 
 * <p><p>
 * We recommend you to run the bot on DM-TrainingDay map.
 * 
 * <p><p>
 * Start the bot and run to it. Note that whenever the bot sees you (you enter bot's field of view}
 * {@link ResponsiveBot#playerAppeared(cz.cuni.amis.pogamut.base3d.worldview.object.event.WorldObjectAppearedEvent)} will be triggered
 * and the bot will greet you.
 * 
 * <p><p>
 * Then try to approach very close to the bot and it will ask you "what do you want". See {@link ResponsiveBot#playerUpdated(cz.cuni.amis.pogamut.base.communication.worldview.object.event.WorldObjectUpdatedEvent) }
 * event listener method.
 * 
 * <p><p>
 * Check out comments inside {@link ResponsiveBot#gb2004BatchEnd(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.EndMessage)} message.
 *
 * @author Rudolf Kadlec aka ik
 * @author Jakub Gemrot aka Jimmy
 */
@AgentScoped
public class ResponsiveBot extends UT2004BotModuleController {

    /**
     * Listener called when someone/something bumps into the bot. The bot
     * responds by moving in the opposite direction than the bump come from.
     * 
     * <p><p> 
     * We're using {@link EventListener} here that is registered by the {@link AnnotationListenerRegistrator}
     * to listen for {@link Bumped} events.
     * 
     * <p><p>
     * Notice that {@link Bumped} is {@link IWorldEvent} only, it's not {@link IWorldObject},
     * thus we're using {@link EventListener}.
     */
    @EventListener(eventClass = Bumped.class)
    protected void bumped(Bumped event) {
        // schema of the vector computations
        //
        //  e<->a<------>t
        //  |   |   v    |
        //  |   |        target - bot will be heading there
        //  |   getLocation()
        //  event.getLocation()

        Location v = event.getLocation().sub(bot.getLocation()).scale(5);
        Location target = bot.getLocation().sub(v);

        // make the bot to go to the computed location while facing the bump source
        move.strafeTo(target, event.getLocation());
    }

    /**
     * Listener called when a player appears. 
     * 
     * <p><p> 
     * We're using {@link ObjectClassEventListener} here that is registered by the {@link AnnotationListenerRegistrator} to
     * listen on all {@link WorldObjectAppearedEvent} that happens on any object
     * of the class {@link Player}. 
     * 
     * <p><p>
     * I.e., whenever the GameBots2004 sends an
     * update about arbitrary {@link Player} object in the game notifying us that the
     * player has become visible (it's {@link Player#isVisible()} is switched to
     * true and the {@link WorldObjectAppearedEvent} is generated), this method
     * is called.
     * 
     * <p><p>
     * Notice that {@link Player} implements {@link IWorldObject} thus you CANNOT use
     * {@link EventListener} to catch events that updates {@link Player} objects.
     */
    @ObjectClassEventListener(eventClass = WorldObjectAppearedEvent.class, objectClass = Player.class)
    protected void playerAppeared(WorldObjectAppearedEvent<Player> event) {
        // greet player when he appears
        body.getCommunication().sendGlobalTextMessage("Hello " + event.getObject().getName() + "!");
    }
    /**
     * Flag indicating whether the player was also close to the bot last time it
     * was updated.
     */
    protected boolean wasCloseBefore = false;

    /**
     * Listener called each time a player is updated. 
     * 
     * <p><p> 
     * Again, we're using {@link ObjectClassEventListener}
     * that is registered by the {@link AnnotationListenerRegistrator} to listen
     * on all {@link WorldObjectUpdatedEvent} that happens on any object of the
     * class {@link Player}. 
     * 
     * <p><p>
     * I.e., whenever the GameBots2004 sends an update
     * about arbitrary {@link Player} in the game notifying us that some
     * information about the player has changed (the {@link WorldObjectUpdatedEvent}
     * is generated), this method is called.
     * 
     * <p><p>
     * Again, {@link Player} implements {@link IWorldObject}, thus you CANNOT use 
     * {@link EventListener} annotation to check for events regarding {@link Player}
     * objects.
     */
    @ObjectClassEventListener(eventClass = WorldObjectUpdatedEvent.class, objectClass = Player.class)
    protected void playerUpdated(WorldObjectUpdatedEvent<Player> event) {
        // Check whether the player is closer than 5 bot diameters.
        // Notice the use of the UnrealUtils class.
        // It contains many auxiliary constants and methods.
        Player player = event.getObject();
        // First player objects are received in HandShake - at that time we don't have Self message yet or players location!!
        if (player.getLocation() == null || info.getLocation() == null) {
            return;
        }
        if (player.getLocation().getDistance(info.getLocation()) < (UnrealUtils.CHARACTER_COLLISION_RADIUS * 10)) {
            // If the player wasn't close enough the last time this listener was called,
            // then ask him what does he want.
            if (!wasCloseBefore) {
                body.getCommunication().sendGlobalTextMessage("What do you want " + player.getName() + "?");
                // Set proximity flag to true.
                wasCloseBefore = true;
            }
        } else {
            // Otherwise set the proximity flag to false.
            wasCloseBefore = false;
        }
    }
    /**
     * Listener that is manually created and manually hooked to the {@link ResponsiveBot#getWorldView()}
     * via {@link IWorldView#addEventListener(Class, IWorldEventListener)}
     * method inside {@link ResponsiveBot#prepareBot(UT2004Bot)}.
     * 
     * <p><p>
     * Note, that this is old/manual way how to add listeners on various events that are rised
     * within the {@link IWorldView} (obtainable from {@link UT2004Bot#getWorldView()} via
     * <code>bot.getWorldView()</code> or simply accessing {@link UT2004BotModuleController#world} field).
     * 
     * <p><p>
     * Such event listener MUST BE registered via some method of {@link IWorldView} offers.
     * This particular listener is registered inside {@link ResponsiveBot#prepareBot(cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot)}.
     * Note that you can add/remove any listener during runtime.
     */
    IWorldEventListener<BotDamaged> botDamagedListener = new IWorldEventListener<BotDamaged>() {

        @Override
        public void notify(BotDamaged event) {
            // the bot was injured - let's move around the level and hope that we will find a health pack
            // note that we have to acquire "SECOND" nearest navpoint, as the first one is the navpoint we're standing at
            NavPoint secondNav = DistanceUtils.getSecondNearest(getWorldView().getAll(NavPoint.class).values(), info.getLocation());
            // always check even for improbable conditions
            if (secondNav == null) {
                // try to locate some navpoint
                move.turnVertical(30);
            } else {
                // move to it
                move.moveTo(secondNav);
            }
        }
    };

    /**
     * Example usage of {@link ObjectListener} that will listen on every change
     * / event that will be raised on the concrete {@link IWorldObject}, in this
     * case on the {@link GameInfo}. 
     * 
     * <p><p>
     * Notice that we have to specify which ID
     * class the world object is using and have explicit representation of it's
     * id - note that this is totally unsuitable for any dynamic IDs, such as
     * NavPoint ids, etc... you will probably never use this annotation.
     * 
     * <p><p>
     * See implementations of {@link IWorldObjectEvent}, which are
     * {@link WorldObjectFirstEncounteredEvent}, {@link WorldObjectAppearedEvent},
     * {@link WorldObjectUpdatedEvent}, {@link WorldObjectDisappearedEvent}
     * and {@link WorldObjectDestroyedEvent}. All such events may be possibly
     * caught by this {@link ObjectListener} annotated method.
     *
     * @param info
     */
    @ObjectListener(idClass = UnrealId.class, objectId = "GameInfoId")
    public void gameInfo1(IWorldObjectEvent<GameInfo> gameInfoEvent) {
        log.warning("GAME INFO EVENT =1=: " + gameInfoEvent);
    }

    /**
     * Example usage of {@link ObjectEventListener} that will listen on SPECIFIC
     * event that is raised on the concrete {@link IWorldObject}. As is the case
     * of {@link ResponsiveBot#gameInfo1(IWorldObjectEvent)}, you will probably
     * never use this.
     *
     * @param gameInfoEvent
     */
    @ObjectEventListener(idClass = UnrealId.class, objectId = "GameInfoId", eventClass = WorldObjectUpdatedEvent.class)
    public void gameInfo2(WorldObjectUpdatedEvent<GameInfo> gameInfoEvent) {
        log.warning("GAME INFO EVENT =2=: " + gameInfoEvent);
    }

    /**
     * Example usage of {@link ObjectClassListener} notice the difference
     * between this listener and {@link ObjectClassEventListener} that is used
     * on {@link ResponsiveBot#playerAppeared(WorldObjectAppearedEvent)}.
     * 
     * <p><p>
     * This method will receive ALL events that are raised on any {@link Player}
     * object whereas {@link ResponsiveBot#playerAppeared(WorldObjectAppearedEvent)}
     * will receive only {@link WorldObjectAppearedEvent}.
     * 
     * <p><p>
     * See implementations of {@link IWorldObjectEvent}, which are
     * {@link WorldObjectFirstEncounteredEvent}, {@link WorldObjectAppearedEvent},
     * {@link WorldObjectUpdatedEvent}, {@link WorldObjectDisappearedEvent}
     * and {@link WorldObjectDestroyedEvent}. All such events may be possibly
     * caught by this {@link ObjectListener} annotated method.
     *
     * @param playerEvent
     */
    @ObjectClassListener(objectClass = Player.class)
    public void playerEvent(IWorldObjectEvent<Player> playerEvent) {
        log.warning("PLAYER EVENT: " + playerEvent);        
    }
    
    /**
     * If you uncomment {@link EventListener} annotation below this comment, this message
     * will be triggered every time the GB2004 sends EndMessage to you. It will (more-less)
     * act the same way as {@link ResponsiveBot#logic()} method.
     * @param event 
     */
    //@EventListener(eventClass=EndMessage.class)
    public void gb2004BatchEnd(EndMessage event) {
        log.info("EndMessage received!");
    }

    /**
     * Initialize all necessary variables here, before the bot actually receives
     * anything from the environment + hook up your custom listener.
     */
    @Override
    public void prepareBot(UT2004Bot bot) {
        // register the botDamagedListener that we have previously created
        getWorldView().addEventListener(BotDamaged.class, botDamagedListener);
    }

    /**
     * Here we can modify initialize command for our bot if we want to.
     *
     * @return
     */
    @Override
    public Initialize getInitializeCommand() {
        return new Initialize();
    }

    /**
     * The bot is initialized in the environment - a physical representation of
     * the bot is present in the game.
     *
     * @param config information about configuration
     * @param init information about configuration
     */
    @Override
    public void botFirstSpawn(GameInfo gameInfo, ConfigChange config, InitedMessage init, Self self) {
        // notify the world (i.e., send message to UT2004) that the bot is up and running
        body.getCommunication().sendGlobalTextMessage("I am alive!");
    }

    /**
     * This method is called only once right before actual logic() method is
     * called for the first time. Similar to {@link ResponsiveBot#botFirstSpawn(cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage, cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.Self)}.
     */
    @Override
    public void beforeFirstLogic() {
    }

    /**
     * Main method that controls the bot - makes decisions what to do next.
     * <p><p> Notice that the method is empty as this bot is completely
     * event-driven.
     */
    @Override
    public void logic() throws PogamutException {
    }

    /**
     * Called each time our bot die. Good for reseting all bot state dependent
     * variables.
     *
     * @param event
     */
    @Override
    public void botKilled(BotKilled event) {
    }

    /**
     * This method is called when the bot is started either from IDE or from
     * command line.
     *
     * @param args
     */
    public static void main(String args[]) throws PogamutException {
        // wrapped logic for bots executions, suitable to run single bot in single JVM    	
        new UT2004BotRunner(ResponsiveBot.class, "ResponsiveBot").setMain(true).startAgent();
    }
}

/*
 * Copyright (C) 2014 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.amis.pogamut.ut2004.navigation.evaluator.bot;

import cz.cuni.amis.pogamut.base.agent.navigation.IPathPlanner;
import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEventListener;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.IUT2004Navigation;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.NavigationState;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Move;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.GameInfo;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.InitedMessage;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.LocationUpdate;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.utils.flag.FlagListener;
import java.util.logging.Level;

/**
 * Bot used for gathering data about jumps.
 *
 * @author Bogo
 */
public class JumpInspectingBot extends EvaluatingBot {

    /**
     * Phases of the bot.
     */
    public enum Phase {

        ToStart, OnStart, Running, Jumping, AfterJump, AtEnd
    }

    private IWorldEventListener<LocationUpdate> listener;

    Phase phase = Phase.ToStart;

    double jumpDistance = 400;

    NavPoint startSpot = null;
    Location endSpot = null;
    int step = 0;
    boolean inEvaluation = false;
    boolean jumpedAlready = false;

    boolean inJump = false;

    Location beforeJumpMax = null;
    Location afterJumpMax = null;

    Location beforeJumpMin = null;
    Location afterJumpMin = null;

	private IPathPlanner myPathPlanner;

	private IUT2004Navigation myNavigation;


    @Override
    protected void initializePathFinding(UT2004Bot bot) {
    	super.initializePathFinding(bot);
        myPathPlanner = NavigationFactory.getPathPlanner(this, bot, "navMesh");
        myNavigation = NavigationFactory.getNavigation(this, bot, "acc");
    }

    double zOnTheGround = -78.15;
    int jumpCount = 0;

    double maxJump = Double.NEGATIVE_INFINITY;

    boolean turned = false;

    @Override
    public void beforeFirstLogic() {
        super.beforeFirstLogic();

    }

    @Override
    public void botInitialized(GameInfo gameInfo, ConfigChange currentConfig, InitedMessage init) {
        super.botInitialized(gameInfo, currentConfig, init);
        bot.getLog().setLevel(Level.WARNING);

        move.getLog().setLevel(Level.WARNING);

        listener = new IWorldEventListener<LocationUpdate>() {

            public void notify(LocationUpdate t) {
                //log.warning("Accepted location update.");
                LocationUpdate locationUpdate = t;
                //log.warning("LOCATION UPDATE - L: {0}, V: {1}, R: {2}, SimTime: {3}", new Object[]{locationUpdate.getLoc(), locationUpdate.getVel(), locationUpdate.getRot(), locationUpdate.getSimTime()});

                Phase lastPhase = phase;
                switch (lastPhase) {
                    case Running:
                        if (inJump) {
                            log.warning("LOCATION UPDATE - L: {0}, V: {1}, R: {2}, SimTime: {3}", new Object[]{locationUpdate.getLoc(), locationUpdate.getVel(), locationUpdate.getRot(), locationUpdate.getSimTime()});
                        }
                        if (inJump && !isOnTheGround(locationUpdate.getLoc())) {
                            phase = Phase.Jumping;
                        }
                        break;
                    case Jumping:
                        if (locationUpdate.getLoc().z > maxJump) {
                            maxJump = locationUpdate.getLoc().z;
                        }
                        log.warning("LOCATION UPDATE - L: {0}, V: {1}, R: {2}, SimTime: {3}", new Object[]{locationUpdate.getLoc(), locationUpdate.getVel(), locationUpdate.getRot(), locationUpdate.getSimTime()});
                        if (isOnTheGround(locationUpdate.getLoc())) {
                            phase = Phase.AfterJump;
                            inJump = false;
                        }
                }

                switch (phase) {
                    case Running:
                        beforeJumpMax = locationUpdate.getLoc();
                        break;
                    case Jumping:
                        if (lastPhase == Phase.Running) {
                            beforeJumpMin = locationUpdate.getLoc();
                        }

                        afterJumpMin = locationUpdate.getLoc();
                        break;
                    case AfterJump:

                        if (lastPhase == Phase.Jumping) {
                            afterJumpMax = locationUpdate.getLoc();
                            //evaluate

                            double jumpLengthMax = afterJumpMax.getDistance2D(beforeJumpMax);
                            double jumpLengthMin = afterJumpMin.getDistance2D(beforeJumpMin);

                            log.log(Level.WARNING, "JUMP FINISHED: MAX: {0} MIN: {1}", new Object[]{jumpLengthMax, jumpLengthMin});
                            log.log(Level.WARNING, "Before MAX loc: {0}", beforeJumpMax);
                            log.log(Level.WARNING, "After MAX loc: {0}", afterJumpMax);
                            log.log(Level.WARNING, "Before MIN loc: {0}", beforeJumpMin);
                            log.log(Level.WARNING, "After MIN loc: {0}", afterJumpMin);
                        }
                }

            }
        };

        world.addEventListener(LocationUpdate.class, listener);

        myNavigation.addStrongNavigationListener(new FlagListener<cz.cuni.amis.pogamut.ut2004.agent.navigation.NavigationState>() {

            public void flagChanged(NavigationState changedValue) {
                if (changedValue == NavigationState.TARGET_REACHED) {
                    if (phase == Phase.ToStart && bot.getLocation().getDistance2D(startSpot.getLocation()) < 100) {
                        phase = Phase.OnStart;
                    }
                }
            }
        });

        String firstSpotId = "DM-1on1-Idoma.InventorySpot11";
        String secondSpotId = "DM-1on1-Roughinery.InventorySpot123";

        startSpot = navPoints.getNavPoint(UnrealId.get(firstSpotId));
        endSpot = new Location(startSpot.getLocation()).setX(600);

    }

    @Override
    public void logic() {
        // mark that another logic iteration has began
        //log.info("--- Logic iteration ---");

        if (bot.getVelocity().size() < 10) {

            switch (phase) {
                case ToStart:
                    body.getAction().respawn(startSpot);
                    phase = Phase.OnStart;
                    break;
                case OnStart:
                    if (info.atLocation(startSpot)) {
                        zOnTheGround = bot.getLocation().z;
                        act.act(new Move(endSpot, null, null, endSpot));
                        phase = Phase.Running;
                    }
                    break;
                default:
                    phase = Phase.ToStart;
                    break;
            }
        }

        if (phase == Phase.Running && isOnTheGround(bot.getLocation()) && bot.getLocation().getDistance2D(startSpot.getLocation()) > jumpDistance) {
            move.jump(true, 0.39, 680);
            //move.jump(250);
            log.warning("JUMP INITIATED");
            inJump = true;
            ++jumpCount;
        }

//            NavPoint current = DistanceUtils.getNearest(navPoints.getNavPoints().values(), bot.getLocation());
//
//            if (step == 0) {
//                if (current == null) {
//                    //Do nothing
//                } else if (current.equals(startSpot)) {
//                    inEvaluation = true;
//                    move.turnTo(endSpot);
//                } else {
//                    move.turnTo(startSpot);
//                }
//
//            } else if (step > 3) {
//                if (current == null) {
//                    myNavigation.navigate(startSpot);
//                } else if (current.equals(startSpot)) {
//                    myNavigation.navigate(endSpot);
//                } else {
//                    myNavigation.navigate(startSpot);
//                }
//
//            }
//
//        }
//
//        ++step;
//
//        if (inEvaluation && step > 8 && isOnTheGround() && bot.getVelocity().size() > 400 && !jumpedAlready) {
//            log.warning("Sending JUMP command.");
//            log.log(Level.WARNING, "INFO: Velocity: {0}\n\tPosition: {1}", new Object[]{bot.getVelocity().size(), bot.getLocation()});
//            move.jump();
//            jumpedAlready = true;
//            ++jumpCount;
//        }
        if (jumpCount > 30) {
            log.log(Level.WARNING, "MAX JUMP: {0}", maxJump);
            log.info("No more jumps to jump, we are finished...");
            isCompleted = true;
            new Runnable() {
                public void run() {
                    bot.stop();
                }
            }.run();
        }

    }

    private boolean isOnTheGround(Location location) {
        return location.z <= zOnTheGround + 1;
    }
}

package cz.cuni.amis.pogamut.ut2004.bot.command;

import java.util.logging.Logger;

import javax.vecmath.Vector3d;

import cz.cuni.amis.pogamut.base3d.worldview.object.ILocated;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.AddRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Configuration;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.FastTrace;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.RemoveRay;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbcommands.Trace;

/**
 * This command module provides basic ray tracing control. Note that you will
 * need to register listeners in your code in order to receive answers to
 * commands issued by this module.
 * 
 * For fastTrace register listener for FastTraceResponse() class. For trace
 * register listener for TraceResponse() class. For autoTracing rays register
 * listener for AutoTraceRay() class.
 * 
 * @author Knight
 */
public class SimpleRayCasting extends BotCommands {

	/**
	 * Sends a ray from actual bot location to desired location. To receive
	 * response register listener for FastTraceResponse() messages and check Id.
	 * 
	 * This ray collides only with level geometry (not with dynamic items -
	 * other players, items).
	 * 
	 * @param id
	 *            user determined id of the ray (use to match response)
	 * @param to
	 *            end location of this ray
	 */
	public void fastTrace(String id, ILocated to) {
		agent.getAct().act(new FastTrace().setId(id).setTo(to.getLocation()));
	}

	/**
	 * Sends a ray from desired location to desired location. To receive
	 * response register listener for FastTraceResponse() messages and check Id.
	 * 
	 * This ray collides only with level geometry (not with dynamic items -
	 * other players, items).
	 * 
	 * @param id
	 *            user determined id of the ray (use to match response)
	 * @param from
	 *            start location of this ray
	 * @param to
	 *            end location of this ray
	 */
	public void fastTrace(String id, ILocated from, ILocated to) {
		agent.getAct().act(
				new FastTrace().setId(id).setFrom(from.getLocation()).setTo(
						to.getLocation()));
	}

	/**
	 * Sends a ray from actual bot location to desired location. To receive
	 * response register listener for TraceResponse() messages and check Id.
	 * 
	 * Normaly this ray collides only with level geometry. If bTraceActors is
	 * set to true it will collide also with dynamic items - other players and
	 * items or vehicles in the map.
	 * 
	 * @param id
	 *            user determined id of the ray (use to match response)
	 * @param to
	 *            end location of this ray
	 */
	public void trace(String id, ILocated to, boolean bTraceActors) {
		agent.getAct().act(
				new Trace().setId(id).setTo(to.getLocation()).setTraceActors(
						bTraceActors));
	}

	/**
	 * Sends a ray from desired location to desired location. To receive
	 * response register listener for TraceResponse() messages and check Id.
	 * 
	 * Normaly this ray collides only with level geometry. If bTraceActors is
	 * set to true it will collide also with dynamic items - other players and
	 * items or vehicles in the map.
	 * 
	 * @param id
	 *            user determined id of the ray (use to match response)
	 * @param from
	 *            start location of this ray
	 * @param to
	 *            end location of this ray
	 */
	public void trace(String id, ILocated from, ILocated to,
			boolean bTraceActors) {
		agent.getAct().act(
				new Trace().setId(id).setFrom(from.getLocation()).setTo(
						to.getLocation()).setTraceActors(bTraceActors));
	}

	/**
	 * Adds a new ray to auto ray tracing rays set. If automatic ray tracing
	 * will be enabled, each ray in this set will be cast (natively in UT) each
	 * synchronous batch and the response will come through ATR synchronous
	 * GameBots messsages (in Pogamut class AutoTraceRay() class). Each ray has
	 * its Id used to change its parameters, its direction, lenght and three
	 * boolean variables. Each ray is casted from actual bot location and this
	 * cannot be changed.
	 * 
	 * To start automatic ray casting (each synchronous batch) use command
	 * enableAutoTracign() or send INIT message with proper parameters
	 * (AutoTrace true).
	 * 
	 * @param id
	 *            String id of this ray, used to change its parameters or delete
	 *            it
	 * @param direction
	 *            vector direction of this ray (1,0,0) for straight ahead,
	 *            (0,1,0) for 90 degrees right, etc. The vector does not have to
	 *            be normalized.
	 * @param length
	 *            length of the ray in UT units. One character in UT is
	 *            approximately 200 UT units long.
	 * @param bFastTrace
	 *            true if we want to use FastTrace function for this ray cast
	 *            (faster, but only world geometry can be traced)
	 * @param bFloorCorrection
	 *            if true the rays direction will be adjusted according to floor
	 *            normal. So if we will climb a hill, the rays will adjust to
	 *            the steepnes of the hill.
	 * @param bTraceActors
	 *            if true and bFastTrace set to true, also dynamic items in the
	 *            map will be traced (other players and items)
	 */
	public void addAutoTraceRay(String id, Vector3d direction, int length,
			boolean bFastTrace, boolean bFloorCorrection, boolean bTraceActors) {
		AddRay newRay = new AddRay();
		newRay.setId(id).setDirection(direction).setLength(length)
				.setFastTrace(bFastTrace).setFloorCorrection(bFloorCorrection)
				.setTraceActors(bTraceActors);
		agent.getAct().act(newRay);
	}

	/**
	 * To change the ray it is sufficient to call addAutoTraceRay function with
	 * proper Id. This function is here to tell this information.
	 * 
	 * @param id
	 *            String id of this ray, used to change its parameters or delete
	 *            it
	 * @param direction
	 *            vector direction of this ray (1,0,0) for straight ahead,
	 *            (0,1,0) for 90 degrees right, etc. The vector does not have to
	 *            be normalized.
	 * @param length
	 *            length of the ray in UT units. One character in UT is
	 *            approximately 200 UT units long.
	 * @param bFastTrace
	 *            true if we want to use FastTrace function for this ray cast
	 *            (faster, but only world geometry can be traced)
	 * @param bFloorCorrection
	 *            if true the rays direction will be adjusted according to floor
	 *            normal. So if we will climb a hill, the rays will adjust to
	 *            the steepnes of the hill.
	 * @param bTraceActors
	 *            if true and bFastTrace set to true, also dynamic items in the
	 *            map will be traced (other players and items)
	 */
	public void changeAutoTraceRay(String id, Vector3d direction, int length,
			boolean bFastTrace, boolean bFloorCorrection, boolean bTraceActors) {
		this.addAutoTraceRay(id, direction, length, bFastTrace,
				bFloorCorrection, bTraceActors);
	}

	/**
	 * This command removes all existing auto trace rays and adds a default auto
	 * trace rays set. This set consists of three rays: first ray with Id
	 * StraghtAhead (direction (1,0,0), length 250 UT units), second ray with Id
	 * 45toLeft (direction (1,-1,0), length 200 UT units) and third ray with Id
	 * 45toRight (direction (1,1,0), length 200 UT units). All rays will be set
	 * with FastTrace to false, TraceActors to false and FloorCorrection to
	 * false.
	 * 
	 * To start automatic ray casting (each synchronous batch) use command
	 * enableAutoTracign().
	 * 
	 */
	public void addDefaultAutoTraceRays() {
		agent.getAct().act(new AddRay().setId("Default"));
	}

	/**
	 * Remove one autoTraceRay with desired id from the list. The ray will be
	 * deleted And no longer visualized in the environment.
	 * 
	 * @param id
	 *            id of the ray we want to delete
	 */
	public void removeAutoTraceRay(String id) {
		agent.getAct().act(new RemoveRay().setId(id));
	}

	/**
	 * Removes all autoTraceRays. The rays will be deleted and no longer
	 * visualized in the environment.
	 * 
	 */
	public void removeAllAutoTraceRays() {
		agent.getAct().act(new RemoveRay().setId("All"));
	}

	/**
	 * Enables auto trace rays. This means that each time GameBots synchronous
	 * batch arrives, the rays will be casted and the results will be a part of
	 * this synchronous batch (ATR messages, in Pogamut AutoTraceRay() class).
	 * To get these results register listener to AutoTraceRays() class.
	 * 
	 */
	public void enableAutoTracing() {
		agent.getAct().act(new Configuration().setAutoTrace(true));
	}

	/**
	 * Disables auto trace rays. This means that auto trace rays messages
	 * (AutoTraceRay() class) will no longer be a part of GameBots synchronous
	 * batch and the rays won't be casted. However the rays won't be deleted.
	 * 
	 */
	public void disableAutoTracing() {
		agent.getAct().act(new Configuration().setAutoTrace(false));
	}

	/**
	 * Constructor. Setups the command module based on given agent and logger.
	 * 
	 * @param agent
	 *            AbstractUT2004Bot we will send commands for
	 * @param log
	 *            Logger to be used for logging runtime/debug info.
	 */
	public SimpleRayCasting(UT2004Bot agent, Logger log) {
		super(agent, log);
	}
}

package cz.cuni.amis.pogamut.ut2004.hideandseek.observer;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry.LevelGeometryModule;
import cz.cuni.amis.pogamut.ut2004.agent.navigation.levelGeometry.RayCastResult;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.PlayerMessage;
import cz.cuni.amis.pogamut.ut2004.hideandseek.protocol.HSGameConfig;
import cz.cuni.amis.pogamut.ut2004.hideandseek.server.HSBotRecord;
import cz.cuni.amis.utils.maps.LazyMap;

public class HSObserverGeom {

	private LevelGeometryModule level;
	private HSGameConfig config;
	
	private List<HSBotRecord<PlayerMessage>> players;
	private HSBotRecord<PlayerMessage> seeker;
	
	private Map<PlayerMessage, Double> visibleTimes = new LazyMap<PlayerMessage, Double>() {

		@Override
		protected Double create(PlayerMessage key) {
			return 0.0d;
		}
		
	};
	
	private Set<HSBotRecord<PlayerMessage>> spotted = new HashSet<HSBotRecord<PlayerMessage>>();
	
	public HSObserverGeom(LevelGeometryModule level, HSGameConfig config) {
		this.level = level;
		this.config = config;
	}
	
	public void setPlayers(List<HSBotRecord<PlayerMessage>> players) {
		this.players = players;
	}
	
	public void setSeeker(HSBotRecord<PlayerMessage> seeker) {
		this.seeker = seeker;
	}
		
	public void tick(double timeDeltaSecs) {
		
		PlayerMessage seeker = this.seeker.getPlayer();
		
		System.out.println("================================");
		System.out.println("SEEKER: " + seeker.getLocation());
		System.out.println("  +-- EYE " + seeker.getRotation().toLocation().setZ(0).getNormalized());
		
		for (HSBotRecord<PlayerMessage> record : players) {
			if (record == this.seeker) continue;
			PlayerMessage player = record.getPlayer();
			
			if (player.getLocation().getDistance(seeker.getLocation()) < 100) {
				// CONSIDER VISIBLE
				System.out.println("NEAR: " + player.getId().getStringId() + " at " + player.getLocation());				
				visible(record, timeDeltaSecs);
				continue;
			}
			
			RayCastResult raycast = level.getLevelGeometry().rayCast(seeker.getLocation(), player.getLocation());
			
			if (raycast.isHit()) {
				// NOT VISIBLE
				notVisible(record, timeDeltaSecs);
				continue;
			}

			// POSSIBLY VISIBLE
			// => FOV check (170deg,170deg)?
			
			System.out.println("POSSIBLE: " + player.getId().getStringId() + " at " + player.getLocation());
			
			Location seekerEye = seeker.getRotation().toLocation().setZ(0).getNormalized();				
			Location toPlayer = player.getLocation().sub(seeker.getLocation()).setZ(0).getNormalized();
			
			System.out.println("  +-- toPlayer " + toPlayer);
			
			double angle = Math.acos(seekerEye.dot(toPlayer));
			double angleDeg = (angle / Math.PI * 180);			
			
			System.out.println("  +-- ANGLE: " + angleDeg);
				
			if (angleDeg >= -85 && angleDeg <= 85) {
				// VISIBLE
				visible(record, timeDeltaSecs);
			} else {
				// NOT VISIBLE
				System.out.println(" +-- NOT IN FOV");
				notVisible(record, timeDeltaSecs);
			}
			
		}
		
	}

	

	private void visible(HSBotRecord<PlayerMessage> record, double timeDeltaSecs) {
		double time = 0;
		if (visibleTimes.containsKey(record.getPlayer())) {
			time = visibleTimes.get(record.getPlayer()) + timeDeltaSecs;
			time = visibleTimes.put(record.getPlayer(), time);
		} else {
			visibleTimes.put(record.getPlayer(), 0.0d);
		}
		
		System.out.println("  +-- VISIBLE " + time + " sec");
		
		if (time*1000 >= config.getSpotTimeMillis()) {
			System.out.println("  +-- SPOTTED!!!");
			spotted.add(record);
		}
	}
	
	private void notVisible(HSBotRecord<PlayerMessage> record, double timeDeltaSecs) {
		double time = 0;
		if (!visibleTimes.containsKey(record.getPlayer())) return;
		
		time = visibleTimes.get(record.getPlayer()) - timeDeltaSecs;
		if (time < 0) {
			visibleTimes.remove(record.getPlayer());
			System.out.println("  +-- NOT VISIBLE (removed)");
		} else {
			visibleTimes.put(record.getPlayer(), time);
			System.out.println("  +-- NOT VISIBLE (left " + time + " sec)");
		}
	}

	public Set<HSBotRecord<PlayerMessage>> getSpotted() {
		return spotted;
	}
	
}

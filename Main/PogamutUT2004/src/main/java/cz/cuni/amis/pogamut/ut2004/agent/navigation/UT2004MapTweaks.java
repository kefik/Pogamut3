package cz.cuni.amis.pogamut.ut2004.agent.navigation;

import java.util.List;

import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.pogamut.ut2004.agent.module.sensor.NavigationGraphBuilder;
import cz.cuni.amis.pogamut.ut2004.bot.impl.UT2004Bot;
import cz.cuni.amis.utils.ExceptionToString;
import cz.cuni.amis.utils.maps.HashMapList;

public class UT2004MapTweaks {

	public static interface IMapTweak {
		public void tweak(NavigationGraphBuilder builder);
	}
	
	private HashMapList<String, IMapTweak> tweaks = new HashMapList<String, UT2004MapTweaks.IMapTweak>();
	private LogCategory log;
	
	public UT2004MapTweaks(UT2004Bot bot) {
		log = bot.getLogger().getCategory(getClass().getSimpleName());
		registerDefaultTweaks();		
	}
	
	public LogCategory getLog() {
		return log;
	}

	public void register(String mapName, IMapTweak tweak) {
		tweaks.add(mapName.toLowerCase(), tweak);
	}
	
	public void clearTweaks(String mapName) {
		tweaks.remove(mapName);
	}
	
	public void clearAllTweaks() {
		tweaks.clear();
	}
	
	public void tweak(NavigationGraphBuilder builder) {
		String mapName = builder.getMapName().toLowerCase();
		
		List<IMapTweak> mapTweaks = this.tweaks.get(mapName);
		
		if (mapTweaks == null || mapTweaks.size() == 0) {
			log.warning("No navigation graph tweaks for map " + builder.getMapName());
			return;
		}
		
		log.warning("Tweaking navigation graph for map " + builder.getMapName());
		
		for (IMapTweak mapTweak : mapTweaks) {
			if (mapTweak == null) continue;
			log.fine("Applying tweak: " + mapTweak.toString());
			try {
				mapTweak.tweak(builder);
			} catch (Exception e) {
				log.severe(ExceptionToString.process("Failed to apply tweak.", e));
			}
		}
	}
	
	// ===============
	// STANDARD TWEAKS
	// ===============

	protected void registerDefaultTweaks() { 
		register(
				"DM-1on1-Albatross", 
				new IMapTweak() {
					@Override
					public void tweak(NavigationGraphBuilder builder) {
						tweak_DM_1on1_Albatross(builder);
					}
				}
			);
		register(
			"DM-1on1-Trite", 
			new IMapTweak() {
				@Override
				public void tweak(NavigationGraphBuilder builder) {
					tweak_DM_1on1_Trite(builder);
				}
			}
		);
		register(
				"DM-1on1-Roughinery-FPS", 
				new IMapTweak() {
					@Override
					public void tweak(NavigationGraphBuilder builder) {
						tweak_DM_1on1_Roughinery_FPS(builder);
					}
				}
			);
		register(
				"DM-Flux2", 
				new IMapTweak() {
					@Override
					public void tweak(NavigationGraphBuilder builder) {
						tweak_DM_Flux2(builder);
					}
				}
			);		
		register(
				"DM-1on1-Lea_ESWC2k5", 
				new IMapTweak() {
					@Override
					public void tweak(NavigationGraphBuilder builder) {
						tweak_DM_1on1_Lea_ESWC2k5(builder);
					}
				}
			);
		register(
				"DM-Rankin-FE", 
				new IMapTweak() {
					@Override
					public void tweak(NavigationGraphBuilder builder) {
						tweak_DM_Rankin_FE(builder);
					}
				}
			);
		register(
				"CTF-Lostfaith", 
				new IMapTweak() {
					@Override
					public void tweak(NavigationGraphBuilder builder) {
						tweak_CTF_Lostfaith(builder);
					}
				}
			);
	}
	
	protected void tweak_CTF_Lostfaith(NavigationGraphBuilder builder) {
		builder.removeEdge("PathNode27", "JumpSpot20");
	}

	protected void tweak_DM_Rankin_FE(NavigationGraphBuilder builder) {
		builder.modifyNavPoint("InventorySpot174").addZ(-30).apply();		
		builder.modifyNavPoint("InventorySpot173").addZ(-30).apply();
		builder.modifyNavPoint("InventorySpot172").addZ(-30).apply();
		builder.modifyNavPoint("InventorySpot171").addZ(-30).apply();
		builder.modifyNavPoint("InventorySpot149").addZ(-30).apply();
		builder.removeEdgesBetween("PathNode70", "PathNode74");
		builder.removeEdgesBetween("PathNode0", "PathNode42");
	}

	protected void tweak_DM_1on1_Lea_ESWC2k5(NavigationGraphBuilder builder) {
		builder.removeEdgesBetween("PathNode115", "PathNode114");
		builder.removeEdgesBetween("PathNode115", "PathNode114");
		builder.modifyNavPoint("InventorySpot17").addZ(-20).apply();
		builder.newNavPoint("CustomPathNode1").setLocation(2814, -4534, -174).createNavPoint();
		builder.createSimpleEdgesBetween("PathNode61", "CustomPathNode1");
		builder.createSimpleEdgesBetween("PathNode59", "CustomPathNode1");
		builder.createSimpleEdgesBetween("PathNode17", "CustomPathNode1");
		builder.newNavPoint("CustomPathNode2").setLocation(2280, -4709, -122).createNavPoint();
		builder.createSimpleEdgesBetween("CustomPathNode2", "CustomPathNode1");
		builder.createSimpleEdgesBetween("CustomPathNode2", "InventorySpot4");
	}

	protected void tweak_DM_Flux2(NavigationGraphBuilder builder) {
		builder.removeEdge("PathNode37", "JumpSpot5");
		
	}

	protected void tweak_DM_1on1_Roughinery_FPS(NavigationGraphBuilder builder) {
		builder.removeEdge("PathNode74", "InventorySpot4");
		builder.removeEdge("PathNode51", "PathNode11");
		builder.removeEdge("PathNode11", "PathNode105");
		builder.removeEdge("PathNode25", "PathNode46");
		builder.removeEdge("PathNode26", "PathNode15");
		builder.removeEdge("PlayerStart12", "PathNode106");
		builder.removeEdge("PathNode126", "InventorySpot44");
		builder.removeEdge("PathNode34", "PathNode33");
		
		builder.modifyNavPoint("InventorySpot28").addZ(-50).apply();
		builder.modifyNavPoint("InventorySpot29").addZ(-50).apply();
		builder.modifyNavPoint("InventorySpot30").addZ(-50).apply();
		builder.modifyNavPoint("InventorySpot31").addZ(-50).apply();
	}

	protected void tweak_DM_1on1_Albatross(NavigationGraphBuilder builder) {
		builder.removeEdge("PathNode54", "JumpSpot4");
		builder.modifyNavPoint("PathNode2").modifyEdgeTo("InventorySpot321").removeJumpFlag().modifyEdge();
		builder.modifyNavPoint("InventorySpot321").modifyEdgeTo("PathNode2").removeJumpFlag().modifyEdge();
		builder.modifyNavPoint("InventorySpot321").modifyEdgeTo("InventorySpot322").removeJumpFlag().modifyEdge();
		builder.modifyNavPoint("InventorySpot322").modifyEdgeTo("InventorySpot321").removeJumpFlag().modifyEdge();
		builder.modifyNavPoint("InventorySpot322").modifyEdgeTo("InventorySpot323").removeJumpFlag().modifyEdge();
		builder.modifyNavPoint("InventorySpot323").modifyEdgeTo("InventorySpot322").removeJumpFlag().modifyEdge();
		builder.modifyNavPoint("InventorySpot323").modifyEdgeTo("PlayerStart3").removeJumpFlag().modifyEdge();
		builder.modifyNavPoint("PlayerStart3").modifyEdgeTo("InventorySpot323").removeJumpFlag().modifyEdge();
		builder.modifyNavPoint("InventorySpot327").addZ(-30).apply();
	}

	protected void tweak_DM_1on1_Trite(NavigationGraphBuilder builder) {
		builder.removeEdge("JumpSpot0", "InventorySpot125");
	}

}

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
		register(
		        "DM-Dust2k5",
		        new IMapTweak() {
                    @Override
                    public void tweak(NavigationGraphBuilder builder) {
                        tweak_DM_Dust2k5(builder);
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
		builder.removeEdge("PathNode122", "JumpSpot1");
		builder.removeEdgesBetween("JumpSpot17", "JumpSpot14");
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
		builder.removeEdge("PathNode11", "JumpSpot4");
		builder.removeEdge("PathNode67", "JumpSpot8");
		builder.removeEdge("PathNode58", "PathNode60");
		builder.removeEdge("InventorySpot91", "JumpSpot10");
	    builder.removeEdge("JumpSpot10", "InventorySpot48");
	    builder.removeEdge("JumpSpot7", "InventorySpot91");
	    
	    builder.createSimpleEdge("JumpSpot10", "PathNode92");
	    builder.modifyNavPoint("JumpSpot10").modifyEdgeTo("PathNode92").setJumpFlag().modifyEdge();
		
	    builder.modifyNavPoint("PathNode55").addY(-50).apply();
		builder.modifyNavPoint("PathNode55").addX(-50).apply();
		builder.modifyNavPoint("PathNode55").addZ(10).apply();
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
	}

	protected void tweak_DM_1on1_Trite(NavigationGraphBuilder builder) {
		builder.removeEdge("JumpSpot0", "InventorySpot125");
	}

	protected void tweak_DM_Dust2k5(NavigationGraphBuilder builder) {
	    /* Not working solution to get to minigun */
	    /*builder.newNavPoint("CustomPathNode1").setLocation(3456, -5639, -93).createNavPoint();
	    builder.createSimpleEdge("InventorySpot114", "CustomPathNode1");
	    builder.createSimpleEdge("CustomPathNode1", "InventorySpot110");*/
	    /*builder.modifyNavPoint("InventorySpot110").addZ(60).apply();
	    builder.createSimpleEdge("PathNode79", "InventorySpot111");
	    builder.modifyNavPoint("PathNode79").modifyEdgeTo("InventorySpot111").setDoubleJump().modifyEdge();
	    builder.removeEdge("InventorySpot114", "InventorySpot110");*/
	    
	    /* Two jumps to get to flak cannon */
	    builder.createSimpleEdge("InventorySpot163", "InventorySpot162");
	    builder.modifyNavPoint("InventorySpot163").modifyEdgeTo("InventorySpot162").setDoubleJump().modifyEdge();
	    builder.createSimpleEdge("InventorySpot162", "InventorySpot161");
        builder.modifyNavPoint("InventorySpot162").modifyEdgeTo("InventorySpot161").setDoubleJump().modifyEdge();
        
        /* Two ways to get to armor */
        builder.createSimpleEdge("InventorySpot136", "InventorySpot109");
        builder.modifyNavPoint("InventorySpot136").modifyEdgeTo("InventorySpot109").setDoubleJump().modifyEdge();
        builder.createSimpleEdge("PathNode52", "InventorySpot109");
        builder.modifyNavPoint("PathNode52").modifyEdgeTo("InventorySpot109").setDoubleJump().modifyEdge();
        
        /* CT link gun */
        builder.createSimpleEdge("InventorySpot160", "InventorySpot149");
        builder.modifyNavPoint("InventorySpot160").modifyEdgeTo("InventorySpot149").setDoubleJump().modifyEdge();
        
        /* T bio rifle */
        builder.createSimpleEdge("InventorySpot153", "InventorySpot144");
        builder.modifyNavPoint("InventorySpot153").modifyEdgeTo("InventorySpot144").setDoubleJump().modifyEdge();
        
        /* T Lightning gun */
        builder.createSimpleEdge("PathNode25", "InventorySpot158");
        builder.modifyNavPoint("PathNode25").modifyEdgeTo("InventorySpot158").setDoubleJump().modifyEdge();
        builder.createSimpleEdge("InventorySpot158", "InventorySpot157");
        builder.modifyNavPoint("InventorySpot158").modifyEdgeTo("InventorySpot157").setDoubleJump().modifyEdge();
        builder.createSimpleEdge("InventorySpot157", "PathNode26");
        builder.modifyNavPoint("InventorySpot157").modifyEdgeTo("PathNode26").setJumpFlag().modifyEdge();
        
        /* T shock rifle */
        builder.createSimpleEdge("PathNode21", "InventorySpot156");
        builder.modifyNavPoint("PathNode21").modifyEdgeTo("InventorySpot156").setDoubleJump().modifyEdge();
    }
}

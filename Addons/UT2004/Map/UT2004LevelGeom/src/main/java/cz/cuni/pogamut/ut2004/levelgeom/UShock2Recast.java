package cz.cuni.pogamut.ut2004.levelgeom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cz.cuni.amis.pogamut.ut2004.agent.params.UT2004AgentParameters;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.factory.direct.remoteagent.UT2004ServerFactory;
import cz.cuni.amis.pogamut.ut2004.server.IUT2004Server;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapper;
import cz.cuni.amis.pogamut.ut2004.utils.UCCWrapperConf;
import cz.cuni.amis.pogamut.ut2004.utils.UT2004ServerRunner;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.StopWatch;
import cz.cuni.pogamut.ut2004.levelgeom.utils.Envelope;
import cz.cuni.pogamut.ut2004.levelgeom.utils.Jumppads;
import cz.cuni.pogamut.ut2004.levelgeom.utils.RecastUtils;
import cz.cuni.pogamut.ut2004.levelgeom.utils.VertexTriangle;
import cz.cuni.pogamut.ut2004.levelgeom.xml.BspNodes;
import cz.cuni.pogamut.ut2004.levelgeom.xml.IndexTriangle;
import cz.cuni.pogamut.ut2004.levelgeom.xml.Precache;
import cz.cuni.pogamut.ut2004.levelgeom.xml.StaticMesh;
import cz.cuni.pogamut.ut2004.levelgeom.xml.Terrain;

public class UShock2Recast {

	// UShock2Recast CONFIGURATION
	
	private File ushockFile;
	private String mapName;
	private File jumppadFile;
	private File envelopeFile;
	private File ut2004Dir;
	private File outputDir;
	
	// UShock2Recast SETTINGS
	
    double maxRecastRange = 200;
    int spacePrecision = 6;
    int bspNodesPrecision = 0;
    
    // UShock2Recast RUNTIME ... perform()
    
    private Jumppads jumppads;
    private Envelope envelope;
	
    public UShock2Recast(File ushockFile, File jumppadFile, File envelopeFile, File ut2004Dir, File outputDir) {
		this.ushockFile = ushockFile;
		this.jumppadFile = jumppadFile;
		this.envelopeFile = envelopeFile;
		this.ut2004Dir = ut2004Dir;
		this.outputDir = outputDir;
		this.mapName = ushockFile.getName().substring(0, ushockFile.getName().lastIndexOf("."));
	}

	private void info(String str) {
        System.out.println(str);
    }
	
	private void warning(String str) {
        System.out.println("!!! " + str);
    }

    public void perform() throws IOException, Exception {
        NullCheck.check(ushockFile, "ushockFile");
        NullCheck.check(outputDir, "outputDir");
        NullCheck.check(jumppadFile, "jumppadFile");
        NullCheck.check(envelopeFile, "envelopeFile");
        boolean ut2004Present = ut2004Dir != null;
        
        File inputFile = ushockFile;
        File outputFile = new File(outputDir, mapName + ".obj");
        File outputFileScale = new File(outputDir, mapName + ".scale");
        File outputFileCentre = new File(outputDir, mapName + ".centre");

        if (jumppadFile.exists()) {
        	readJumppadFile(jumppadFile);
        }
        if (envelopeFile.exists()) {
        	readEnvelopeFile(envelopeFile);
        }
        
        if ((!jumppadFile.exists() || !envelopeFile.exists()) && ut2004Present) {
        	boolean createJumppadFile = !jumppadFile.exists();
        	boolean createEnvelopeFile = !envelopeFile.exists();
        	
        	processMap(mapName, ut2004Dir, jumppadFile, envelopeFile, createJumppadFile, createEnvelopeFile);
        	
        	if (createJumppadFile) readJumppadFile(jumppadFile);
        	if (createEnvelopeFile) readEnvelopeFile(envelopeFile);
        }
        
        if (!jumppadFile.exists()) {
        	warning("Neither jumppad file is present nor UT2004 directory specified, resulting data for Recast might not be precise and the resulting NavMesh may cover jumppad areas.");
        }
        if (!envelopeFile.exists()) {
        	warning("Neither envelope file is present nor UT2004 directory specified, resulting data will contain much more triangles than is required, produced NavMesh might be unreasonably large, which may affect its runtime.");
        }
        
        info("Loading data exported by UShock from: " + inputFile.getAbsolutePath());

        StopWatch watchAll = new StopWatch();
        StopWatch watch = new StopWatch();

        Precache uShock = Precache.loadXML_Use_JFlex(inputFile);

        info("Loaded in: " + watch.stopStr());

        if (uShock == null) {
            throw new RuntimeException("Failed to load 'precache' from file " + inputFile.getAbsolutePath());
        }

        Point3DSpace space = new Point3DSpace(spacePrecision);

        if (uShock.bspNodes != null && uShock.bspNodes.bspNodes != null) {
            BspNodes bspNodes = uShock.bspNodes;
            info("BSP Nodes: " + bspNodes.bspNodes.size());

            info("Rounding vertices (1/10^" + bspNodesPrecision + ")...");
            bspNodes.round(bspNodesPrecision);

            info("Pruning in-line vertices...");
            int pruned = bspNodes.pruneVertices();
            info("Pruned: " + pruned + " vertices");

            info("Removing invalid BSP nodes...");
            int removed = bspNodes.removeInvalidNodes();
            info("Removed: " + removed + " BSP nodes");

            info("Adding BSP node vertices into Point3DSpace...");
            space.add(bspNodes);

            info("BSP vertices processing took " + watch.stopStr());
        } else {
            info("BSP nodes NOT present...");
        }

        if (uShock.terrains != null) {
            info("Terrains: " + uShock.terrains.size());
            int i = 0;
            for (Terrain terrain : uShock.terrains) {
                ++i;
                info(i + "/" + uShock.terrains.size() + ": Adjusting terrain nodes (rotating, scaling, translating)...");
                terrain.adjustVertices();
                info(i + "/" + uShock.terrains.size() + ": Adding terrain node vertices into Point3DSpace...");
                space.add(terrain);
            }

            info("Terrains vertices processing took " + watch.stopStr());
        } else {
            info("Terrain NOT present.");
        }

        if (uShock.staticMeshes != null && uShock.staticMeshes.staticMeshes != null) {
            info("Static meshes: " + uShock.staticMeshes.staticMeshes.size());
            info("Adjusting static meshes vertices (rotating, scaling, translating)...");
            uShock.staticMeshes.adjustVertices();
            info("Adding static meshes vertices into Point3DSpace...");
            space.add(uShock.staticMeshes);
            info("Static meshes vertices processing took " + watch.stopStr());
        } else {
            info("Static meshes NOT present.");
        }

        info("Retrieving vertices from Point3DSpace...");
        List<Point3D> points = space.getPointsSorted();

        List<VertexTriangle> triangles = null;

        if (uShock.bspNodes != null && uShock.bspNodes.bspNodes != null) {
            watch.stop();
            info("Cutting BSP node polygons to triangles...");
            triangles = uShock.bspNodes.toTriangles();
            info("Triangles: " + triangles.size());
            info("Cutting took " + watch.stopStr());
        }

        List<IndexTriangle> indexTriangles = new ArrayList<IndexTriangle>();

        if (uShock.terrains != null) {
            int i = 0;
            for (Terrain terrain : uShock.terrains) {
                ++i;
                info(i + "/" + uShock.terrains.size() + ": Adding Terrain triangles...");
                for (IndexTriangle triangle : terrain.triangles) {
                    // triangles have reversed order of indices
                    int shuffle = terrain.vertices.get(triangle.i1).spaceIndex;
                    triangle.i1 = terrain.vertices.get(triangle.i3).spaceIndex;
                    triangle.i2 = terrain.vertices.get(triangle.i2).spaceIndex;
                    triangle.i3 = shuffle;

                    indexTriangles.add(triangle);
                }
            }
        }
        if (uShock.staticMeshes != null && uShock.staticMeshes.staticMeshes != null) {

            for (StaticMesh staticMesh : uShock.staticMeshes.staticMeshes) {

                for (IndexTriangle triangle : staticMesh.triangles) {
                    triangle.i1 = staticMesh.vertices.get(triangle.i1).spaceIndex;
                    triangle.i2 = staticMesh.vertices.get(triangle.i2).spaceIndex;
                    triangle.i3 = staticMesh.vertices.get(triangle.i3).spaceIndex;

                    indexTriangles.add(triangle);
                }
            }
        }

        envelope.processData(points, triangles, indexTriangles);

        points = envelope.getFilteredPoints();

        triangles = envelope.getFilteredTriangles();

        List<IndexTriangle> indexTriangles2 = envelope.getFilteredIndexTriangles();

        if (jumppads != null && jumppads.hasJumppads()) {
            jumppads.addJumppadsBlockers(points, indexTriangles2);
            info("Jumppads on the map - Adding Jumppad blockers!");
        } else {
            info("No Jumppads on the map");
        }

        info("Vertices: " + points.size());

        info("Counting centre...");
        double[] centre = {(envelope.getMaxX() + envelope.getMinX()) / 2, (envelope.getMaxY() + envelope.getMinY()) / 2, (envelope.getMaxZ() + envelope.getMinZ()) / 2};
        info("Centre is: " + " x: " + centre[0] + " y: " + centre[1] + " z: " + centre[2]);
        double[] transVect = {-centre[0], -centre[1], -centre[2]};
        info("Translating centre to [0,0,0]...");
        for (Point3D point : points) {
            point.translate(transVect);
        }

        info("Counting max dimension...");
        double maxRange = envelope.getMaxRange();
        info("Max dimension is: " + maxRange);
        info("Max Recast dimension is: " + maxRecastRange);
        double scale = maxRecastRange / maxRange;
        info("Scale factor is: " + scale);
        info("Scaling vertices...");
        for (Point3D point : points) {
            point.scale(scale);
        }

        FileWriter writer;
        PrintWriter print;

        info("Writing scale factor into " + outputFileScale.getAbsolutePath());
        writer = new FileWriter(outputFileScale);
        print = new PrintWriter(writer);
        print.print(String.valueOf(1 / scale));
        print.close();

        info("Writing centre into " + outputFileCentre.getAbsolutePath());
        writer = new FileWriter(outputFileCentre);
        print = new PrintWriter(writer);
        print.print(centre[0] + " " + centre[1] + " " + centre[2]);
        print.close();

        info("Writing Recast obj file into " + outputFile.getAbsolutePath());
        writer = new FileWriter(outputFile);
        print = new PrintWriter(writer);
        try {
            info("Outputing vertices...");
            RecastUtils.outputVertices(print, points);

            info("Outputing BSP node triangles...");
            RecastUtils.outputVertexTriangles(print, triangles);

            RecastUtils.outputIndexTriangles(print, indexTriangles2);

            print.flush();
        } finally {
            print.close();
        }

        info("DONE! [in " + watchAll.checkStr() + "]");

    }
    
    // ========
    // JUMPPADS
    // ========

	private void processMap(String mapName, File ut2004Dir, File jumppadFile, File envelopeFile, boolean createJumppadFile, boolean createEnvelopeFile) throws IOException {
		info("Processing UT2004 map " + mapName + " ...");
		if (createJumppadFile) info("Going to extract jumppads...");
		if (createEnvelopeFile) info("Going to extract map envelope (mins/maxs)...");
		
		UCCWrapperConf conf = new UCCWrapperConf();
		conf.setMapName(mapName);
		conf.setUnrealHome(ut2004Dir.getAbsolutePath());
		conf.setStartOnUnusedPort(true);
		
		info("Starting UCC server...");
		
		UCCWrapper ucc = new UCCWrapper(conf);
		
		UT2004ServerFactory factory = new UT2004ServerFactory<UT2004AgentParameters>();
		UT2004ServerRunner runner = new UT2004ServerRunner<IUT2004Server, UT2004AgentParameters>(factory, "UCC-Server", ucc.getHost(), ucc.getControlPort());
		
		info("Starting UT2004Server Java instance...");
		
		IUT2004Server server = runner.startAgent();
		
		Collection<NavPoint> navPoints = server.getWorldView().getAll(NavPoint.class).values();
		
		info("There are " + navPoints.size() + " navpoints within the map " + mapName + " ...");
		
		if (createJumppadFile) {		
			info("Probing navpoints for jumppads / spots...");
			
			info("Creating jumppad file at: " + jumppadFile.getAbsolutePath() + " ...");
			
			FileWriter outputWriter = new FileWriter(jumppadFile);
			PrintWriter printWriter = new PrintWriter(outputWriter);
			
			int count = 0;
			
			for (NavPoint np : navPoints) {
				if (np.isJumpPad() || np.isJumpSpot()) {
					++count;
					printWriter.println(np.getLocation().x + ";" + np.getLocation().y + ";" + np.getLocation().z);
				}
			}
			
			if (count == 0) {
				info("NO JUMPPADS/SPOTS FOUND!");
			} else {
				info("THERE ARE " + count + " JUMPPADS/SPOTS WITHIN THE MAP!");
			}
			
			try { printWriter.close(); } catch (Exception e) {};
			try { outputWriter.close(); } catch (Exception e) {};
			
			info("Jumppad file created.");
		}
		
		if (createEnvelopeFile) {
			info("Probing navpoints for the envelope...");
			
			double minX = Double.POSITIVE_INFINITY;
			double minY = Double.POSITIVE_INFINITY;
			double minZ = Double.POSITIVE_INFINITY;
			
			double maxX = Double.NEGATIVE_INFINITY;
			double maxY = Double.NEGATIVE_INFINITY;
			double maxZ = Double.NEGATIVE_INFINITY;
			
			for (NavPoint np : navPoints) {
				double x = np.getLocation().x;
				double y = np.getLocation().y;
				double z = np.getLocation().z;
				if (x < minX) minX = x;
				if (x > maxX) maxX = x;
				if (y < minY) minY = y;
				if (y > maxY) maxY = y;
				if (z < minZ) minZ = z;
				if (z > maxZ) maxZ = z;				
			}
			
			info("Creating envelope file at: " + envelopeFile.getAbsolutePath() + " ...");
			
			FileWriter outputWriter = new FileWriter(envelopeFile);
			PrintWriter printWriter = new PrintWriter(outputWriter);
			
			printWriter.println(minX + ";" + minY + ";" + minZ + ";" + maxX + ";" + maxY + ";" + maxZ);
			
			try { printWriter.close(); } catch (Exception e) {};
			try { outputWriter.close(); } catch (Exception e) {};
			
			info("Envelope file created.");
		}
		
		info("Stopping UT2004Server java instance...");
		
		server.kill();
		
		info("Stopping UCC server...");
		
		ucc.stop();
		
		if (createJumppadFile) info("Jumppad information obtained successfully.");
		if (createEnvelopeFile) info("Envelope information obtained successfully.");
	}

	private void readJumppadFile(File jumppadFile) {
		info("Reading jumppads file from: " + jumppadFile.getAbsolutePath() + " ...");
		jumppads = new Jumppads(jumppadFile, 75, 200);
		if (jumppads.hasJumppads()) {
			info("There are " + jumppads.getJumppadPoints().size() + " jumppads within the map.");
		} else {
			info("There are no jumppads within the map.");
		}
	}
	
	private void readEnvelopeFile(File envelopeFile) {
		info("Reading envelope file from: " + envelopeFile.getAbsolutePath() + " ...");
		envelope = new Envelope(envelopeFile, 1000);
		info("Envelope: X[min=" + envelope.getMinX() + ",max=" + envelope.getMaxX() + "], Y[min=" + envelope.getMinY() + ",max=" + envelope.getMaxY() + "], Z[min=" + envelope.getMinZ() + ",max=" + envelope.getMaxZ() + "]");
	}

}

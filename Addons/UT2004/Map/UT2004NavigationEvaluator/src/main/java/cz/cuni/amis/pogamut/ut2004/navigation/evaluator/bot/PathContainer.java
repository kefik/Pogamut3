/*
 * Copyright (C) 2013 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
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

import cz.cuni.amis.pogamut.base.communication.worldview.object.WorldObjectId;
import cz.cuni.amis.pogamut.base3d.worldview.IVisionWorldView;
import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.utils.collections.MyCollections;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Container for path evaluation. Provides paths which should be evaluated.
 *
 * @author Bogo
 */
public class PathContainer {
    
    private final int tabooRetryCount = 1;
    IVisionWorldView world;
    private HashMap<WorldObjectId, Set<WorldObjectId>> paths;
    private HashMap<Path, Integer> tabooPaths;
    
    private Set<NavPoint> starts = null;
    
    private boolean isInitialized = false;
    
    private Path currentTabooPath = null;
    private boolean loadFromFile;
    private File dataFile;

    /**
     * Create container for given world view.
     *
     * @param world
     *
     */
    public PathContainer(IVisionWorldView world) {
        this.world = world;
        paths = new HashMap<WorldObjectId, Set<WorldObjectId>>();
        tabooPaths = new HashMap<Path, Integer>();
        isInitialized = world != null;
    }
    
    public PathContainer(Object object, boolean loadFromFile, File dataFile) {
        this.loadFromFile = loadFromFile;
        this.dataFile = dataFile;
    }

    /**
     * Get any path from container. Returns null if container is empty.
     *
     * @return Path to navigate
     */
    public Path getPath() {
        if (isEmpty()) {
            return null;
        }
        if (paths.isEmpty()) {
            if (currentTabooPath != null) {
                resetTabooPath();
            }
            currentTabooPath = MyCollections.getRandom(tabooPaths.keySet());
            return currentTabooPath;
        }
        WorldObjectId start = MyCollections.getRandom(paths.keySet());
        Set<WorldObjectId> ends = paths.get(start);
        while (ends == null) {
            start = MyCollections.getRandom(paths.keySet());
            ends = paths.get(start);
        }
        if (ends != null) {
            NavPoint startNavPoint = (NavPoint) world.get(start);
            WorldObjectId end = MyCollections.getRandom(ends);
            ends.remove(end);
            if (ends.isEmpty()) {
                paths.remove(start);
                if (getStarts() != null) {
                    getStarts().remove(startNavPoint);
                }
            }
            return new Path(startNavPoint, (NavPoint) world.get(end));
        }
        return null;
    }
    
    private void resetTabooPath() {
        tabooPaths.remove(currentTabooPath);
        currentTabooPath = null;
    }

    /**
     * Get path with start at given {@link NavPoint}. When such path doesn't
     * exist, returns path from nearest {@link NavPoint} from which some path
     * exists.
     *
     * @param start Where the path should start
     * @return Path to navigate
     *
     */
    public Path getPath(NavPoint start) {
        if (isEmpty()) {
            return null;
        }
        NavPoint pathStart = start;
        Set<WorldObjectId> ends = paths.get(pathStart.getId());
        while (ends == null) {
            getStarts().remove(pathStart);
            paths.remove(pathStart.getId());
            return null; //Possibly remove in time
            //pathStart = DistanceUtils.getNearest(getStarts(), start);
            //ends = paths.get(pathStart.getId());
        }
        WorldObjectId end = MyCollections.getRandom(ends);
        ends.remove(end);
        if (ends.isEmpty()) {
            paths.remove(pathStart.getId());
            if (getStarts() != null) {
                getStarts().remove(pathStart);
            }
        }
        return new Path(pathStart, (NavPoint) world.get(end));
    }

    /**
     * Builds set of paths between all {@link NavPoint}s of given map.
     */
    public void build() {
        build(-1);
    }

    /**
     * Build set of all relevant paths for given map. Relevant paths are paths
     * between {@link NavPoint}s which are either inventory spots or player
     * starts.
     *
     * @param limit Max number of paths built. If limit < 0 build all paths.
     */
    public void buildRelevant(int limit) {
        Map<WorldObjectId, NavPoint> navPoints = world.getAll(NavPoint.class);
        //Hack
        HashSet<WorldObjectId> relevantNavPoints = new HashSet<WorldObjectId>();
        for (NavPoint navPoint : navPoints.values()) {
            if (navPoint.isInvSpot() || navPoint.isPlayerStart()) {
                relevantNavPoints.add(navPoint.getId());
            }
        }
//        for (String navPoint : triteNavPoints) {
//            relevantNavPoints.add(UnrealId.get(navPoint));
//        }
        buildPaths(relevantNavPoints, limit);
    }

    /**
     * Build set of all relevant paths for given map. Relevant paths are paths
     * between {@link NavPoint}s which are either inventory spots or player
     * starts.
     */
    public void buildRelevant() {
        buildRelevant(-1);
    }

    /**
     * Checks whether container is empty.
     *
     * @return Container is empty
     */
    public boolean isEmpty() {
        return paths.isEmpty() && (tabooPaths.isEmpty() || (tabooPaths.size() == 1 && currentTabooPath != null));
    }

    /**
     * Returns size of the container. Iterates through values of Map with paths.
     * If sufficient, use isEmpty() instead.
     *
     * @return Number of paths in the container.
     */
    public int size() {
        if (isEmpty()) {
            return 0;
        } else {
            int size = 0;
            for (Set<WorldObjectId> set : paths.values()) {
                size += set.size();
            }
            return size + (currentTabooPath == null ? tabooPaths.size() : (tabooPaths.size() - 1));
        }
    }
    
    void build(int limit) {
        Map<WorldObjectId, NavPoint> navPoints = world.getAll(NavPoint.class);
        Set<WorldObjectId> navPointsIds = new HashSet<WorldObjectId>(navPoints.keySet());
        buildPaths(navPointsIds, limit);
    }
    
    private void buildPaths(Set<WorldObjectId> navPoints, int limit) {
        paths.clear();
        int pathCount = navPoints.size() * (navPoints.size() - 1);
        boolean buildIncrementaly = limit < pathCount / 5;
        
        if (limit < 0 || !buildIncrementaly) {
            HashSet<WorldObjectId> relevantEnds = new HashSet<WorldObjectId>(navPoints);
            for (WorldObjectId navPointId : navPoints) {
                HashSet<WorldObjectId> ends = new HashSet<WorldObjectId>(relevantEnds);
                ends.remove(navPointId);
                paths.put(navPointId, ends);
            }
        }
        if (limit > 0) {
            if (!buildIncrementaly) {
                while (pathCount > limit) {
                    getPath();
                    --pathCount;
                }
            } else {
                pathCount = 0;
                while (pathCount < limit) {
                    WorldObjectId start = MyCollections.getRandom(navPoints);
                    WorldObjectId end = MyCollections.getRandom(navPoints);
                    if (start.equals(end)) {
                        //Path which ends where it starts is not valid!
                        continue;
                    }
                    pathCount += addPath(start, end);
                }
            }
        }
    }

    /**
     * Loads path container from CSV file.
     *
     * @param file
     * @param isRepeat
     */
    public void buildFromFile(File file, boolean isRepeat) {
        paths.clear();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            
            if (isRepeat) {
                //Skip first line - contains column descriptors
                reader.readLine();
            }
            String line = reader.readLine();
            while (line != null) {
                String[] splitLine = line.split(";");
                if (splitLine.length >= (isRepeat ? 3 : 2)) {
                    WorldObjectId startId = WorldObjectId.get(splitLine[isRepeat ? 1 : 0]);
                    WorldObjectId endId = WorldObjectId.get(splitLine[isRepeat ? 2 : 1]);
                    if (!isRepeat && splitLine.length == 3) {
                        //TODO: Add as taboo path
                        addPath(startId, endId);
                    } else {
                        addPath(startId, endId);
                    }
                }
                line = reader.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PathContainer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PathContainer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(PathContainer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void removePathsFromFile(String path) {
        paths.clear();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
            //Skip first line - contains column descriptors
            reader.readLine();
            String line = reader.readLine();
            while (line != null) {
                String[] splitLine = line.split(";");
                if (splitLine.length >= 3) {
                    WorldObjectId startId = WorldObjectId.get(splitLine[1]);
                    WorldObjectId endId = WorldObjectId.get(splitLine[2]);
                    removePath(startId, endId);
                }
                line = reader.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PathContainer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PathContainer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(PathContainer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public void exportToFile(String path) {
        File pathsFile = new File(path);
        if (isEmpty()) {
            pathsFile.delete();
            return;
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(pathsFile));
            
            for (Entry<WorldObjectId, Set<WorldObjectId>> entry : paths.entrySet()) {
                String start = entry.getKey().getStringId();
                for (WorldObjectId end : entry.getValue()) {
                    writer.write(String.format("%s;%s", start, end.getStringId()));
                    writer.newLine();
                }
            }
            for (Entry<Path, Integer> tabooEntry : tabooPaths.entrySet()) {
                Path tabooPath = tabooEntry.getKey();
                writer.write(String.format("%s;%s;%d", tabooPath.getStart().getId().getStringId(), tabooPath.getEnd().getId().getStringId(), tabooEntry.getValue()));
                writer.newLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PathContainer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PathContainer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(PathContainer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    private int addPath(WorldObjectId start, WorldObjectId end) {
        Set<WorldObjectId> pathsFromStart = paths.get(start);
        if (pathsFromStart == null) {
            pathsFromStart = new HashSet<WorldObjectId>();
            paths.put(start, pathsFromStart);
        }
        if (pathsFromStart.add(end)) {
            return 1;
        }
        return 0;
    }
    
    public boolean addTabooPath(Path path) {
        if (currentTabooPath != null && !currentTabooPath.equals(path)) {
            resetTabooPath();
        }
        Integer retries = tabooPaths.get(path);
        if (retries == null) {
            tabooPaths.put(path, tabooRetryCount);
        } else if (retries <= 1) {
            tabooPaths.remove(path);
            return false;
        } else {
            tabooPaths.put(path, retries - 1);
        }
        return true;
    }
    
    public void setWorld(IVisionWorldView world) {
        this.world = world;
        this.isInitialized = world != null;
        if (isInitialized && loadFromFile) {
            //bui
        }
    }
    
    public boolean isInitialized() {
        return isInitialized;
    }
    
    private Set<NavPoint> getStarts() {
        if (starts == null && isInitialized) {
            starts = new HashSet<NavPoint>();
            for (WorldObjectId startId : paths.keySet()) {
                NavPoint start = (NavPoint) world.get(startId);
                starts.add(start);
            }
        }
        return starts;
    }
    
    private void removePath(WorldObjectId startId, WorldObjectId endId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private List<String> triteNavPoints = Arrays.asList("DM-1on1-Trite.PathNode37",
       //     "DM-1on1-Trite.InventorySpot68",
       //     "DM-1on1-Trite.InventorySpot70",
       //     "DM-1on1-Trite.InventorySpot112",
      //      "DM-1on1-Trite.PlayerStart6",
            "DM-1on1-Trite.InventorySpot72",
            "DM-1on1-Trite.InventorySpot89",
            "DM-1on1-Trite.InventorySpot106",
            "DM-1on1-Trite.InventorySpot105",
            "DM-1on1-Trite.InventorySpot98");
    
}

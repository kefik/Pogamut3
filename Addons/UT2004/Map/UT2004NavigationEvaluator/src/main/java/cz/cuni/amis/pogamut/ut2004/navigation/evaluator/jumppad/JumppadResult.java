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
package cz.cuni.amis.pogamut.ut2004.navigation.evaluator.jumppad;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Result of collecting data about jump pads for their highlighting in the map
 * geometry before creating mesh.
 *
 * @author Bogo
 */
class JumppadResult {

    private final JumppadCollectorTask task;

    private final List<NavPoint> jumppads;

    JumppadResult(JumppadCollectorTask task) {
        this.task = task;
        this.jumppads = new LinkedList<NavPoint>();
    }

    void export() {
        if (jumppads.isEmpty()) {
            return;
        }

        FileWriter fstream = null;
        try {

            String fullFilePath = task.getResultPath() + task.getFileName();
            File resultFile = new File(fullFilePath);
            resultFile.getParentFile().mkdirs();
            fstream = new FileWriter(resultFile);
            BufferedWriter out = new BufferedWriter(fstream);
            for (NavPoint jumppad : jumppads) {
                Location location = jumppad.getLocation();
                out.write(String.format("%f;%f;%f\n", location.x, location.y, location.z));
            }
            out.close();
        } catch (IOException ex) {
        } finally {
            try {
                if (fstream != null) {
                    fstream.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    void add(NavPoint navPoint) {
        jumppads.add(navPoint);
    }

}

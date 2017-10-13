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
package cz.cuni.amis.pogamut.ut2004.navigation.evaluator.data;

import cz.cuni.amis.pogamut.base3d.worldview.object.Location;
import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.NavPoint;
import cz.cuni.amis.pogamut.ut2004.navigation.evaluator.task.MapEnvelopeTask;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Result of map path evaluation.
 *
 * @author Bogo
 */
public class EnvelopeResult {

    private double minX = Double.POSITIVE_INFINITY;
    private double maxX = Double.NEGATIVE_INFINITY;
    private double minY = Double.POSITIVE_INFINITY;
    private double maxY = Double.NEGATIVE_INFINITY;
    private double minZ = Double.POSITIVE_INFINITY;
    private double maxZ = Double.NEGATIVE_INFINITY;
    private MapEnvelopeTask task = null;

    public EnvelopeResult(MapEnvelopeTask task) {
        this.task = task;
    }

    public void checkNavPoint(NavPoint navPoint) {
        Location location = navPoint.getLocation();
        if (location.x < minX) {
            minX = location.x;
        }

        if (location.y < minY) {
            minY = location.y;
        }

        if (location.z < minZ) {
            minZ = location.z;
        }

        if (location.x > maxX) {
            maxX = location.x;
        }

        if (location.y > maxY) {
            maxY = location.y;
        }

        if (location.z > maxZ) {
            maxZ = location.z;
        }
    }

    /**
     * Export complete statistics about evaluation.
     */
    public void export() {
        FileWriter fstream = null;
        try {

            String fullFilePath = task.getResultPath() + task.getFileName();
            File resultFile = new File(fullFilePath);
            resultFile.getParentFile().mkdirs();
            fstream = new FileWriter(resultFile);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(String.format("%f;%f;%f;%f;%f;%f\n", minX, minY, minZ, maxX, maxY, maxZ));
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
}

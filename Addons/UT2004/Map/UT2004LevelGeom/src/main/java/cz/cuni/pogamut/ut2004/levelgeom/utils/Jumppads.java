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
package cz.cuni.pogamut.ut2004.levelgeom.utils;

import cz.cuni.pogamut.ut2004.levelgeom.Point3D;
import cz.cuni.pogamut.ut2004.levelgeom.xml.IndexTriangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bogo
 */
public class Jumppads {

    int radius;
    int top;

    private boolean hasJumppads = false;

    private final List<Point3D> jumppadsCentres = new ArrayList<Point3D>();

    public Jumppads(File jumppadFile, int radius, int top) {
        if (jumppadFile.exists()) {
            hasJumppads = true;
            this.radius = radius;
            this.top = top;

            try {
                FileInputStream stream = new FileInputStream(jumppadFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        stream));

                while (reader.ready()) {
                    String line = reader.readLine();

                    String[] pointsStrings = line.split(";");
                    double x = Float.parseFloat(pointsStrings[0].replace(',', '.'));
                    double y = Float.parseFloat(pointsStrings[1].replace(',', '.'));
                    double z = Float.parseFloat(pointsStrings[2].replace(',', '.'));

                    Point3D jumppadCentre = new Point3D(x, y, z, Math.pow(10, 6));
                    jumppadsCentres.add(jumppadCentre);
                }

                reader.close();

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public boolean hasJumppads() {
        return hasJumppads;
    }
    
    public List<Point3D> getJumppadPoints() {
    	return jumppadsCentres;
    }

    public void addJumppadsBlockers(List<Point3D> points, List<IndexTriangle> triangles) {

        for (Point3D jumppad : jumppadsCentres) {
            addJumpadBlocker(jumppad, points, triangles);
        }
    }

    private void addJumpadBlocker(Point3D jumppad, List<Point3D> points, List<IndexTriangle> triangles) {
        points.add(new Point3D(jumppad.x - radius, jumppad.y - radius, jumppad.z, Math.pow(10, 6)));
        points.add(new Point3D(jumppad.x - radius, jumppad.y + radius, jumppad.z, Math.pow(10, 6)));
        points.add(new Point3D(jumppad.x + radius, jumppad.y - radius, jumppad.z, Math.pow(10, 6)));
        points.add(new Point3D(jumppad.x + radius, jumppad.y + radius, jumppad.z, Math.pow(10, 6)));
        jumppad.z += top;
        points.add(jumppad);

        int topIndex = points.size() - 1;

        int first = topIndex - 4;

        IndexTriangle triangle1 = new IndexTriangle();
        triangle1.i1 = first;
        triangle1.i2 = first + 1;
        triangle1.i3 = topIndex;
        triangles.add(triangle1);

        IndexTriangle triangle2 = new IndexTriangle();
        triangle2.i1 = first + 2;
        triangle2.i2 = first;
        triangle2.i3 = topIndex;
        triangles.add(triangle2);

        IndexTriangle triangle3 = new IndexTriangle();
        triangle3.i1 = first + 3;
        triangle3.i2 = first + 2;
        triangle3.i3 = topIndex;
        triangles.add(triangle3);

        IndexTriangle triangle4 = new IndexTriangle();
        triangle4.i1 = first + 1;
        triangle4.i2 = first + 3;
        triangle4.i3 = topIndex;
        triangles.add(triangle4);

        IndexTriangle triangle5 = new IndexTriangle();
        triangle5.i1 = first;
        triangle5.i2 = first + 1;
        triangle5.i3 = first + 3;
        triangles.add(triangle5);

        IndexTriangle triangle6 = new IndexTriangle();
        triangle6.i1 = first;
        triangle6.i2 = first + 3;
        triangle6.i3 = first + 2;
        triangles.add(triangle6);
    }

}

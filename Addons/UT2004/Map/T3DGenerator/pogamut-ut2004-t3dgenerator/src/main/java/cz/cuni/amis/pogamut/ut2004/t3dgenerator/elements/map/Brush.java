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
package cz.cuni.amis.pogamut.ut2004.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealBean;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealChild;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealChildCollection;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.elements.AbstractUnrealBean;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Martin Cerny
 */
@UnrealBean("Brush")
public class Brush extends AbstractUnrealBean{

    @UnrealChildCollection(encloseIn="PolyList")
    private List<Polygon> polygons;
    
    public Brush() {
        super("Model"); //this is the class used in references...
        polygons = new ArrayList<Polygon>();
    }    
    
    public List<Polygon> getPolygons() {
        return polygons;
    }
    
    public void addPolygon(Polygon polygon){
        polygons.add(polygon);
    }
    
    
}

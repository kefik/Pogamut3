package cz.cuni.amis.pogamut.ut2004.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.FieldName;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealChild;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealProperty;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.ECSGOperation;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.Vector3D;
import cz.cuni.amis.pogamut.ut2004.t3dgenerator.datatypes.Scale;
import cz.cuni.amis.pogamut.ut2004.t3dgenerator.elements.AbstractActor;
import java.util.Arrays;
import java.util.Collections;

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

/**
 *
 * @author Martin Cerny
 */
public class BrushActor extends AbstractActor {

    private Vector3D postPivot;
    
    @FieldName("CsgOper")
    private ECSGOperation csgOperation;
    
    private Scale mainScale;
    private Scale postScale;
    
    @UnrealChild
    @UnrealProperty
    private Brush brush;
    
    
    public BrushActor(Brush brush) {
        super("Brush");
        this.brush = brush;
    }   

    protected static Polygon createDefaultPolygon(Vector3D[] points, String textureName, Vector3D objectCenter, boolean textureInside){
        Vector3D edge1 = points[1].subtract(points[0]);
        Vector3D edge2 = points[points.length - 1].subtract(points[0]);
        Vector3D textureNormal = edge1.crossProduct( edge2);

        Vector3D[] pointsCopy = Arrays.copyOf(points,points.length);
        
        float polygonPlaneCoefficient = -textureNormal.dotProduct(points[0]);
        if((polygonPlaneCoefficient < 0 && !textureInside)
                || (polygonPlaneCoefficient > 0 && textureInside)){
            //reversing the polygon vertices forces the texture to face on the other side
           Collections.reverse(Arrays.asList(pointsCopy));
        } 
        
        return new Polygon(textureName, 
                points[0],
                textureNormal.normalize(),
                edge1.normalize(),
                edge2.normalize() /* TODO - find a unit vector in the correct plane orthogonal to edge1*/,
                pointsCopy);
    }
    
    public static BrushActor createFromTwoPolygons(Vector3D[] poly1, Vector3D[] poly2, String textureName, ECSGOperation csgOperation){
        return createFromTwoPolygons(poly1, poly2, textureName, textureName, textureName, csgOperation);
    }
    
    public static BrushActor createFromTwoPolygons(Vector3D[] poly1, Vector3D[] poly2, String poly1Texture, String poly2Texture, String sideTexture, ECSGOperation csgOperation){
        if(poly1.length != poly2.length) {
            throw new IllegalArgumentException("Polygons must have equal size");
        }
        if(poly1.length < 3) {
            throw new IllegalArgumentException("Polygons must consist of at least 3 points");
        }
        Vector3D objectCenter = Vector3D.centroid(Vector3D.centroid(poly1), Vector3D.centroid(poly2));
        
        boolean textureInside;
        if(csgOperation == ECSGOperation.ADD){
            textureInside = false;
        } else {
            textureInside = true;
        }
        
        
        //translate coordinates relative to object center
        int polygonSize = poly1.length;
        
        Vector3D [] poly1Translated = new Vector3D[polygonSize];
        Vector3D [] poly2Translated = new Vector3D[polygonSize];
        for(int i = 0; i < polygonSize; i++){
            poly1Translated[i] = poly1[i].subtract(objectCenter);
            poly2Translated[i] = poly2[i].subtract(objectCenter);
        }
        
        Brush b = new Brush();
        //at the first polygon
        b.addPolygon(createDefaultPolygon(poly1Translated, poly1Texture, objectCenter, textureInside));
        
        //add conecting tetragons
        for(int i = 0; i < polygonSize; i++){
            int nextIndex = i + 1;
            if(nextIndex >= polygonSize){
                nextIndex = 0;
            }
            b.addPolygon(createDefaultPolygon(new Vector3D[] {poly1Translated[i], poly1Translated[nextIndex], poly2Translated[nextIndex], poly2Translated[i]}, sideTexture, objectCenter, textureInside));
        }
        
        b.addPolygon(createDefaultPolygon(poly2Translated, poly2Texture, objectCenter, textureInside));        
        
        BrushActor ret = new BrushActor(b);
        ret.setLocation(objectCenter);
        ret.setCsgOperation(csgOperation);        
        return ret;
    }
    public static BrushActor createCube(Vector3D center, float edgeLength, String textureName, ECSGOperation csgOperation){        
        return createCube(center, edgeLength, textureName, textureName, textureName, csgOperation);
    }
    
    public static BrushActor createCube(Vector3D center, float edgeLength, String topTexture, String bottomTexture, String sidesTexture, ECSGOperation csgOperation){        
        return createCuboid(center, edgeLength, edgeLength, edgeLength, topTexture, bottomTexture, sidesTexture, csgOperation);
    }
    
    public static BrushActor createCuboid(Vector3D center, float xSize, float ySize, float zSize, String textureName, ECSGOperation csgOperation){
        return createCuboid(center, xSize, ySize, zSize, textureName, textureName, textureName, csgOperation);
    }
    
    public static BrushActor createCuboid(Vector3D center, float xSize, float ySize, float zSize, String topTexture, String bottomTexture, String sidesTexture, ECSGOperation csgOperation){
        float xHalf = xSize / 2; 
        float yHalf = ySize / 2; 
        float zHalf = zSize / 2; 
        Vector3D[] topSide = new Vector3D[] {
            center.add(new Vector3D(-xHalf, -yHalf, +zHalf)),
            center.add(new Vector3D(-xHalf, +yHalf, +zHalf)),
            center.add(new Vector3D(+xHalf, +yHalf, +zHalf)),
            center.add(new Vector3D(+xHalf, -yHalf, +zHalf))
        };
        Vector3D[] bottomSide = new Vector3D[] {
            center.add(new Vector3D(-xHalf, -yHalf, -zHalf)),
            center.add(new Vector3D(-xHalf, +yHalf, -zHalf)),
            center.add(new Vector3D(+xHalf, +yHalf, -zHalf)),
            center.add(new Vector3D(+xHalf, -yHalf, -zHalf))
        };

        return createFromTwoPolygons(topSide, bottomSide, topTexture, bottomTexture, sidesTexture, csgOperation);
        
    }

    public Vector3D getPostPivot() {
        return postPivot;
    }

    public void setPostPivot(Vector3D postPivot) {
        this.postPivot = postPivot;
    }

    public ECSGOperation getCsgOperation() {
        return csgOperation;
    }

    public void setCsgOperation(ECSGOperation csgOperation) {
        this.csgOperation = csgOperation;
    }

    public Scale getMainScale() {
        return mainScale;
    }

    public void setMainScale(Scale mainScale) {
        this.mainScale = mainScale;
    }

    public Scale getPostScale() {
        return postScale;
    }

    public void setPostScale(Scale postScale) {
        this.postScale = postScale;
    }

    public Brush getBrush() {
        return brush;
    }

    public void setBrush(Brush brush) {
        this.brush = brush;
    }
    
    
}

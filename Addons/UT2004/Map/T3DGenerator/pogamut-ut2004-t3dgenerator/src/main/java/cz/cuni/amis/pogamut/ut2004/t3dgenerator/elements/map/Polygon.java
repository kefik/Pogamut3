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

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.StaticText;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealBean;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealHeaderField;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.Vector3D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

/**
 * A polygon in a brush. It SEEMS that textureNormal, textureU and textureV are recomputed upon importing to UnrealEd
 * from vertices. This means that the order of vertices is significant.
 * @author Martin Cerny
 */
@UnrealBean("Polygon")
public class Polygon {
    @UnrealHeaderField
    private String texture;
    
    @UnrealHeaderField
    private Long flags;
    
    @UnrealHeaderField
    private Integer link;
    
    /**
     * The following fields are transient, because they are translated specifically for this class
     * with the propertyText method
     */
    
    private transient Vector3D textureOrigin;
    private transient Vector3D textureNormal;
    private transient Vector3D textureU;    
    private transient Vector3D textureV;
    private transient List<Vector3D> vertices;

    private static final NumberFormat numberFormat;
    
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        numberFormat = new DecimalFormat("+00000.000000;-00000.000000", symbols);
    }
    
    public Polygon() {
    }

    public Polygon(String texture, Vector3D textureOrigin, Vector3D textureNormal, Vector3D textureU, Vector3D textureV, List<Vector3D> vertices) {
        this.texture = texture;
        this.textureOrigin = textureOrigin;
        this.textureNormal = textureNormal;
        this.textureU = textureU;
        this.textureV = textureV;
        this.vertices = vertices;
    }
    
    public Polygon(String texture, Vector3D textureOrigin, Vector3D textureNormal, Vector3D textureU, Vector3D textureV, Vector3D[] vertices) {
        this(texture, textureOrigin, textureNormal, textureU, textureV, Arrays.asList(vertices));
    }
    
    protected String translateVectorToT3d(String label, Vector3D vector){
        //adding 0.0 eliminates negative zero, which in turn causes trouble
        return "\t\t\t\t\t" + label + " " + numberFormat.format(vector.getX() + 0.0f) + "," + numberFormat.format(vector.getY() + 0.0f) + "," + numberFormat.format(vector.getZ() + 0.0f) + "\n";
    }
    
    @StaticText
    public String translateToT3D(){
        StringBuilder theText = new StringBuilder();
        theText.append(translateVectorToT3d("Origin", textureOrigin));
        theText.append(translateVectorToT3d("Normal", textureNormal));
        theText.append(translateVectorToT3d("TextureU", textureU));
        theText.append(translateVectorToT3d("TextureV", textureV));
        for(Vector3D v : vertices){
            theText.append(translateVectorToT3d("Vertex", v));
        }
        return theText.toString();
    }   

    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public Long getFlags() {
        return flags;
    }

    public void setFlags(Long flags) {
        this.flags = flags;
    }

    public Integer getLink() {
        return link;
    }

    public void setLink(Integer link) {
        this.link = link;
    }

    public Vector3D getTextureOrigin() {
        return textureOrigin;
    }

    public void setTextureOrigin(Vector3D textureOrigin) {
        this.textureOrigin = textureOrigin;
    }

    public Vector3D getTextureNormal() {
        return textureNormal;
    }

    public void setTextureNormal(Vector3D textureNormal) {
        this.textureNormal = textureNormal;
    }

    public Vector3D getTextureU() {
        return textureU;
    }

    public void setTextureU(Vector3D textureU) {
        this.textureU = textureU;
    }

    public Vector3D getTextureV() {
        return textureV;
    }

    public void setTextureV(Vector3D textureV) {
        this.textureV = textureV;
    }

    public List<Vector3D> getVertices() {
        return vertices;
    }
    
    public void addVertex(Vector3D vertex) {
        vertices.add(vertex);
    }
}

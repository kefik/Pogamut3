package cz.cuni.amis.nb.pogamut.unreal.map;

import cz.cuni.amis.nb.pogamut.unreal.timeline.map.GlColor;
import cz.cuni.amis.pogamut.base3d.worldview.object.Location;

/**
 * Storage structure used during blending phase of rendering.
 * Stores triangle in worldview with necessary info. For now only colors
 *
 * Only triangle to make sorting and possible intersections easy. Poly may be more fitting.
 * 
 * @author Honza
 */
public class BlendTriangle {
    /**
     * Vertex of triangle.
     */
    protected BlendVertex[] verts;

    public BlendTriangle() {
        verts = new BlendVertex[3];
    }

    /**
     * Return actual collection of vers, not a copy
     */
    public BlendVertex[] getVerts() {
        return verts;
    }

    /**
     * Create new BlendVertex for vertex i of poly.
     * @param i index of vertex, from zero to num of verts
     * @param location location fo vertex
     * @param color color f vertex
     */
    public void setVertex(int i, Location location, GlColor color) {
        verts[i] = new BlendVertex(location, color);
    }
}

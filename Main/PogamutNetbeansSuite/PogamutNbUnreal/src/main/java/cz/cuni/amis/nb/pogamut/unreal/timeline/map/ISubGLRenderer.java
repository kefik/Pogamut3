/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.nb.pogamut.unreal.timeline.map;

import cz.cuni.amis.nb.pogamut.unreal.map.BlendTriangle;
import java.util.List;
import javax.media.opengl.GL;

/**
 * This is a subrenderer, it's job is to render some stuff.
 * Basically I had few {@link GLEventListener}s and there were troubles
 * with using them directly (order of rendering), so I have now only these
 * subrenderes that are part of {@link GLRendererCollection}.
 *
 * @param <T> Class of object this renderer draws.
 * @author Honza
 */
public interface ISubGLRenderer<T> {
    /**
     * Here should be done preparation for rendering (e.g. generation of display
     * lists from massive data)
     * @param gl
     */
    public void prepare(GL gl);

    /**
     * Display stuff you want to. Assume that settings have already been set in
     * {@link GLRendererCollection}
     * @param gl
     */
    public void render(GL gl);

    /**
     * Return object this renderer draws.
     *
     * Because objects we want to draw can change rapidly, we have to remove and
     * add subrenderers based on passed objects (renderer R draws object A, now
     * we don't want to draw A anymore, we have to go through subrenderers to find
     * which ones draws it).
     * @return Object this renderer draws.
     */
    public T getObject();

    /**
     * Because blending phase of rendering can be done only after all opaque
     * objects has been drawn and because polys has to be back-to-front ordered
     * every renderer will return list of its blended triangles so final
     * renderer can sort all blended triangles from all blended renderers and
     * do it correctly.
     * @return List of blended triangles this renderer wants to render. Empty collection
     *         if no such exists.
     */
    public List<BlendTriangle> getBlendedTris();
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.nb.pogamut.unreal.timeline.map;

import cz.cuni.amis.nb.pogamut.unreal.map.BlendTriangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.media.opengl.GL;

/**
 * Basically list of {@link ISubGLRenderer}s with stuff for manipulating it
 * and rendering it.
 *
 * Holds order of subrenderers.
 *
 * @param <T>  Type of object subrenderers of this collection draws.
 * @author Honza
 */
public class GLRendererCollection<T> implements ISubGLRenderer<GLRendererCollection<T>> {
    
    private List<ISubGLRenderer<T>> renderers = Collections.synchronizedList(new ArrayList<ISubGLRenderer<T>>());

    /**
     * Add renderer to collection. It will be rendered after all previous renders are drawn.
     * @param subrenderer
     */
    public synchronized void addSubRenderer(ISubGLRenderer<T> subrenderer) {
        renderers.add(subrenderer);
    }

    /**
     * Remove subrenderer from collection
     * @param subrenderer renderer to be removed
     * @return true if subrenderes was in collection
     */
    public synchronized boolean removeSubRenderer(ISubGLRenderer<T> subrenderer) {
        return renderers.remove(subrenderer);
    }
    
    /**
     * List of subrenderes.
     * @return Unmodifiable list of subrenderes.
     */
    public synchronized List<ISubGLRenderer<T>> getSubRenderes() {
        return Collections.unmodifiableList(new ArrayList<ISubGLRenderer<T>>(renderers));
    }

    /**
     * Return list of renderers that draw passed object
     * @return list of renderers with object o, empty list if no found
     */
    public synchronized List<ISubGLRenderer<T>> getRenderersOf(T o) {
        ArrayList<ISubGLRenderer<T>> result = new ArrayList<ISubGLRenderer<T>>();

        for (ISubGLRenderer<T> renderer : renderers) {
            if (renderer.getObject() == o) {
                result.add(renderer);
            }
        }
        return result;
    }

    /**
     * Remove all renderers that draw object o.
     * @param o object that may have renderers in collection we want to remove
     */
    public synchronized void removeRenderersOf(T o) {
        List<ISubGLRenderer<T>> renderersOf = getRenderersOf(o);
        for (ISubGLRenderer<T> renderer : renderersOf) {
            removeSubRenderer(renderer);
        }
    }

    /**
     * Get set of all objects this collection draws.
     * @return all objects this collection draws
     */
    public synchronized Set<T> getDrawnObjects() {
        HashSet<T> set = new HashSet<T>();
        for (ISubGLRenderer<T> renderer : renderers) {
            set.add(renderer.getObject());
        }
        return set;
    }

    @Override
    public synchronized void render(GL gl) {
        ISubGLRenderer[] renderersArray = renderers.toArray(new ISubGLRenderer[0]);
        for (ISubGLRenderer subrenderer : renderersArray) {
            subrenderer.render(gl);
        }
    }

    @Override
    public GLRendererCollection<T> getObject() {
        return this;
    }


    /**
     * Prepare all renderers for rendering
     * @param gl
     */
    @Override
    public synchronized void prepare(GL gl) {
        for (ISubGLRenderer<T> renderer : renderers) {
            renderer.prepare(gl);
        }
    }

    @Override
    public synchronized List<BlendTriangle> getBlendedTris() {
        List<BlendTriangle> list = new LinkedList<BlendTriangle>();
        ISubGLRenderer[] renderersArray = renderers.toArray(new ISubGLRenderer[0]);

        for (ISubGLRenderer subrenderer : renderersArray) {
            list.addAll(subrenderer.getBlendedTris());
        }
        
        return list;
    }
}

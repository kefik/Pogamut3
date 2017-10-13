/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.udk.t3dgenerator.elements;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.FieldName;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealBean;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealHeaderField;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealProperty;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.DynamicReference;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.StaticReference;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.elements.AbstractUnrealBean;

/**
 * Abstract predecessor for most Unreal objects.
 * A descendant needs to use {@link UnrealBean} annotation to specify the objec type.
 * @author Martin Cerny
 */
public class AbstractBean extends AbstractUnrealBean {
    @UnrealHeaderField
    @UnrealProperty
    @FieldName("ObjectArchetype")
    private UnrealReference archetype;

    /**
     * Get a name of default archetype for a specified className.
     * Useful in creating archetype references.
     * @param className
     * @return 
     */
    public static String getDefaultArchetype(String className) {
        return "Engine.Default__" + className;
    }

    public AbstractBean() {
    }   
    
    public AbstractBean(String className) {
        this(className,getDefaultArchetype(className), null);        
    }

    public AbstractBean(String className, UnrealReference archetype) {
        this(className, archetype, null);
    }

    public AbstractBean(String className, String archetypeName) {
        this(className, archetypeName, null);
    }

    public AbstractBean(String className, String archetypeName, String name) {
        this(className, new StaticReference(className, archetypeName), name);
    }

    public AbstractBean(String className, UnrealReference archetype, String name) {
        super(className, name);
        this.archetype = archetype;
    }

    public UnrealReference getArchetype() {
        return archetype;
    }
}

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
package cz.cuni.amis.pogamut.unreal.t3dgenerator.elements;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.FieldName;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealHeaderField;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealProperty;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.DynamicReference;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;

/**
 *
 * @author Martin Cerny
 */
public abstract class AbstractUnrealBean implements IUnrealReferencable, IUnrealReferencableByName {
    @UnrealHeaderField(value = "Class")
    protected String className;
    @UnrealHeaderField
    protected String name;

    public AbstractUnrealBean() {
    }    
    
    public AbstractUnrealBean(String className) {
        this(className,  null);
    }

    public AbstractUnrealBean(String className, String name) {
        this.className = className;
        this.name = name;
    }

    @Override
    public String getClassName() {
        return className;
    }

    public String getName() {
        return name;
    }

    /**
     * Gets name of the object that should be used for references. Default implementation
     * returns {@link #getName() }, but may be overriden.
     * @return
     */
    @UnrealProperty
    @FieldName(value = "Name")
    @Override
    public String getNameForReferences() {
        return getName();
    }

    @Override
    public UnrealReference getReference() {
        return new DynamicReference(this);
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets name of the object that should be used for references. Default implementation
     * calls {@link #setName(String) }, but may be overriden.
     * @return
     */
    @Override
    public void setNameForReferences(String nameForReferences) {
        setName(nameForReferences);
    }
    
}

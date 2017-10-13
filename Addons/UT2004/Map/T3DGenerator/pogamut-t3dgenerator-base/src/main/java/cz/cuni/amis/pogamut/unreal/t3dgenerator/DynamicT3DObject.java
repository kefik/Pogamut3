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
package cz.cuni.amis.pogamut.unreal.t3dgenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A dynamically created T3D object.
 * @author Martin Cerny
 */
public class DynamicT3DObject {
    private String beanType;
    private Map<String, Object> headerFields;
    private Map<String, Object> properties;
    private List children;

    public DynamicT3DObject(String beanType) {
        this.beanType = beanType;
        headerFields = new HashMap<String, Object>();
        properties = new HashMap<String, Object>();
        children = new ArrayList();
    }

    public String getBeanType() {
        return beanType;
    }
        

    public Map<String, Object> getHeaderFields() {
        return headerFields;
    }


    public Map<String, Object> getProperties() {
        return properties;
    }

    public List getChildren() {
        return children;
    }

    public void addChildren(Collection children) {
        this.children.addAll(children);
    }
    
    public void addChild(Object child) {
        this.children.add(child);
    }
    
}

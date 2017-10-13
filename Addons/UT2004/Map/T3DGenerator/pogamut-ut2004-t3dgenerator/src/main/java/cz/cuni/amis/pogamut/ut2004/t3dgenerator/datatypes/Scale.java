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
package cz.cuni.amis.pogamut.ut2004.t3dgenerator.datatypes;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealDataType;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.Vector3D;

/**
 *
 * @author Martin Cerny
 */
@UnrealDataType
public class Scale {
    private Vector3D scale;
    private float sheerRate;
    private ESheerAxis sheerAxis;

    public Scale(Vector3D scale, float sheerRate, ESheerAxis sheerAxis) {
        this.scale = scale;
        this.sheerRate = sheerRate;
        this.sheerAxis = sheerAxis;
    }

    
    
    public Vector3D getScale() {
        return scale;
    }

    public void setScale(Vector3D scale) {
        this.scale = scale;
    }

    public float getSheerRate() {
        return sheerRate;
    }

    public void setSheerRate(float sheerRate) {
        this.sheerRate = sheerRate;
    }

    public ESheerAxis getSheerAxis() {
        return sheerAxis;
    }

    public void setSheerAxis(ESheerAxis sheerAxis) {
        this.sheerAxis = sheerAxis;
    }
    
    
}

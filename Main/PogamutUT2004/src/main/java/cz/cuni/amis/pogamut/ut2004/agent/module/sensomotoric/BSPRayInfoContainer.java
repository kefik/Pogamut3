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
package cz.cuni.amis.pogamut.ut2004.agent.module.sensomotoric;

import cz.cuni.amis.pogamut.unreal.communication.messages.UnrealId;
import javax.vecmath.Vector3d;

/**
 *
 * @author Jakub Tomek
 * A container for a BSP ray's parameters. They need to be saved because the AutoTraceray Object will be constantly updated
 * and these updates will be based on bot's poisiton and these parameters
 */
public class BSPRayInfoContainer {
    public UnrealId unrealId; 
    public String id;
    public Vector3d direction;
    public int length;
    public boolean floorCorrection;

    BSPRayInfoContainer(UnrealId unrealId, Vector3d direction, int length, boolean floorCorrection) {
        this.unrealId = unrealId;
        this.direction = direction;
        this.length = length;
        this.floorCorrection = floorCorrection;
    }
}

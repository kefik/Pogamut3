/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealDataType;

/**
 * Rotation of an object.
 * @author Martin Cerny
 */
@UnrealDataType
public class Rotation3D {
    private int pitch;
    private int yaw;
    private int roll;

    public Rotation3D(int pitch, int yaw, int roll) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    public int getPitch() {
        return pitch;
    }

    public int getRoll() {
        return roll;
    }

    public int getYaw() {
        return yaw;
    }

}

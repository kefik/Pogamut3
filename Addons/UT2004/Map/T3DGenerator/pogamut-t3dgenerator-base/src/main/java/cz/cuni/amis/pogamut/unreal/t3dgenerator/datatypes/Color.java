/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealDataType;

/**
 * A color in UDK.
 * @author Martin Cerny
 */
@UnrealDataType
public class Color {
    private int r;
    private int g;
    private int b;
    private int a;

    public Color(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    public int getG() {
        return g;
    }

    public int getR() {
        return r;
    }

    
}

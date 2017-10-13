/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealDataType;

/**
 * A vector in 3D space of unreal.
 * @author Martin Cerny
 */
@UnrealDataType
public class Vector3D {
    float x;
    float y;
    float z;

    public Vector3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }



    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public Vector3D multiply(float a){
        return new Vector3D(x * a, y * a, z * a);
    }

    public Vector3D divide(float a){
        return new Vector3D(x / a, y / a, z / a);
    }

    public Vector3D add(Vector3D p){
        return new Vector3D(x + p.getX(), y + p.getY(), z + p.getZ());
    }

    public Vector3D subtract(Vector3D p){
        return new Vector3D(x - p.getX(), y - p.getY(), z - p.getZ());
    }
    

    public Vector3D negateX(){
        return new Vector3D(-getX(), getY(),getZ());
    }
    public Vector3D negateY(){
        return new Vector3D(getX(), -getY(),getZ());
    }
    
    public Vector3D negateXandY(){
        return new Vector3D(-getX(), -getY(),getZ());
    }
    
    public Vector3D switchXandY(){
        return new Vector3D(getY(), getX(), getZ());                
    }
    
    public Vector3D negate(){
        return new Vector3D(-x, -y, -z);
    }
    
    public Vector3D crossProduct(Vector3D p){
        return new Vector3D(
                y * p.z - z * p.y,
                z * p.x - x * p.z,
                x * p.y - y * p.x);
    }

    public float dotProduct(Vector3D v){
        return x * v.x + y * v.y + z * v.z;
    }
         
    public double length(){
        return Math.sqrt(x * x + y * y + z * z);
    }
        
    public Vector3D normalize(){
        return divide((float)length());
    }
        
    
   
    public static final Vector3D ZERO = new Vector3D(0, 0, 0);
    public static final Vector3D X_AXIS = new Vector3D(1, 0, 0);
    public static final Vector3D Y_AXIS = new Vector3D(0, 1, 0);
    public static final Vector3D Z_AXIS = new Vector3D(0, 0, 1);
    
    public static Vector3D centroid(Vector3D ... points){
        if(points.length <= 0){
            throw new IllegalArgumentException("At least one point must be given");
        }
        Vector3D sum = points[0];
        for(int i = 1; i < points.length ; i++){
            sum = sum.add(points[i]);
        }
        return sum.divide(points.length);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vector3D other = (Vector3D) obj;
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
            return false;
        }
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y)) {
            return false;
        }
        if (Float.floatToIntBits(this.z) != Float.floatToIntBits(other.z)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Float.floatToIntBits(this.x);
        hash = 31 * hash + Float.floatToIntBits(this.y);
        hash = 31 * hash + Float.floatToIntBits(this.z);
        return hash;
    }

    @Override
    public String toString() {
        return "Point3D{" + "x=" + x + "y=" + y + "z=" + z + '}';
    }


}

package cz.cuni.amis.pogamut.unreal.communication.worldview.map;

/**
 * Basic box for holding a dimensions of a 3D block.
 * @author Honza
 */
public class Box {
    public double minX;
    public double minY;
    public double minZ;

    public double maxX;
    public double maxY;
    public double maxZ;

    public Box(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;

        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public double getDeltaX() {
        return maxX - minX;
    }

    public double getDeltaY() {
        return maxY - minY;
    }

    public double getDeltaZ() {
        return maxZ - minZ;
    }

    public double getCenterX() {
        return minX + getDeltaX()/2;
    }

    public double getCenterY() {
        return minY + getDeltaY()/2;
    }

    public double getCenterZ() {
        return minZ + getDeltaZ()/2;
    }

    /**
     * Get minimal dimension of a box.
     * @return minimal dimension of box
     */
    public double getMinDelta() {
        double min = Double.MAX_VALUE;
        if (min > getDeltaX())
            min = getDeltaX();

        if (min > getDeltaY())
            min = getDeltaY();
        
        if (min > getDeltaZ())
            min = getDeltaZ();

        return min;
    }
}

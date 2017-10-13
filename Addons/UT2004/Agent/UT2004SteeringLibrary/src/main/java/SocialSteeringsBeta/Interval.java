package SocialSteeringsBeta;

/**
 * @author Petr
 */
public class Interval {
    private int min;
    private int max;

    public Interval(int mn, int mx) {
        min = mn;
        max = mx;
    }

    public boolean in(int x) {
        return x >= min && x <= max;
    }
    
    public boolean in(int x,double tolerance) {
        return x+tolerance >= min && x-tolerance <= max;
    }

    public int avg() {
        return (min + max) / 2;
    }

    public int getMax() {
        return max;
    }
    
     public int getMin() {
        return min;
    }
}
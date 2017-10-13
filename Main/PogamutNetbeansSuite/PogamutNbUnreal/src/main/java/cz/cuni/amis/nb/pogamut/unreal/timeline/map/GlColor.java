package cz.cuni.amis.nb.pogamut.unreal.timeline.map;

import java.awt.Color;

/**
 * AWT provided Color class is not very suitable for OpenGl color manipulation so
 * this class is supposed to remedy it.
 *
 * @author Honza
 */
public class GlColor {

    public double r;
    public double g;
    public double b;
    /**
     * Alpha, 1 is fully opaque, 0 is fully transparent
     */
    public double a;

    /**
     * Create new GlColor
     * @param r In range &lt;0-1&gt;
     * @param g In range &lt;0-1&gt;
     * @param b In range &lt;0-1&gt;
     * @param a In range &lt;0-1&gt; 0 transparent, 1 opaque
     */
    public GlColor(double r, double g, double b, double a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    /**
     * Create fully opaque GlColor.
     * @param r In range &lt;0-1&gt;
     * @param g In range &lt;0-1&gt;
     * @param b In range &lt;0-1&gt;
     */
    public GlColor(double r, double g, double b) {
        this(r, g, b, 1.0);
    }

    /**
     * Create GlColor based on passed Color and alpha
     * @param color Basic color
     * @param a In range &lt;0-1&gt; 0 transparent, 1 opaque
     */
    public GlColor(Color color, double alpha) {
        this(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0, alpha);
    }

    /**
     * Create opaque color based on passed color.
     * @param color
     */
    public GlColor(Color color) {
        this(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0);
    }

    public GlColor(GlColor color) {
        this(color.r, color.g, color.b, color.a);
    }

    /**
     * Create a mixed color from passed color and this color in following fashion:
     * <pre>this * (1-portion) + mixing * portion</pre>
     * @param mixing Color that will be mixed with this one.
     * @param portion how much of mixing color will be used
     * @return Mixed color, not that this color won't be changed
     */
    public GlColor getMixedWith(GlColor mixing, double portion) {
        double thisPortion = 1 - portion;
        return new GlColor(
                thisPortion * r + portion * mixing.r,
                thisPortion * g + portion * mixing.g,
                thisPortion * b + portion * mixing.b,
                thisPortion * a + portion * mixing.a);
    }

    @Override
    public String toString() {
        return "[r:" + r + "; g:" + g + "; b:" + b + "; a: " + a + "]";
    }
}

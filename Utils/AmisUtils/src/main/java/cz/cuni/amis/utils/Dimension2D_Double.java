package cz.cuni.amis.utils;

import java.awt.geom.Dimension2D;
import java.io.Serializable;

/**
 * WHY Dimension2D hasn't default Float and Double implementation?
 * @author Radim Vansa <radim.vansa@matfyz.cz>
 *
 */
public class Dimension2D_Double extends Dimension2D implements Serializable {

	protected double width;
	protected double height;
	
	public Dimension2D_Double() {
	}
	
	public Dimension2D_Double(double w, double h) {
		width = w;
		height = h;
	}
	
	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;		
	}

}

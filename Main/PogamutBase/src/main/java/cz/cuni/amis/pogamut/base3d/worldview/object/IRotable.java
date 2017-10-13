package cz.cuni.amis.pogamut.base3d.worldview.object;

/**
 * General interface for objects that are rotary within the world.
 *
 * @author Juraj 'Loque' Simlovic
 */
public interface IRotable
{
	/**
	 * Retreives current rotation of the object.
	 *
	 * @return Current rotation of the object, represented as yaw, roll and
	 * pitch.
	 */
	Rotation getRotation ();

}

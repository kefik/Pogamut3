package cz.cuni.amis.pogamut.base3d.worldview.object;

/**
 * General interface for objects that are moveable within the world.
 *
 * @author Juraj 'Loque' Simlovic
 */
public interface ILocomotive
{
	/**
	 * Retreives current direction and velocity of movement of the object.
	 *
	 * @return Current direction in which the object is moving and absolute
	 * velocity of the object within the world. The direction is represented
	 * as a vector within the world's coordinates. The size of velocity is
	 * represented by length of that vector.
	 */
	Velocity getVelocity ();

}

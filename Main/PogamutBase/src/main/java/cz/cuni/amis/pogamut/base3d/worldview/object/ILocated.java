package cz.cuni.amis.pogamut.base3d.worldview.object;

/**
 * General interface for objects that are located within the world.
 *
 * @author Juraj 'Loque' Simlovic
 */
public interface ILocated
{
	/**
	 * Retreives current location of the object.
	 *
	 * @return Current location of the object, represented as a point within the
	 * world's coordinates.
	 */
	Location getLocation();

}

package cz.cuni.amis.utils.objectmanager;

/**
 * Simple factory interface for any kind of object.
 * 
 * @author Jimmy
 *
 * @param <Producing>
 */
public interface IObjectFactory<Producing> {
	
	public Producing newObject();

}

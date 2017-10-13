package cz.cuni.amis.pogamut.multi.communication.worldview.object;


/**
 * General interface for local parts of objects whose visiblility may change over time.
 * (since isVisible is most certainly a local property)
 * @author srlok
 *
 */
public interface ILocalViewable extends ILocalWorldObject {
	
	/**
	 * Tells whether the object is currently visible.
	 * @return
	 */
	public boolean isVisible();
}

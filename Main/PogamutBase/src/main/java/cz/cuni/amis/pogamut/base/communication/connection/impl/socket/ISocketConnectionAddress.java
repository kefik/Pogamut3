package cz.cuni.amis.pogamut.base.communication.connection.impl.socket;


import cz.cuni.amis.pogamut.base.communication.connection.IWorldConnectionAddress;

/**
 * Connection descriptor for sockets ... contains host:port.
 * 
 * @author Jimmy
 */
public interface ISocketConnectionAddress extends IWorldConnectionAddress {

	public String getHost();
	
	public int getPort();
	
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.base.communication.connection.impl.socket;

import java.net.URI;
import java.net.URL;

import com.google.inject.name.Named;

import cz.cuni.amis.utils.NullCheck;

/**
 * Simple implementation of the interface ISocketConnectionAddress.
 * @author Jimmy
 */
public class SocketConnectionAddress implements ISocketConnectionAddress {

	public static final String ADDRESS_DEPENDENCY = "HOST_DEPENDENCY";
	
    private String host;
    private int port;

    public SocketConnectionAddress(@Named(ADDRESS_DEPENDENCY) SocketConnectionAddress address) {
    	this(address.host, address.port);
    }
    
    public SocketConnectionAddress(String host, int port) {
        this.host = host;
        NullCheck.check(host, "host");
        this.port = port;
        if (port < 0 || port > 65535) throw new IllegalArgumentException("port=" + port + " is out of range.");
    }

    public SocketConnectionAddress(URL url) {
        this(url.getHost(), url.getPort());
    }

    public SocketConnectionAddress(URI uri) {
    	this(uri.getHost(), uri.getPort());
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }
    
    public String toString() {
    	return "SocketConnectionAddress[" + host + ":" + port + "]";
    }
}
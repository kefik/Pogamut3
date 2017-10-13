/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.base.server;

import java.net.URI;

import cz.cuni.amis.pogamut.base.agent.IAgent;

/**
 * Interface for servers representing remote worlds.
 * @author ik
 */
public interface IWorldServer<A extends IAgent> extends IWorldAgentsObserver<A>, IAgent {
	
    /**
     * Address of the world. eg. gb04://localhost:3000
     * @return
     */
    URI getWorldAddress();


}

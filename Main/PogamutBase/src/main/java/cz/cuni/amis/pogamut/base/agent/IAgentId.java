package cz.cuni.amis.pogamut.base.agent;

import java.io.Serializable;

import javax.management.MXBean;

import cz.cuni.amis.utils.flag.Flag;
import cz.cuni.amis.utils.token.IToken;

/**
 * Extension of the {@link IToken}, it provides an ability to give an agent a human-readable
 * name.
 * <p><p>
 * Serializable! Implementors must behave accordingly.
 *  
 * @author Jimmy
 */
@MXBean
public interface IAgentId extends IToken, Serializable {

	/**
	 * Contains a human-readable name of the agent.
	 * <p><p>
	 * Note that the name is quite different string than {@link IAgentId#getToken()}. The
	 * token contains unique-identifier of the agent across whole JVMs in the world, but
	 * the name is just human-readable identifier that can be even changed over time.
	 * <p><p>
	 * Therefore, the name should not be used for any compares or interpretation.
	 *   
	 * @return
	 */
	public Flag<String> getName();
	
	/**
	 * Must return token that is unique even across different JVMs.
	 * @return token
	 */
	@Override
	public String getToken();
	
}

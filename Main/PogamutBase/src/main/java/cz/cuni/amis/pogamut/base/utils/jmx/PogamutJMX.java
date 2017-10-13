package cz.cuni.amis.pogamut.base.utils.jmx;

import javax.management.ObjectName;

import cz.cuni.amis.pogamut.base.agent.IAgentId;
import cz.cuni.amis.pogamut.base.agent.impl.AbstractAgent;
import cz.cuni.amis.pogamut.base.utils.Pogamut;
import cz.cuni.amis.pogamut.base.utils.PogamutProperty;
import cz.cuni.amis.pogamut.base.utils.exception.PogamutJMXNameException;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.token.IToken;

/**
 * PogamutJMX serves as a placeholder for creating {@link ObjectName}s for various Pogamut components (classes).
 * <p><p>
 * It will be important to you only in the case when you will need to interface Pogamut agents via JMX, it will allow
 * you to construct correct {@link ObjectName}s that identifies them inside the MBean server.
 * <p><p>
 * Still the biggest trick is to obtain {@link IAgentId} (or at least its {@link IAgentId#getToken()}) in order to obtain
 * correct {@link PogamutJMX#getAgentObjectName(IToken)}.
 * <p><p>
 * Note that concrete object names of various agent's components might be obtained by calling static method on concrete class,
 * i.e., JMXLogCategories#getJMXLogCategoryName.
 * 
 * @author Jimmy
 */
public class PogamutJMX {

	/**
	 * Used by AbstractAgent (i.e., AgentJMXComponents) class
	 */
	public static final String AGENT_SUBTYPE = "agent";
	
	/**
	 * Used by Act class
	 */
	public static final String ACT_NAME = "act";
	
	/**
	 * Used by LogCategory class
	 */
	public static final String LOGCATEGORIES_NAME = "logcat";
	
	/**
	 * LogCategory class
	 */ 
	public static final String LOGCATEGORY_SUBTYPE = "logcategory";
	
	/**
	 * Flag class
	 */
	public static final String FLAGS_SUBTYPE = "flags";

	/**
	 * introspection FolderToJMXEnabledAdapter class
	 */
	public static final String INTROSPECTION_NAME = "introspection";

	/**
	 * Used by AgentLogger class. 
	 */
	public static String AGENT_LOGGER_SUBTYPE = "agentlogger";
	
	/*====================================================================================*/
	
	/**
	 * Returns an ObjectName for the Pogamut's agents given by his 'agentId'.
	 * <p><p>
	 * Used by AgentJMXComponents.
	 * 
	 * @param agentId id of the agent that 
	 */
	public static ObjectName getAgentObjectName(IToken agentId) {
		return getAgentObjectName(agentId.getToken());
	}

	/**
	 * Returns an ObjectName for the Pogamut's agents given by his 'agentId'.
	 * <p><p>
	 * Used by AgentJMXComponents.
	 * 
	 * @param agentId id of the agent that
	 */
	public static ObjectName getAgentObjectName(String agentId) {
		return PogamutJMX.getObjectName(getPogamutJMXDomain(), PogamutJMX.AGENT_SUBTYPE + "-" + Pogamut.getPlatform().getProperty(PogamutProperty.POGAMUT_JMX_SUBDOMAIN.getKey()), agentId);
	}
	
	/*====================================================================================*/
	
	/**
	 * Returns an JMX Domain id used by Pogamut's agents.
	 * 
	 * @return jmx domain used by Pogamut's agents.
	 */
	public static String getPogamutJMXDomain() {
		return Pogamut.getPlatform().getProperty(PogamutProperty.POGAMUT_JMX_DOMAIN.getKey());
	}
	
	/**
	 * Creates hierarchical ObjectNames given a parent and name of the MBean.
	 * Extra elements can be inserted into the path through typeExtra param.
	 * 
	 * @param parent
	 *            parent's ObjectName
	 * @param childName
	 *            name of this object, if null no name element will be appended
	 * @param childTypeExtra
	 *            extra element inserted to the end of hierarchical path, may be null
	 * @return ObjectName of form: domain=[parent's domain],type=[parent's
	 *         type].[parent's name].[child extra type],name=[childName]
	 * @throws PogamutJMXNameException
	 */
	public static ObjectName getObjectName(ObjectName parent, String childName, String childTypeExtra) throws PogamutJMXNameException {
		NullCheck.check(parent, "parent");
		
		String parentDomain = parent.getDomain();
		String parentType = parent.getKeyProperty("type");
		String parentName = parent.getKeyProperty("name");
		
		if (childTypeExtra != null) {
			String[] childTypeParts = childTypeExtra.split(".");
			if (childTypeParts.length != 0) {
				for (int i = 0; i < childTypeParts.length; ++i) {
					childTypeParts[i] = getSafeObjectNamePart(childTypeParts[i]);
				}
				childTypeExtra = childTypeParts[0];
				for (int i = 1; i < childTypeParts.length; ++i) {
					childTypeExtra += "." + childTypeParts[i];
				}
			} else {
				childTypeExtra = getSafeObjectNamePart(childTypeExtra);
			}
		}
		
		String childType = (childTypeExtra == null ?
									parentType + "." + parentName
								:	parentType + "." + parentName + "." + childTypeExtra
						   );
		
		return getObjectName(parentDomain, childType, childName);		
	}
	
	/**
	 * Creates hierarchical ObjectNames given a parent and name of the MBean.
	 * Extra elements can be inserted into the path through typeExtra param.
	 * 
	 * @param parent 
	 *            parent's ObjectName
	 * @param childName
	 *            name of this object, if null no name element will be appended
	 * @return ObjectName of form: domain=[parent's domain],type=[parent's
	 *         type].[parent's name],name=[name]
	 * @throws PogamutJMXNameException
	 */
	public static ObjectName getObjectName(ObjectName parent, String childName) throws PogamutJMXNameException {
		NullCheck.check(parent, "parent");
		NullCheck.check(childName, "childName");
		
		String parentDomain = parent.getKeyProperty("domain");
		String parentType = parent.getKeyProperty("type");
		String parentName = parent.getKeyProperty("name");
		
		String childType = parentType + "." + parentName;
		
		return getObjectName(parentDomain, childType, childName);		
	}
	
	/**
	 * Replaces JMX URL chars with '_'.
	 * @param str
	 * @return
	 */
	public static String getSafeObjectNamePart(String str) {
		return 
			str.replace('[', '_').replace(']', '_').replace('(', '_').replace(')', '_')
			   .replace('.', '_').replace(',', '_').replace('/', '_').replace('*', '_')
			   .replace('?', '_').replace(':', '_');
	}
	
	/**
	 * Returns well formed JMX indentificator based on the domain / type / name.
	 * <p><p>
	 * Can be used to create ObjectName instance from javax.management package to identify
	 * the object inside MBeanServer.
	 * 
	 * @param jmxDomain must be well formed
	 * @param type must be well formed
	 * @param name may be null, may contain invalid chars (will be replaced with '_')
	 * @return instance containing well-formed ObjectName
	 * @throws PogamutJMXNameException
	 */
	public static ObjectName getObjectName(String jmxDomain, String type, String name) throws PogamutJMXNameException {
		
		if (name != null) {
			name = getSafeObjectNamePart(name);
		}
		
		String jmxName = (name == null ?
								jmxDomain+":type="+type
							:	jmxDomain+":type="+type+",name="+name
						 );
		try {
			return ObjectName.getInstance(jmxName);
		} catch (Exception e) {
			throw new PogamutJMXNameException(jmxName, e);
		}
	}

}

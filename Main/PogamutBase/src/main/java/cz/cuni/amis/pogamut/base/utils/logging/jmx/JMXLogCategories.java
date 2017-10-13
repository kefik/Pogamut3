package cz.cuni.amis.pogamut.base.utils.logging.jmx;

import java.util.Map;
import java.util.logging.Level;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;


import cz.cuni.amis.pogamut.base.utils.exception.PogamutJMXNameException;
import cz.cuni.amis.pogamut.base.utils.jmx.PogamutJMX;
import cz.cuni.amis.pogamut.base.utils.logging.ILogCategories;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.exception.PogamutException;

/**
 * JMX decorator for ILogCategories. Every new log category will implicitly have 
 * handler with JMXLogPublisher attached.
 * 
 * @author Jimmy
 */

public class JMXLogCategories implements ILogCategories, JMXLogCategoriesMBean {	
	
	/**
	 * Wrapped categories we're decorating.
	 */
	protected ILogCategories logCategories;
	
	/**
	 * MBean server for the log categories.
	 */
	protected MBeanServer mBeanServer;
	
    /**
     * Id of the MBean.
     */
    protected ObjectName objectName = null;

	/**
	 * JMXLogCategories differs from LogCategories by implicit handler with JMXLogPublisher in every 
	 * log category it produces.
	 * <p><p>
	 * Can't be instantiated twice for one (JMX Domain,mBeanServer)!
	 * 
	 * @param logCategories
	 * @param mBeanServer
	 * @param parent
	 * @throws InstanceAlreadyExistsException raised if instantiated twice for one jmx domain
	 * @throws MBeanRegistrationException
	 * @throws NotCompliantMBeanException
	 * @throws MalformedObjectNameException
	 * @throws NullPointerException
	 */
	public JMXLogCategories(ILogCategories logCategories, MBeanServer mBeanServer, ObjectName parent) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException, NullPointerException {
		this.logCategories = logCategories;
		this.mBeanServer = mBeanServer;
		
        objectName = ObjectName.getInstance(getJMXLogCategoriesName(parent));
        mBeanServer.registerMBean(this, objectName);
        for (LogCategory category : getCategories().values()) {
			category.addHandler(newJMXLogPublisher(category.getName()));
		}		
	}
	
	/**
	 * Returns existing category by the name or adds new one.
	 * <p><p>
	 * Contains handler with JMXLogPublisher attached.
	 * <p><p>
	 * If you wish to add another handler do:
	 * LogCategory myCategory = categories.getCategory("my log"); // create new category
	 * myCategory.newHandler(new LogPublisher.ConsolePublisher()); // add new handler with output to the console
	 * 
	 * @param name
	 * @return
	 */
    @Override
	public synchronized LogCategory getCategory(String name) {
		if (hasCategory(name)) {
			return logCategories.getCategory(name);
		} else {
			// creating new category
			LogCategory newCategory = logCategories.getCategory(name);
			newCategory.addHandler(newJMXLogPublisher(name));		
			return newCategory;
		}
	}
	
	private JMXLogPublisher newJMXLogPublisher(String name) {
        try {
            ObjectName categoryObjName = ObjectName.getInstance(getJMXLogCategoryName(name));
            JMXLogPublisher logPublisher = new JMXLogPublisher(categoryObjName, name);
            mBeanServer.registerMBean(logPublisher, categoryObjName);
            return logPublisher;
        } catch (Exception e) {
        	throw new PogamutException("Can't register JMXLogPublisher with name " + getJMXLogCategoryName(objectName, name) + ".", e, this);
        }
	}

   	@Override
	public Map<String, LogCategory> getCategories() {
		return logCategories.getCategories();
	}

	@Override
	public String[] getCategoryNames() {
		return logCategories.getCategoryNames();
	}

	@Override
	public String[] getCategoryNamesSorted() {
		return logCategories.getCategoryNamesSorted();
	}

	@Override
	public boolean hasCategory(String name) {		
		return logCategories.hasCategory(name);
	}

	@Override
	public void setLevel(Level newLevel) {
		logCategories.setLevel(newLevel);
	}

	@Override
	public void addLogCategory(String name, LogCategory category) {
		logCategories.addLogCategory(name, category);
	}
	
	@Override
    public ObjectName getJMXLogCategoryName(String categoryName) throws PogamutJMXNameException {
        return getJMXLogCategoryName(objectName, categoryName);
    }

	public static ObjectName getJMXLogCategoryName(ObjectName parent, String categoryName) throws PogamutJMXNameException {
        return PogamutJMX.getObjectName(parent, categoryName);
	}
	
	@Override
	public ObjectName getJMXLogCategoriesName() {
		return objectName;
	}
	
	/**
     * Gets LogCategories's object name given parent's name.
     * @param parent
     * @return
     */
    public static ObjectName getJMXLogCategoriesName(ObjectName parent) throws PogamutJMXNameException{
        return PogamutJMX.getObjectName(parent, PogamutJMX.LOGCATEGORIES_NAME);
    }

}

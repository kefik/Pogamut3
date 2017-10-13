package cz.cuni.amis.pogamut.base.utils.jmx;

import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.loading.ClassLoaderRepository;

import cz.cuni.amis.utils.HashCode;
import cz.cuni.amis.utils.NullCheck;
import cz.cuni.amis.utils.SafeEquals;

/**
 * Wrapper of the {@link MBeanServer} interface, that is using to store references of
 * registered mbeans and listeners allowing to unregister/register them again using
 * {@link PogamutMBeanServer#unregisterAll()} and {@link PogamutMBeanServer#registerAll()}.
 * 
 * @author Jimmy
 *
 */
public class PogamutMBeanServer implements MBeanServer {
	
	private static interface RegisteredListener {
		public void register() throws InstanceNotFoundException;
		public void unregister();
	}
	
	private static interface RegisteredMBean {
		public void register() throws InstanceNotFoundException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException;
		public void unregister();
	}
	
	private class Listener1 implements RegisteredListener {
		
		private ObjectName name;
		private ObjectName listener;
		private NotificationFilter filter;
		private Object handback;

		private int hashCode;

		public Listener1(ObjectName name,
				ObjectName listener, NotificationFilter filter, Object handback) {
			this.name = name;
			this.listener = listener;
			this.filter = filter;
			this.handback = handback;
			HashCode hc = new HashCode();
			hc.add(name);
			hc.add(listener);
			hc.add(filter);
			hc.add(handback);
			this.hashCode = hc.getHash();
		}
		
		@Override
		public int hashCode() {
			return hashCode;
		}
		
		public boolean equals(Object o) {
			if (o == null) return false;
			if (!(o instanceof Listener1)) return false;
			Listener1 l = (Listener1)o;
			return SafeEquals.equals(name, l.name) && SafeEquals.equals(listener, l.listener) && SafeEquals.equals(filter, l.filter) && SafeEquals.equals(handback, l.handback);
		}
		
		@Override
		public void unregister() {
			try {
				innerRemoveNotificationListener(name, listener, filter, handback);
			} catch (Exception e) {				
			}
		}
		
		public void register() throws InstanceNotFoundException {
			innerAddNotificationListener(name, listener, filter, handback);
		}
		
		@Override
		public String toString() {
			return "Listener1[name=" + name + ", listener=" + listener + "]";
		}

	}
	
	private class Listener2 implements RegisteredListener {
		
		private ObjectName name;
		private NotificationListener listener;
		private NotificationFilter filter;
		private Object handback;

		private int hashCode;

		public Listener2(ObjectName name, NotificationListener listener, NotificationFilter filter, Object handback) {
			this.name = name;
			this.listener = listener;
			this.filter = filter;
			this.handback = handback;
			HashCode hc = new HashCode();
			hc.add(name);
			hc.add(listener);
			hc.add(filter);
			hc.add(handback);
			this.hashCode = hc.getHash();
		}
		
		@Override
		public int hashCode() {
			return hashCode;
		}
		
		public boolean equals(Object o) {
			if (o == null) return false;
			if (!(o instanceof Listener2)) return false;
			Listener2 l = (Listener2)o;
			return SafeEquals.equals(name, l.name) && SafeEquals.equals(listener, l.listener) && SafeEquals.equals(filter, l.filter) && SafeEquals.equals(handback, l.handback);
		}
		
		@Override
		public void unregister() {
			try {
				innerRemoveNotificationListener(name, listener, filter, handback);
			} catch (Exception e) {				
			}
		}
		
		public void register() throws InstanceNotFoundException {
			innerAddNotificationListener(name, listener, filter, handback);
		}
		
		@Override
		public String toString() {
			return "Listener2[name=" + name + ", listener=" + listener + "]";
		}
		
	}
	
	private class MBean1 implements RegisteredMBean {

		private ObjectName name;
		private Object mBean;
		private int hashCode;

		public MBean1(ObjectName name, Object bean) {
			this.name = name;
			this.mBean = bean;
			HashCode hc = new HashCode();
			hc.add(name);
			this.hashCode = hc.getHash();
		}
		
		@Override
		public int hashCode() {
			return hashCode;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (!(obj instanceof MBean1)) return false;
			MBean1 mBean = (MBean1)obj;
			return SafeEquals.equals(name, mBean.name);
		}
		
		@Override
		public void register() throws InstanceNotFoundException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
			innerRegisterMBean(mBean, name);
		}

		@Override
		public void unregister() {
			try {
				innerUnregisterMBean(name);
			} catch (Exception e) {				
			}
		}
		
		@Override
		public String toString() {
			return "MBean1[name=" + name + ", object=" + mBean + "]";
		}

		
	}
	
	private MBeanServer mbs;
	
	private Set<RegisteredMBean> unregisteredMBeans = new HashSet<RegisteredMBean>(); 
	
	private Set<RegisteredMBean> mBeans = new HashSet<RegisteredMBean>();
	
	private Set<RegisteredListener> unregisteredListeners = new HashSet<RegisteredListener>();
	
	private Set<RegisteredListener> listeners = new HashSet<RegisteredListener>();
	
	private Set<Listener1> listeners1 = new HashSet<Listener1>();
	
	private Set<Listener2> listeners2 = new HashSet<Listener2>();

	public PogamutMBeanServer() {
		this(MBeanServerFactory.createMBeanServer());
	}
	
	public PogamutMBeanServer(MBeanServer mBeanServer) {
		this.mbs = mBeanServer;
		NullCheck.check(this.mbs, "mBeanServer");
	}
	
	public synchronized void clearSaved() {
		unregisteredMBeans.clear();
		unregisteredListeners.clear();		
	}
	
	/**
	 * Unreagister all listeners and mbeans from the server.
	 */
	public synchronized void unregisterAll() {
		Iterator<RegisteredListener> iter1 = listeners.iterator();
		while (iter1.hasNext()) {
			RegisteredListener listener = iter1.next();
			listener.unregister();
			iter1.remove();
			unregisteredListeners.add(listener);
		}
		Iterator<RegisteredMBean> iter2 = mBeans.iterator();
		while (iter2.hasNext()) {
			RegisteredMBean mBean = iter2.next();
			mBean.unregister();
			unregisteredMBeans.add(mBean);
			iter2.remove();
		}	
	}
	
	public synchronized void registerAll() throws InstanceNotFoundException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
		Iterator<RegisteredMBean> iter1 = unregisteredMBeans.iterator();
		while (iter1.hasNext()) {
			RegisteredMBean mBean = iter1.next();
			mBean.register();
			iter1.remove();
			mBeans.add(mBean);
		}
		Iterator<RegisteredListener> iter2 = unregisteredListeners.iterator();
		while (iter2.hasNext()) {
			RegisteredListener listener = iter2.next();
			listener.register();
			iter2.remove();
			if (listener instanceof Listener1) {
				listeners1.add((Listener1) listener);
			} else {
				listeners2.add((Listener2) listener);
			}
		}
	}

	@Override
	public synchronized void addNotificationListener(ObjectName name, NotificationListener listener, NotificationFilter filter,
			Object handback) throws InstanceNotFoundException {		
		mbs.addNotificationListener(name, listener, filter, handback);
		Listener2 l = new Listener2(name, listener, filter, handback);
		listeners.add(l);
		listeners2.add(l);
	}
	
	private synchronized void innerAddNotificationListener(ObjectName name, NotificationListener listener, NotificationFilter filter,
			Object handback) throws InstanceNotFoundException {		
		mbs.addNotificationListener(name, listener, filter, handback);		
	}

	@Override
	public synchronized void addNotificationListener(ObjectName name, ObjectName listener,
			NotificationFilter filter, Object handback)
			throws InstanceNotFoundException {
		mbs.addNotificationListener(name, listener, filter, handback);
		Listener1 l = new Listener1(name, listener, filter, handback);
		listeners.add(l);
		listeners1.add(l);
	}
	
	private synchronized void innerAddNotificationListener(ObjectName name, ObjectName listener,
			NotificationFilter filter, Object handback)
			throws InstanceNotFoundException {
		mbs.addNotificationListener(name, listener, filter, handback);
	}

	@Override
	public synchronized ObjectInstance createMBean(String className, ObjectName name)
			throws ReflectionException, InstanceAlreadyExistsException,
			MBeanRegistrationException, MBeanException,
			NotCompliantMBeanException {
		throw new UnsupportedOperationException("Not supported by PogamutMBeanServer yet...");
	}

	@Override
	public synchronized ObjectInstance createMBean(String className, ObjectName name,
			ObjectName loaderName) throws ReflectionException,
			InstanceAlreadyExistsException, MBeanRegistrationException,
			MBeanException, NotCompliantMBeanException,
			InstanceNotFoundException {
		throw new UnsupportedOperationException("Not supported by PogamutMBeanServer yet...");
	}

	@Override
	public synchronized ObjectInstance createMBean(String className, ObjectName name,
			Object[] params, String[] signature) throws ReflectionException,
			InstanceAlreadyExistsException, MBeanRegistrationException,
			MBeanException, NotCompliantMBeanException {
		throw new UnsupportedOperationException("Not supported by PogamutMBeanServer yet...");
	}

	@Override
	public synchronized ObjectInstance createMBean(String className, ObjectName name,
			ObjectName loaderName, Object[] params, String[] signature)
			throws ReflectionException, InstanceAlreadyExistsException,
			MBeanRegistrationException, MBeanException,
			NotCompliantMBeanException, InstanceNotFoundException {
		throw new UnsupportedOperationException("Not supported by PogamutMBeanServer yet...");		
	}

	@Deprecated
	@Override
	public ObjectInputStream deserialize(ObjectName name, byte[] data)
			throws InstanceNotFoundException, OperationsException {
		return mbs.deserialize(name, data);
	}

	@Deprecated
	@Override
	public ObjectInputStream deserialize(String className, byte[] data)
			throws OperationsException, ReflectionException {
		return mbs.deserialize(className, data);
	}

	@Deprecated
	@Override
	public ObjectInputStream deserialize(String className,
			ObjectName loaderName, byte[] data)
			throws InstanceNotFoundException, OperationsException,
			ReflectionException {
		return mbs.deserialize(className, loaderName, data);
	}

	@Override
	public Object getAttribute(ObjectName name, String attribute)
			throws MBeanException, AttributeNotFoundException,
			InstanceNotFoundException, ReflectionException {
		return mbs.getAttribute(name, attribute);
	}

	@Override
	public AttributeList getAttributes(ObjectName name, String[] attributes)
			throws InstanceNotFoundException, ReflectionException {
		return mbs.getAttributes(name, attributes);
	}

	@Override
	public ClassLoader getClassLoader(ObjectName loaderName)
			throws InstanceNotFoundException {
		return mbs.getClassLoader(loaderName);
	}

	@Override
	public ClassLoader getClassLoaderFor(ObjectName mbeanName)
			throws InstanceNotFoundException {
		return mbs.getClassLoaderFor(mbeanName);
	}

	@Override
	public ClassLoaderRepository getClassLoaderRepository() {
		return mbs.getClassLoaderRepository();
	}

	@Override
	public String getDefaultDomain() {
		return mbs.getDefaultDomain();
	}

	@Override
	public String[] getDomains() {
		return mbs.getDomains();
	}

	@Override
	public Integer getMBeanCount() {
		return mbs.getMBeanCount();
	}

	@Override
	public MBeanInfo getMBeanInfo(ObjectName name)
			throws InstanceNotFoundException, IntrospectionException,
			ReflectionException {
		return mbs.getMBeanInfo(name);
	}

	@Override
	public ObjectInstance getObjectInstance(ObjectName name)
			throws InstanceNotFoundException {
		return mbs.getObjectInstance(name);
	}

	@Override
	public Object instantiate(String className) throws ReflectionException,
			MBeanException {
		return mbs.instantiate(className);
	}

	@Override
	public Object instantiate(String className, ObjectName loaderName)
			throws ReflectionException, MBeanException,
			InstanceNotFoundException {
		return mbs.instantiate(className, loaderName);
	}

	@Override
	public Object instantiate(String className, Object[] params,
			String[] signature) throws ReflectionException, MBeanException {
		return mbs.instantiate(className, params, signature);
	}

	@Override
	public Object instantiate(String className, ObjectName loaderName,
			Object[] params, String[] signature) throws ReflectionException,
			MBeanException, InstanceNotFoundException {
		return mbs.instantiate(className, loaderName, params, signature);
	}

	@Override
	public Object invoke(ObjectName name, String operationName,
			Object[] params, String[] signature)
			throws InstanceNotFoundException, MBeanException,
			ReflectionException {
		return mbs.invoke(name, operationName, params, signature);
	}

	@Override
	public boolean isInstanceOf(ObjectName name, String className)
			throws InstanceNotFoundException {
		return mbs.isInstanceOf(name, className);
	}

	@Override
	public boolean isRegistered(ObjectName name) {
		return mbs.isRegistered(name);
	}

	@Override
	public Set<ObjectInstance> queryMBeans(ObjectName name, QueryExp query) {
		return mbs.queryMBeans(name, query);
	}

	@Override
	public Set<ObjectName> queryNames(ObjectName name, QueryExp query) {
		return mbs.queryNames(name, query);
	}

	@Override
	public synchronized ObjectInstance registerMBean(Object object, ObjectName name)
			throws InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException {		
		ObjectInstance obj = mbs.registerMBean(object, name);
		mBeans.add(new MBean1(name, object));
		unregisteredMBeans.remove(new MBean1(name, object));
		return obj;
	}
	
	private synchronized ObjectInstance innerRegisterMBean(Object object, ObjectName name)
			throws InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException {		
		return mbs.registerMBean(object, name);
	}	

	@Override
	public synchronized void removeNotificationListener(ObjectName name, ObjectName listener)
			throws InstanceNotFoundException, ListenerNotFoundException {
		mbs.removeNotificationListener(name, listener);

		// TODO: slow implementation ... but fast one takes a lot of time to do :-)
		Iterator<Listener1> iter = listeners1.iterator();
		while(iter.hasNext()) {
			Listener1 l = iter.next();
			if (SafeEquals.equals(name, l.name) && SafeEquals.equals(listener, l.listener)) {
				listeners.remove(l);
				unregisteredListeners.remove(l);
				iter.remove();
			}
		}		
	}
	
	@Override
	public synchronized void removeNotificationListener(ObjectName name,
			NotificationListener listener) throws InstanceNotFoundException,
			ListenerNotFoundException {		
		mbs.removeNotificationListener(name, listener);
		
		// TODO: slow implementation ... but fast one takes a lot of time to do :-)
		Iterator<Listener2> iter = listeners2.iterator();
		while(iter.hasNext()) {
			Listener2 l = iter.next();
			if (SafeEquals.equals(name, l.name) && SafeEquals.equals(listener, l.listener)) {
				listeners.remove(l);
				unregisteredListeners.remove(l);
				iter.remove();
			}
		}
	}

	@Override
	public synchronized void removeNotificationListener(ObjectName name, ObjectName listener, NotificationFilter filter, Object handback)
			throws InstanceNotFoundException, ListenerNotFoundException {
		mbs.removeNotificationListener(name, listener, filter, handback);
		Listener1 l = new Listener1(name, listener, filter, handback);
		listeners.remove(l);
		listeners1.remove(l);
		unregisteredListeners.remove(l);
	}
	
	private synchronized void innerRemoveNotificationListener(ObjectName name, ObjectName listener, NotificationFilter filter, Object handback)
			throws InstanceNotFoundException, ListenerNotFoundException {
		mbs.removeNotificationListener(name, listener, filter, handback);
	}

	@Override
	public synchronized void removeNotificationListener(ObjectName name,	NotificationListener listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException,
			ListenerNotFoundException {		
		mbs.removeNotificationListener(name, listener, filter, handback);
		Listener2 l = new Listener2(name, listener, filter, handback);
		listeners.remove(l);
		listeners2.remove(l);
		unregisteredListeners.remove(l);
	}
	
	private synchronized void innerRemoveNotificationListener(ObjectName name,	NotificationListener listener, NotificationFilter filter, Object handback) throws InstanceNotFoundException,
			ListenerNotFoundException {		
		mbs.removeNotificationListener(name, listener, filter, handback);
	}

	@Override
	public void setAttribute(ObjectName name, Attribute attribute)
			throws InstanceNotFoundException, AttributeNotFoundException,
			InvalidAttributeValueException, MBeanException, ReflectionException {
		mbs.setAttribute(name, attribute);
	}

	@Override
	public AttributeList setAttributes(ObjectName name, AttributeList attributes)
			throws InstanceNotFoundException, ReflectionException {
		return mbs.setAttributes(name, attributes);
	}

	@Override
	public synchronized void unregisterMBean(ObjectName name)
			throws InstanceNotFoundException, MBeanRegistrationException {		
		mbs.unregisterMBean(name);
		mBeans.remove(new MBean1(name, null));
		unregisteredMBeans.remove(new MBean1(name, null));
	}
	
	private synchronized void innerUnregisterMBean(ObjectName name)
			throws InstanceNotFoundException, MBeanRegistrationException {		
		mbs.unregisterMBean(name);
	}

}

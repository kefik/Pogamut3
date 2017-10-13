/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.base.utils.jmx;

import cz.cuni.amis.introspection.Folder;
import cz.cuni.amis.introspection.jmx.FolderMBean;
import cz.cuni.amis.pogamut.base.agent.exceptions.CantStartJMXException;
import cz.cuni.amis.pogamut.base.agent.exceptions.JMXAlreadyEnabledException;
import cz.cuni.amis.pogamut.base.agent.jmx.AgentJMXComponents;
import cz.cuni.amis.pogamut.base.agent.jmx.IJMXEnabled;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * Adapter turning introspection folder into IJMXEnabled component.
 * Only the root folder is adapted through this object.
 * @author Ik
 */
public class FolderToIJMXEnabledAdapter implements IJMXEnabled {

    

    /** Folder being adapted. */
    Folder folder = null;

    public FolderToIJMXEnabledAdapter(Folder folder) {
        this.folder = folder;
    }

    @Override
    public void enableJMX(MBeanServer mBeanServer, ObjectName parent) throws JMXAlreadyEnabledException, CantStartJMXException {

        try {
            ObjectName newName = getFolderObjectNameForParent(parent, null);
            FolderMBean.exportFolderHierarchy(folder, mBeanServer, newName.getDomain(), newName.getKeyProperty("type"));
        } catch (Exception ex) {
            throw new CantStartJMXException("Failed to export an introspection folder to JMX server.", ex);
        }
    }

    public static ObjectName getFolderObjectNameForParent(ObjectName parent, String folderName) throws MalformedObjectNameException {
        return PogamutJMX.getObjectName(parent, folderName, PogamutJMX.INTROSPECTION_NAME);
    }
}

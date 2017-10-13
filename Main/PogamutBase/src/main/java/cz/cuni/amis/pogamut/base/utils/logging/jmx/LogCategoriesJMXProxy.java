package cz.cuni.amis.pogamut.base.utils.logging.jmx;

import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import cz.cuni.amis.introspection.jmx.DynamicProxy;
import cz.cuni.amis.pogamut.base.utils.logging.AbstractLogCategories;
import cz.cuni.amis.pogamut.base.utils.logging.LogCategory;
import cz.cuni.amis.utils.exception.PogamutJMXException;
import cz.cuni.amis.utils.maps.LazyMap;

/**
 *
 * @author ik
 */
public class LogCategoriesJMXProxy extends AbstractLogCategories {

    DynamicProxy proxy = null;
    MBeanServerConnection mbsc = null;
    ObjectName objectName = null;
    Map<String, LogCategory> categories = new LazyMap<String, LogCategory>() {

        @Override
        protected LogCategory create(String key) {
            try {
                return new LogCategoryJMXProxy(mbsc, objectName, key);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    };

    public LogCategoriesJMXProxy(MBeanServerConnection mbsc, ObjectName parentName) throws PogamutJMXException {
        objectName = JMXLogCategories.getJMXLogCategoriesName(parentName);
        proxy = new DynamicProxy(objectName, mbsc);
        this.mbsc = mbsc;
    }

    @Override
    protected Map<String, LogCategory> getCategoriesInternal() {
        // update
        for(String catName : getCategoryNames()) {
            categories.get(catName);
        }
        return categories;
    }

    @Override
    public LogCategory getCategory(String name) {
        return getCategoriesInternal().get(name);
    }

    @Override
    public String[] getCategoryNames() {
        try {
            return (String[]) proxy.getAttribute("CategoryNames");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}

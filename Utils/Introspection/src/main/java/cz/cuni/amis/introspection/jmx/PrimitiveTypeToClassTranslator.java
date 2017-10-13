package cz.cuni.amis.introspection.jmx;

import java.util.HashMap;
import java.util.Map;

/**
 * Translates primitive type to corresponding wrapper type class.
 * @author ik
 */
public class PrimitiveTypeToClassTranslator {

    static private Map<String, Class> typeMap = null;

    public synchronized static Class get(String primitiveType) {
        if (typeMap == null) {
            typeMap = new HashMap<String, Class>();
            typeMap.put("int", Integer.TYPE);
            typeMap.put("long", Long.TYPE);
            typeMap.put("double", Double.TYPE);
            typeMap.put("byte", Byte.TYPE);
            typeMap.put("float", Float.TYPE);
            typeMap.put("boolean", Boolean.TYPE);
            typeMap.put("char", Character.TYPE);
            typeMap.put("void", Void.TYPE);
            typeMap.put("short", Short.TYPE);

        }
        return typeMap.get(primitiveType);
    }
}

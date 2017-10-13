package cz.cuni.pogamut.posh.properties;

import java.util.Arrays;
import org.openide.nodes.PropertySupport;

/**
 * Create combo box property editor. It has
 * array of T items that are used as possible values of property.
 * Should be mainly for enums.
 * TODO: Perhaps remove
 * @author Honza
 */
public class ComboBoxProperty<T> extends PropertySupport.Reflection<Integer> {

    /**
     * Class that is dummy property
     * @param <T>
     */
    private static class Items<T> {

        private T[] items;
        private Class tClass;
        private Object propertyObject;
        private String getter;
        private String setter;

        private Items(T[] list, Class tClass, Object propertyObject, String getter, String setter) {
            this(list, 0, tClass, propertyObject, getter, setter);
        }

        private Items(T[] list, int selected, Class tClass, Object propertyObject, String getter, String setter) {
            this.items = Arrays.copyOf(list, list.length);
            this.propertyObject = propertyObject;
            this.getter = getter;
            this.setter = setter;
        }

        public Integer getItemId() throws Exception {
            T currentItem = getProperty();
            for (int i = 0; i < items.length; ++i) {
                if (items[i].equals(currentItem)) {
                    return i;
                }
            }
            throw new IllegalStateException("Currently selected value is not in object.");
        }

        public void setItemId(Integer newItemId) throws Exception {
            if (newItemId == null) {
                return;
            }
            if (newItemId.intValue() >= items.length) {
                return;
            }
            if (newItemId.intValue() < 0) {
                return;
            }
            setProperty(items[newItemId]);
        }

        private void setProperty(T newItem) throws Exception {
            propertyObject.getClass().getMethod(setter, tClass).invoke(propertyObject, newItem);
        }

        private T getProperty() throws Exception {
            return (T) propertyObject.getClass().getMethod(getter).invoke(propertyObject);
        }
    }

    public ComboBoxProperty(T[] items, Class tClass, Object propertyObject, String getter, String setter) throws NoSuchMethodException {
        super(new Items<T>(items, tClass, propertyObject, getter, setter), Integer.class, "getItemId", "setItemId");

        int[] intValues = new int[items.length];
        String[] stringKeys = new String[items.length];

        for (int i = 0; i < items.length; ++i) {
            intValues[i] = i;
            stringKeys[i] = items[i].toString();
        }
        this.setValue("intValues", intValues);
        this.setValue("stringKeys", stringKeys);
    }
}

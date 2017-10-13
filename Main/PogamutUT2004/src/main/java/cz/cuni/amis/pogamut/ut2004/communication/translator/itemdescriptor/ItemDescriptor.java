package cz.cuni.amis.pogamut.ut2004.communication.translator.itemdescriptor;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemType;
import cz.cuni.amis.pogamut.ut2004.communication.messages.ItemTypeTranslator;
import cz.cuni.amis.utils.ClassUtils;

/**
 * A parent of all item descriptors.
 * 
 * Contains attributes common for all descriptors plus some common functions.
 * 
 * @author Ondrej, knight
 */
public class ItemDescriptor {
	
	
	public static final ItemDescriptor NONE = new ItemDescriptor();

    @ItemDescriptorField
    private int amount;
    @ItemDescriptorField
    private String inventoryType;
    @ItemDescriptorField
    private ItemType pickupType;
    @ItemDescriptorField
    private ItemType.Category itemCategory;

    public int getAmount() {
        return amount;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public ItemType getPickupType() {
        return pickupType;
    }

    public ItemType.Category getItemCategory() {
        return itemCategory;
    }

    /**
     * Converts a fully qualified field name (e.g. private boolean cz.cuni.amis.MyClass.myBoolean)
     * to a field name (e.g. myBoolean).
     *
     * @param string - complete identifier of a field in a class
     * @return name of a field
     */
    protected String fieldToName(String string) {
        String result = string.substring(string.lastIndexOf(".") + 1);
        return firstCharToUpperCase(result);
    }

    protected String firstCharToUpperCase(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    /**
     * This method does the mapping from a map of attributes contained it ITCMsg and the attributes of the descriptor.
     *
     * NOTE: names of the attributes must be equal to the keys of the HashMap contained in ITCMsg.
     *
     * @param configMsg
     */
    protected void doReflexion(Object configMsg, Class<? extends ItemDescriptor> clazz) {
        Field[] configMsgFields = configMsg.getClass().getDeclaredFields();
        Map<String, Field> configMsgFieldsMap = new HashMap<String, Field>();
        for (Field field : configMsgFields) {
            configMsgFieldsMap.put(field.getName(), field);
        }
        List<Field> descFields = ClassUtils.getAllFields(clazz, false);
        for (Field descField : descFields) {
            if (!descField.isAnnotationPresent(ItemDescriptorField.class)) {
                continue;
            }
            synchronized(descField) {
	            String key = fieldToName(descField.toString());
	            Field configField = configMsgFieldsMap.get(key);
	            synchronized(configField) {
		            try {
		            	descField.setAccessible(true);
		                if (configField == null) {
		                	descField.set(this, null);
		                } else {
		                    configField.setAccessible(true);
		                    descField.set(this, configField.get(configMsg));
		                    configField.setAccessible(false);
		                }
		            } catch (IllegalArgumentException e) {
		                e.printStackTrace();
		            } catch (IllegalAccessException e) {
		                e.printStackTrace();
		            } finally {
		                descField.setAccessible(false);
		            }
	            }
            }
        }
    }

    @Override
    public String toString() {
        return new String("ITEM DESCRIPTOR for PickupType: " + pickupType + ", Inv.Type: " + inventoryType
                + ", Amount: " + amount + ", category: " + itemCategory.toString());
    }
}

package cz.cuni.pogamut.shed.presenter;

import cz.cuni.amis.pogamut.sposh.elements.*;
import cz.cuni.amis.pogamut.sposh.executor.ParamInfo;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveData;
import cz.cuni.pogamut.shed.widget.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Presenter class from MVP design pattern (in MVC, controller gets input
 * directly, in MVP presenter gets input from the view). This presenter is
 * mostly responsible for notifying {@link INameMapListener}s about changes in
 * name mapping.
 * 
 * <em>Note about widget modification:</em> Adding new widget has marked parts of
 * the scene for revalidation, but the scene itself hasn't been revalidated. You
 * must validate changed scene to relayer everything!!
 *
 * @author HonzaH
 */
public class ShedPresenter {

    /**
     * Scene that the presenter will be changing, basically a view.
     */
    private final ShedScene scene;
    /**
     * plan that is being presented.
     */
    private final PoshPlan lapTree;
    /**
     * Map of primitives from FQN of the primitive to human readable name
     * provided by {@link PrimitiveInfo}.
     */
    private final Map<String, PrimitiveData> nameMap = new HashMap<String, PrimitiveData>();
    /**
     * Listeners on changes in name mapping.
     */
    private final Set<INameMapListener> nameMapListeners = new HashSet<INameMapListener>();

    public ShedPresenter(ShedScene scene, PoshPlan lapTree) {
        this.scene = scene;
        this.lapTree = lapTree;
    }

    final ShedScene getScene() {
        return scene;
    }

    final PoshPlan getLapTree() {
        return lapTree;
    }

    void addNameMapListener(INameMapListener listener) {
        nameMapListeners.add(listener);
    }

    void removeNameMapListener(INameMapListener listener) {
        nameMapListeners.remove(listener);
    }

    INameMapListener[] getNameMapListener() {
        int nameListenersSize = nameMapListeners.size();
        return nameMapListeners.toArray(new INameMapListener[nameListenersSize]);
    }

    private void emitNameChange(String key, String oldName, String newName) {
        INameMapListener[] listeners = getNameMapListener();
        for (INameMapListener listener : listeners) {
            listener.nameMapChanged(key, oldName, newName);
        }
    }

    /**
     * Get all keys for which there is a name mapping.
     *
     * @return Unmodifiable set of all keys.
     */
    public Set<String> getAllNameMappingKeys() {
        Set<String> keySet = nameMap.keySet();
        return Collections.unmodifiableSet(keySet);
    }

    /**
     * Get human readble name for the @key.
     *
     * @param primitiveName FQN for which we want the human readable name.
     * @return Human readable name or null if mapping doesn't exist.
     */
    public String getNameMapping(String primitiveName) {
        PrimitiveData record = nameMap.get(primitiveName);
        
        return record != null ? record.name : null;
    }

    /**
     * Get sorted parameters for primitve of key
     * @param key FQN name of primitive
     * @return 
     */
    public ParamInfo[] getPrimitiveParameters(String key) {
        PrimitiveData record = nameMap.get(key);
        if (record == null) {
            return new ParamInfo[0];
        }
        ArrayList<ParamInfo> params = new ArrayList<ParamInfo>(record.params);
        Collections.sort(params);
        return params.toArray(new ParamInfo[params.size()]);
    }
    
    /**
     * Set human readable name for the @key.
     *
     * @param key FQN for which we set the human readable name
     * @param value Human readable name or null if mapping cancelled
     */
    public void setNameMapping(String key, PrimitiveData record) {
        key = key.trim();

        PrimitiveData oldRecord = nameMap.get(key);

        nameMap.put(key, record);

        emitNameChange(key, oldRecord != null ? oldRecord.name : null, record != null ? record.name : null);
    }

    /**
     * Get metadata from {@link PrimitiveInfo} about primitive with name
     * @param name Name of primitive, generally FQN of a class.
     * @return Found metadata or null if no metadata for passed name.
     */
    PrimitiveData getMetadata(String name) {
        return nameMap.get(name);
    }
}
/**
 * Interface for listeners wanting to update their elements according to the
 * name mapping. In our case, mapping will be from FQN to some human readable
 * name, retrieved from {@link PrimitiveInfo} annotation.
 */
interface INameMapListener {

    /**
     * Notify the listener that mapping for the @key has changed.
     *
     * @param key FQN of a class
     * @param oldName What was the original human readable name for the key
     * @param newName Waht is the new human readable name for the key.
     */
    void nameMapChanged(String key, String oldName, String newName);
}
package cz.cuni.pogamut.posh.explorer;

import cz.cuni.amis.pogamut.sposh.executor.IAction;
import cz.cuni.amis.pogamut.sposh.executor.ISense;
import cz.cuni.amis.pogamut.sposh.executor.PrimitiveInfo;

/**
 * Exchange container used for passing info between crawler and explorer.
 * Contains information about primitive {@link IAction} and {@link ISense}, i.e. what is stored in {@link PrimitiveInfo}.
 * @author Honza
 */
public class PrimitiveData implements Comparable<PrimitiveData> {

    /** Fully qualified name of the primitive class */
    public final String classFQN;
    /** Name of the primitive, not necessary unique */
    public final String name;
    /** Description of the primitive */
    public final String description;
    /** Tags for primitive */
    public final String[] tags;

    /**
     * Create new instance of PrimitiveData
     * @param classFQN fully qualified name of the primitive class.
     * @param name name of primitive
     * @param description description of primitive
     * @param tags tags of the primitive
     */
    public PrimitiveData(String classFQN, String name, String description, String[] tags) {
        this.classFQN = classFQN;
        this.name = name;
        this.description = description;
        this.tags = tags;
    }

    /**
     * Get simple class name (not FQN)
     * @return simple name of classFQN
     */
    public  String getClassName() {
        return classFQN.replaceFirst("^.*\\.", "");
    }

    /**
     * Compare this data to another. First ignorcase-compare of names, if same,
     * compare FQN of primitives.
     * @param o The other comparison object
     * @return 
     */
    @Override
    public int compareTo(PrimitiveData o) {
        if (this == o)
            return 0;

        String myName = name != null ? name : getClassName();
        String oName = o.name != null ? o.name : o.getClassName();

        int nameComparison = myName.toLowerCase().compareTo(oName.toLowerCase());
        if (nameComparison != 0)
            return nameComparison;

        return classFQN.compareTo(o.classFQN);
    }

    @Override
    public String toString() {
        return (name != null ? name : getClassName()) + "(" + classFQN + ")";
    }
}

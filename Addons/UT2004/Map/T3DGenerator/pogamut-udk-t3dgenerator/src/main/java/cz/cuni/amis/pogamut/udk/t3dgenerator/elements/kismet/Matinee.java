/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.kismet;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.DynamicReference;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.StaticReference;

/**
 *
 * @author Martin Cerny
 */
public class Matinee extends AbstractKismetObject {
    public static final String CHANGE_DIR_LINK = "ChangeDir";
    public static final String COMPLETED_LINK = "Completed";
    public static final String DATA_LINK = "Data";
    public static final String PAUSE_LINK = "Pause";
    public static final String PLAY_LINK = "Play";
    public static final String REVERSE_LINK = "Reverse";
    public static final String REVERSED_LINK = "Reversed";
    public static final String STOP_LINK = "Stop";

    public Matinee(String archetypeName, MatineeData data){
        super("SeqAct_Interp", archetypeName, new String[]{PLAY_LINK, REVERSE_LINK, STOP_LINK, PAUSE_LINK, CHANGE_DIR_LINK}, new String[]{COMPLETED_LINK, REVERSED_LINK}, new String[] {DATA_LINK});
        setInstanceVersion(2);
        addVariableLinkTarget(DATA_LINK, new DynamicReference(data));
    }

    public Matinee(MatineeData data){
        this("Engine.Default__SeqAct_Interp",data);
    }
}

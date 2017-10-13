/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.kismet;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.Vector3D;

/**
 *
 * @author Martin Cerny
 */
public class LogAction extends AbstractKismetObject{

    public static final String IN_LINK = "In";
    public static final String OUT_LINK = "Out";
    public static final String TARGET_LINK = "Target";

    private Boolean outputToScreen;
    private Float targetDuration;
    private Vector3D targetOffset;
    
    public LogAction(String text) {
        super("SeqAct_Log", "Engine.Default__SeqAct_Log", new String[] {IN_LINK}, new String[]{OUT_LINK}, new String[]{TARGET_LINK});
        setObjComment(text);
        setInstanceVersion(3);
    }

    public Boolean getOutputToScreen() {
        return outputToScreen;
    }

    public void setOutputToScreen(Boolean outputToScreen) {
        this.outputToScreen = outputToScreen;
    }

    public Float getTargetDuration() {
        return targetDuration;
    }

    public void setTargetDuration(Float targetDuration) {
        this.targetDuration = targetDuration;
    }

    public Vector3D getTargetOffset() {
        return targetOffset;
    }

    public void setTargetOffset(Vector3D targetOffset) {
        this.targetOffset = targetOffset;
    }


}

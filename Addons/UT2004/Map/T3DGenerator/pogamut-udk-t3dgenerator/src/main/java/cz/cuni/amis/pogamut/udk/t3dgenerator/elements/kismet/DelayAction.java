/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.kismet;

/**
 * A delay action in kismet.
 * @author Martin Cerny
 */
public class DelayAction extends AbstractKismetObject{
    public static final String ABORTED_LINK = "Aborted";
    public static final String DURATION_LINK = "Duration";
    public static final String FINISHED_LINK = "Finished";
    public static final String PAUSE_LINK = "Pause";
    public static final String START_LINK = "Start";
    public static final String STOP_LINK = "Stop";
    
    
    public static final String CLASSNAME = "SeqAct_Delay";
    
    private Float duration;
    private Boolean startWillRestart;
    
    public DelayAction(String archetypeName, Float duration, Boolean startWillRestart){
        super(CLASSNAME, archetypeName, new String[]{START_LINK, STOP_LINK, PAUSE_LINK}, new String[]{FINISHED_LINK, ABORTED_LINK}, new String[]{DURATION_LINK});
        this.duration = duration;
        this.startWillRestart = startWillRestart;
    }

    public DelayAction(Float duration, Boolean startWillRestart){
        this(getDefaultArchetype(CLASSNAME), duration, startWillRestart);
    }

    public DelayAction(Float duration){
        this(duration, null);
    }

    public Float getDuration() {
        return duration;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }

    public Boolean getStartWillRestart() {
        return startWillRestart;
    }

    public void setStartWillRestart(Boolean startWillRestart) {
        this.startWillRestart = startWillRestart;
    }
    
    
}

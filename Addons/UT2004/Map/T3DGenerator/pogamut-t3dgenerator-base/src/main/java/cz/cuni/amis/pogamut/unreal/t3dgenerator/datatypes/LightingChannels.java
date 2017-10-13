/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.FieldName;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealDataType;

/**
 * 
 * @author Martin Cerny
 * @see <a href="http://wiki.beyondunreal.com/UE3:LightingChannelsObject_%28UDK%29">http://wiki.beyondunreal.com/UE3:LightingChannelsObject_%28UDK%29</a>
 */
@UnrealDataType
public class LightingChannels {
    private Boolean initialized;
    private Boolean dynamic;

    /**
     * Name changed because static is reserved word in Java
     */
    @FieldName("Static")
    private Boolean staticChannel;

    public LightingChannels(boolean initialized, boolean dynamic) {
        this.initialized = initialized;
        this.dynamic = dynamic;
    }

    public LightingChannels() {
    }

    public LightingChannels(Boolean initialized, Boolean dynamic, Boolean Static) {
        this.initialized = initialized;
        this.dynamic = dynamic;
        this.staticChannel = Static;
    }




    public Boolean getDynamic() {
        return dynamic;
    }

    public Boolean getInitialized() {
        return initialized;
    }

    public Boolean getStaticChannel() {
        return staticChannel;
    }


    
}

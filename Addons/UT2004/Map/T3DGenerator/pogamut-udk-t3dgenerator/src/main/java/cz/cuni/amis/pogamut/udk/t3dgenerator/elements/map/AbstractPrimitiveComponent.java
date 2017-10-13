/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealHeaderField;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.LightingChannels;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.NullReference;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;
import cz.cuni.amis.pogamut.udk.t3dgenerator.elements.AbstractObject;

/**
 * Components of other objects. Those differ from simple subobjects.
 * Objects that are components use (for whatever reaseon) ObjName for references instead of regular name.
 * @author Martin Cerny
 * @see <a href="http://wiki.beyondunreal.com/UE3:Component_(UDK)">http://wiki.beyondunreal.com/UE3:Component_(UDK)</a>
 * @see <a href="http://wiki.beyondunreal.com/UE3:PrimitiveComponent_(UDK)">http://wiki.beyondunreal.com/UE3:PrimitiveComponent_(UDK)</a>
 */
public class AbstractPrimitiveComponent extends AbstractObject {

    @UnrealHeaderField
    private String objName;
    private UnrealReference replacementPrimitive = new NullReference();
    private LightingChannels lightingChannels = new LightingChannels(true, true);
    private Boolean hiddenGame = null;
    private Boolean alwaysLoadOnClient = null;
    private Boolean alwaysLoadOnServer = null;

    public AbstractPrimitiveComponent(String componentName, String archetypeName) {
        this(componentName, archetypeName, componentName + "Component");
    }

    public AbstractPrimitiveComponent(String componentName, UnrealReference archetype) {
        this(componentName, archetype, componentName + "Component");
    }

    public AbstractPrimitiveComponent(String componentName, String archetypeName, String className) {
        super(className, archetypeName);
        setName(componentName);
    }

    public AbstractPrimitiveComponent(String componentName, UnrealReference archetype, String className) {
        super(className, archetype);
        setName(componentName);
    }

    public String getObjName() {
        return objName;
    }

    public void setObjName(String objName) {
        this.objName = objName;
    }

    @Override
    public String getNameForReferences() {
        return getObjName();
    }

    @Override
    public void setNameForReferences(String nameForReferences) {
        setObjName(nameForReferences);
    }

    public UnrealReference getReplacementPrimitive() {
        return replacementPrimitive;
    }

    public LightingChannels getLightingChannels() {
        return lightingChannels;
    }

    public void setLightingChannels(LightingChannels lightingChannels) {
        this.lightingChannels = lightingChannels;
    }

    public void setReplacementPrimitive(UnrealReference replacementPrimitive) {
        this.replacementPrimitive = replacementPrimitive;
    }

    public Boolean getHiddenGame() {
        return hiddenGame;
    }

    public void setHiddenGame(Boolean hiddenGame) {
        this.hiddenGame = hiddenGame;
    }

    public Boolean getAlwaysLoadOnClient() {
        return alwaysLoadOnClient;
    }

    public Boolean getAlwaysLoadOnServer() {
        return alwaysLoadOnServer;
    }

    public void setAlwaysLoadOnClient(Boolean alwaysLoadOnClient) {
        this.alwaysLoadOnClient = alwaysLoadOnClient;
    }

    public void setAlwaysLoadOnServer(Boolean alwaysLoadOnServer) {
        this.alwaysLoadOnServer = alwaysLoadOnServer;
    }
}

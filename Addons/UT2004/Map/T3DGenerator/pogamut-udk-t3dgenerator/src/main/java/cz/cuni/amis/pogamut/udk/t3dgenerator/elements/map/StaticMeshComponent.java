/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.map;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.StaticText;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.LightingChannels;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.StaticReference;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;

/**
 * Tho "body" of a static mesh.
 * @author Martin Cerny
 * @see <a href="http://wiki.beyondunreal.com/UE3:StaticMeshComponent_%28UDK%29">http://wiki.beyondunreal.com/UE3:StaticMeshComponent_%28UDK%29</a>
 */
public class StaticMeshComponent extends AbstractPrimitiveComponent {
    public static final String CLASSNAME = "StaticMesh";

    private UnrealReference staticMesh;
    private Integer previewEnvironmentShadowing;
    
    @StaticText
    private final String staticText = "VertexPositionVersionNumber=1\n            "
                + "PreviewEnvironmentShadowing=218\n            " //this value is pretty much a guess
                + "bAllowApproximateOcclusion=True\n            "
                + "bForceDirectLightMap=True\n            "
                + "bForceDirectLightMap=True\n            "
                + "bUsePrecomputedShadows=True\n            ";

    public StaticMeshComponent(String meshName){
        super(CLASSNAME, "Engine.Default__StaticMeshActor:StaticMeshComponent0");
        init(meshName);        
    }
    
    public StaticMeshComponent(String archetypeName, String meshName) {
        super(CLASSNAME, archetypeName);
        init(meshName);
    }

    private void init(String meshName) {
        this.staticMesh = new StaticReference(CLASSNAME, meshName);
        setLightingChannels(new LightingChannels(Boolean.TRUE, null, Boolean.TRUE));
    }

    public Integer getPreviewEnvironmentShadowing() {
        return previewEnvironmentShadowing;
    }

    public void setPreviewEnvironmentShadowing(Integer previewEnvironmentShadowing) {
        this.previewEnvironmentShadowing = previewEnvironmentShadowing;
    }

    public UnrealReference getStaticMesh() {
        return staticMesh;
    }

    public void setStaticMesh(UnrealReference staticMesh) {
        this.staticMesh = staticMesh;
    }

    public String getStaticText() {
        return staticText;
    }

    
    
    
}

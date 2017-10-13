/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.datatypes;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.FieldName;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.UnrealDataType;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;
import cz.cuni.amis.pogamut.unreal.t3dgenerator.datatypes.UnrealReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An output link in kismet.
 * @author Martin Cerny
 */
@UnrealDataType
public class KismetOutputLink extends KismetAbstractLink {
    Collection<KismetLinkTarget> links;

    @FieldName("LinkedOp")
    UnrealReference linkOrigin;

    public KismetOutputLink(List<KismetLinkTarget> links, int drawY, int overrideDelta) {
        super(drawY, overrideDelta);
        this.links = links;
    }

    public KismetOutputLink(List<KismetLinkTarget> links) {
        this.links = links;
    }

    public KismetOutputLink(KismetLinkTarget singleTargetLink){
        this.links = new ArrayList<KismetLinkTarget>(Collections.singletonList(singleTargetLink));
    }

    public boolean removeLink(KismetLinkTarget o) {
        return links.remove(o);
    }

    public boolean addLink(KismetLinkTarget e) {
        return links.add(e);
    }

    public UnrealReference getLinkOrigin() {
        return linkOrigin;
    }

    public void setLinkOrigin(UnrealReference linkOrigin) {
        this.linkOrigin = linkOrigin;
    }

    public Collection<KismetLinkTarget> getLinks() {
        return links;
    }

    
    

}

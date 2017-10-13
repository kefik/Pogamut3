/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.nb.util.maven;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jimmy
 */
@XStreamAlias(value="archetype-catalog")
public class MavenArchetypeCatalog {

    @XStreamAlias(value="archetypes")
    private List<MavenArchetype> archetypes = new ArrayList<MavenArchetype>();

    private MavenArchetypeCatalog readResolve() {
        if (archetypes == null) archetypes = new ArrayList<MavenArchetype>();
        return this;
    }

    public List<MavenArchetype> getArchetypes() {
        return archetypes;
    }

    public void setArchetypes(List<MavenArchetype> archetypes) {
        this.archetypes = archetypes;
    }

}

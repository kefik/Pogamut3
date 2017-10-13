/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.nb.util.maven;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @author Jimmy
 */
@XStreamAlias(value="archetype")
public class MavenArchetype {

    @XStreamAlias(value="groupId")
    private String groupId;
    
    @XStreamAlias(value="artifactId")
    private String artifactId;
    
    @XStreamAlias(value="version")
    private String version;
    
    @XStreamAlias(value="repository")
    private String repository;
    
    @XStreamAlias(value="description")
    private String description;

    private MavenArchetype readResolve() {
        if (groupId != null) groupId = groupId.trim();
        if (artifactId != null) artifactId = artifactId.trim();
        if (version != null) version = version.trim();
        if (repository != null) repository = repository.trim();
        if (description != null) description = description.trim();
        return this;
    }

    

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}

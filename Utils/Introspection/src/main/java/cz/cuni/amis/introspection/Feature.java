/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.introspection;

/**
 *
 * @author Ik
 */
public class Feature {

    protected String name = null;

    public Feature(String name) {
        if (name.contains(".")) {
            throw new RuntimeException("Feature name can't contain the dot character.");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

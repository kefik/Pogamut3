/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.unreal.t3dgenerator;

/**
 * Context employed by {@link DefaultT3dGenerator} in preprocessing elements.
 * @author Martin Cerny
 */
public class DefaultT3dGeneratorContext implements IT3dGeneratorContext {
    private INamingFactory namingFactory;

    public DefaultT3dGeneratorContext(INamingFactory namingFactory) {
        this.namingFactory = namingFactory;
    }

    @Override
    public INamingFactory getNamingFactory() {
        return namingFactory;
    }


}

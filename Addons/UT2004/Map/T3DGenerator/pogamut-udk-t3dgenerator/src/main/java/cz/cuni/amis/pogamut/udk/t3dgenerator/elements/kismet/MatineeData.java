/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.udk.t3dgenerator.elements.kismet;

import cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations.StaticText;


/**
 *
 * @author Martin Cerny
 */
public class MatineeData extends KismetVariable {
    @StaticText
    private String contents;

    public MatineeData(String contents) {
        super("InterpData","Engine.Default__InterpData");
        this.contents = contents;
    }

    public String getContents() {
        return contents;
    }

    

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.cuni.amis.pogamut.unreal.t3dgenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple naming factory that creates names in form "ClassName_ordinalNumber"
 * @author Martin Cerny
 */
public class SequenceNamingFactory implements INamingFactory {
    private Map<String, Integer> sequences;

    public SequenceNamingFactory() {
        this.sequences = new HashMap<String, Integer>();
    }



    @Override
    public String getName(String objectClass) {
        if(sequences.get(objectClass) == null){
            sequences.put(objectClass,0);
        }

        Integer currentNumber = sequences.get(objectClass);
        sequences.put(objectClass, currentNumber + 1);

        return objectClass + "_" + currentNumber;
    }


}

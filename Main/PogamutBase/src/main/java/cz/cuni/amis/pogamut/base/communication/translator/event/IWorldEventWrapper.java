/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.base.communication.translator.event;

import cz.cuni.amis.pogamut.base.communication.worldview.event.IWorldEvent;


/**
 * Interface for classes wrapping the IWorldEvent.
 * @author ik
 */
public interface IWorldEventWrapper extends IWorldChangeEvent {
    /**
     * @return WorldEvent transported (wrapped) by this class
     */
    IWorldEvent getWorldEvent();
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.amis.pogamut.unreal.communication.messages.gbinfomessages;

/**

Info batch message. Starts with SMAP message, ends with EMAP
message. Hold information about available maps on the server
(maps to which we can change the game).
Corresponding GameBots message is  IMAP.
 */
public interface IMapList {

    /**

    Name of one map in map list on the server.

     */
    public String getName();

    public String toHtmlString();
}
